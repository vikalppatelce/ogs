/**
 * 
 */
package com.application.ui.activity;

import java.io.File;
import java.util.ArrayList;

import org.apache.commons.net.io.Util;
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
import android.widget.TextView;

import com.application.beans.City;
import com.application.beans.MessageObject;
import com.application.messenger.ConnectionsManager;
import com.application.ui.view.CircleImageView;
import com.application.ui.view.CityGridAdapter;
import com.application.ui.view.Crouton;
import com.application.ui.view.DrawerArrowDrawable;
import com.application.ui.view.Style;
import com.application.utils.AppConstants;
import com.application.utils.ApplicationLoader;
import com.application.utils.BuildVars;
import com.application.utils.DBConstant;
import com.application.utils.RequestBuilder;
import com.application.utils.RestClient;
import com.application.utils.Utilities;
import com.chat.ttogs.R;
import com.flurry.android.FlurryAgent;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

/**
 * @author Vikalp Patel(VikalpPatelCE)
 * 
 */
public class CitySelectActivity extends ActionBarActivity {

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

	private LinearLayout mParentLayout;
	private ProgressBar mProgressBar;
	private Button mRetryButton;

	private ArrayList<City> mArrayListCity;
	private CityGridAdapter mAdapter;
	
	private ImageLoader mImageLoader;
	private DisplayImageOptions mDisplayImageOptions;
	
	private Intent mIntent;
	private String mIntentGroupId;
	private String mIntentGroupName;
	private String mIntentGroupJabberId;
	
	
	private static final String TAG = CitySelectActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setWindowFullScreen();
		setContentView(R.layout.activity_select_city);
		initCustomActionBar();
		initUi();
		setDrawerLayout();
		setUiListener();
		getIntentData();
		setUniversalImageLoader();

		if (!ApplicationLoader.getPreferences().getPullCityFirstTime()) { // First// Time
			if (Utilities.isInternetConnected()) {
				getDataFromApi(true);
			} else {
				mRetryButton.setVisibility(View.VISIBLE);
			}
		} else {
			addObjectsFromDb();
			setGridViewWithData();
			getDataFromApi(false);
		}
		Utilities.checkIfUserExpires(CitySelectActivity.this);
	}

	private void setWindowFullScreen() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
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
		}catch(Exception e){
			Log.i(TAG, e.toString());
		}
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unregisterReceiver(mBroadCastReceiver);
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

	private void initUi() {
		mEditSearch = (EditText) findViewById(R.id.editTextSearch);
		mGridView = (GridView) findViewById(R.id.gridViewCitySelect);
		mParentLayout = (LinearLayout) findViewById(R.id.parentLayout);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		mRetryButton = (Button) findViewById(R.id.retryButton);
	}

	private void initCustomActionBar() {
		mActionBarTitle = (TextView) findViewById(R.id.actionBarTitle);
		mActionBarBack = (ImageView) findViewById(R.id.actionBarBackArrowOnRight);
		mActionBarDrawer = (ImageView) findViewById(R.id.actionBarDrawer);

		mActionBarTitle.setText("TTOGS CITY NAME");
		hideActionBar();
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

	private void setUiListener() {
		mRetryButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(Utilities.isInternetConnected()){
					mRetryButton.setVisibility(View.GONE);
					if(!ApplicationLoader.getPreferences().getPullCityFirstTime()){ //first time
						getDataFromApi(true);
					}else{
						getDataFromApi(false);
					}
				}else{
					Crouton.makeText(
							CitySelectActivity.this,
							getResources().getString(
									R.string.no_internet_connection),
							Style.ALERT).show();
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
				if(!TextUtils.isEmpty(searchQuery.toString())){
//					applyFilter(searchQuery.toString());
					mAdapter.getFilter().filter(searchQuery.toString());
				}else{
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
			new AsyncTaskCity(CitySelectActivity.this,
					buildJSONRequestForCity(), isParseFromJSON).execute();
		} else {
			if(isParseFromJSON){
				Crouton.makeText(
						CitySelectActivity.this,
						ApplicationLoader.getApplication().getApplicationContext()
								.getResources()
								.getString(R.string.no_internet_connection),
						Style.ALERT).show();	
			}
		}
	}

	private JSONObject buildJSONRequestForCity() {
		return RequestBuilder.getPostCityByUser();
	}

	private void parseJSONFromCityApi(String mResponseFromApi) {
		try {
			mArrayListCity = new ArrayList<City>();
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
			addCityToDB();
			ApplicationLoader.getPreferences().setPullCityFirstTime(true);
			Utilities.parseJSONUserInfo(mResponseFromApi);
		} catch (Exception e) {
			Log.i(TAG, e.toString());
		}
	}
	
	private void addObjectsFromDb(){
		mArrayListCity = new ArrayList<City>();
		Cursor mCursor = getContentResolver().query(DBConstant.City_Columns.CONTENT_URI, null, null, null, null);
		if(mCursor!=null && mCursor.getCount() > 0){
			mCursor.moveToFirst();
			int intColumnCityId = mCursor.getColumnIndex(DBConstant.City_Columns.COLUMN_CITY_ID);
			int intColumtCityName =mCursor.getColumnIndex(DBConstant.City_Columns.COLUMN_CITY_NAME);
			int intColumtCityIsActive =mCursor.getColumnIndex(DBConstant.City_Columns.COLUMN_CITY_IS_ACTIVE);
			do {
				City Obj = new City();
				Obj.setCityId(mCursor.getString(intColumnCityId));
				Obj.setCityName(mCursor.getString(intColumtCityName));
				Obj.setCityIsActive(Integer.parseInt(mCursor.getString(intColumtCityIsActive)));
				addCounterToObjects(Obj);
				mArrayListCity.add(Obj);
			} while (mCursor.moveToNext());
		}
	}
	
	private void addCounterToObjects(City obj){
		try{
			obj.setCityUnreadCount("0");
			Cursor mCursorChat = getContentResolver().query(DBConstant.Chat_Columns.CONTENT_URI, null, DBConstant.Chat_Columns.COLUMN_CITY_ID+"=?", new String[]{obj.getCityId()}, null);
			if(mCursorChat!=null && mCursorChat.getCount() > 0){
				obj.setCityUnreadCount(String.valueOf(mCursorChat.getCount()));
			}
		}catch(Exception e){
			Log.i(TAG, e.toString());
			obj.setCityUnreadCount("0");
		}
	}
	
	private void syncCityWithDB(String mReponseFromApi){
		try{
			JSONObject mJSONObject = new JSONObject(mReponseFromApi);
			JSONObject mJSONObjectInner = mJSONObject.getJSONObject("data");
			JSONArray mJSONArray = mJSONObjectInner.getJSONArray("cities");
			for (int i = 0; i < mJSONArray.length(); i++) {
				City obj = new City();
				JSONObject mJSONObjectCity = mJSONArray.getJSONObject(i);
				if(!checkIfCityExistsInDB(mJSONObjectCity.getString("city_id"))){
					obj.setCityId(mJSONObjectCity.getString("city_id"));
					obj.setCityName(mJSONObjectCity.getString("city_name"));
					obj.setCityIsActive(1);
					obj.setCityUnreadCount("0");
					
					ContentValues values = new ContentValues();
					values.put(DBConstant.City_Columns.COLUMN_CITY_ID, obj.getCityId());
					values.put(DBConstant.City_Columns.COLUMN_CITY_NAME, obj.getCityName());
					
					getContentResolver().insert(DBConstant.City_Columns.CONTENT_URI, values);
					mArrayListCity.add(obj);
					
					mAdapter.notifyDataSetChanged();
				}else if(checkIfCityExistsInDB(mJSONObjectCity.getString("city_id"))){
					 //TO DO : IS ACTIVE OR NOT ACTIVE
				}
			}
			Utilities.parseJSONUserInfo(mReponseFromApi);
		}catch(Exception e){
			Log.i(TAG, e.toString());
		}
	}
	
	private boolean checkIfCityExistsInDB(String mCityId){
		Cursor mCursor = getContentResolver().query(DBConstant.City_Columns.CONTENT_URI, null, DBConstant.City_Columns.COLUMN_CITY_ID+"=?", new String[]{mCityId}, null);
		if(mCursor!=null && mCursor.getCount() > 0){
			return true;
		}else{
			return false;
		}
	}
	
	private void addCityToDB() {
		if (mArrayListCity != null && mArrayListCity.size() > 0) {
			for (int i = 0; i < mArrayListCity.size(); i++) {
				ContentValues values  = new ContentValues();
				values.put(DBConstant.City_Columns.COLUMN_CITY_ID, mArrayListCity.get(i).getCityId());
				values.put(DBConstant.City_Columns.COLUMN_CITY_NAME, mArrayListCity.get(i).getCityName());
				values.put(DBConstant.City_Columns.COLUMN_CITY_IS_ACTIVE, mArrayListCity.get(i).getCityIsActive());
				getApplication().getApplicationContext().getContentResolver().insert(DBConstant.City_Columns.CONTENT_URI, values);
			}
		}
	}
	
	private void updateUnreadCounts(){
		for (int i = 0; i < mArrayListCity.size(); i++) {
			try{
				Cursor mCursorCity = getContentResolver().query(DBConstant.Group_Columns.CONTENT_URI, null, DBConstant.Group_Columns.COLUMN_GROUP_ID +"=?", new String[]{mIntentGroupId+"_"+mArrayListCity.get(i).getCityId()}, null);
				if(mCursorCity!=null && mCursorCity.getCount()> 0){
					mCursorCity.moveToFirst();
					String groupId = mCursorCity.getString(mCursorCity.getColumnIndex(DBConstant.Group_Columns.COLUMN_GROUP_JABBER_ID));
					
					Cursor mCursor = ApplicationLoader.getApplication()
							.getContentResolver().query(DBConstant.Chat_Columns.CONTENT_URI	,null,
									DBConstant.Chat_Columns.COLUMN_CITY_ID+ "=?" + " AND " + DBConstant.Chat_Columns.COLUMN_GROUP_ID + "=?" + " AND " + DBConstant.Chat_Columns.COLUMN_ISREAD + "=?",
									new String[]{mArrayListCity.get(i)
											.getCityId(), groupId,"0"}, null);
					if(mCursor!=null && mCursor.getCount() > 0){
						mCursor.moveToFirst();
						mArrayListCity.get(i).setCityUnreadCount(String.valueOf(mCursor.getCount()));
					}else{
						mArrayListCity.get(i).setCityUnreadCount("0");
					}
				}
			}
			catch(Exception e){
				mArrayListCity.get(i).setCityUnreadCount("0");				
			}
		}
	}

	private void setGridViewWithData() {
		if (mArrayListCity != null && mArrayListCity.size() > 0) {
			mProgressBar.setVisibility(View.GONE);
			mParentLayout.setVisibility(View.VISIBLE);

			mAdapter = new CityGridAdapter(CitySelectActivity.this,
					mArrayListCity);
			mGridView.setAdapter(mAdapter);
			setGridViewListener();
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
				String CityId = view.getTag(R.id.TAG_CITY_ID).toString();
				String CityName = view.getTag(R.id.TAG_CITY_NAME).toString();
				String CityIsActive = view.getTag(R.id.TAG_CITY_ACTIVE).toString();
				Intent mIntent = new Intent(CitySelectActivity.this, GroupChatActivity.class);
				mIntent.putExtra("CITY_ID", CityId);
				mIntent.putExtra("CITY_NAME", CityName);
				mIntent.putExtra("CITY_IS_ACTIVE", CityIsActive);
				
				Cursor mCursor = getContentResolver().query(DBConstant.Group_Columns.CONTENT_URI, null, DBConstant.Group_Columns.COLUMN_GROUP_ID + "=?", new String[]{mIntentGroupId+"_"+CityId}, null);
				if(mCursor!=null && mCursor.getCount() > 0){
					mCursor.moveToFirst();
					mIntentGroupJabberId = mCursor.getString(mCursor.getColumnIndex(DBConstant.Group_Columns.COLUMN_GROUP_JABBER_ID));
					mIntent.putExtra("notificationFromGroupJabberId", mIntentGroupJabberId);	
				}
				
				mIntent.putExtra("notificationFromConnectionsManager", false);
				mIntent.putExtra("notificationFromGroupId", mIntentGroupId);
				mIntent.putExtra("notificationFromGroupName", CityName+" "+mIntentGroupName);
				mIntent.putExtra("notificationFromCityId", CityId);
				mIntent.putExtra("notificationFromCityIsActive", CityIsActive);
				
				if(!TextUtils.isEmpty(mIntentGroupJabberId)){
					startActivity(mIntent);	
				}
				
			}
		});
	}
	
	private void getIntentData(){
		try{
			mIntent = getIntent();
			mIntentGroupId = mIntent.getStringExtra("notificationFromGroupId");
			int lastUnderScore = mIntentGroupId.lastIndexOf("_");
			mIntentGroupId = mIntentGroupId.substring(0,lastUnderScore);
			mIntentGroupName = mIntent.getStringExtra("notificationFromGroupName");
			mActionBarTitle.setText(mIntentGroupName);
		}catch(Exception e){
			Log.i(TAG, e.toString());
		}
	}

	private void applyFilter(String searchQuery) {
		ArrayList<City> mListCitySearch = new ArrayList<City>();

		for (int i = 0; i < mArrayListCity.size(); i++) {
			if(mArrayListCity.get(i).getCityName().contains(searchQuery)){
				mListCitySearch.add(mArrayListCity.get(i));
			}
		}
		
		if(mListCitySearch!=null && mListCitySearch.size() > 0){
			mAdapter = new CityGridAdapter(CitySelectActivity.this, mListCitySearch);
			mGridView.setAdapter(mAdapter);
			mAdapter.notifyDataSetChanged();
		}
	}

	public class AsyncTaskCity extends AsyncTask<Void, Void, Void> {
		private Context mContext;
		private ProgressBar mProgressBar;
		private Button mRetryBtn;
		private String mResponseFromApi;
		private boolean mIsCityData = false;
		private boolean mIsSuccess = false;
		private JSONObject mJSONObjectToSend;
		private boolean isParseFromJSON;

		public AsyncTaskCity(Context mContext, JSONObject mJSONObjectToSend, boolean isParseFromJSON) {
			super();
			this.mJSONObjectToSend = mJSONObjectToSend;
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
				mResponseFromApi = RestClient.postJSON(
						AppConstants.API.URL, mJSONObjectToSend);
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

			if (!TextUtils.isEmpty(mResponseFromApi) || BuildVars.DEBUG_VERSION) {
				if (BuildVars.DEBUG_VERSION) {
					mResponseFromApi = Utilities.readFile("apiCityByUser.json");
				}
				try {
					JSONObject mJSONObject = new JSONObject(mResponseFromApi);
					mIsSuccess = mJSONObject.getBoolean("success");
					if(mIsSuccess){
						JSONObject mJSONObjectInner = mJSONObject.getJSONObject("data");
						JSONArray mJSONArray = mJSONObjectInner.getJSONArray("cities");
						if (mJSONArray.length() > 0) {
							mIsCityData = true;
						}	
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					mIsCityData = false;
				}
			}

			if (mIsCityData) {
				if(isParseFromJSON){
					parseJSONFromCityApi(mResponseFromApi);
					setGridViewWithData();
				}else{
					syncCityWithDB(mResponseFromApi);
					Utilities.parseJSONUserInfo(mResponseFromApi);
				}
			}else{
				mRetryButton.setVisibility(View.VISIBLE);	
			}
		}
	}
	
	private void setUniversalImageLoader() {
		mImageLoader = ImageLoader.getInstance();
		mImageLoader.init(ImageLoaderConfiguration.createDefault(this));
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
			drawerIntent = new Intent(CitySelectActivity.this, ProfileEditActivity.class);
			break;
		/*case 2:
			drawerIntent = new Intent(CitySelectActivity.this, SettingsActivity.class);
			break;*/
		case 2:
			drawerIntent = new Intent(CitySelectActivity.this, AboutActivity.class);
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
