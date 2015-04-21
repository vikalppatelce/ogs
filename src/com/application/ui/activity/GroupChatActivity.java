package com.application.ui.activity;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.application.beans.Banner;
import com.application.beans.Member;
import com.application.beans.MessageObject;
import com.application.messenger.ConnectionsManager;
import com.application.ui.view.ChatAdapter;
import com.application.ui.view.CircleImageView;
import com.application.ui.view.Crouton;
import com.application.ui.view.DrawerArrowDrawable;
import com.application.ui.view.LayoutListView;
import com.application.ui.view.Style;
import com.application.utils.AppConstants;
import com.application.utils.ApplicationLoader;
import com.application.utils.BuildVars;
import com.application.utils.DBConstant;
import com.application.utils.ImageCompression;
import com.application.utils.RequestBuilder;
import com.application.utils.RestClient;
import com.application.utils.Utilities;
import com.chat.ttogs.R;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class GroupChatActivity extends ActionBarActivity {

	private static final String TAG = GroupChatActivity.class.getSimpleName();
	private static final int INTENT_PHOTO = 1;
	private static final int INTENT_AUDIO = 2;
	private static final int INTENT_VOICE = 3;

	/*
	 * Drawer Layout Variables
	 */
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] mDrawerTitles;
	private String[] mDrawerDetailTitles;

	DrawerArrayAdapter mDrawerAdapter;

	private DrawerArrowDrawable drawerArrowDrawable;
	private float offset;
	private boolean flipped;
	private Resources mResources;

	// private ImageView mImageViewActionBar;
	// private TextView mTextViewActionBar;

	/*
	 * Activity Variables
	 */
	private RelativeLayout mActionBarLayout;
	private ImageView mActionBarDrawer;
	private ImageView mActionBarBack;
	private TextView mActionBarTitle;

	private TextView mPopUpMenuAttachPhoto;
	private TextView mPopUpMenuAttachAudio;
	private TextView mPopUpMenuAttachVoice;
	
	
	private RelativeLayout mChatEnteredLayout;
	private EditText mChatEditText;
	private ImageView mChatSendIv;
	
	private ImageView chatAdsImageView;
	
	private Button mChatListViewLoadMore;

	private LayoutListView mChatListView;
	private ChatAdapter mChatAdapter;
	private ArrayList<MessageObject> mListMessageObject;
	private ArrayList<MessageObject> mListMessageObjectCoreSupply;

	private Cursor mCursor;
	
	private Intent mIntent;
	
	private ImageLoader mImageLoader;
	private DisplayImageOptions mDisplayImageOptions;

//	private static final String mIntentGroupId = "testroom1@" + "conference."
//			+ AppConstants.XMPP.TALE;
	
	private String mIntentGroupId; /*= "testroom1@" + "conference."
			+ AppConstants.XMPP.TALE;*/
	private boolean mIntentFromConnectionsManager = false;
	private String mIntentGroupJabberId;
	private String mIntentGroupName;
	private String mIntentCityId;
	private String mIntentCityIsActive;
	
	private long messageTime;
	
	private AdView mAdView;
	
	private int mCounterLoadMore = 1;
	private boolean isToResumeAfterDialog = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setWindowFullScreen();
		setContentView(R.layout.activity_mother_group);
		initUi();
		initCustomActionBar();
		setDrawerLayout();
		setUiListener();
		getIntentData();
		setUniversalImageLoader();
		loadAds();
		setChatListView();
		Utilities.checkIfUserExpires(GroupChatActivity.this);
	}
	
	private void setWindowFullScreen() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		setChatListView();
		registerReceiver(mBroadCastReceiver, new IntentFilter(
				ConnectionsManager.BROADCAST_ACTION));
		registerReceiver(mBroadCastReceiverDebuggable, new IntentFilter(
				ConnectionsManager.BROADCAST_ACTION_DEBUG));
		ConnectionsManager.getInstance().generateBadgeNotification();
		if(!ConnectionsManager.getInstance().isXMPPConnected()){
			ConnectionsManager.getInstance().initPushConnection();
			mChatSendIv.setImageDrawable(getResources().getDrawable(R.drawable.chat_send_inactive));
		}else{
			mChatSendIv.setImageDrawable(getResources().getDrawable(R.drawable.chat_send));
		}
		if (mAdView != null) {
            mAdView.resume();
        }
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unregisterReceiver(mBroadCastReceiver);
		unregisterReceiver(mBroadCastReceiverDebuggable);
		if (mAdView != null) {
            mAdView.pause();
        }
	}
	
	/** Called before the activity is destroyed */
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }
    
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		if(mIntentFromConnectionsManager){
			Intent mIntent = new Intent(GroupChatActivity.this, GroupSelectActivity.class);
			startActivity(mIntent);
			finish();
		}else{
			finish();
		}
	}

	private void initUi() {
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		mChatEditText = (EditText) findViewById(R.id.chatEditText);
		mChatSendIv = (ImageView) findViewById(R.id.chatSend);

		mChatListView = (LayoutListView) findViewById(R.id.chat_list_view);
		
		mChatEnteredLayout = (RelativeLayout)findViewById(R.id.chat_compose_panel);
		
		chatAdsImageView = (ImageView)findViewById(R.id.chatAdsImageView);
		
		mAdView = (AdView)findViewById(R.id.adView);
	}

	private void initCustomActionBar() {
		
		mActionBarLayout = (RelativeLayout)findViewById(R.id.actionBarLayout);
		
		mActionBarTitle = (TextView) findViewById(R.id.actionBarTitle);
		mActionBarBack = (ImageView) findViewById(R.id.actionBarBackArrowOnRight);
		mActionBarDrawer = (ImageView) findViewById(R.id.actionBarDrawer);

		mActionBarDrawer.setImageResource(android.R.color.transparent);
		mActionBarTitle.setText("TTLOGS CITY NAME");
		mActionBarBack.setVisibility(View.VISIBLE);

		hideActionBar();
		setActionBarListener();
	}

	private void hideActionBar(){
		try{
			if(Build.VERSION.SDK_INT < 11){
				getSupportActionBar().hide();
			}
		}catch(Exception e){
			Log.i(TAG, e.toString());
		}
	}
	
	private void getIntentData(){
		try{
			mIntent = getIntent();
			mIntentFromConnectionsManager = mIntent.getBooleanExtra("notificationFromConnectionsManager", false);
			mIntentGroupJabberId = mIntent.getStringExtra("notificationFromGroupJabberId");
			mIntentGroupId = mIntent.getStringExtra("notificationFromGroupId");
			mIntentGroupName = mIntent.getStringExtra("notificationFromGroupName");
			mIntentCityId = mIntent.getStringExtra("notificationFromCityId");
			mIntentCityIsActive = mIntent.getStringExtra("notificationFromCityIsActive");
			mActionBarTitle.setText(mIntentGroupName);
			
			if(mIntentCityIsActive.compareToIgnoreCase("0") == 0){
				mChatSendIv.setVisibility(View.GONE);
				mChatEditText.setEnabled(false);
				mChatEditText.setText(getResources().getString(R.string.no_user_subscribed_to_group));
				mActionBarBack.setEnabled(false);
				
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)mChatEditText.getLayoutParams();
				params.setMargins(10, 0, 0, 0); //substitute parameters for left, top, right, bottom
				mChatEditText.setLayoutParams(params);
				
			}
			
			if(!mIntentFromConnectionsManager){
				Utilities.cancelNotification(GroupChatActivity.this);
			}
		}catch(Exception e){
			Log.i(TAG, e.toString());
		}
	}
	
	private void setUiListener() {
		mChatSendIv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(Utilities.isInternetConnected()){
					if(ConnectionsManager.getInstance().isXMPPConnected()){
						if (!TextUtils.isEmpty(mChatEditText.getText().toString())) {
							Utilities.searchQueue.postRunnable(new Runnable() {
								@Override
								public void run() {
									NTPUDPClient timeClient = new NTPUDPClient();
								    InetAddress inetAddress;
									try {
												inetAddress = InetAddress.getByName(AppConstants.API.TIME_SERVER);
												TimeInfo timeInfo = timeClient.getTime(inetAddress);
												ConnectionsManager.getInstance().pushMessage(
														mIntentGroupJabberId,
														mChatEditText.getText().toString().trim(), 0,
														String.valueOf(timeInfo.getMessage().getTransmitTimeStamp().getTime()), "",
														mIntentCityId,
														mIntentGroupId,
														ApplicationLoader.getPreferences().getUserId(),
														ApplicationLoader.getPreferences().getJabberId());	
											    runOnUiThread(new Runnable() {
													@Override
													public void run() {
														// TODO Auto-generated method stub
														mChatEditText.setText("");
													}
												});
												mChatListView.post(new Runnable() {
								                    @Override
								                    public void run() {
								                    	try{
								                    		mChatListView.setSelectionFromTop(mListMessageObject.size() - 1, -100000 - mChatListView.getPaddingTop());	
								                    	}catch(Exception e){
								                    		Log.i(TAG, e.toString());
								                    	}
								                    }
								                });
									} catch (UnknownHostException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									// TODO Auto-generated method stub
								}
							});
						/*ConnectionsManager.getInstance().pushMessage(
								mIntentGroupJabberId,
								mChatEditText.getText().toString().trim(), 0,
								String.valueOf(System.currentTimeMillis()), "",
								mIntentCityId,
								mIntentGroupId,
								ApplicationLoader.getPreferences().getUserId(),
								ApplicationLoader.getPreferences().getJabberId());
						mChatEditText.setText("");
						mChatListView.post(new Runnable() {
		                    @Override
		                    public void run() {
		                    	try{
		                    		mChatListView.setSelectionFromTop(mListMessageObject.size() - 1, -100000 - mChatListView.getPaddingTop());	
		                    	}catch(Exception e){
		                    		Log.i(TAG, e.toString());
		                    	}
		                    }
		                });*/
					} else {
						Crouton.cancelAllCroutons();
						Crouton.makeText(GroupChatActivity.this,
								"Please Enter Message!", Style.ALERT).show();
						}
					}else{
						Crouton.cancelAllCroutons();
						Crouton.makeText(GroupChatActivity.this,
								"Connection Lost", Style.ALERT).show();
						ConnectionsManager.getInstance().initPushConnection();
					}
				}else{
					Crouton.cancelAllCroutons();
					Crouton.makeText(GroupChatActivity.this, getResources().getString(R.string.no_internet_connection), Style.ALERT).show();
				}
			}
		});
	}
	
	private void setActionBarListener(){
		mActionBarBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(Utilities.isInternetConnected()){
					showAttachDialog();
				}else{
					Crouton.cancelAllCroutons();
					Crouton.makeText(GroupChatActivity.this, getResources().getString(R.string.no_internet_connection), Style.ALERT).show();
				}
			}
		});
		
		mActionBarTitle.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showGroupDetails();
			}
		});
	}

	private void setChatListView() {
		if (!setChatAdapterMessageObjects()) {
			mChatAdapter = new ChatAdapter(GroupChatActivity.this,
					mListMessageObject, mImageLoader);
			if(mListMessageObject.size() > 19){
				getHeaderView();
				setUiHeaderViewListener();
//				for(int i = 0; i < mListMessageObjectCoreSupply.size()/20;i++){
					mChatListView.addHeaderView(mChatListViewLoadMore);	
//				}
			}
			mChatListView.setAdapter(mChatAdapter);
		}
	}
	
	private void getHeaderView(){
		LayoutInflater li = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = li.inflate(R.layout.item_chat_load_more, null, false);
		
		mChatListViewLoadMore = (Button)v.findViewById(R.id.chatLoadMore);
	}
	
	private void setUiHeaderViewListener(){
		mChatListViewLoadMore.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				Crouton.makeText(GroupChatActivity.this, "Load More!", Style.INFO).show();
				loadMoreChatListViewData();
			}
		});
	}

	private boolean setChatAdapterMessageObjects() {
		mListMessageObject = new ArrayList<MessageObject>();
		mListMessageObjectCoreSupply = new ArrayList<MessageObject>();
		addMessageObjectsToList();
		addMessageObjectsToListInChunks();
		return mListMessageObject.isEmpty();
	}
	
	private void loadMoreChatListViewData(){
		mCounterLoadMore++;
		addMessageObjectsToListAll();
		mChatListView.removeHeaderView(mChatListViewLoadMore);
		mChatAdapter.syncMessageObjectList(mListMessageObject);
		
		mChatListView.post(new Runnable() {
            @Override
            public void run() {
//                mChatListView.setSelectionFromTop(mListMessageObject.size() - 20, -100000 - mChatListView.getPaddingTop());
                try{
                	mChatListView.setSelection(mListMessageObject.size() - 22);
                }catch(Exception e){
                	Log.i(TAG, e.toString());
                }
            }
        });
	}
	
	private void addMessageObjectsToListInChunks(){
		if(mListMessageObjectCoreSupply.size() > 20){
			for (int i = mListMessageObjectCoreSupply.size()-20; i < mListMessageObjectCoreSupply.size(); i++) {
				mListMessageObject.add(mListMessageObjectCoreSupply.get(i));
			}
		}else {
			mListMessageObject = new ArrayList<MessageObject>(mListMessageObjectCoreSupply);
		}
	}
	
	private void addMessageObjectsToListAll(){
		mListMessageObject = new ArrayList<MessageObject>(mListMessageObjectCoreSupply);
	}

	private void addMessageObjectsToList() {
		updateMessagesAreRead();
		mCursor = getChatCursor();
		if (mCursor != null && mCursor.getCount() > 0) {
			mCursor.moveToFirst();
			int intColumnMessageId = mCursor
					.getColumnIndex(DBConstant.Chat_Columns.COLUMN_MESSAGE_ID);
			int intColumnGroupId = mCursor
					.getColumnIndex(DBConstant.Chat_Columns.COLUMN_GROUP_ID);
			int intColumnUserId = mCursor
					.getColumnIndex(DBConstant.Chat_Columns.COLUMN_USER_ID);
			int intColumnMessage = mCursor
					.getColumnIndex(DBConstant.Chat_Columns.COLUMN_MESSAGE);
			int intColumnMessageTimeStamp = mCursor
					.getColumnIndex(DBConstant.Chat_Columns.COLUMN_TIMESTAMP);
			int intColumnMessageTime = mCursor
					.getColumnIndex(DBConstant.Chat_Columns.COLUMN_MESSAGE_TIME);
			int intColumnMessageDate = mCursor
					.getColumnIndex(DBConstant.Chat_Columns.COLUMN_MESSAGE_DATE);
			int intColumnMessageIsUserLeft = mCursor.getColumnIndex(DBConstant.Chat_Columns.COLUMN_USER_SENT_MESSAGE);
			int intColumnMessageCityId = mCursor.getColumnIndex(DBConstant.Chat_Columns.COLUMN_CITY_ID);
			int intColumnMessageUserIdMySQl = mCursor.getColumnIndex(DBConstant.Chat_Columns.COLUMN_USER_ID_MYSQL);
			
			int intMessageType = mCursor.getColumnIndex(DBConstant.Chat_Columns.COLUMN_TYPE);
			int intFilePath = mCursor.getColumnIndex(DBConstant.Chat_Columns.COLUMN_PATH);
			int intFileLink = mCursor.getColumnIndex(DBConstant.Chat_Columns.COLUMN_FILE_LINK);
			do{
				MessageObject messageObject = new MessageObject();
				messageObject.setMessageId(mCursor
						.getString(intColumnMessageId));
				messageObject.setMessageText(mCursor
						.getString(intColumnMessage));
				messageObject.setUserId(mCursor.getString(intColumnUserId));
				messageObject.setGroupId(mCursor.getString(intColumnGroupId));
				messageObject.setMessageTimeStamp(mCursor
						.getString(intColumnMessageTimeStamp));
				messageObject.setMessageType(intMessageType);
				messageObject.setMessageTime(mCursor
						.getString(intColumnMessageTime));
				messageObject.setMessageDate(mCursor
						.getString(intColumnMessageDate));
				messageObject.setMessageUserIdMySQL(mCursor.getString(intColumnMessageUserIdMySQl));
				messageObject.setCityId(mCursor.getString(intColumnMessageCityId));
				messageObject.setFilePath(mCursor.getString(intFilePath));
				messageObject.setMessageType(Integer.parseInt(mCursor.getString(intMessageType)));
				messageObject.setMessageFileLink(mCursor.getString(intFileLink));
				messageObject.setThisUserSentRight(mCursor.getString(
						intColumnMessageIsUserLeft).contains("0") ? true
						: false);
//				mListMessageObject.add(messageObject);
				mListMessageObjectCoreSupply.add(messageObject);
			}while (mCursor.moveToNext());
		}
	}

	private Cursor getChatCursor() {
		return ApplicationLoader
				.getApplication()
				.getContentResolver()
				.query(DBConstant.Chat_Columns.CONTENT_URI, null,
						DBConstant.Chat_Columns.COLUMN_GROUP_ID + "=?",
						new String[] { mIntentGroupJabberId },
						DBConstant.Chat_Columns.COLUMN_TIMESTAMP + " ASC");
	}
	
	private void updateMessagesAreRead(){
		ContentValues values = new ContentValues();
		values.put(DBConstant.Chat_Columns.COLUMN_ISREAD, "1");
		ApplicationLoader
				.getApplication()
				.getContentResolver()
				.update(DBConstant.Chat_Columns.CONTENT_URI, values,
						DBConstant.Chat_Columns.COLUMN_GROUP_ID + "=?",
						new String[] { mIntentGroupJabberId });
	}

	private BroadcastReceiver mBroadCastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			try {
//				updateUICrouton(intent);
				MessageObject messageObject = intent
						.getParcelableExtra("MessageObject");
				if (messageObject.getGroupId().compareToIgnoreCase(mIntentGroupJabberId) == 0) {
					updateUIChatListView(intent);
					updateMessagesAreRead();
					ConnectionsManager.getInstance().generateBadgeNotification();
				}else{
					ConnectionsManager.getInstance()
					.generateNotification(ApplicationLoader.getApplication().getApplicationContext(),
							messageObject.getGroupId(),messageObject.getCityId(), messageObject.getUserId(), messageObject);
				}
			} catch (Exception e) {
				Log.i(TAG, e.toString());
			}
		}
	};
	
	private BroadcastReceiver mBroadCastReceiverDebuggable = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			try {
//				updateUICrouton(intent);
				String mDebuggable = intent
						.getStringExtra("debugConnections");
					if(BuildVars.DEBUGGING_XMPP){
						updateUIDebuggableConnections(mDebuggable);	
					}
					if(mDebuggable.equalsIgnoreCase(BuildVars.DEBUG_CONNECTED)){
						mChatSendIv.setImageDrawable(getResources().getDrawable(R.drawable.chat_send));
					}else{
						mChatSendIv.setImageDrawable(getResources().getDrawable(R.drawable.chat_send_inactive));
					}
			} catch (Exception e) {
				Log.i(TAG, e.toString());
			}
		}
	};

	private void updateUIDebuggableConnections(String mDebuggable){
		Crouton.cancelAllCroutons();
		Crouton.makeText(GroupChatActivity.this, mDebuggable, Style.CONFIRM).show();
	}
	
	private void updateUICrouton(Intent mIntent) {
		Crouton.makeText(GroupChatActivity.this,
				mIntent.getStringExtra("MessageObjectString"), Style.INFO)
				.show();
	}

	private void updateUIChatListView(Intent mIntent) {
		if (mChatAdapter == null) {
			mListMessageObject = new ArrayList<MessageObject>();
			MessageObject messageObject = mIntent
					.getParcelableExtra("MessageObject");
			mListMessageObject.add(messageObject);
			mChatAdapter = new ChatAdapter(GroupChatActivity.this,
					mListMessageObject, mImageLoader);
			mChatListView.setAdapter(mChatAdapter);
		} else {
			MessageObject messageObject = mIntent
					.getParcelableExtra("MessageObject");
			mChatAdapter.addMessageObject(messageObject);
		}
		mChatListView.setSelectionFromTop(mListMessageObject.size() - 1,
				-100000 - mChatListView.getPaddingTop());
	}
	
	private void setUniversalImageLoader() {
		mImageLoader = ImageLoader.getInstance();
		mImageLoader.init(ImageLoaderConfiguration.createDefault(this));
		mDisplayImageOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_launcher)
				.showImageForEmptyUri(R.drawable.ic_launcher)
				.showImageOnFail(R.drawable.ic_launcher).cacheInMemory()
				.cacheOnDisc().build();
	}
	
	
	private void showAttachDialog() {
		final Dialog dialog = new Dialog(GroupChatActivity.this);
		try {
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.getWindow().setBackgroundDrawable(
					new ColorDrawable(android.graphics.Color.TRANSPARENT));
			WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
			lp.dimAmount = 0.0f;
			lp.gravity = Gravity.TOP | Gravity.RIGHT;
			lp.y = mActionBarLayout.getHeight();
			dialog.getWindow().setAttributes(lp);
			dialog.getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			// dialog.getWindow().addFlags(
			// WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		} catch (Exception e) {
			Log.e("inputDialog", e.toString());
		}
		dialog.setCanceledOnTouchOutside(true);
		dialog.setContentView(R.layout.dialog_attach_chat);

		mPopUpMenuAttachPhoto = (TextView) dialog
				.findViewById(R.id.dialogAttachPhoto);
		mPopUpMenuAttachAudio = (TextView) dialog
				.findViewById(R.id.dialogAttachAudio);
		mPopUpMenuAttachVoice = (TextView) dialog
				.findViewById(R.id.dialogAttachVoice);

		mPopUpMenuAttachPhoto.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(Utilities.isInternetConnected()){
					startPhotoSelectActivity();
				}else{
					Crouton.cancelAllCroutons();
					Crouton.makeText(GroupChatActivity.this, getResources().getString(R.string.no_internet_connection), Style.ALERT).show();
				}
				dialog.dismiss();
			}
		});
		
		mPopUpMenuAttachAudio.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(Utilities.isInternetConnected()){
					startAudioSelectActivity();	
				}else{
					Crouton.cancelAllCroutons();
					Crouton.makeText(GroupChatActivity.this, getResources().getString(R.string.no_internet_connection), Style.ALERT).show();
				}
				dialog.dismiss();
			}
		});

		mPopUpMenuAttachVoice.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(Utilities.isInternetConnected()){
					showRecorderDialog();	
				}else{
						Crouton.cancelAllCroutons();
						Crouton.makeText(GroupChatActivity.this, getResources().getString(R.string.no_internet_connection), Style.ALERT).show();
				}
				
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	public void startAudioSelectActivity(){
		try{
			Intent audioPickerIntent = new Intent();
			audioPickerIntent.setType("audio/*");
			audioPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(audioPickerIntent,INTENT_AUDIO);
		}catch(Exception e){
			Log.i(TAG, e.toString());
		}
	}
	
	public void startPhotoSelectActivity() {
        try {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, INTENT_PHOTO);
        } catch (Exception e) {
            Log.i(TAG, e.toString());
        }
    }
	
	/*
	 * 
	 */

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent mIntent) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, mIntent);
		if(resultCode == Activity.RESULT_OK){
			if(requestCode == INTENT_PHOTO){
				Uri selectedImage = mIntent.getData();
				final String mPicturePath = Utilities.getPath(selectedImage);
				Log.i(TAG, "mPicturePath : "+mPicturePath);
//				Uri mUri = Utilities.getImagePath();
				Utilities.searchQueue.postRunnable(new Runnable() {
					@Override
					public void run() {
						NTPUDPClient timeClient = new NTPUDPClient();
					    InetAddress inetAddress;
						try {
								inetAddress = InetAddress.getByName(AppConstants.API.TIME_SERVER);
								TimeInfo timeInfo = timeClient.getTime(inetAddress);
								messageTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
//								messageTime = System.currentTimeMillis();
								Uri mUri = Uri.fromFile(new File(Utilities.getFilePath(1, Utilities.getJabberGroupIdWithoutTale(mIntentGroupJabberId), String.valueOf(messageTime))));
								try {
									Utilities.copy(new File(mPicturePath),
											new File(mUri.getPath()));
									Utilities.galleryAddPic(GroupChatActivity.this, mUri);
									String mCompressPath = ImageCompression
											.compressImage(mUri.getPath());
									uploadFileTask(1, mCompressPath);
								} catch (IOException e) {
									Log.e(TAG, e.toString());
								}
						}catch(Exception e){
						}
					}
				});
			}
			
			if(requestCode == INTENT_AUDIO){
				final Uri selectedAudio = mIntent.getData();
				
				Utilities.searchQueue.postRunnable(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						NTPUDPClient timeClient = new NTPUDPClient();
					    InetAddress inetAddress;
						try{
							inetAddress = InetAddress.getByName(AppConstants.API.TIME_SERVER);
							TimeInfo timeInfo = timeClient.getTime(inetAddress);
							messageTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
//							messageTime = System.currentTimeMillis();
							Uri mUri = Uri.fromFile(
									new File(
											Utilities.getFilePath(2,Utilities
															.getJabberGroupIdWithoutTale(mIntentGroupJabberId),
													String.valueOf(messageTime))));
							try {
								Utilities.copy(new File(Utilities.getPath(selectedAudio)),
										new File(mUri.getPath()));
								uploadFileTask(2, mUri.getPath());
							} catch (IOException e) {
								Log.e(TAG, e.toString());
							}
							Log.i(TAG, "mAudioPath : "+Utilities.getPath(mUri));						
						}catch(Exception e){
							
						}
					}
				});
			}
			
			if(requestCode == INTENT_VOICE){
				uploadFileTask(2, mIntent.getStringExtra("mPath"));
			}
		}
	}
	
	private void uploadFileTask(int fileType, String filePath){
		JSONObject mJSONObject = buildJSONRequestForFileUpload(fileType, filePath, Utilities.getFileExtension(filePath));
		new AsyncTaskUploadFile(GroupChatActivity.this, mJSONObject, fileType).execute();
	}
	
	private JSONObject buildJSONRequestForFileUpload(int fileType, String filePath, String fileExtension){
		return RequestBuilder.getPostUploadFile(fileType, filePath, fileExtension);
	}
	
	public class AsyncTaskUploadFile extends AsyncTask<Void, Void, Void>{

		private boolean isSuccess = false;
		private JSONObject mJSONObject;
		private ProgressDialog mProgressDialog;
		private Context mContext;
		private String mResponseFromApi;
		private int fileType;
		
		public AsyncTaskUploadFile(Context mContext,JSONObject mJSONObject, int fileType){
			this.mContext = mContext;
			this.mJSONObject = mJSONObject;
			this.fileType = fileType;
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setMessage("Uploading...");
			mProgressDialog.setCancelable(false);
			mProgressDialog.setCanceledOnTouchOutside(false);
			mProgressDialog.show();
		}
		
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				mResponseFromApi = RestClient.postJSON(AppConstants.API.URL, mJSONObject);
				JSONObject mJSONObj = new JSONObject(mResponseFromApi);
				isSuccess = mJSONObj.getBoolean("success");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch(Exception e){
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(mProgressDialog!=null){
				mProgressDialog.dismiss();
			}
			
			if(isSuccess){
				parseJSONUploaded(mResponseFromApi,fileType);
			}else{
				Crouton.makeText(GroupChatActivity.this, "Upload Failed! Please try again!", Style.ALERT).show();
			}
		}
	}
	
	private void parseJSONUploaded(String mResponseFromApi,int fileType){
		JSONObject mJSONObject;
		JSONObject mJSONObjectdata;
		try{
			mJSONObject= new JSONObject(mResponseFromApi);
			mJSONObjectdata = mJSONObject.getJSONObject("data");
			String messageFileLink = mJSONObjectdata.getString("file_path");
			pushMessage(messageTime, fileType, messageFileLink);
		}catch(Exception e){
			Log.i(TAG, e.toString());
		}
	}
	
	private void pushMessage(long messageTime, int messageType , String messageFileLink){
		ConnectionsManager.getInstance().pushMessage(
				mIntentGroupJabberId,
				"", messageType,
				String.valueOf(messageTime), messageFileLink,
				mIntentCityId,
				mIntentGroupId,
				ApplicationLoader.getPreferences().getUserId(),
				ApplicationLoader.getPreferences().getJabberId());
		
		mChatListView.post(new Runnable() {
            @Override
            public void run() {
            	try{
            		mChatListView.setSelectionFromTop(mListMessageObject.size() - 1, -100000 - mChatListView.getPaddingTop());	
            	}catch(Exception e){
            		Log.i(TAG, e.toString());
            	}
            }
        });
	}

	
	private void loadAds() {
		if(!BuildVars.DEBUG_VERSION){
			try{
				final ArrayList<Banner> mArrayListBanner= retrieveArrayListFromPreferencesUsingGSON();
				if (mArrayListBanner != null && mArrayListBanner.size() > 0) {
					/*if (System.currentTimeMillis() > Utilities
							.getMilliSecondsFromData(Integer.parseInt(mArrayListBanner.get(2).getBannerStartDate().substring(0, 4)),
									Integer.parseInt(mArrayListBanner.get(2).getBannerStartDate().substring(5, 7)),
									Integer.parseInt( mArrayListBanner.get(2).getBannerStartDate().substring(8, 10)))
							&&
							System.currentTimeMillis() < Utilities
							.getMilliSecondsFromData(Integer.parseInt(mArrayListBanner.get(2).getBannerEndDate().substring(0, 4)),
									Integer.parseInt(mArrayListBanner.get(2).getBannerEndDate().substring(5, 7)),
									Integer.parseInt( mArrayListBanner.get(2).getBannerEndDate().substring(8, 10))))*/
//						if(Utilities.isToShowBannerAds(Utilities.getSystemDateYYYYMMDD(), mArrayListBanner.get(2).getBannerStartDate(), mArrayListBanner.get(2).getBannerEndDate()))
					if (mArrayListBanner.get(2).isBannerIsAdsOn())
						{
//						mImageLoader.displayImage(mArrayListBanner.get(1).getBannerImage(), chatAdsImageView);
						if(new File(AppConstants.ADS_DIRECTORY_PATH+Utilities.getFileNameFromPath(mArrayListBanner.get(2).getBannerImage())).exists()){
							chatAdsImageView.setImageBitmap(BitmapFactory.decodeFile(AppConstants.ADS_DIRECTORY_PATH+Utilities.getFileNameFromPath(mArrayListBanner.get(2).getBannerImage())));
						}else{
							mImageLoader.displayImage(mArrayListBanner.get(2).getBannerImage(), chatAdsImageView, new ImageLoadingListener() {
								
								@Override
								public void onLoadingStarted(String arg0, View arg1) {
									// TODO Auto-generated method stub
								}
								
								@Override
								public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
									// TODO Auto-generated method stub
								}
								
								@Override
								public void onLoadingComplete(String arg0, View arg1, Bitmap mBitmap) {
									// TODO Auto-generated method stub
									Utilities.writeBitmapToSDCardAdsImages(mBitmap, Utilities.getFileNameFromPath(mArrayListBanner.get(2).getBannerImage()));
								}
								
								@Override
								public void onLoadingCancelled(String arg0, View arg1) {
									// TODO Auto-generated method stub
								}
							});
					}
					}else{
						chatAdsImageView.setVisibility(View.GONE);
						mAdView.setVisibility(View.VISIBLE);
						 AdRequest adRequest = new AdRequest.Builder()
		                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
		                    .build();
						 //Start loading the ad in the background.
						 mAdView.loadAd(adRequest);
					}
				}else{
					chatAdsImageView.setVisibility(View.GONE);
					mAdView.setVisibility(View.VISIBLE);
					 AdRequest adRequest = new AdRequest.Builder()
	                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
	                    .build();
					 //Start loading the ad in the background.
					 mAdView.loadAd(adRequest);
				}
			}catch(Exception e){
				Log.i(TAG, e.toString());
			}
		}
	}
	
	private ArrayList<Banner> retrieveArrayListFromPreferencesUsingGSON(){
		Gson gson = new Gson();
		String json = ApplicationLoader.getPreferences().getAdsListBannerObject();
		Type type = new TypeToken<ArrayList<Banner>>() {}.getType();
		ArrayList<Banner> mArrayListBanner = gson.fromJson(json, type);
		return mArrayListBanner;
	}
	
	private void showRecorderDialog(){
		Utilities.searchQueue.postRunnable(new Runnable() {
			@Override
			public void run() {
				NTPUDPClient timeClient = new NTPUDPClient();
			    InetAddress inetAddress;
				try {
						inetAddress = InetAddress.getByName(AppConstants.API.TIME_SERVER);
						TimeInfo timeInfo = timeClient.getTime(inetAddress);
						messageTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
						
						final String mPath  = Utilities.getFilePath(2,Utilities
								.getJabberGroupIdWithoutTale(mIntentGroupJabberId),
						String.valueOf(messageTime));
						
						Intent mIntent = new Intent(GroupChatActivity.this, VoiceRecorderActivity.class);
						mIntent.putExtra("mPath", mPath);
						startActivityForResult(mIntent, INTENT_VOICE);
						
				}catch(Exception e){
				}
			}
		});
		/*final Dialog mDialog = new Dialog(GroupChatActivity.this);
		mDialog.setCancelable(true);
		mDialog.setCanceledOnTouchOutside(true);
		mDialog.setTitle("Voice Note");
		mDialog.setContentView(R.layout.dialog_audio_record);
		Window dialogWindow = mDialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CLIP_VERTICAL);
		dialogWindow.setAttributes(lp);

		final TextView mDialogStart = (TextView) mDialog
				.findViewById(R.id.dialogRecordStartStop);
		final Chronometer mDialogAudioTimer = (Chronometer) mDialog
				.findViewById(R.id.dialogAudioRecordTimer);

		mDialog.show();*/
		/*messageTime = System.currentTimeMillis();
		final String mPath  = Utilities.getFilePath(2,Utilities
				.getJabberGroupIdWithoutTale(mIntentGroupJabberId),
		String.valueOf(messageTime));
		
		Intent mIntent = new Intent(GroupChatActivity.this, VoiceRecorderActivity.class);
		mIntent.putExtra("mPath", mPath);
		startActivityForResult(mIntent, INTENT_VOICE);*/
		/*mDialogStart.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				VoiceRecorder mRecorder = new VoiceRecorder(GroupChatActivity.this);
				MediaRecorder mRecorder = null;
				if(mDialogStart.getText().toString().equalsIgnoreCase("start")){
					mDialogStart.setText("Stop");
					mDialogAudioTimer.setBase(SystemClock.elapsedRealtime());
					mDialogAudioTimer.start();
					
					mRecorder = new MediaRecorder();
			        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
			        mRecorder.setOutputFile(mPath);
			        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

			        try {
			            mRecorder.prepare();
			        } catch (IOException e) {
			            Log.e(TAG, "prepare() failed");
			        }
			        mRecorder.start();
    				try {
						mRecorder.startRecording(mPath);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
//					mDialogStart.setText("Start");
//					String mStopTime = mDialogAudioTimer.getText().toString();
					mDialogAudioTimer.stop();
					mRecorder.stop();
					mRecorder.release();
					mRecorder = null;
//					mRecorder.stopRecordingForceFully();
					broadCastNewFileAdded(mPath);
//					mDialogAudioTimer.setText(mStopTime);
					mDialogAudioTimer.refreshDrawableState();
					mDialogAudioTimer.setBase(SystemClock.elapsedRealtime());
					mDialog.cancel();
					uploadFileTask(2, mPath);
				}
			}
		});*/
	}
	
	public void broadCastNewFileAdded(String mPath){
		try{
			Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		    File f = new File(mPath);
		    Uri contentUri = Uri.fromFile(f);
		    mediaScanIntent.setData(contentUri);
		    sendBroadcast(mediaScanIntent);
		}catch(Exception e){
		}
	}
	
	
	private void showGroupDetails(){
		Dialog mDialog = new Dialog(GroupChatActivity.this);
		mDialog.setCancelable(true);
		mDialog.setCanceledOnTouchOutside(true);
		mDialog.setTitle(mActionBarTitle.getText().toString());
		mDialog.setContentView(R.layout.dialog_group_members);
		Window dialogWindow = mDialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CLIP_VERTICAL);
		dialogWindow.setAttributes(lp);

		ListView mDialogListView = (ListView)mDialog.findViewById(R.id.membersListView);
		ProgressBar mDialogProgress = (ProgressBar) mDialog
				.findViewById(R.id.MemberInfoContainerProgress);
		LinearLayout mDialogMemberInfoLayout = (LinearLayout)mDialog.findViewById(R.id.MemberInfoContainerLayout);
		
		if(Utilities.isInternetConnected()){
			new AsyncViewGroupDetails(RequestBuilder.getPostViewGroupDetails(mIntentGroupJabberId), mDialogMemberInfoLayout, mDialogProgress,mDialogListView, mDialog).execute();
		}
		mDialog.show();
	}
	
	public class AsyncViewGroupDetails extends AsyncTask<Void, Void, Void>{
		private boolean isSuccess;
		private JSONObject mJSONObjectToSend;
		private String mResponseFromApi;
		private ArrayList<Member> mArrayListMember;
		private ProgressBar mDialogProgressBar;
		private ListView mDialogListView;
		private LinearLayout mDialogContainer;
		private Dialog mDialog;
		
		public AsyncViewGroupDetails(JSONObject mJSONObjectToSend,
				LinearLayout mDialogContainer, ProgressBar mDialogProgressBar,
				ListView mDialogListView, Dialog mDialog) {
			this.mJSONObjectToSend = mJSONObjectToSend;
			this.mDialogContainer = mDialogContainer;
			this.mDialogListView = mDialogListView;
			this.mDialogProgressBar = mDialogProgressBar;
			this.mDialog = mDialog;
		}
		
		
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			mDialogContainer.setVisibility(View.GONE);
			mDialogProgressBar.setVisibility(View.VISIBLE);
		}



		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				mResponseFromApi = RestClient.postJSON(AppConstants.API.URL, mJSONObjectToSend);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch(Exception e){
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(BuildVars.DEBUG_VERSION){
				mResponseFromApi = Utilities.readFile("apiGroupDetails.json");
			}
			
			if(!TextUtils.isEmpty(mResponseFromApi)){
				mArrayListMember = showGroupDetailsDialog(mResponseFromApi);
			}
			
			if(mArrayListMember!=null){
				mDialogContainer.setVisibility(View.VISIBLE);
				mDialogProgressBar.setVisibility(View.GONE);	
				MemberListAdapter mAdapter = new MemberListAdapter(GroupChatActivity.this, mArrayListMember);
				mDialogListView.setAdapter(mAdapter);
				
				mDialogListView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parentView, View view,
							int position, long id){
						// TODO Auto-generated method stub
						String userId = view.getTag(R.id.TAG_USER_ID).toString();
						String userName = view.getTag(R.id.TAG_NAME).toString();
						mDialog.dismiss();
						showUserInfo(userName, userId);
					}
				});
			}
		}
	}
	
	public class MemberListAdapter extends BaseAdapter{

		private ArrayList<Member> mListMember;
		private Context mContext;
		public MemberListAdapter(Context mContext, ArrayList<Member> mListMember){
			this.mListMember = mListMember;
			this.mContext = mContext;
		}
		/* (non-Javadoc)
		 * @see android.widget.Adapter#getCount()
		 */
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mListMember.size();
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getItem(int)
		 */
		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getItemId(int)
		 */
		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.item_list_members, parent,
					false);
			CircleImageView mCircleImageView  = (CircleImageView)rowView.findViewById(R.id.itemMembersImageView);
			TextView mTextView  = (TextView)rowView.findViewById(R.id.itemMembersNameTv);
			
			mTextView.setText(mListMember.get(position).getMemberName());
			mCircleImageView.setVisibility(View.GONE);
//			mImageLoader.displayImage(mListMember.get(position).getMemberImage(), mCircleImageView);
			rowView.setTag(R.id.TAG_USER_ID, mListMember.get(position).getMemberId());
			rowView.setTag(R.id.TAG_NAME, mListMember.get(position).getMemberName());
			return rowView;
		}
	}
	
	private ArrayList<Member> showGroupDetailsDialog(String mResponseFromApi){
		try {
			JSONObject mJSONObject = new JSONObject(mResponseFromApi);
			if(mJSONObject.getBoolean("success")){
				return parseGroupDetails(mResponseFromApi);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return null;
	}
	
	private ArrayList<Member> parseGroupDetails(String mResponseFromApi){
		try {
			JSONObject mJSONObject = new JSONObject(mResponseFromApi);
			JSONObject mJSONObjectData = mJSONObject.getJSONObject("data");
			String mGroupTitle = mIntentGroupName;
			String mGroupId = mJSONObjectData.getString("room_jid");
			JSONArray mJSONArrayMember = mJSONObjectData.getJSONArray("members");
			ArrayList<Member> mArrayListMember = new ArrayList<Member>();
			for(int i =0 ;i<mJSONArrayMember.length();i++){
				JSONObject mJSONObjectEachMember = mJSONArrayMember.getJSONObject(i);
				Member  mObj = new Member();
				mObj.setMemberGroupId(mGroupId);
				mObj.setMemberGroupName(mGroupTitle);
				mObj.setMemberId(mJSONObjectEachMember.getString("user_id"));
				mObj.setMemberName(mJSONObjectEachMember.getString("display_name"));
				mObj.setMemberImage(mJSONObjectEachMember.getString("image"));
				mArrayListMember.add(mObj);
			}
			addMembersToDb(mArrayListMember);
			return mArrayListMember;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	
	private void addMembersToDb(ArrayList<Member> mArrMembers){
		
	}
	
	
	public void showUserInfo(String mName, String mUserId) {
		Dialog mDialog = new Dialog(GroupChatActivity.this);
		mDialog.setCancelable(true);
		mDialog.setCanceledOnTouchOutside(true);
		mDialog.setTitle(mName);
		mDialog.setContentView(R.layout.dialog_user_info);
		Window dialogWindow = mDialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CLIP_VERTICAL);
		dialogWindow.setAttributes(lp);

		CircleImageView mDialogImage = (CircleImageView)mDialog.findViewById(R.id.dialogUserInfoPicure);
		TextView mDialogNameTv = (TextView) mDialog
				.findViewById(R.id.dialogUserInfoName);
		TextView mDialogCityTv = (TextView) mDialog
				.findViewById(R.id.dialogUserInfoCity);
		final TextView mDialogMobileTv = (TextView) mDialog
				.findViewById(R.id.dialogUserInfoMobile);
		TextView mDialogServiceTv = (TextView)mDialog.findViewById(R.id.dialogUserInfoService);
		ProgressBar mDialogProgress = (ProgressBar) mDialog
				.findViewById(R.id.userInfoContainerProgress);
		LinearLayout mDialogUserInfoLayout = (LinearLayout)mDialog.findViewById(R.id.userInfoContainerLayout);
		
		mDialogMobileTv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!TextUtils.isEmpty(mDialogMobileTv.getText().toString())){
					Uri number = Uri.parse("tel:"+mDialogMobileTv.getText().toString());
			        Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
			        startActivity(callIntent);
				}
			}
		});
		
		new AsyncTaskShowUserInfo(mDialogImage, mDialogNameTv, mDialogCityTv,
				mDialogMobileTv,mDialogServiceTv, mDialogProgress,mDialogUserInfoLayout, mUserId).execute();
		mDialog.show();
	}

	public class AsyncTaskShowUserInfo extends AsyncTask<Void, Void, Void> {

		ProgressBar mDialogProgress;
		LinearLayout mDialogUserLayout;
		TextView mDialogMobileTv;
		TextView mDialogCityTv;
		TextView mDialogNameTv;
		TextView mDialogServiceTv;
		CircleImageView mDialogImage;
		String mResponseFromApi;
		String userJabberId;
		boolean isResonseFromApi = false;
		boolean isSuccess = false;

		public AsyncTaskShowUserInfo(CircleImageView mDialogImage,TextView mDialogNameTv,
				TextView mDialogCityTv, TextView mDialogMobileTv, TextView mDialogServiceTv,
				ProgressBar mDialogProgress,LinearLayout mDialogUserLayout, String userJabberId) {
			this.mDialogCityTv = mDialogCityTv;
			this.mDialogMobileTv = mDialogMobileTv;
			this.mDialogNameTv = mDialogNameTv;
			this.mDialogProgress = mDialogProgress;
			this.mDialogServiceTv = mDialogServiceTv;
			this.mDialogImage = mDialogImage;
			this.userJabberId = userJabberId;
			this.mDialogUserLayout = mDialogUserLayout;
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				mResponseFromApi = RestClient.postJSON(AppConstants.API.URL, RequestBuilder.getViewUserProfile(userJabberId));
				isResonseFromApi = true;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				isResonseFromApi = false;
			}catch(Exception e){
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(isResonseFromApi){
				mDialogProgress.setVisibility(View.GONE);
				if(BuildVars.DEBUG_VERSION){
					mResponseFromApi = Utilities.readFile("apiUserProfile.json");
				}
				try {
					JSONObject mJSONObject = new JSONObject(mResponseFromApi);
					isSuccess = mJSONObject.getBoolean("success");
					if(isSuccess){
						JSONObject mJSONObjectData = mJSONObject.getJSONObject("data");
						JSONObject mJSONObjectDataInfo = mJSONObjectData.getJSONObject("view_user_info");
						mDialogProgress.setVisibility(View.GONE);
						mDialogUserLayout.setVisibility(View.VISIBLE);
						mDialogMobileTv.setText(mJSONObjectDataInfo.getString("mobile_no"));
						mDialogNameTv.setText(mJSONObjectDataInfo.getString("display_name"));
						mDialogCityTv.setText(mJSONObjectDataInfo.getString("city"));	
						mDialogServiceTv.setText(mJSONObjectDataInfo.getString("service"));
						mImageLoader.displayImage(mJSONObjectDataInfo.getString("user_image_path"), mDialogImage);
						try{
							SpannableString content = new SpannableString(mDialogMobileTv.getText().toString());
							content.setSpan(new UnderlineSpan(), 0, mDialogMobileTv.getText().toString().length(), 0);
							mDialogMobileTv.setText(content);
						}catch(Exception e){
							Log.i(TAG, e.toString());
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			mDialogProgress.setVisibility(View.VISIBLE);
			mDialogUserLayout.setVisibility(View.GONE);
		}
	}
	
	private void addGroupTitleInDrawer(){
		try{
			String mGroupNames[] = Utilities.addGroupInDrawerFromDB();
			if(mGroupNames !=null && mGroupNames.length > 0){
				mDrawerTitles =null;
				mDrawerDetailTitles =null;
				mDrawerTitles = new String[4+mGroupNames.length];
				mDrawerDetailTitles = new String[4+mGroupNames.length];
				mDrawerTitles[0] = "Group Chat";
				mDrawerTitles[1] = "Edit Profile";
				mDrawerTitles[2] = "About Us";
				mDrawerDetailTitles = mDrawerTitles;
				
				int j = 0;
				for(int i = 3 ; i < (mGroupNames.length + mDrawerTitles.length +1); i++){
					mDrawerTitles[i] = mGroupNames[j];
					mDrawerDetailTitles[i]  = mGroupNames[j];
					j++;
				}
			}
		}catch(Exception e){
		}
	}
	
	/*
	 * Drawer Implementation
	 */
	private void setDrawerLayout() {
		mResources = getResources();
		drawerArrowDrawable = new DrawerArrowDrawable(mResources);
		drawerArrowDrawable.setStrokeColor(mResources
				.getColor(android.R.color.white));
		// mImageViewActionBar.setImageDrawable(drawerArrowDrawable);
		mActionBarDrawer.setImageDrawable(drawerArrowDrawable);

		mTitle = mDrawerTitle = getTitle();
		mDrawerTitles = getResources()
				.getStringArray(R.array.drawer_menu_array);
		mDrawerDetailTitles = getResources().getStringArray(
				R.array.drawer_menu_detail_array);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		
		/*
		 * Add Group Names in Drawer
		 */
//		addGroupTitleInDrawer();
		
		mDrawerAdapter = new DrawerArrayAdapter(this, mDrawerTitles,
				mDrawerDetailTitles);
		mDrawerList.setAdapter(mDrawerAdapter);

		// getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		// getSupportActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {
			@Override
			public void onDrawerClosed(View view) {
				getSupportActionBar().setTitle(mTitle);
				supportInvalidateOptionsMenu();
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				getSupportActionBar().setTitle(mDrawerTitle);
				supportInvalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		mActionBarDrawer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mDrawerLayout.isDrawerVisible(Gravity.START)) {
					mDrawerLayout.closeDrawer(Gravity.START);
				} else {
					mDrawerLayout.openDrawer(Gravity.START);
				}
			}
		});

		mDrawerLayout
				.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
					@Override
					public void onDrawerSlide(View drawerView, float slideOffset) {
						offset = slideOffset;

						// Sometimes slideOffset ends up so close to but not
						// quite 1 or 0.
						if (slideOffset >= .995) {
							flipped = true;
							drawerArrowDrawable.setFlip(flipped);
						} else if (slideOffset <= .005) {
							flipped = false;
							drawerArrowDrawable.setFlip(flipped);
						}
						try{
							drawerArrowDrawable.setParameter(offset);	
						}catch(IllegalArgumentException e){
							Log.i(TAG, e.toString());
						}
					}
				});
	}

	/* The click listner for ListView in the navigation drawer */
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {
		Fragment fragment = null;
		Bundle args = new Bundle();
		Intent drawerIntent = null;
		switch (position) {
		case 1:
			drawerIntent = new Intent(GroupChatActivity.this, ProfileEditActivity.class);
			break;
		/*case 2:
			drawerIntent = new Intent(GroupChatActivity.this, SettingsActivity.class);
			break;*/
		case 2:
			drawerIntent = new Intent(GroupChatActivity.this, AboutActivity.class);
			break;
		default:
			break;
		}
		mDrawerLayout.closeDrawer(mDrawerList);
		if (drawerIntent != null)
			startActivity(drawerIntent);
	}

	public class DrawerArrayAdapter extends ArrayAdapter<String> {
		private final Context context;
		private final String[] values;
		private final String[] values2;

		public DrawerArrayAdapter(Context context, String[] values,
				String[] values2) {
			super(context, R.layout.item_list_drawer, values);
			this.context = context;
			this.values = values;
			this.values2 = values2;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.item_list_drawer, parent,
					false);

			FrameLayout mDrawerProfileLayout = (FrameLayout) rowView
					.findViewById(R.id.drawer_layout_profile);
			LinearLayout mDrawerMenuLayout = (LinearLayout) rowView
					.findViewById(R.id.drawer_layout_menu);

			final CircleImageView mDrawerProfileImageView = (CircleImageView) rowView
					.findViewById(R.id.drawer_profile_image);

			TextView mDrawerProfileFullNameView = (TextView) rowView
					.findViewById(R.id.drawer_profile_full_name);
			TextView mDrawerTitleView = (TextView) rowView
					.findViewById(R.id.drawer_layout_menu_title);
			TextView mDrawerTitleSubView = (TextView) rowView
					.findViewById(R.id.drawer_layout_menu_subtitle);
			ImageView mDrawerIconView = (ImageView) rowView
					.findViewById(R.id.drawer_layout_menu_icon);

			switch (position) {
			case 0:
				mDrawerMenuLayout.setVisibility(View.GONE);
				try{
					mDrawerProfileFullNameView.setText(ApplicationLoader.getPreferences().getDisplayName());
//					mImageLoader.displayImage(ApplicationLoader.getPreferences().getProfilePicPath(), mDrawerProfileImageView);
					
					if(new File(AppConstants.PROFILE_DIRECTORY_PATH+Utilities.getFileNameFromPath(ApplicationLoader.getPreferences().getProfilePicPath())).exists()){
						mDrawerProfileImageView.setImageBitmap(BitmapFactory.decodeFile(AppConstants.PROFILE_DIRECTORY_PATH+Utilities.getFileNameFromPath(ApplicationLoader.getPreferences().getProfilePicPath())));
					}else{
						mImageLoader.displayImage(ApplicationLoader.getPreferences().getProfilePicPath(), mDrawerProfileImageView, new ImageLoadingListener() {
							
							@Override
							public void onLoadingStarted(String arg0, View arg1) {
								// TODO Auto-generated method stub
							}
							
							@Override
							public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
								// TODO Auto-generated method stub
							}
							
							@Override
							public void onLoadingComplete(String arg0, View arg1, Bitmap mBitmap) {
								// TODO Auto-generated method stub
								Utilities.writeBitmapToSDCardProfileImages(mBitmap, Utilities.getFileNameFromPath(ApplicationLoader.getPreferences().getProfilePicPath()));
							}
							
							@Override
							public void onLoadingCancelled(String arg0, View arg1) {
								// TODO Auto-generated method stub
							}
						});
					}
				}catch(Exception e){
				}
				break;
			case 1:
				mDrawerProfileLayout.setVisibility(View.GONE);
				mDrawerMenuLayout.setVisibility(View.VISIBLE);
				mDrawerTitleView.setText(values[position]);
				mDrawerTitleSubView.setText(values2[position]);
				break;
			case 2:
				mDrawerProfileLayout.setVisibility(View.GONE);
				mDrawerMenuLayout.setVisibility(View.VISIBLE);
				mDrawerTitleView.setText(values[position]);
				mDrawerTitleSubView.setText(values2[position]);
				break;
			/*case 3:
				mDrawerProfileLayout.setVisibility(View.GONE);
				mDrawerMenuLayout.setVisibility(View.VISIBLE);
				mDrawerTitleView.setText(Html.fromHtml(getResources().getString(R.string.str_activity_groups)));
				mDrawerTitleSubView.setText(values2[position]);
				break;*/
			default:
				/*mDrawerProfileLayout.setVisibility(View.GONE);
				mDrawerMenuLayout.setVisibility(View.VISIBLE);
				mDrawerTitleView.setText(values[position]);
				mDrawerTitleSubView.setText(values2[position]);*/
				break;
			}
			return rowView;
		}
	}
	
	@Override
	public boolean onKeyDown(int keycode, KeyEvent e) {
	    switch(keycode) {
	        case KeyEvent.KEYCODE_MENU:
	            return true;
	    }
	    return super.onKeyDown(keycode, e);
	}
	
	/*
	 * Flurry Analytics
	 */
	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, BuildVars.FLURRY_ID);
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
}
