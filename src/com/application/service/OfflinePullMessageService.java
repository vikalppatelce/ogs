/**
 * 
 */
package com.application.service;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.application.beans.LostMessagesInfo;
import com.application.beans.MessageObject;
import com.application.messenger.ConnectionsManager;
import com.application.utils.AppConstants;
import com.application.utils.ApplicationLoader;
import com.application.utils.BuildVars;
import com.application.utils.DBConstant;
import com.application.utils.RequestBuilder;
import com.application.utils.RestClient;
import com.application.utils.Utilities;

/**
 * @author Vikalp Patel(VikalpPatelCE)
 *
 */
public class OfflinePullMessageService extends IntentService{
	private static final String TAG = OfflinePullMessageService.class.getSimpleName();
	
	public OfflinePullMessageService() {
        super("OfflinePullMessageService");
        Log.i(TAG, "Service Started");
    }
	/**
	 * @param name
	 */
	public OfflinePullMessageService(String name) {
		super("OfflinePullMessageService");
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	/* (non-Javadoc)
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent(Intent mIntent) {
		// TODO Auto-generated method stub
		fetchOfflineLostMessagePackets();
	}
	
	private void fetchOfflineLostMessagePackets(){
		Log.i(TAG, "onHandleIntent");
		Utilities.globalQueue.postRunnable(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String mResponseFromApi;
				JSONObject mJSONObject = buildJSONRequestForOfflineLostMessagePackets();
				try {
					mResponseFromApi = RestClient.postJSON(AppConstants.API.URL, mJSONObject);
					if(BuildVars.DEBUG_VERSION){
						mResponseFromApi = Utilities.readFile("apiOfflineLostMessages.json");
					}
					if(!TextUtils.isEmpty(mResponseFromApi)){
						parseJSONObjectFromApi(mResponseFromApi);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	private JSONObject buildJSONRequestForOfflineLostMessagePackets(){
		ArrayList<LostMessagesInfo> mListLostMessagesInfo = new ArrayList<LostMessagesInfo>();
		fetchLastMessageIdAgainstRoomFromDB(mListLostMessagesInfo);
		JSONObject mJSONObject = RequestBuilder.getPostOfflieLostMessages(RequestBuilder.getOfflineLostMessagesRoomAndLastMessagesIdArray(mListLostMessagesInfo));
		Log.i(TAG, mJSONObject.toString());
		return mJSONObject;
	}
	
	private void fetchLastMessageIdAgainstRoomFromDB(ArrayList<LostMessagesInfo> mListLostMessagesInfo){
//		Cursor mCursor = ApplicationLoader.getApplication().getContentResolver().query(DBConstant.Group_Columns.CONTENT_URI, null, null, null, null);
		Cursor mCursor = ApplicationLoader.getApplication().getContentResolver().query(DBConstant.Group_Columns.CONTENT_URI, null, DBConstant.Group_Columns.COLUMN_GROUP_IS_ACTIVE + "=?", new String[]{"1"}, null);
		if(mCursor!=null && mCursor.getCount() > 0){
			mCursor.moveToNext();
			do {
				String mGroupJabberId = mCursor.getString(mCursor.getColumnIndex(DBConstant.Group_Columns.COLUMN_GROUP_JABBER_ID));
				
				Cursor mCursorChat = ApplicationLoader.getApplication().getContentResolver().query(DBConstant.Chat_Columns.CONTENT_URI, null, DBConstant.Chat_Columns.COLUMN_GROUP_ID + "=?", new String[]{mGroupJabberId}, DBConstant.Chat_Columns.COLUMN_TIMESTAMP + " DESC");
				if(mCursorChat!=null && mCursorChat.getCount() > 0){
					mCursorChat.moveToNext();
					LostMessagesInfo Obj = new LostMessagesInfo();
					Obj.setmLastMessageId(mCursorChat.getString(mCursorChat.getColumnIndex(DBConstant.Chat_Columns.COLUMN_MESSAGE_ID)));
					Obj.setmRoomJabberId(mGroupJabberId);
					mListLostMessagesInfo.add(Obj);					
				}
				mCursorChat.close();
			} while (mCursor.moveToNext());
		}
		mCursor.close();
	}
	
	private void parseJSONObjectFromApi(String mResponseFromApi){
		try {
			JSONObject mJSONObject = new JSONObject(mResponseFromApi);
			if(mJSONObject.getBoolean("success")){
				JSONObject mJSONObjectData = mJSONObject.getJSONObject("data");
				JSONArray mJSONMessageArray = mJSONObjectData.getJSONArray("room_info");
				for (int i = 0; i < mJSONMessageArray.length(); i++) {
					JSONObject mJSONMessageObject = mJSONMessageArray.getJSONObject(i);
					addMessageObjectsToDb(mJSONMessageObject);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.i(TAG, e.toString());
		}catch(Exception e){
			e.printStackTrace();
			Log.i(TAG, e.toString());
		}
	}
	
	private void addMessageObjectsToDb(JSONObject mJSONObject){
		try{
			String messageText = mJSONObject.getString("message");
			int messageType = mJSONObject.getInt("messageType");
			String messageFileLink = mJSONObject.getString("messageFileLink");
			String messageTime = mJSONObject.getString("messageTime");
			String messageFrom = mJSONObject.getString("messageFrom");
			String messageIdFromJSON = mJSONObject.getString("messageId");
			String messageCityId = mJSONObject.getString("messageCityId");
			String messageUserIdMySQL = mJSONObject.getString("messageUserIdMySQL");
			String messageUserJabberId = mJSONObject.getString("messageUserJabberId");
			String messageGroupIdMySQL = mJSONObject.getString("messageGroupIdMySQL");
			String groupId = mJSONObject.getString("messageTo");
			
			String userId = messageIdFromJSON.substring(0, messageIdFromJSON.length() - messageTime.length());
			
			if (!messageUserJabberId.equalsIgnoreCase(ApplicationLoader.getPreferences().getJabberId())) {
				Cursor mCursor  = ApplicationLoader.getApplication().getContentResolver().query(DBConstant.Chat_Columns.CONTENT_URI, null, DBConstant.Chat_Columns.COLUMN_MESSAGE_ID+"=?", new String[]{messageIdFromJSON}, null);
				if(mCursor!=null && mCursor.getCount() == 0){
					ContentValues values = new ContentValues();
					values.put(DBConstant.Chat_Columns.COLUMN_USER_ID,messageFrom);
					values.put(DBConstant.Chat_Columns.COLUMN_GROUP_ID,groupId);
					values.put(DBConstant.Chat_Columns.COLUMN_CITY_ID, messageCityId);
					values.put(DBConstant.Chat_Columns.COLUMN_USER_ID_MYSQL, messageUserIdMySQL);
					values.put(DBConstant.Chat_Columns.COLUMN_USER_JABBER_ID, messageUserJabberId);
					values.put(DBConstant.Chat_Columns.COLUMN_GROUP_ID_MYSQL, messageGroupIdMySQL);
					values.put(DBConstant.Chat_Columns.COLUMN_MESSAGE,messageText);
					values.put(DBConstant.Chat_Columns.COLUMN_MESSAGE_ID,messageIdFromJSON);
					values.put(DBConstant.Chat_Columns.COLUMN_TIMESTAMP,messageTime);
					values.put(DBConstant.Chat_Columns.COLUMN_MESSAGE_DATE,Utilities.getDate(Long.parseLong(messageTime)));
					values.put(DBConstant.Chat_Columns.COLUMN_MESSAGE_TIME,Utilities.getTime(Long.parseLong(messageTime)));
					values.put(DBConstant.Chat_Columns.COLUMN_USER_SENT_MESSAGE,"1");
					values.put(DBConstant.Chat_Columns.COLUMN_TYPE, messageType);
					values.put(DBConstant.Chat_Columns.COLUMN_FILE_LINK, messageFileLink);
					values.put(DBConstant.Chat_Columns.COLUMN_PATH, Utilities.getFilePath(messageType,Utilities.getJabberGroupIdWithoutTale(groupId),messageTime));
					
					ApplicationLoader.getApplication().getContentResolver().insert(DBConstant.Chat_Columns.CONTENT_URI,values);
					
					Intent mIntent = new Intent(ConnectionsManager.BROADCAST_ACTION);
					mIntent.putExtra("MessageObjectString",userId + " : " + mJSONObject.toString());
					
					MessageObject messageObject = new MessageObject();
					messageObject.setMessageId(messageIdFromJSON);
					messageObject.setMessageText(messageText);
					messageObject.setMessageType(messageType);
					messageObject.setGroupId(groupId);
					messageObject.setThisUserSentRight(false);
					messageObject.setMessageTimeStamp(messageTime);
					messageObject.setCityId(messageCityId);
					messageObject.setGroupIdMySQL(messageGroupIdMySQL);
					messageObject.setMessageUserIdMySQL(messageUserIdMySQL);
					messageObject.setMessageUserJabberId(messageUserJabberId);
					messageObject.setMessageFileLink(messageFileLink);
					messageObject.setMessageDate(Utilities.getDate(Long.parseLong(messageTime)));
					messageObject.setMessageTime(Utilities.getTime(Long.parseLong(messageTime)));
					messageObject.setUserId(messageFrom);
					messageObject.setFilePath(Utilities.getFilePath(messageType, Utilities.getJabberGroupIdWithoutTale(groupId),messageTime));
					
					mIntent.putExtra("MessageObject",messageObject);
					ConnectionsManager.getInstance().pushNotification(groupId, messageCityId,messageFrom, messageObject,mIntent);	
				}
			} else {// your message only! Use it as
					// acknowledgement
				ContentValues values = new ContentValues();
				values.put(DBConstant.Chat_Columns.COLUMN_USER_ID,messageFrom);
				values.put(DBConstant.Chat_Columns.COLUMN_GROUP_ID,groupId);
				values.put(DBConstant.Chat_Columns.COLUMN_MESSAGE,messageText);
				values.put(DBConstant.Chat_Columns.COLUMN_MESSAGE_ID,messageIdFromJSON);
				values.put(DBConstant.Chat_Columns.COLUMN_TIMESTAMP,messageTime);
				values.put(DBConstant.Chat_Columns.COLUMN_TYPE, messageType);
				values.put(DBConstant.Chat_Columns.COLUMN_ISDELIEVERED,"1");
				values.put(DBConstant.Chat_Columns.COLUMN_USER_SENT_MESSAGE,"0");
				values.put(DBConstant.Chat_Columns.COLUMN_FILE_LINK, messageFileLink);
				values.put(DBConstant.Chat_Columns.COLUMN_PATH, Utilities.getFilePath(messageType,Utilities.getJabberGroupIdWithoutTale(groupId), messageTime));
				ApplicationLoader.getApplication().getContentResolver()
						.update(DBConstant.Chat_Columns.CONTENT_URI,values,DBConstant.Chat_Columns.COLUMN_MESSAGE_ID+ "=?",new String[] { messageIdFromJSON });
			}
		}catch(JSONException e){
			Log.i(TAG, e.toString());
		}
		catch(Exception e){
			Log.i(TAG, e.toString());
		}
	}
}
