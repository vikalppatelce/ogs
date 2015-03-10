package com.application.messenger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.application.beans.InBoxMessages;
import com.application.beans.MessageObject;
import com.application.ui.activity.CitySelectActivity;
import com.application.ui.activity.GroupChatActivity;
import com.application.ui.activity.GroupSelectActivity;
import com.application.utils.AppConstants;
import com.application.utils.ApplicationLoader;
import com.application.utils.BadgeUtils;
import com.application.utils.BuildVars;
import com.application.utils.DBConstant;
import com.application.utils.RequestBuilder;
import com.application.utils.Utilities;
import com.digitattva.ttogs.R;

public class ConnectionsManager {
	private static final String TAG = ConnectionsManager.class.getSimpleName();
	public static final String BROADCAST_ACTION = "com.application.ui.MotherGroupActivity";
	public static final String BROADCAST_ACTION_DEBUG = "com.application.ui.MotherGroupActivity.Debugging_Purpose";
	private static volatile ConnectionsManager Instance = null;
	private XMPPConnection mXMPPConnection;
	private ConnectionConfiguration mConnectionConfig;
//	private MultiUserChat[] mMultiUserChat;
	private HashMap<String, MultiUserChat> mMultiUserChat; 
	private PacketFilter filter;

	public static ConnectionsManager getInstance() {
		ConnectionsManager localInstance = Instance;
		if (localInstance == null) {
			synchronized (ConnectionsManager.class) {
				localInstance = Instance;
				if (localInstance == null) {
					Instance = localInstance = new ConnectionsManager();
				}
			}
		}
		return localInstance;
	}

	public void initPushConnection() {
		if (!TextUtils.isEmpty(ApplicationLoader.getPreferences().getJabberId())) {
			Utilities.stageQueue.postRunnable(new Runnable() {
				@Override
				public void run() {
					try {

						SASLAuthentication.supportSASLMechanism("PLAIN", 0);
						mConnectionConfig = new ConnectionConfiguration(
								AppConstants.XMPP.HOST, 5222,
								AppConstants.XMPP.TALE);
						mXMPPConnection = new XMPPTCPConnection(
								mConnectionConfig);
						mConnectionConfig.setDebuggerEnabled(true);
						mConnectionConfig
								.setSecurityMode(SecurityMode.disabled);
						mXMPPConnection.connect();
						if(BuildVars.DEBUG_VERSION){
							mXMPPConnection.login(AppConstants.XMPP.DEBUG_USERNAME
									+ "@"
									+ AppConstants.XMPP.TALE, AppConstants.XMPP.DEBUG_PASSWORD);
							
							ApplicationLoader.getPreferences().setJabberId(AppConstants.XMPP.DEBUG_USERNAME);
						}else{
							mXMPPConnection.login(ApplicationLoader
									.getPreferences().getJabberId()
									+ "@"
									+ AppConstants.XMPP.TALE, AppConstants.XMPP.PASSWORD);	
						}
						
						if(BuildVars.DEBUGGING_PURPOSE){
							showDebugConnections(BuildVars.DEBUG_LOGGED_IN);
							showDebugConnections(BuildVars.DEBUG_CONNECTING);
						}
						
						int mTotalGroupsNumber = getTotalNumberOfGroups();
						mMultiUserChat = new HashMap<String, MultiUserChat>(mTotalGroupsNumber);
						
						joinMultiUserChatRoom(mMultiUserChat);

						filter = new MessageTypeFilter(Message.Type.groupchat);
						mXMPPConnection.addPacketListener(new PacketListener() {
							@Override
							public void processPacket(Packet packet) {
								Message message = (Message) packet;
								if (message.getBody() != null) {
									JSONObject mJSONObject;
									try {
										mJSONObject = new JSONObject(message.getBody());
										String from = message.getFrom();
										String Body = message.getBody();
										String messageId = message.getPacketID();
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
//										String groupId = Utilities
//												.getJabberGroupId(from);
//										String userId = Utilities
//												.getJabberUserId(from);
										String groupId = mJSONObject.getString("messageTo");
										String userId = messageIdFromJSON.substring(0, messageIdFromJSON.length() - messageTime.length());
										if (!messageUserJabberId.equalsIgnoreCase(ApplicationLoader.getPreferences().getJabberId())) {
											if(Long.parseLong(messageTime) > Long.parseLong(ApplicationLoader.getPreferences().getUserRegistrationTime())){
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
													
													Intent mIntent = new Intent(BROADCAST_ACTION);
													mIntent.putExtra("MessageObjectString",userId + " : " + Body);
													
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
//													pushNotification(groupId, messageCityId,userId, messageObject,mIntent);	
													pushNotification(groupId, messageCityId,messageFrom, messageObject,mIntent);	
												}
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
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}
						}, filter);
					} catch (SmackException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (XMPPException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		} else {
			/*generateLoginNotification(ApplicationLoader.getApplication()
					.getApplicationContext());*/
		}
	}

	public void pushMessage(final String mGroupJabberId, final String message,
			final int messageType, final String messageTime,
			final String messageFileLink, final String messageCityId, final String messageGroupIdMySQL,
			final String messageUserIdMySQL, final String messageUserJabberId) {
		if (!TextUtils.isEmpty(mGroupJabberId)) {
			if (mXMPPConnection != null) {
				if (mXMPPConnection.isConnected()) {
					if (mMultiUserChat != null) {
						Utilities.stageQueue.postRunnable(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								try {
									Message msg = new Message(mGroupJabberId,Message.Type.groupchat);
//									msg.setBody(message);
									if(messageType == 0){
										if(!TextUtils.isEmpty(message)){
											msg.setBody(RequestBuilder
													.getMessageObjectToJSON(message,ApplicationLoader.getPreferences().getJabberId()+ messageTime,String.valueOf(messageTime),messageType,
															ApplicationLoader.getPreferences().getDisplayName(),
															mGroupJabberId, messageFileLink, "", messageCityId, messageGroupIdMySQL,messageUserIdMySQL, messageUserJabberId).toString());
											mMultiUserChat.get(mGroupJabberId).sendMessage(msg);
											addMessageToDatabase(mGroupJabberId,
													message,messageType, messageTime, messageFileLink,messageCityId, messageGroupIdMySQL,messageUserIdMySQL, messageUserJabberId);											
										}
									}else{
										msg.setBody(RequestBuilder
												.getMessageObjectToJSON(message,ApplicationLoader.getPreferences().getJabberId()+ messageTime,String.valueOf(messageTime),messageType,
														ApplicationLoader.getPreferences().getDisplayName(),
														mGroupJabberId, messageFileLink, "", messageCityId, messageGroupIdMySQL,messageUserIdMySQL, messageUserJabberId).toString());
										mMultiUserChat.get(mGroupJabberId).sendMessage(msg);
										addMessageToDatabase(mGroupJabberId,
												message,messageType, messageTime, messageFileLink,messageCityId, messageGroupIdMySQL,messageUserIdMySQL, messageUserJabberId);
									}
								} catch (NotConnectedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (XMPPException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
					} else {
						initPushConnection();
						if(BuildVars.DEBUGGING_PURPOSE){
							showDebugConnections(BuildVars.DEBUG_LOGGING_IN);
						}
					}
				} else {
					initPushConnection();
					if(BuildVars.DEBUGGING_PURPOSE){
						showDebugConnections(BuildVars.DEBUG_LOGGING_IN);
					}
				}
			} else {
				initPushConnection();
				if(BuildVars.DEBUGGING_PURPOSE){
					showDebugConnections(BuildVars.DEBUG_LOGGING_IN);
				}
			}
		}
	}
	
	public void joinMultiUserChatRoom(MultiUserChat[] mMultiUserChat){
		int mTotalGroups = getTotalNumberOfGroups(); 
		Cursor mCursor = getCursorGroups();
		
		if(mCursor!=null && mCursor.getCount() > 0){
			mCursor.moveToFirst();
			int intColumnGroupJabberId = mCursor.getColumnIndex(DBConstant.Group_Columns.COLUMN_GROUP_ID);
			for (int i = 0; i < mTotalGroups; i++) {
				try {
					mMultiUserChat[i] = new MultiUserChat(mXMPPConnection, mCursor.getString(intColumnGroupJabberId));
					mMultiUserChat[i].join(ApplicationLoader.getPreferences().getJabberId());
					Log.i(TAG, mCursor.getString(intColumnGroupJabberId));
					mCursor.moveToNext();
				} catch (NoResponseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (XMPPErrorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NotConnectedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public void joinMultiUserChatRoom(HashMap<String, MultiUserChat> mMultiUserChat){
		int mTotalGroups = getTotalNumberOfGroups(); 
		Cursor mCursor = getCursorGroups();
		
		if(mCursor!=null && mCursor.getCount() > 0){
			mCursor.moveToFirst();
			int intColumnGroupJabberId = mCursor.getColumnIndex(DBConstant.Group_Columns.COLUMN_GROUP_JABBER_ID);
			for (int i = 0; i < mTotalGroups; i++) {
				try {
					Log.i(TAG, mCursor.getString(intColumnGroupJabberId));
					mMultiUserChat.put(mCursor.getString(intColumnGroupJabberId), new MultiUserChat(mXMPPConnection, mCursor.getString(intColumnGroupJabberId)));
					mMultiUserChat.get(mCursor.getString(intColumnGroupJabberId)).join(ApplicationLoader.getPreferences().getJabberId());
					Log.i(TAG, mCursor.getString(intColumnGroupJabberId));
					mCursor.moveToNext();
				} catch (NoResponseException e) {
					// TODO Auto-generated catch block
					mCursor.moveToNext();
					e.printStackTrace();
				} catch (XMPPErrorException e) {
					// TODO Auto-generated catch block
					mCursor.moveToNext();
					e.printStackTrace();
				} catch (NotConnectedException e) {
					// TODO Auto-generated catch block
					mCursor.moveToNext();
					e.printStackTrace();
				} catch(Exception e){
					mCursor.moveToNext();
					e.printStackTrace();
				}
			}
			if(BuildVars.DEBUGGING_PURPOSE){
				showDebugConnections(BuildVars.DEBUG_CONNECTED);
			}
		}
	}
	
	public void showDebugConnections(String mConnectionMethod){
		try {
			
			Intent mIntent = new Intent(BROADCAST_ACTION_DEBUG);
			mIntent.putExtra("debugConnections",mConnectionMethod);
			
			ActivityManager am = (ActivityManager) ApplicationLoader
					.getApplication()
					.getSystemService(
							ApplicationLoader.getApplication().ACTIVITY_SERVICE);
			// get the info from the currently running task
			List<ActivityManager.RunningTaskInfo> taskInfo = am
					.getRunningTasks(1);
			ComponentName componentInfo = taskInfo.get(0).topActivity;
			// if app is running
			if (componentInfo.getPackageName().equalsIgnoreCase(ApplicationLoader.getApplication().getResources()
									.getString(com.digitattva.ttogs.R.string.package_name))) {
				ApplicationLoader.getApplication().getApplicationContext().sendBroadcast(mIntent);
			}
		} catch (Exception e) {
			Log.i(TAG, e.toString());
		}
	}
	
	public int getTotalNumberOfGroups(){
		Cursor mCursor = ApplicationLoader.getApplication().getContentResolver()
				.query(DBConstant.Group_Columns.CONTENT_URI, null, null, null, null);
		if(mCursor!=null){
			return mCursor.getCount();
		}
		return 0;
	}
	
	public Cursor getCursorGroups(){
		return ApplicationLoader.getApplication().getContentResolver().query(DBConstant.Group_Columns.CONTENT_URI, null, DBConstant.Group_Columns.COLUMN_GROUP_IS_ACTIVE + "=?", new String[]{"1"}, DBConstant.Group_Columns.COLUMN_ID + " ASC");
	}
	
	public int getGroupNoInDatabase(String mGroupJabberId){
		Cursor mCursor = ApplicationLoader.getApplication().getContentResolver().
		query(DBConstant.Group_Columns.CONTENT_URI, null, DBConstant.Group_Columns.COLUMN_GROUP_ID + "=?", new String[]{mGroupJabberId}, null);
		
		if(mCursor!=null && mCursor.getCount() > 0){
			mCursor.moveToFirst();
			return Integer.parseInt(mCursor.getString(mCursor.getColumnIndex(DBConstant.Group_Columns.COLUMN_ID)));
		}
		return -1;
	}

	public void addMessageToDatabase(String groupId, String message,
			int messageType, String messageTime, String messageFileLink,
			String messageCityId, String messageGroupIdMySQL,
			String messageUserIdMySQL, String messageUserJabberId) {
		ContentValues values = new ContentValues();
		values.put(DBConstant.Chat_Columns.COLUMN_USER_ID, ApplicationLoader.getPreferences().getDisplayName());
		values.put(DBConstant.Chat_Columns.COLUMN_GROUP_ID, groupId);
		values.put(DBConstant.Chat_Columns.COLUMN_MESSAGE, message);
		values.put(DBConstant.Chat_Columns.COLUMN_CITY_ID, messageCityId);
		values.put(DBConstant.Chat_Columns.COLUMN_GROUP_ID_MYSQL, messageGroupIdMySQL);
		values.put(DBConstant.Chat_Columns.COLUMN_USER_ID_MYSQL, messageUserIdMySQL);
		values.put(DBConstant.Chat_Columns.COLUMN_USER_JABBER_ID, messageUserJabberId);
		values.put(DBConstant.Chat_Columns.COLUMN_MESSAGE_ID, ApplicationLoader.getPreferences().getJabberId()+messageTime);
		values.put(DBConstant.Chat_Columns.COLUMN_TIMESTAMP,messageTime);
		values.put(DBConstant.Chat_Columns.COLUMN_MESSAGE_DATE,Utilities.getDate(Long.valueOf(messageTime)));
		values.put(DBConstant.Chat_Columns.COLUMN_MESSAGE_TIME,Utilities.getTime(Long.valueOf(messageTime)));
		values.put(DBConstant.Chat_Columns.COLUMN_USER_SENT_MESSAGE, "0");
		values.put(DBConstant.Chat_Columns.COLUMN_TYPE, messageType);
		values.put(DBConstant.Chat_Columns.COLUMN_PATH, Utilities.getFilePath(messageType, Utilities.getJabberGroupIdWithoutTale(groupId), messageTime));
		values.put(DBConstant.Chat_Columns.COLUMN_FILE_LINK, messageFileLink);
		ApplicationLoader.getApplication().getContentResolver().insert(DBConstant.Chat_Columns.CONTENT_URI, values);
		Intent mIntent = new Intent(BROADCAST_ACTION);
		mIntent.putExtra("MessageObjectString", ApplicationLoader.getPreferences().getJabberId() + " : " + message);
		MessageObject messageObject = new MessageObject();
		messageObject.setMessageId(ApplicationLoader.getPreferences().getJabberId()+messageTime);
		messageObject.setMessageText(message);
		messageObject.setMessageType(getMessageType(message));
		messageObject.setGroupId(groupId);
		messageObject.setGroupIdMySQL(messageGroupIdMySQL);
		messageObject.setCityId(messageCityId);
		messageObject.setMessageUserIdMySQL(messageUserIdMySQL);
		messageObject.setMessageUserJabberId(messageUserJabberId);
		messageObject.setMessageType(messageType);
		messageObject.setThisUserSentRight(true);
		messageObject.setMessageTime(Utilities.getTime(Long.valueOf(messageTime)));
		messageObject.setUserId(ApplicationLoader.getPreferences().getDisplayName());
		messageObject.setMessageDate(Utilities.getDate(Long.valueOf(messageTime)));
		messageObject.setFilePath(Utilities.getFilePath(messageType, Utilities.getJabberGroupIdWithoutTale(groupId), messageTime));
		messageObject.setMessageTimeStamp(String.valueOf(Long.valueOf(messageTime)));
		mIntent.putExtra("MessageObject", messageObject);
		pushNotification(groupId, messageCityId,ApplicationLoader.getPreferences()
				.getDisplayName(), messageObject, mIntent);
	}

	public void pushNotification(String mGroupJabberId, String mCityId, String mUseJabberId,
			MessageObject mMessageObject, Intent mIntent) {
		try {
			ActivityManager am = (ActivityManager) ApplicationLoader
					.getApplication()
					.getSystemService(
							ApplicationLoader.getApplication().ACTIVITY_SERVICE);
			// get the info from the currently running task
			List<ActivityManager.RunningTaskInfo> taskInfo = am
					.getRunningTasks(1);
			ComponentName componentInfo = taskInfo.get(0).topActivity;
			// if app is running
			if (componentInfo.getPackageName().equalsIgnoreCase(ApplicationLoader.getApplication().getResources()
									.getString(com.digitattva.ttogs.R.string.package_name))) {
				ApplicationLoader.getApplication().getApplicationContext().sendBroadcast(mIntent);
			} else {
				generateNotification(ApplicationLoader.getApplication()
						.getApplicationContext(), mGroupJabberId, mCityId,mUseJabberId, mMessageObject);
			}
		} catch (Exception e) {
			Log.i(TAG, e.toString());
		}
	}

	public void generateNotification(Context mContext, String mFromGroup, String mCity,
			String mFromUser, MessageObject mMessageObject) {
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
			Cursor mCursor = ApplicationLoader.getApplication().getContentResolver().query(DBConstant.Chat_Columns.CONTENT_URI, null, DBConstant.Chat_Columns.COLUMN_ISREAD + "=?", new String[]{"0"}, null);
			if(mCursor!=null && mCursor.getCount() > 1){
				generateMultipleMessageNotification(mContext, mFromGroup,mCity, mFromUser, mMessageObject);
			}else{
				generateSingleMessageNotification(mContext, mFromGroup,mCity, mFromUser, mMessageObject);
			}
		}else{
			generateSingleMessageNotification(mContext, mFromGroup,mCity, mFromUser, mMessageObject);
		}
		generateBadgeNotification();
	}
	
	public void generateSingleMessageNotification(Context mContext, String mFromGroup, String mCity,
			String mFromUser, MessageObject mMessageObject){
		String mGroupName= ApplicationLoader.getApplication().getResources().getString(R.string.app_name);
		String mMessageText = "";
		Intent mNotificationIntent = new Intent(mContext, GroupChatActivity.class);
		mNotificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		mNotificationIntent.putExtra("notificationFromGroupJabberId", mFromGroup);
		mNotificationIntent.putExtra("notificationFromCityId", mCity);
		mNotificationIntent.putExtra("notificationFromConnectionsManager", true);
		mNotificationIntent.putExtra("notificationFromCityIsActive", "1");
		try{
			Cursor mCursor = ApplicationLoader.getApplication().getContentResolver().query(DBConstant.Group_Columns.CONTENT_URI, null, DBConstant.Group_Columns.COLUMN_GROUP_JABBER_ID+"=?", new String[]{mFromGroup}, null);
			if(mCursor!=null && mCursor.getCount() > 0){
				mCursor.moveToFirst();
				mGroupName = mCursor.getString(mCursor.getColumnIndex(DBConstant.Group_Columns.COLUMN_GROUP_NAME));
				mNotificationIntent.putExtra("notificationFromGroupName", mGroupName);	
			}
		}catch(Exception e){
			Log.i(TAG, e.toString());
		}
		
		PendingIntent mContentIntent = PendingIntent.getActivity(mContext, 0,
	            mNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		mMessageText = Utilities.getNotificationMessageText(mMessageObject);
		
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				mContext).setSmallIcon(R.drawable.ic_notification_icon)
//				.setContentTitle(mFromGroup + " : " + mFromUser)
				.setContentTitle(mGroupName)
				.setTicker(mFromUser+" : "+mMessageText)
				.setContentText(mFromUser+" : "+mMessageText);
		mBuilder.setDefaults(-1);
		mBuilder.setOnlyAlertOnce(true);
		mBuilder.setAutoCancel(true);
		mBuilder.setContentIntent(mContentIntent);
		mBuilder.setGroup("messages");
        mBuilder.setGroupSummary(true);
        
        Notification notification = new NotificationCompat.BigTextStyle(mBuilder).bigText(mMessageText).build();
        
		NotificationManager mNotificationManager = (NotificationManager) ApplicationLoader
				.getApplication()
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(AppConstants.NOTIFICATION_ID);
		mNotificationManager.notify(AppConstants.NOTIFICATION_ID, notification);
	}
	
	
	public void generateMultipleMessageNotification(Context mContext, String mFromGroup, String mCity,
			String mFromUser, MessageObject mMessageObject){
		boolean isFromDifferentConversation = false;
		int differentConversation = 1;
		String mGroupName= ApplicationLoader.getApplication().getResources().getString(R.string.app_name);
		String mMessageText = "";
		String mTotalMessages ="";
		String mConversation = " conversation";
//		ArrayList<InBoxMessages> mMessageList = new ArrayList<InBoxMessages>();
		ArrayList<String> mMessageList = new ArrayList<String>();
		try{
			Cursor mCursor = ApplicationLoader.getApplication().getContentResolver().query(DBConstant.Chat_Columns.CONTENT_URI, null, DBConstant.Chat_Columns.COLUMN_ISREAD +"=?", new String[]{"0"}, DBConstant.Chat_Columns.COLUMN_ID + " DESC");
			if(mCursor!=null && mCursor.getCount() > 0){
				mTotalMessages = String.valueOf(mCursor.getCount());
				mCursor.moveToFirst();
				do {
					InBoxMessages obj = new InBoxMessages();
//					obj.setmMessage(mCursor.getString(mCursor.getColumnIndex(DBConstant.Chat_Columns.COLUMN_USER_ID)) + " : "+ mCursor.getString(mCursor.getColumnIndex(DBConstant.Chat_Columns.COLUMN_MESSAGE)));
//					obj.setmMessageGroupId(mCursor.getString(mCursor.getColumnIndex(DBConstant.Chat_Columns.COLUMN_USER_JABBER_ID)));
					mMessageList.add(mCursor.getString(mCursor.getColumnIndex(DBConstant.Chat_Columns.COLUMN_USER_ID)) + " : "+ mCursor.getString(mCursor.getColumnIndex(DBConstant.Chat_Columns.COLUMN_MESSAGE)));
				} while (mCursor.moveToNext());
			}
			
		}catch(Exception e){
		}
		
		Intent mNotificationIntent = new Intent(mContext, GroupSelectActivity.class);
		mNotificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		mNotificationIntent.putExtra("notificationFromGroupJabberId", mFromGroup);
		mNotificationIntent.putExtra("notificationFromCityId", mCity);
		mNotificationIntent.putExtra("notificationFromConnectionsManager", true);
		mNotificationIntent.putExtra("notificationFromCityIsActive", "1");
		
		PendingIntent mContentIntent = PendingIntent.getActivity(mContext, 0,
	            mNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		
		mMessageText = Utilities.getNotificationMessageText(mMessageObject);
		
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				mContext).setSmallIcon(R.drawable.ic_notification_icon)
//				.setContentTitle(mFromGroup + " : " + mFromUser)
				.setContentTitle(mGroupName)
				.setContentText(mMessageText);
		mBuilder.setAutoCancel(true);
		mBuilder.setContentInfo(mTotalMessages + " messages");
		mBuilder.setContentIntent(mContentIntent);
		mBuilder.setGroup("messages");
        mBuilder.setGroupSummary(true);
		
		NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle(mBuilder)
		.setBigContentTitle(mGroupName)
		.setSummaryText(mTotalMessages+" Messages " + mConversation);
		
		/*try{
			for (int i = 0; i < mMessageList.size(); i++) {
				inboxStyle.addLine(mMessageList.get(i).toString());
			}
		}catch(Exception e){
			Log.i(TAG, e.toString());
		}*/
		
		try{
			for (int i = 0; i < mMessageList.size(); i++) {
				inboxStyle.addLine(mMessageList.get(i).toString());
				/*try{
					if(mMessageList.get(i - 1).getmMessageGroupId().compareToIgnoreCase(mMessageList.get(i).getmMessageGroupId()) != 0){
						differentConversation++;
						isFromDifferentConversation = true;
					}
				}catch(Exception e){
					
				}*/
			}
		}catch(Exception e){
			Log.i(TAG, e.toString());
		}
		
		mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI, AudioManager.STREAM_NOTIFICATION);
		mBuilder.setStyle(inboxStyle);
		
		NotificationManager mNotificationManager = (NotificationManager) ApplicationLoader
				.getApplication()
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(AppConstants.NOTIFICATION_ID);
		mNotificationManager.notify(AppConstants.NOTIFICATION_ID, mBuilder.build());
	}
	
	

	private void generateLoginNotification(Context mContext) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				mContext)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(
						mContext.getResources().getString(R.string.app_name))
				.setContentText(
						mContext.getResources().getString(
								R.string.notification_login));
		mBuilder.setDefaults(-1);
		mBuilder.setOnlyAlertOnce(true);
		mBuilder.setAutoCancel(true);
		NotificationManager mNotificationManager = (NotificationManager) ApplicationLoader
				.getApplication()
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(AppConstants.NOTIFICATION_ID);
		mNotificationManager.notify(AppConstants.NOTIFICATION_ID, mBuilder.build());
	}
	
	public void generateBadgeNotification(){
		try{
			Cursor mCursor = ApplicationLoader.getApplication().getContentResolver().query(DBConstant.Chat_Columns.CONTENT_URI, null, DBConstant.Chat_Columns.COLUMN_ISREAD +"=?", new String[]{"0"}, null);
			if(mCursor!=null && mCursor.getCount() > 0){
				int mCount = mCursor.getCount();
				BadgeUtils.setBadge(ApplicationLoader.getApplication().getApplicationContext(), mCount);	
			}else{
				BadgeUtils.clearBadge(ApplicationLoader.getApplication().getApplicationContext());
			}
		}catch(Exception e){
			Log.i(TAG, e.toString());
			clearBadgeNotification();
		}
	}
	
	public void clearBadgeNotification(){
		try{
			BadgeUtils.clearBadge(ApplicationLoader.getApplication().getApplicationContext());
		}catch(Exception e){
			Log.i(TAG, e.toString());
		}
	}
	
	public int getMessageType(String message){
		if ((message.contains("http://") || message.contains("https://"))
				&& !message.contains(".mp3")) {
			return 1;
		} else if ((message.contains("http://") || message.contains("https://"))
				&& message.contains(".mp3")) {
			return 2;
		}
		return 0;
	}
}
