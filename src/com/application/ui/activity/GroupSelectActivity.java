/**
 * 
 */
package com.application.ui.activity;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.application.beans.Banner;
import com.application.beans.City;
import com.application.beans.Group;
import com.application.beans.MessageObject;
import com.application.messenger.ConnectionsManager;
import com.application.service.NotificationsService;
import com.application.ui.view.CircleImageView;
import com.application.ui.view.Crouton;
import com.application.ui.view.DrawerArrowDrawable;
import com.application.ui.view.GroupGridAdapter;
import com.application.ui.view.Style;
import com.application.utils.AppConstants;
import com.application.utils.AppConstants.XMPP;
import com.application.utils.ApplicationLoader;
import com.application.utils.BuildVars;
import com.application.utils.DBConstant;
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

/**
 * @author Vikalp Patel(VikalpPatelCE)
 * 
 */
public class GroupSelectActivity extends ActionBarActivity {

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
	
	/*
	 * Activity Variables
	 */
	private EditText mEditSearch;
	private GridView mGridView;

	private ImageView mActionBarDrawer;
	private ImageView mActionBarBack;
	private TextView mActionBarTitle;

	private RelativeLayout mParentLayout;
	private ProgressBar mProgressBar;
	private Button mRetryButton;
	
	private ImageView mAdsImageView;

	private ArrayList<Group> mArrayListGroup;
	private GroupGridAdapter mAdapter;
	
	private Intent mIntent;
	
	private AdView mAdView;

	private ImageLoader mImageLoader;
	private DisplayImageOptions mDisplayImageOptions;
	
	private static final String TAG = GroupSelectActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setWindowFullScreen();
		setContentView(R.layout.activity_select_groups);
		initCustomActionBar();
		initUi();
		setUniversalImageLoader();
		getIntentData();
		setUiListener();
		setDrawerLayout();
		loadAds();
		if (!ApplicationLoader.getPreferences().getPullGroupFirstTime()) {
			if (Utilities.isInternetConnected()) {
				getDataFromApi(true);
			} else {
				mRetryButton.setVisibility(View.VISIBLE);
			}
		} else { // refreshing everytime : Sync Groups & City
			addObjectsFromDB();
			setGridViewWithData();
			getDataFromApi(false);
		}
//		Utilities.checkIfUserExpires(GroupSelectActivity.this);
//		Utilities.CheckAppUpdateAvailable(GroupSelectActivity.this);
//		Utilities.sendDBInMail(GroupSelectActivity.this);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		registerReceiver(mBroadCastReceiver, new IntentFilter(
				ConnectionsManager.BROADCAST_ACTION));
		try{
			updateUnreadCounts();
			mAdapter.notifyDataSetChanged();
			ConnectionsManager.getInstance().generateBadgeNotification();
			if (mAdView != null) {
                mAdView.resume();
            }
			Utilities.checkIfUserExpires(GroupSelectActivity.this);
			Utilities.CheckAppUpdateAvailable(GroupSelectActivity.this);
		}catch(Exception e){
			Log.i(TAG, e.toString());
		}
//		Utilities.cancelNotification(ApplicationLoader.getApplication().getApplicationContext());
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unregisterReceiver(mBroadCastReceiver);
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

	private void setWindowFullScreen() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	private void initUi() {
		mEditSearch = (EditText) findViewById(R.id.editTextSearch);
		mGridView = (GridView) findViewById(R.id.gridViewGroupSelect);
		mParentLayout = (RelativeLayout) findViewById(R.id.parentLayout);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		mRetryButton = (Button) findViewById(R.id.retryButton);
		mAdsImageView = (ImageView)findViewById(R.id.groupSelectAds);
		mAdView = (AdView)findViewById(R.id.adView);
	}

	private void initCustomActionBar() {
		mActionBarTitle = (TextView) findViewById(R.id.actionBarTitle);
		mActionBarBack = (ImageView) findViewById(R.id.actionBarBackArrowOnRight);
		mActionBarDrawer = (ImageView) findViewById(R.id.actionBarDrawer);

		mActionBarTitle.setText("TTOGS GROUP NAMES");
		
		hideActionBar();
	}
	
	private void getIntentData(){
		try{
			mActionBarTitle.setText("TTOGS");
			mIntent = getIntent();
		}catch(Exception e){
			Log.i(TAG, e.toString());
		}
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
	
	private BroadcastReceiver mBroadCastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				MessageObject messageObject = intent
						.getParcelableExtra("MessageObject");
				updateUnreadCounts();
				mAdapter.notifyDataSetChanged();
					ConnectionsManager.getInstance()
					.generateNotification(ApplicationLoader.getApplication().getApplicationContext(),
						messageObject.getGroupId(), messageObject.getCityId(),
						messageObject.getUserId(),
						messageObject);
			} catch (Exception e) {
				Log.i(TAG, e.toString());
			}
		}
	};

	private void setUiListener() {
		mRetryButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mRetryButton.setVisibility(View.GONE);
				if(!ApplicationLoader.getPreferences().getPullGroupFirstTime()){
					getDataFromApi(true);	
				}else{
					getDataFromApi(false);
				}
			}
		});

		mEditSearch.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable searchQuery) {
				// TODO Auto-generated method stub
				if (!TextUtils.isEmpty(searchQuery.toString())) {
					// applyFilter(searchQuery.toString());
					mAdapter.getFilter().filter(searchQuery.toString());
				} else {
					mAdapter.getFilter().filter("");
				}
			}
		});
	}

	private void getDataFromApi(boolean isParseFromJSON) {
		if (Utilities.isInternetConnected()) {
			if(isParseFromJSON){
				mParentLayout.setVisibility(View.GONE);
				mProgressBar.setVisibility(View.VISIBLE);	
			}else{
				mParentLayout.setVisibility(View.VISIBLE);
				mProgressBar.setVisibility(View.GONE);
			}
			new AsyncTaskGroup(GroupSelectActivity.this,
					buildJSONRequestForGroup(),buildJSONRequestForCity(),isParseFromJSON).execute();
		} else {
			if(isParseFromJSON){
				Crouton.makeText(
						GroupSelectActivity.this,
						ApplicationLoader.getApplication().getApplicationContext()
								.getResources()
								.getString(R.string.no_internet_connection),
						Style.ALERT).show();	
			}
		}
	}

	private JSONObject buildJSONRequestForGroup() {
		return RequestBuilder.getPostGroupByUser();
	}
	
	private JSONObject buildJSONRequestForCity() {
		return RequestBuilder.getPostCityByUser();
	}

	private void parseJSONFromGroupApi(String mResponseFromApi, String mResponseFromCityApi) {
		try {
			mArrayListGroup = new ArrayList<Group>();
			JSONObject mJSONObject = new JSONObject(mResponseFromApi);
			JSONObject mJSONObjectData = mJSONObject.getJSONObject("data");
			JSONArray mJSONArray = mJSONObjectData.getJSONArray("groups");
			
			JSONObject mJSONObjectCity = new JSONObject(mResponseFromCityApi);
			JSONObject mJSONObjectCityInner = mJSONObjectCity.getJSONObject("data");
			JSONArray mJSONArrayCity = mJSONObjectCityInner.getJSONArray("cities");
			
			for (int j = 0; j < mJSONArrayCity.length(); j++) {
				JSONObject mJSONObjectCityId = mJSONArrayCity.getJSONObject(j);
				String mIntentCityId = mJSONObjectCityId.getString("city_id"); 
				for (int i = 0; i < mJSONArray.length(); i++) {
					Group obj = new Group();
					JSONObject mJSONObjectInner = mJSONArray.getJSONObject(i);
					obj.setGroupId(mJSONObjectInner.getString("group_id")+"_"+mIntentCityId);
					obj.setGroupCityId(mIntentCityId);
					obj.setGroupIdMySQL(mJSONObjectInner.getString("group_id"));
					obj.setGroupName(mJSONObjectInner.getString("group_name"));
//					obj.setGroupImageLocal(AppConstants.GROUP_IMAGE_DIRECTORY_PATH+mJSONObjectInner.getString("group_id")+"_"+mIntentCityId+".jpg");
					obj.setGroupImageLocal(AppConstants.GROUP_IMAGE_DIRECTORY_PATH+Utilities.getFileNameFromPath(mJSONObjectInner.getString("filepath")));
					if(BuildVars.DEBUG_VERSION){
						obj.setGroupImagePath(mJSONObjectInner.getString("group_image_path"));
						obj.setGroupJabberId(mJSONObjectInner.getString("group_jabber_id"));
					}else{
						obj.setGroupImagePath(mJSONObjectInner.getString("filepath"));
						obj.setGroupJabberId(mJSONObjectInner.getString("group_id")+"_"+mIntentCityId+"@conference."+XMPP.TALE);
					}
					obj.setGroupCreatedAt(mJSONObjectInner.getString("group_name"));
					obj.setGroupUnreadCount("0");
					obj.setGroupIsActive("1");
					
					mArrayListGroup.add(obj);
				}
			}
			addGroupToDB();
			updateUnreadCounts();
			ApplicationLoader.getPreferences().setPullGroupFirstTime(true);
			Utilities.parseJSONUserInfo(mResponseFromApi);
			parseCityToAddInDB(mResponseFromCityApi);
		} catch (Exception e) {
			Log.i(TAG, e.toString());
		}
	}
	
	private void filterObjects(ArrayList<Group> mArrayListGroup){
		try{
			for (int i = 0; i < mArrayListGroup.size(); i++) {
				String mGroupId = mArrayListGroup.get(i).getGroupId();
				int skipTheseObject = i;
				ArrayList<Group> mGroupListDuplicate = new ArrayList<Group>();
				for(int j = 0; j < mArrayListGroup.size();j++){
					if(j!=skipTheseObject){
						mGroupListDuplicate.add(mArrayListGroup.get(j));	
					}
				}
				
				for(int k = 0 ; k < mGroupListDuplicate.size() ; k++){
						int lastUnderScoreGroup = mArrayListGroup.get(i).getGroupId().lastIndexOf("_");
						int lastUnderScoreGroupDuplicate = mGroupListDuplicate.get(k).getGroupId().lastIndexOf("_");
						
					if (mGroupId
							.substring(0, lastUnderScoreGroup)
							.equalsIgnoreCase(mGroupListDuplicate
											.get(k)
											.getGroupId()
											.substring(0,lastUnderScoreGroupDuplicate))) {
						if(mGroupListDuplicate.get(k).getGroupIsActive().equalsIgnoreCase("1")){
							mArrayListGroup.get(i).setGroupIsActive("1");
						}
						
						for (int f = 0; f < mArrayListGroup.size(); f++) {
							if(mArrayListGroup.get(f).getGroupName().equalsIgnoreCase(mGroupListDuplicate.get(k).getGroupName())){
								mArrayListGroup.get(f).setGroupIsActive("1");
							}
						}
						
						mArrayListGroup.remove(i);
					}
				}
			}
		}catch(Exception e){
			Log.i(TAG, e.toString());
		}
	}
	
	private void addObjectsFromDB(){
		try{
		 mArrayListGroup = new ArrayList<Group>();
//		 Cursor mCursor = getContentResolver().query(DBConstant.Group_Columns.CONTENT_URI, null, DBConstant.Group_Columns.COLUMN_GROUP_IS_ACTIVE +"=?", new String[]{"1"}, DBConstant.Group_Columns.COLUMN_GROUP_ID_MYSQl + " ASC");
		 Cursor mCursor = getContentResolver().query(DBConstant.Group_Columns.CONTENT_URI, null, null, null, DBConstant.Group_Columns.COLUMN_GROUP_ID_MYSQl + " ASC");
		 if(mCursor!=null && mCursor.getCount() > 0){
			 mCursor.moveToNext();
			 int intColumnGroupId = mCursor.getColumnIndex(DBConstant.Group_Columns.COLUMN_GROUP_ID);
			 int intColumnGroupJabberId = mCursor.getColumnIndex(DBConstant.Group_Columns.COLUMN_GROUP_JABBER_ID);
			 int intColumnGroupCityId = mCursor.getColumnIndex(DBConstant.Group_Columns.COLUMN_CITY_ID);
			 int intColumnGroupName = mCursor.getColumnIndex(DBConstant.Group_Columns.COLUMN_GROUP_NAME);
			 int intColumnGroupImage = mCursor.getColumnIndex(DBConstant.Group_Columns.COLUMN_GROUP_IMAGE);
			 int intColumnGroupActive = mCursor.getColumnIndex(DBConstant.Group_Columns.COLUMN_GROUP_IS_ACTIVE);
			 int intColumnGroupCreated = mCursor.getColumnIndex(DBConstant.Group_Columns.COLUMN_GROUP_CREATED);
			 int intColumnGroupImageLocal = mCursor.getColumnIndex(DBConstant.Group_Columns.COLUMN_GROUP_IMAGE_LOCAL);
			 int intColumnGroupIdMySQL = mCursor.getColumnIndex(DBConstant.Group_Columns.COLUMN_GROUP_ID_MYSQl);
			 do {
				Group obj = new Group();
				obj.setGroupId(mCursor.getString(intColumnGroupId));
				obj.setGroupCityId(mCursor.getString(intColumnGroupCityId));
				obj.setGroupJabberId(mCursor.getString(intColumnGroupJabberId));
				obj.setGroupName(mCursor.getString(intColumnGroupName));
				obj.setGroupImagePath(mCursor.getString(intColumnGroupImage));
				obj.setGroupCreatedAt(mCursor.getString(intColumnGroupCreated));
				obj.setGroupImageLocal(mCursor.getString(intColumnGroupImageLocal));
				obj.setGroupUnreadCount("0");
				obj.setGroupIsActive(mCursor.getString(intColumnGroupActive));
				obj.setGroupIdMySQL(mCursor.getString(intColumnGroupIdMySQL));
				mArrayListGroup.add(obj);
			} while (mCursor.moveToNext());
		 }
		}catch(Exception e){
			Log.i(TAG, e.toString());
		}
	}
	
	private void parseCityToAddInDB(String mResponseFromApi){
		try {
			ArrayList<City> mArrayListCity = new ArrayList<City>();
			JSONObject mJSONObject = new JSONObject(mResponseFromApi);
			JSONObject mJSONObjectInner = mJSONObject.getJSONObject("data");
			JSONArray mJSONArray = mJSONObjectInner.getJSONArray("cities");
			for (int i = 0; i < mJSONArray.length(); i++) {
				City obj = new City();
				JSONObject mJSONObjectCity = mJSONArray.getJSONObject(i);
				obj.setCityId(mJSONObjectCity.getString("city_id"));
				obj.setCityName(mJSONObjectCity.getString("city_name"));
				obj.setCityIsActive(1);
				obj.setCityUnreadCount("0");
				mArrayListCity.add(obj);
			}
			addCityToDB(mArrayListCity);
			ApplicationLoader.getPreferences().setPullCityFirstTime(true);
			Utilities.parseJSONUserInfo(mResponseFromApi);
		} catch (Exception e) {
			Log.i(TAG, e.toString());
		}
	}
	
	private void addCityToDB(ArrayList<City> mArrayListCity) {
		if (mArrayListCity != null && mArrayListCity.size() > 0) {
			for (int i = 0; i < mArrayListCity.size(); i++) {
				ContentValues values  = new ContentValues();
				values.put(DBConstant.City_Columns.COLUMN_CITY_ID, mArrayListCity.get(i).getCityId());
				values.put(DBConstant.City_Columns.COLUMN_CITY_NAME, mArrayListCity.get(i).getCityName());
				getApplication().getApplicationContext().getContentResolver().insert(DBConstant.City_Columns.CONTENT_URI, values);
			}
		}
	}
	
	
	
	private void addGroupToDB() {
		if (mArrayListGroup != null && mArrayListGroup.size() > 0) {
			for (int i = 0; i < mArrayListGroup.size(); i++) {
				ContentValues values  = new ContentValues();
				values.put(DBConstant.Group_Columns.COLUMN_GROUP_ID, mArrayListGroup.get(i).getGroupId());
				values.put(DBConstant.Group_Columns.COLUMN_CITY_ID, mArrayListGroup.get(i).getGroupCityId());
				values.put(DBConstant.Group_Columns.COLUMN_GROUP_JABBER_ID, mArrayListGroup.get(i).getGroupJabberId());
				values.put(DBConstant.Group_Columns.COLUMN_GROUP_NAME, mArrayListGroup.get(i).getGroupName());
				values.put(DBConstant.Group_Columns.COLUMN_GROUP_IMAGE, mArrayListGroup.get(i).getGroupImagePath());
				values.put(DBConstant.Group_Columns.COLUMN_GROUP_CREATED, mArrayListGroup.get(i).getGroupCreatedAt());
				values.put(DBConstant.Group_Columns.COLUMN_GROUP_IMAGE_LOCAL, mArrayListGroup.get(i).getGroupImageLocal());
				values.put(DBConstant.Group_Columns.COLUMN_GROUP_IS_ACTIVE, mArrayListGroup.get(i).getGroupIsActive());
				values.put(DBConstant.Group_Columns.COLUMN_GROUP_ID_MYSQl, mArrayListGroup.get(i).getGroupIdMySQL());
				
				getApplication().getApplicationContext().getContentResolver().insert(DBConstant.Group_Columns.CONTENT_URI, values);
			}
//			syncGroupCity(mArrayListGroup);
		}
//		Utilities.sendDBInMail(GroupSelectActivity.this);
	}
	
	private void syncGroupCity(ArrayList<Group> mArrayListGroup){
		try{
			Cursor mCursor = getContentResolver().query(DBConstant.City_Columns.CONTENT_URI, null, null, null, null);
			mCursor.moveToFirst();
			if(mCursor!=null && mCursor.getCount() > 0){
				for (int i = 0; i < mArrayListGroup.size(); i++) {
					do {
						ContentValues values  = new ContentValues();
						values.put(DBConstant.Group_Columns.COLUMN_GROUP_ID, mArrayListGroup.get(i).getGroupId()+"_"+mCursor.getString(mCursor.getColumnIndex(DBConstant.City_Columns.COLUMN_CITY_ID)));
						values.put(DBConstant.Group_Columns.COLUMN_CITY_ID, mCursor.getString(mCursor.getColumnIndex(DBConstant.City_Columns.COLUMN_CITY_ID)));
						if(BuildVars.DEBUG_VERSION){
							values.put(DBConstant.Group_Columns.COLUMN_GROUP_JABBER_ID, mArrayListGroup.get(i).getGroupJabberId());
						}else{
							values.put(DBConstant.Group_Columns.COLUMN_GROUP_JABBER_ID, mArrayListGroup.get(i).getGroupId() + "_"+mCursor.getString(mCursor.getColumnIndex(DBConstant.City_Columns.COLUMN_CITY_ID))+"@conference."+XMPP.TALE);
						}
						values.put(DBConstant.Group_Columns.COLUMN_GROUP_NAME, mArrayListGroup.get(i).getGroupName());
						values.put(DBConstant.Group_Columns.COLUMN_GROUP_IMAGE, mArrayListGroup.get(i).getGroupImagePath());
						values.put(DBConstant.Group_Columns.COLUMN_GROUP_CREATED, mArrayListGroup.get(i).getGroupCreatedAt());
						
						getApplication().getApplicationContext().getContentResolver().insert(DBConstant.Group_Columns.CONTENT_URI, values);
					} while (mCursor.moveToNext());
				}
			}
		}catch(Exception e){
			Log.i(TAG, e.toString());
		}
	}
	
	private void updateUnreadCounts(){
		if(mArrayListGroup!=null && mArrayListGroup.size() > 0){
			for (int i = 0; i < mArrayListGroup.size(); i++) {
				try{
					String mUnreadCounter = String.valueOf(ApplicationLoader
							.getApplication()
							.getContentResolver()
							.query(DBConstant.Chat_Columns.CONTENT_URI,
									null,DBConstant.Chat_Columns.COLUMN_GROUP_ID+ "=?" + " AND " + DBConstant.Chat_Columns.COLUMN_ISREAD + "=?",
									new String[]{mArrayListGroup.get(i)
											.getGroupJabberId(),"0"}, null).getCount());
					mArrayListGroup.get(i).setGroupUnreadCount(mUnreadCounter);
				}catch(Exception e){
					Log.i(TAG, "updateUnreadCounts");
					mArrayListGroup.get(i).setGroupUnreadCount("0");
				}
				
				/*try{
					ContentValues values = new ContentValues();
					values.put(DBConstant.Group_Columns.COLUMN_GROUP_UNREAD_COUNTER, mUnreadCounter);
					getContentResolver().update(DBConstant.Group_Columns.CONTENT_URI, values, DBConstant.Group_Columns.COLUMN_GROUP_JABBER_ID, new String[]{mArrayListGroup.get(i).getGroupJabberId()});
				}catch(Exception e){
					Log.i(TAG, e.toString());
				}*/
			}
		}
	}

	private void setGridViewWithData() {
		filterObjects(mArrayListGroup);
		if (mArrayListGroup != null && mArrayListGroup.size() > 0) {
			mProgressBar.setVisibility(View.GONE);
			mParentLayout.setVisibility(View.VISIBLE);

			mAdapter = new GroupGridAdapter(GroupSelectActivity.this,
					mArrayListGroup, mImageLoader, mDisplayImageOptions);
			mGridView.setAdapter(mAdapter);
			setGridViewListener();
			Intent mIntent  = new Intent(GroupSelectActivity.this, NotificationsService.class);
			startService(mIntent);
		} else {
			mProgressBar.setVisibility(View.GONE);
			mRetryButton.setVisibility(View.VISIBLE);
		}
	}

	private void setGridViewListener() {
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parentView, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String groupId = view.getTag(R.id.TAG_GROUP_ID).toString();
				String groupName = view.getTag(R.id.TAG_GROUP_NAME).toString();
				String groupCityId = view.getTag(R.id.TAG_GROUP_CITY_ID).toString();
				String groupJabberId = view.getTag(R.id.TAG_GROUP_JABBER_ID).toString();
				String groupIsActive = view.getTag(R.id.TAG_GROUP_ACTIVE).toString();

				Cursor mCursor = getContentResolver().query(DBConstant.City_Columns.CONTENT_URI, null, DBConstant.City_Columns.COLUMN_CITY_NAME+ "=?" +" AND " + DBConstant.City_Columns.COLUMN_CITY_IS_ACTIVE +"=?", new String[]{"All","1"}, null);
				if(mCursor!=null && mCursor.getCount() > 0){
					
					Cursor mCursorCity = getContentResolver().query(DBConstant.City_Columns.CONTENT_URI, null, DBConstant.City_Columns.COLUMN_CITY_IS_ACTIVE + "=?", new String[]{"1"}, null);
					if(mCursorCity!=null && mCursorCity.getCount() > 1){
						Intent mIntent  = new Intent(GroupSelectActivity.this, CitySelectActivity.class);
						mIntent.putExtra("notificationFromConnectionsManager", false);
//						mIntent.putExtra("notificationFromGroupJabberId", groupJabberId);
						mIntent.putExtra("notificationFromGroupId", groupId);
						mIntent.putExtra("notificationFromGroupName", groupName);
						startActivity(mIntent);		
					}else{
						Intent mIntent  = new Intent(GroupSelectActivity.this, GroupChatActivity.class);
						mIntent.putExtra("notificationFromConnectionsManager", false);
						mIntent.putExtra("notificationFromGroupId", groupId);
						mIntent.putExtra("notificationFromGroupName", groupName);
						mIntent.putExtra("notificationFromGroupJabberId", groupJabberId);
						mIntent.putExtra("notificationFromCityId", groupCityId);
						mIntent.putExtra("notificationFromCityIsActive", groupIsActive);
						startActivity(mIntent);						
					}
					mCursorCity.close();
				}else{
					Intent mIntent  = new Intent(GroupSelectActivity.this, CitySelectActivity.class);
					mIntent.putExtra("notificationFromConnectionsManager", false);
//					mIntent.putExtra("notificationFromGroupJabberId", groupJabberId);
					mIntent.putExtra("notificationFromGroupId", groupId);
					mIntent.putExtra("notificationFromGroupName", groupName);
					startActivity(mIntent);	
				}
			}
		});
	}
	
	/*private void syncGroupWithDB(String mReponseFromApi){
		try{
			JSONObject mJSONObject = new JSONObject(mReponseFromApi);
			JSONObject mJSONObjectData = mJSONObject.getJSONObject("data");
			JSONArray mJSONArray = mJSONObjectData.getJSONArray("groups");
			for (int i = 0; i < mJSONArray.length(); i++) {
				Group obj = new Group();
				JSONObject mJSONObjectGroup = mJSONArray.getJSONObject(i);
				Cursor mCursor = getContentResolver().query(DBConstant.City_Columns.CONTENT_URI, null, null, null, null);
				mCursor.moveToFirst();
				if(mCursor!=null && mCursor.getCount() > 0){
					do {
						if(!checkIfGroupExistsInDB(mJSONObjectGroup.getString("group_id")+"_"+ mCursor.getString(mCursor.getColumnIndex(DBConstant.City_Columns.COLUMN_CITY_ID)))){
							obj.setGroupId(mJSONObjectGroup.getString("group_id")+"_"+ mCursor.getString(mCursor.getColumnIndex(DBConstant.City_Columns.COLUMN_CITY_ID)));
							obj.setGroupCityId(mIntentCityId);
							obj.setGroupName(mJSONObjectGroup.getString("group_name"));
							if(BuildVars.DEBUG_VERSION){
								obj.setGroupImagePath(mJSONObjectGroup
										.getString("group_image_path"));
								obj.setGroupJabberId(mJSONObjectGroup.getString("group_jabber_id"));
							}else{
								obj.setGroupImagePath(mJSONObjectGroup
										.getString("filepath"));
								obj.setGroupJabberId(mJSONObjectGroup.getString("group_id")+"_"+mIntentCityId+"@conference."+XMPP.TALE);
							}
							obj.setGroupCreatedAt(mJSONObjectGroup.getString("group_name"));
							obj.setGroupName(mJSONObjectGroup.getString("group_name"));
							obj.setGroupUnreadCount("0");
							
							ContentValues values = new ContentValues();
							values.put(DBConstant.Group_Columns.COLUMN_GROUP_ID, obj.getGroupId());
							values.put(DBConstant.Group_Columns.COLUMN_CITY_ID, obj.getGroupCityId());
							values.put(DBConstant.Group_Columns.COLUMN_GROUP_JABBER_ID, obj.getGroupJabberId());
							values.put(DBConstant.Group_Columns.COLUMN_GROUP_NAME, obj.getGroupName());
							values.put(DBConstant.Group_Columns.COLUMN_GROUP_IMAGE, obj.getGroupImagePath());
							values.put(DBConstant.Group_Columns.COLUMN_GROUP_CREATED, obj.getGroupCreatedAt());
//							values.put(DBConstant.Group_Columns.COLUMN_GROUP_IS_ACTIVE, obj.getGroupIsActive());
							
							getContentResolver().insert(DBConstant.Group_Columns.CONTENT_URI, values);
							mArrayListGroup.add(obj);
							
							mAdapter.notifyDataSetChanged();
						} else if(checkIfGroupExistsInDB(mJSONObjectGroup.getString("group_id")+"_"+ mCursor.getString(mCursor.getColumnIndex(DBConstant.City_Columns.COLUMN_CITY_ID)))){
							 //TO DO : IS ACTIVE OR NOT
						}
					} while (mCursor.moveToNext());
				}
			}
		}catch(Exception e){
			Log.i(TAG, e.toString());
		}
	}*/
	
	private void addDistinctGroupInDb(){
		try{
			for(int i = 0 ; i < mArrayListGroup.size();i++){
				ContentValues values = new ContentValues();
				values.put(DBConstant.GroupDistinct_Columns.COLUMN_GROUP_ID, mArrayListGroup.get(i).getGroupId());
				values.put(DBConstant.GroupDistinct_Columns.COLUMN_GROUP_ID_MYSQl, mArrayListGroup.get(i).getGroupIdMySQL());
				values.put(DBConstant.GroupDistinct_Columns.COLUMN_GROUP_IMAGE, mArrayListGroup.get(i).getGroupImagePath());
				values.put(DBConstant.GroupDistinct_Columns.COLUMN_GROUP_IMAGE_LOCAL, mArrayListGroup.get(i).getGroupImageLocal());
				values.put(DBConstant.GroupDistinct_Columns.COLUMN_GROUP_NAME, mArrayListGroup.get(i).getGroupName());
				values.put(DBConstant.GroupDistinct_Columns.COLUMN_GROUP_IS_ACTIVE, mArrayListGroup.get(i).getGroupIsActive());
				values.put(DBConstant.GroupDistinct_Columns.COLUMN_GROUP_JABBER_ID, mArrayListGroup.get(i).getGroupJabberId());
				values.put(DBConstant.GroupDistinct_Columns.COLUMN_CITY_ID, mArrayListGroup.get(i).getGroupCityId());
				values.put(DBConstant.GroupDistinct_Columns.COLUMN_GROUP_UNREAD_COUNTER, mArrayListGroup.get(i).getGroupUnreadCount());
				getContentResolver().insert(DBConstant.GroupDistinct_Columns.CONTENT_URI, values);
			}
		}catch(Exception e){
			Log.i(TAG, e.toString());
		}
	}
	
	
	
	private void syncGroupWithDB(String mReponseFromGroupApi, String mResponseFromCityApi){
		try{
			JSONObject mJSONObject = new JSONObject(mReponseFromGroupApi);
			JSONObject mJSONObjectData = mJSONObject.getJSONObject("data");
			JSONArray mJSONArray = mJSONObjectData.getJSONArray("groups");
			
			JSONObject mJSONObjectCity = new JSONObject(mResponseFromCityApi);
			JSONObject mJSONObjectDataCity = mJSONObjectCity.getJSONObject("data");
			JSONArray mJSONArrayCity = mJSONObjectDataCity.getJSONArray("cities");
			
			//MARK ALL CITY : INACTIVE
			ContentValues valuesCityInactive = new ContentValues();
			valuesCityInactive.put(DBConstant.City_Columns.COLUMN_CITY_IS_ACTIVE, "0");
			getContentResolver().update(DBConstant.City_Columns.CONTENT_URI, valuesCityInactive, null, null);
			//MARK ALL GROUPS : INACTIVE
			ContentValues valuesGroupInactive = new ContentValues();
			valuesGroupInactive.put(DBConstant.Group_Columns.COLUMN_GROUP_IS_ACTIVE, "0");
			getContentResolver().update(DBConstant.Group_Columns.CONTENT_URI, valuesGroupInactive, null, null);

			for (int z = 0; z < mArrayListGroup.size(); z++) {
				mArrayListGroup.get(z).setGroupIsActive("0");
			}
			
			for (int j = 0; j < mJSONArrayCity.length(); j++) {
				JSONObject JSONObjectCityId = mJSONArrayCity.getJSONObject(j);
				String mIntentCityId = JSONObjectCityId.getString("city_id");
				
				for (int i = 0; i < mJSONArray.length(); i++) {
					Group obj = new Group();
					JSONObject mJSONObjectGroup = mJSONArray.getJSONObject(i);
					Cursor mCursor = getContentResolver().query(DBConstant.Group_Columns.CONTENT_URI, null, null, null, null);
					mCursor.moveToFirst();
					if(mCursor!=null && mCursor.getCount() > 0){
						do {
							if(!checkIfGroupExistsInDB(mJSONObjectGroup.getString("group_id")+"_"+ mIntentCityId)){
								//GROUP DOESN'T EXISTS IN DB : BUT DO EXISTS IN JSON : ADD IT
								obj.setGroupId(mJSONObjectGroup.getString("group_id")+"_"+ mIntentCityId);
								obj.setGroupCityId(mIntentCityId);
								obj.setGroupIdMySQL(mJSONObjectGroup.getString("group_id"));
								obj.setGroupName(mJSONObjectGroup.getString("group_name"));
								if(BuildVars.DEBUG_VERSION){
									obj.setGroupImagePath(mJSONObjectGroup
											.getString("group_image_path"));
									obj.setGroupJabberId(mJSONObjectGroup.getString("group_jabber_id"));
								}else{
									obj.setGroupImagePath(mJSONObjectGroup
											.getString("filepath"));
									obj.setGroupJabberId(mJSONObjectGroup.getString("group_id")+"_"+mIntentCityId+"@conference."+XMPP.TALE);
								}
								obj.setGroupCreatedAt(mJSONObjectGroup.getString("group_name"));
								obj.setGroupName(mJSONObjectGroup.getString("group_name"));
								obj.setGroupUnreadCount("0");
								obj.setGroupIsActive("1");
//								obj.setGroupImageLocal(AppConstants.GROUP_IMAGE_DIRECTORY_PATH+mJSONObjectGroup.getString("group_id")+"_"+mIntentCityId+".jpg");
								obj.setGroupImageLocal(AppConstants.GROUP_IMAGE_DIRECTORY_PATH+Utilities.getFileNameFromPath(mJSONObjectGroup.getString("filepath")));
								ContentValues values = new ContentValues();
								values.put(DBConstant.Group_Columns.COLUMN_GROUP_ID, obj.getGroupId());
								values.put(DBConstant.Group_Columns.COLUMN_CITY_ID, obj.getGroupCityId());
								values.put(DBConstant.Group_Columns.COLUMN_GROUP_JABBER_ID, obj.getGroupJabberId());
								values.put(DBConstant.Group_Columns.COLUMN_GROUP_NAME, obj.getGroupName());
								values.put(DBConstant.Group_Columns.COLUMN_GROUP_IMAGE, obj.getGroupImagePath());
								values.put(DBConstant.Group_Columns.COLUMN_GROUP_CREATED, obj.getGroupCreatedAt());
								values.put(DBConstant.Group_Columns.COLUMN_GROUP_IS_ACTIVE, obj.getGroupIsActive());
								values.put(DBConstant.Group_Columns.COLUMN_GROUP_IMAGE_LOCAL, obj.getGroupImageLocal());
								values.put(DBConstant.Group_Columns.COLUMN_GROUP_ID_MYSQl, obj.getGroupIdMySQL());
								
								getContentResolver().insert(DBConstant.Group_Columns.CONTENT_URI, values);
								mArrayListGroup.add(obj);
								
								mAdapter.notifyDataSetChanged();
							}/* else if(checkIfGroupExistsInDB(mJSONObjectGroup.getString("group_id")+"_"+ mCursor.getString(mCursor.getColumnIndex(DBConstant.City_Columns.COLUMN_CITY_ID)))){
								 //TO DO : IS ACTIVE OR NOT : GROUP EXISTS IN DB BUT DOESN'T EXISTS IN JSON : UPDATE INACTIVE
								ContentValues values = new ContentValues();
								values.put(DBConstant.Group_Columns.COLUMN_GROUP_IS_ACTIVE, "1");
								getContentResolver().update(DBConstant.Group_Columns.CONTENT_URI, values, DBConstant.Group_Columns.COLUMN_GROUP_ID + "=?", new String[]{mJSONObjectGroup.getString("group_id")+"_"+ mCursor.getString(mCursor.getColumnIndex(DBConstant.City_Columns.COLUMN_CITY_ID))});
								for (int l = 0; l < mArrayListGroup.size(); l++) {
									if(mArrayListGroup.get(l).getGroupId().compareToIgnoreCase(mJSONObjectGroup.getString("group_id")+"_"+ mCursor.getString(mCursor.getColumnIndex(DBConstant.City_Columns.COLUMN_CITY_ID))) == 0){
										mArrayListGroup.get(l).setGroupIsActive("1");
									}
									mAdapter.notifyDataSetChanged();
								}
							}*/
							 else if(checkIfGroupExistsInDBOnly(mJSONObjectGroup.getString("group_id"))){
							 //TO DO : UPDATE GROUP NAME AND IMAGE PATH
								 try{
									ContentValues values = new ContentValues();
									values.put(DBConstant.Group_Columns.COLUMN_GROUP_NAME, mJSONObjectGroup.getString("group_name"));
									values.put(DBConstant.Group_Columns.COLUMN_GROUP_IMAGE_LOCAL, AppConstants.GROUP_IMAGE_DIRECTORY_PATH+Utilities.getFileNameFromPath(mJSONObjectGroup.getString("filepath")));
									values.put(DBConstant.Group_Columns.COLUMN_GROUP_IMAGE, mJSONObjectGroup.getString("filepath"));
									getContentResolver().update(DBConstant.Group_Columns.CONTENT_URI, values, DBConstant.Group_Columns.COLUMN_GROUP_ID_MYSQl+"=?", new String[]{mJSONObjectGroup.getString("group_id")});
								 }catch(Exception e){
									 Log.i(TAG, e.toString());
								 }
							 }
						} while (mCursor.moveToNext());
					}
				}
				
				//ACCORDING TO CITY : GROUP: MARK IT AS ACTIVE
				Cursor mCursorCity = getContentResolver().query(DBConstant.City_Columns.CONTENT_URI, null, DBConstant.City_Columns.COLUMN_CITY_ID + "=?", new String[]{mIntentCityId}, null);
				if(mCursorCity!=null && mCursorCity.getCount() > 0){
					mCursorCity.moveToFirst();
					String mCityId = mCursorCity.getString(mCursorCity.getColumnIndex(DBConstant.City_Columns.COLUMN_CITY_ID));
					ContentValues valuesCityActive = new ContentValues();
					valuesCityActive.put(DBConstant.City_Columns.COLUMN_CITY_IS_ACTIVE, "1");
					try {
						getContentResolver().update(DBConstant.City_Columns.CONTENT_URI, valuesCityActive, DBConstant.City_Columns.COLUMN_CITY_ID+"=?", new String[]{mIntentCityId});
					} catch (Exception e) {
						// TODO: handle exception
					}
					
					for (int s = 0; s < mArrayListGroup.size(); s++) {
						if(mArrayListGroup.get(s).getGroupCityId().compareToIgnoreCase(mCityId) == 0){
							mArrayListGroup.get(s).setGroupIsActive("1");
						}
					}
					
					//ADDED TO FIX BUG : 10.03.15 : ADD ANOTHER CITY -> REMOVE ALL -> ADD ALL
					String mGroupIdMySql[];
					Cursor mCursorIdMySQL = getContentResolver().query(DBConstant.Group_Columns.CONTENT_URI, null, DBConstant.Group_Columns.COLUMN_CITY_ID+"=?", new String[]{mCityId}, null);
					int gh = 0;
					if(mCursorIdMySQL!=null && mCursorIdMySQL.getCount() > 0){
						mCursorIdMySQL.moveToFirst();
						mGroupIdMySql = new String[mCursorIdMySQL.getCount()];
						do {
							mGroupIdMySql[gh] = mCursorIdMySQL.getString(mCursorIdMySQL.getColumnIndex(DBConstant.Group_Columns.COLUMN_GROUP_ID_MYSQl));
							gh++;
						} while (mCursorIdMySQL.moveToNext());
						
						if(mGroupIdMySql.length > 0){
							for(int n = 0 ;n < mGroupIdMySql.length ; n++){
								for (int u = 0; u < mArrayListGroup.size(); u++) {
									if(mArrayListGroup.get(u).getGroupIdMySQL().equalsIgnoreCase(mGroupIdMySql[n])){
										mArrayListGroup.get(u).setGroupIsActive("1");
									}
								}	
							}
						}
					}

					
					ContentValues valuesGroupActive = new ContentValues();
					valuesGroupActive.put(DBConstant.Group_Columns.COLUMN_GROUP_IS_ACTIVE, "1");
					try{
						getContentResolver().update(DBConstant.Group_Columns.CONTENT_URI, valuesGroupActive, DBConstant.Group_Columns.COLUMN_CITY_ID+"=?", new String[]{mIntentCityId});
					}catch(Exception e){
						
					}
				}
				mCursorCity.close();
			}
			
			//IF GROUP : REMOVES : MARK IT AS INACTIVE
			String mGroupNameArr[] = new String[mJSONArray.length()];
			StringBuilder mStringNotIn = new StringBuilder();
			for (int m = 0; m < mJSONArray.length(); m++){
				mGroupNameArr[m] = mJSONArray.getJSONObject(m).getString("group_name");
				mStringNotIn.append("\"").append(mGroupNameArr[m]).append("\",");
			}
			ContentValues valuesGroupInactiveBulk = new ContentValues();
			valuesGroupInactiveBulk.put(DBConstant.Group_Columns.COLUMN_GROUP_IS_ACTIVE, "0");
			getContentResolver().update(DBConstant.Group_Columns.CONTENT_URI, valuesGroupInactiveBulk, DBConstant.Group_Columns.COLUMN_GROUP_NAME + " NOT IN ("+ mStringNotIn.toString().substring(0, mStringNotIn.length()-1) +")", null);
			
			//TO DO : IF GROUP REMOVES IT WOULD BE REFLECTED ONCE APPLICATION WILL BE STARTED ONCE AGAIN
			/*for(int p = 0 ; p < mGroupNameArr.length ; p++){
				for(int h = 0 ; h < mArrayListGroup.size() ; h++){
					if(mArrayListGroup.get(h).getGroupName().equalsIgnoreCase(mGroupNameArr[p])){
						mArrayListGroup.get(h).setGroupIsActive("1");
					}else{
						mArrayListGroup.get(h).setGroupIsActive("0");
					}
				}
			}*/
		
			//UPDATE CITY
			for(int q = 0 ;q < mJSONArrayCity.length(); q++){
				String mCityId = mJSONArrayCity.getJSONObject(q).getString("city_id");
				Cursor mCursorCityInsertInDB = getContentResolver().query(DBConstant.City_Columns.CONTENT_URI, null, DBConstant.City_Columns.COLUMN_CITY_ID + "=?", new String[]{mCityId}, null);
				if(mCursorCityInsertInDB!=null && mCursorCityInsertInDB.getCount() > 0){
					ContentValues mContentValues = new ContentValues();
					mContentValues.put(DBConstant.City_Columns.COLUMN_CITY_IS_ACTIVE, "1");
					getContentResolver().update(DBConstant.City_Columns.CONTENT_URI, mContentValues, DBConstant.City_Columns.COLUMN_CITY_ID+"=?", new String[]{mCityId});
				}else if(mCursorCityInsertInDB!=null){
					ContentValues mContentValues = new ContentValues();
					mContentValues.put(DBConstant.City_Columns.COLUMN_CITY_IS_ACTIVE, "1");
					mContentValues.put(DBConstant.City_Columns.COLUMN_CITY_ID, mCityId);
					mContentValues.put(DBConstant.City_Columns.COLUMN_CITY_NAME, mJSONArrayCity.getJSONObject(q).getString("city_name"));
					getContentResolver().insert(DBConstant.City_Columns.CONTENT_URI, mContentValues);
				}
			}
			
			//GET ALL CITY ID
			String mCityAllId;
			
			Cursor mCursorCityAll = getContentResolver().query(DBConstant.City_Columns.CONTENT_URI, null, DBConstant.City_Columns.COLUMN_CITY_NAME + "=?", new String[]{"All"}, null);
			if(mCursorCityAll!=null && mCursorCityAll.getCount() >0){
				mCursorCityAll.moveToNext();
				mCityAllId = mCursorCityAll.getString(mCursorCityAll.getColumnIndex(DBConstant.City_Columns.COLUMN_CITY_ID));
				//SET GROUP INACTIVE : ALL CITY ONE IS ACTIVE
				Cursor mCursorGroupAllInactive  = getContentResolver().query(DBConstant.Group_Columns.CONTENT_URI, null, DBConstant.Group_Columns.COLUMN_GROUP_IS_ACTIVE + "=?" + " AND " + DBConstant.Group_Columns.COLUMN_CITY_ID + "=?", new String[]{"0",mCityAllId}, null);
				if (mCursorGroupAllInactive != null && mCursorGroupAllInactive.getCount() > 0) {
					mCursorGroupAllInactive.moveToFirst();
					do {
						for (int v = 0; v < mArrayListGroup.size(); v++) {
							if(mArrayListGroup.get(v).getGroupName().equalsIgnoreCase(mCursorGroupAllInactive.getString(mCursorGroupAllInactive.getColumnIndex(DBConstant.Group_Columns.COLUMN_GROUP_NAME)))){
								mArrayListGroup.get(v).setGroupIsActive("0");
							}
						}
					} while (mCursorGroupAllInactive.moveToNext());
				}
				mCursorGroupAllInactive.close();
			}
			mCursorCityAll.close();
			
			
			for (int d = 0; d < mJSONArrayCity.length(); d++) {
				JSONObject JSONObjectCityId = mJSONArrayCity.getJSONObject(d);
				String mCityId = JSONObjectCityId.getString("city_id");
				//ADDED TO FIX BUG : 10.03.15 : ADD ANOTHER CITY -> REMOVE ALL -> ADD ALL
				String mGroupIdMySql[];
				Cursor mCursorIdMySQL = getContentResolver().query(DBConstant.Group_Columns.CONTENT_URI, null, DBConstant.Group_Columns.COLUMN_CITY_ID+"=?", new String[]{mCityId}, null);
				int gh = 0;
				if(mCursorIdMySQL!=null && mCursorIdMySQL.getCount() > 0){
					mCursorIdMySQL.moveToFirst();
					mGroupIdMySql = new String[mCursorIdMySQL.getCount()];
					do {
						mGroupIdMySql[gh] = mCursorIdMySQL.getString(mCursorIdMySQL.getColumnIndex(DBConstant.Group_Columns.COLUMN_GROUP_ID_MYSQl));
						gh++;
					} while (mCursorIdMySQL.moveToNext());
					
					if(mGroupIdMySql.length > 0){
						for(int n = 0 ;n < mGroupIdMySql.length ; n++){
							for (int u = 0; u < mArrayListGroup.size(); u++) {
								if(mArrayListGroup.get(u).getGroupIdMySQL().equalsIgnoreCase(mGroupIdMySql[n])){
									mArrayListGroup.get(u).setGroupIsActive("1");
								}
							}	
						}
					}
				}
			}
			
			//INACTIVE GROUP : WHICH ARE NOT IN JSON
			Cursor mInActiveGroup = getContentResolver().query(DBConstant.Group_Columns.CONTENT_URI, null, DBConstant.Group_Columns.COLUMN_GROUP_IS_ACTIVE+"=?", new String[]{"0"}, null);
			if(mInActiveGroup!=null && mInActiveGroup.getCount() > 0){
				mInActiveGroup.moveToFirst();
				do {
					for(int i = 0 ; i < mArrayListGroup.size() ;i++){
						if(mArrayListGroup.get(i).getGroupIdMySQL().equalsIgnoreCase(mInActiveGroup.getString(mInActiveGroup.getColumnIndex(DBConstant.Group_Columns.COLUMN_GROUP_ID_MYSQl)))){
							mArrayListGroup.get(i).setGroupIsActive("0");
						}
					}
				} while (mInActiveGroup.moveToNext());
			}
			mInActiveGroup.close();
		
			mAdapter.notifyDataSetChanged();
			ConnectionsManager.getInstance().initPushConnection();
			Utilities.parseJSONUserInfo(mResponseFromCityApi);
		}catch(Exception e){
			Log.i(TAG, e.toString());
		}
	}
	
	private boolean checkIfGroupExistsInDB(String mGroupId){
		Cursor mCursor = getContentResolver().query(DBConstant.Group_Columns.CONTENT_URI, null, DBConstant.Group_Columns.COLUMN_GROUP_ID+"=?", new String[]{mGroupId}, null);
		if(mCursor!=null && mCursor.getCount() > 0){
			return true;
		}else if(mCursor!=null && mCursor.getCount() == 0){
			return false;	
		}else{
			return false;
		}
	}
	
	private boolean checkIfGroupExistsInDBOnly(String mGroupId){
		Cursor mCursor = getContentResolver().query(DBConstant.Group_Columns.CONTENT_URI, null, DBConstant.Group_Columns.COLUMN_GROUP_ID_MYSQl+"=?", new String[]{mGroupId}, null);
		if(mCursor!=null && mCursor.getCount() > 0){
			return true;
		}else if(mCursor!=null && mCursor.getCount() == 0){
			return false;	
		}else{
			return false;
		}
	}

	public class AsyncTaskGroup extends AsyncTask<Void, Void, Void> {
		private Context mContext;
		private ProgressBar mProgressBar;
		private Button mRetryBtn;
		private String mResponseFromGroupApi;
		private String mResponseFromCityApi;
		private boolean mIsGroupData = false;
		private boolean mIsCityData = false;
		private JSONObject mJSONObjectGroupToSend;
		private JSONObject mJSONObjectCityToSend;
		private boolean isParseFromJSON;
		private boolean isSuccess = false;
		private boolean mIsSuccess = false;
		

		public AsyncTaskGroup(Context mContext, JSONObject mJSONObjectGroupToSend, JSONObject mJSONObjectCityToSend,boolean isParseFromJSON) {
			super();
			this.mJSONObjectCityToSend = mJSONObjectCityToSend;
			this.mJSONObjectGroupToSend = mJSONObjectGroupToSend;
			this.mContext = mContext;
			this.isParseFromJSON = isParseFromJSON;
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
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
				mResponseFromGroupApi = RestClient.postJSON(
						AppConstants.API.URL, mJSONObjectGroupToSend);
				mResponseFromCityApi = RestClient.postJSON(
						AppConstants.API.URL, mJSONObjectCityToSend);
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

			if ((!TextUtils.isEmpty(mResponseFromGroupApi) && !TextUtils.isEmpty(mResponseFromCityApi)) || BuildVars.DEBUG_VERSION) {
				if (BuildVars.DEBUG_VERSION) {
					mResponseFromGroupApi = Utilities.readFile("apiGroups.json");
					mResponseFromCityApi = Utilities.readFile("apiCityByUser.json");
				}
				try {
					JSONObject mJSONObject = new JSONObject(mResponseFromGroupApi);
					if(mJSONObject.getBoolean("success")){
						isSuccess = true;
						JSONObject mJSONObjectData = mJSONObject.getJSONObject("data");
						JSONArray mJSONArray = mJSONObjectData.getJSONArray("groups");
						if (mJSONArray.length() > 0) {
							mIsGroupData = true;
						}	
					}else{
						Utilities.parseJSONUserInactiveStatus(mResponseFromGroupApi, GroupSelectActivity.this);
					}
					
					JSONObject mJSONObjectCity = new JSONObject(mResponseFromCityApi);
					mIsSuccess = mJSONObjectCity.getBoolean("success");
					if(mIsSuccess){
						JSONObject mJSONObjectInner = mJSONObjectCity.getJSONObject("data");
						JSONArray mJSONArray = mJSONObjectInner.getJSONArray("cities");
						if (mJSONArray.length() > 0) {
							mIsCityData = true;
						}	
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					mIsGroupData = false;
				}
			}

			if (mIsGroupData && mIsCityData) {
				if(isParseFromJSON){
					parseJSONFromGroupApi(mResponseFromGroupApi, mResponseFromCityApi);
				}else{
					syncGroupWithDB(mResponseFromGroupApi, mResponseFromCityApi);
					Utilities.parseJSONUserInfo(mResponseFromGroupApi);
				}
				addDistinctGroupInDb();
				setGridViewWithData();
			}

		}
	}

	private void setUniversalImageLoader() {
		mImageLoader = ImageLoader.getInstance();
		mImageLoader.init(ImageLoaderConfiguration.createDefault(this));
	}
	
	/*
	 * Ads Implementation
	 */
	
	private void loadAds() {
		if(!BuildVars.DEBUG_VERSION){
			try{
				final ArrayList<Banner> mArrayListBanner= retrieveArrayListFromPreferencesUsingGSON();
				if (mArrayListBanner != null && mArrayListBanner.size() > 0) {
					/*if (System.currentTimeMillis() > Utilities
							.getMilliSecondsFromData(Integer.parseInt(mArrayListBanner.get(1).getBannerStartDate().substring(0, 4)),
									Integer.parseInt(mArrayListBanner.get(1).getBannerStartDate().substring(5, 7)),
									Integer.parseInt( mArrayListBanner.get(1).getBannerStartDate().substring(8, 10)))
							&&
							System.currentTimeMillis() < Utilities
							.getMilliSecondsFromData(Integer.parseInt(mArrayListBanner.get(1).getBannerEndDate().substring(0, 4)),
									Integer.parseInt(mArrayListBanner.get(1).getBannerEndDate().substring(5, 7)),
									Integer.parseInt( mArrayListBanner.get(1).getBannerEndDate().substring(8, 10))))*/
//						if(Utilities.isToShowBannerAds(Utilities.getSystemDateYYYYMMDD(), mArrayListBanner.get(1).getBannerStartDate(), mArrayListBanner.get(1).getBannerEndDate()))
					if (mArrayListBanner.get(1).isBannerIsAdsOn())
						{
//						mImageLoader.displayImage(mArrayListBanner.get(1).getBannerImage(), mAdsImageView);
						if(new File(AppConstants.ADS_DIRECTORY_PATH+Utilities.getFileNameFromPath(mArrayListBanner.get(1).getBannerImage())).exists()){
							mAdsImageView.setImageBitmap(BitmapFactory.decodeFile(AppConstants.ADS_DIRECTORY_PATH+Utilities.getFileNameFromPath(mArrayListBanner.get(1).getBannerImage())));
						}else{
							mImageLoader.displayImage(mArrayListBanner.get(1).getBannerImage(), mAdsImageView, new ImageLoadingListener() {
								
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
									Utilities.writeBitmapToSDCardAdsImages(mBitmap, Utilities.getFileNameFromPath(mArrayListBanner.get(1).getBannerImage()));
								}
								
								@Override
								public void onLoadingCancelled(String arg0, View arg1) {
									// TODO Auto-generated method stub
								}
							});
					}
					}else{
						mAdsImageView.setVisibility(View.GONE);
						mAdView.setVisibility(View.VISIBLE);
						 AdRequest adRequest = new AdRequest.Builder()
		                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
		                    .build();
						 //Start loading the ad in the background.
						 mAdView.loadAd(adRequest);
					}
				}else{
					mAdsImageView.setVisibility(View.GONE);
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
			Log.i(TAG, e.toString());
		}
	}
	
	/*
	 * Drawer Initilization
	 */
	
	private void setDrawerLayout() {
		mResources = getResources();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		
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
			drawerIntent = new Intent(GroupSelectActivity.this, ProfileEditActivity.class);
			break;
		/*case 2:
			drawerIntent = new Intent(GroupSelectActivity.this, SettingsActivity.class);
			break;*/
		case 2:
			drawerIntent = new Intent(GroupSelectActivity.this, AboutActivity.class);
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
				mDrawerTitleSubView.setText("Groups :");
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
