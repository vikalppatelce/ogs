/**
 * 
 */
package com.application.ui.activity;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.application.ui.view.CircleImageView;
import com.application.ui.view.Crouton;
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
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

/**
 * @author Vikalp Patel(VikalpPatelCE)
 * 
 */
public class ProfileEditActivity extends ActionBarActivity {

	private ImageView mActionBarBack;
	private TextView mActionBarUpdateTv;

	private ImageView mProfileUploadIv;
	private CircleImageView mProfileIv;
	
	private ProgressBar mProgressBar;

	private TextView mUserNameTv;
	private TextView mCityTv;
	private TextView mMobileTv;
	private TextView mEmailTv;
	private TextView mEndDate;
	private TextView mJabber;
	
	private LinearLayout mCityLayout;
	private LinearLayout mMobileLayout;
	private LinearLayout mEmailLayout;
	private LinearLayout mJabberLayout;
	private LinearLayout mLayout;
	private LinearLayout mScrollLayout;

	private String mCityId;
	private String mCityName;

	private String mPicturePath;
	private String mProfilePicPath;
	private String mCompressPath;

	private Uri mUri;

	private static final int GET_CITY = 10001;
	private static final int UPLOAD_PICTURE = 10002;
	
	private ImageLoader mImageLoader;

	private static final String TAG = ProfileEditActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setWindowFullScreen();
		setContentView(R.layout.activity_profile_edit);
		initUi();
		initActionBar();
		hideActionBar();
		setUiListener();
		setActionBarListener();
		setUniversalImageLoader();
		setDataFromPreferences();
	}

	private void initUi() {
		mProfileUploadIv = (ImageView) findViewById(R.id.activityProfileImageUpload);
		mProfileIv = (CircleImageView) findViewById(R.id.activityProfileImageView);
		
		mProgressBar = (ProgressBar)findViewById(R.id.activityProfileImageViewProgress);

		mUserNameTv = (TextView) findViewById(R.id.activityProfileUserName);
		mCityTv = (TextView) findViewById(R.id.activityProfileSelectedCity);
		mMobileTv = (TextView) findViewById(R.id.activityProfileMobileNumber);
		mEmailTv = (TextView) findViewById(R.id.activityProfileEmailAddress);
		mEndDate = (TextView)findViewById(R.id.activityProfileEndDate);
		mJabber = (TextView)findViewById(R.id.activityProfileJabber);

		mCityLayout = (LinearLayout) findViewById(R.id.activityProfileCityLayout);
		mMobileLayout = (LinearLayout) findViewById(R.id.activityProfileMobileLayout);
		mEmailLayout = (LinearLayout) findViewById(R.id.activityProfileEmailLayout);
		mJabberLayout = (LinearLayout)findViewById(R.id.activityProfileJabberlayout);
		
		mLayout = (LinearLayout)findViewById(R.id.mLayout);
		mScrollLayout = (LinearLayout)findViewById(R.id.mScrollLayout);
	}

	private void initActionBar() {
		mActionBarBack = (ImageView) findViewById(R.id.actionBarDrawer);
		mActionBarUpdateTv = (TextView) findViewById(R.id.actionBarTitle);
	}

	private void setUniversalImageLoader() {
		mImageLoader = ImageLoader.getInstance();
		mImageLoader.init(ImageLoaderConfiguration.createDefault(this));
	}
	
	private void setUiListener() {
		mProfileUploadIv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(
						Intent.createChooser(intent, "Select Picture"),
						UPLOAD_PICTURE);
			}
		});
	}
	
	private void setDataFromPreferences() {
		try {
			mMobileTv.setText(ApplicationLoader.getPreferences()
					.getUserNumber());
			mEmailTv.setText(ApplicationLoader.getPreferences()
					.getUserEmailId());
			mCityTv.setText(ApplicationLoader.getPreferences().getUserCity());
			mUserNameTv.setText(ApplicationLoader.getPreferences()
					.getDisplayName());
			mEndDate.setText(ApplicationLoader.getPreferences().getEndTime());
			if(BuildVars.DEBUGGING_XMPP){
				mJabberLayout.setVisibility(View.VISIBLE);
				mJabber.setText(ApplicationLoader.getPreferences().getJabberId());
				showRoomsJoin();
			}
			
			mProfileIv.setVisibility(View.VISIBLE);
			if(new File(AppConstants.PROFILE_DIRECTORY_PATH+Utilities.getFileNameFromPath(ApplicationLoader.getPreferences().getProfilePicPath())).exists()){
				mProfileIv.setImageBitmap(BitmapFactory.decodeFile(AppConstants.PROFILE_DIRECTORY_PATH+Utilities.getFileNameFromPath(ApplicationLoader.getPreferences().getProfilePicPath())));
			}else{
				mImageLoader.displayImage(ApplicationLoader.getPreferences().getProfilePicPath(), mProfileIv, new ImageLoadingListener() {
					
					@Override
					public void onLoadingStarted(String arg0, View arg1) {
						// TODO Auto-generated method stub
						mProgressBar.setVisibility(View.VISIBLE);
					}
					
					@Override
					public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
						// TODO Auto-generated method stub
					}
					
					@Override
					public void onLoadingComplete(String arg0, View arg1, Bitmap mBitmap) {
						// TODO Auto-generated method stub
						mProfileIv.setVisibility(View.VISIBLE);
						mProgressBar.setVisibility(View.GONE);
						Utilities.writeBitmapToSDCardProfileImages(mBitmap, Utilities.getFileNameFromPath(ApplicationLoader.getPreferences().getProfilePicPath()));
					}
					
					@Override
					public void onLoadingCancelled(String arg0, View arg1) {
						// TODO Auto-generated method stub
					}
				});
			}
			
			/*mImageLoader.displayImage(ApplicationLoader.getPreferences().getProfilePicPath(), mProfileIv, new ImageLoadingListener() {
				@Override
				public void onLoadingStarted(String arg0, View arg1) {
					// TODO Auto-generated method stub
					mProgressBar.setVisibility(View.VISIBLE);
				}
				
				@Override
				public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
					// TODO Auto-generated method stub
				}
				
				@Override
				public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
					// TODO Auto-generated method stub
					mProfileIv.setVisibility(View.VISIBLE);
					mProgressBar.setVisibility(View.GONE);
				}
				
				@Override
				public void onLoadingCancelled(String arg0, View arg1) {
					// TODO Auto-generated method stub
				}
			});*/
		} catch (Exception e) {

		}
	}

	private void setActionBarListener() {
		mActionBarBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		mActionBarUpdateTv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				updateProfile();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent mIntent) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, mIntent);
		if (requestCode == GET_CITY) {
			if (resultCode == Activity.RESULT_OK) {
				mCityName = mIntent.getStringExtra("city_name");
				mCityId = mIntent.getStringExtra("city_id");
				updateCityUi();
			}
		}

		if (requestCode == UPLOAD_PICTURE) {
			if (resultCode == Activity.RESULT_OK) {
				Uri selectedImage = mIntent.getData();
				mPicturePath = Utilities.getPath(selectedImage);
				mUri = Utilities.getImagePath();
				try {
					Utilities.copy(new File(mPicturePath),
							new File(mUri.getPath()));
					Utilities.galleryAddPic(ProfileEditActivity.this, mUri);
					mProfilePicPath = mPicturePath.toString().substring(
							mPicturePath.toString().lastIndexOf("/") + 1);
					mCompressPath = ImageCompression
							.compressImage(mPicturePath);
					;
					mProfileIv.setImageBitmap(Utilities.getBitmapFromUri(Uri
							.parse("file:///" + mPicturePath)));
				} catch (IOException e) {
					Log.e(TAG, e.toString());
				}
			}
		}
	}

	private void updateCityUi() {
		mCityTv.setText(mCityName);
	}

	private void updateProfile() {
		if (!TextUtils.isEmpty(mCompressPath)) {
			if (Utilities.isInternetConnected()) {
				new AsyncUpdateProfile(ProfileEditActivity.this,
						buildJSONRequestForProfile()).execute();
			} else {
				Crouton.makeText(
						ProfileEditActivity.this,
						getResources().getString(
								R.string.no_internet_connection), Style.ALERT)
						.show();
			}
		}
	}
	
	private JSONObject buildJSONRequestForProfile(){
		return RequestBuilder.getPostProfileUpdateData(mCompressPath, Utilities.getFileExtension(mCompressPath));
	}
	

	private void setWindowFullScreen() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	private void hideActionBar() {
		try {
			if (Build.VERSION.SDK_INT < 11) {
				getSupportActionBar().hide();
			}
		} catch (Exception e) {
			Log.i(TAG, e.toString());
		}
	}

	private void showUpdateDialog(boolean isMobile, String mString) {
		final boolean isMobileFlag = isMobile;
		final Dialog mDialog = new Dialog(ProfileEditActivity.this);
		mDialog.setContentView(R.layout.dialog_profile_update);
		
		Button mCancel = (Button) mDialog
				.findViewById(R.id.dialogProfileUpdateCancel);
		Button mUpdate = (Button) mDialog
				.findViewById(R.id.dialogProfileUpdateOk);

		final EditText mEditText = (EditText) mDialog
				.findViewById(R.id.dialogProfileUpdateBox);

		if(isMobile){
			mEditText.setText(mString);
			mDialog.setTitle("Update Mobile");
		}else{
			mEditText.setText(mString);
			mDialog.setTitle("Update Email");
		}
		
		mUpdate.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(validateFields(isMobileFlag, mEditText.getText().toString())){
					updateUiAfterDialog(mEditText.getText().toString(), isMobileFlag);
					mDialog.cancel();
				}else{
					if(isMobileFlag){
						Crouton.makeText(ProfileEditActivity.this, "Please Enter Valid Mobile", Style.ALERT).show();	
					}else{
						Crouton.makeText(ProfileEditActivity.this, "Please Enter Valid Email", Style.ALERT).show();
					}
				}
			}
		});
		
		mCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mDialog.cancel();
			}
		});
		
		mDialog.show();
	}
	
	private void updateUiAfterDialog(String mString , boolean isMobileFlag){
		if(isMobileFlag){
			mMobileTv.setText(mString);
		}else{
			mEmailTv.setText(mString);
		}
	}
	
	private boolean validateFields(boolean isMobileFlag, String validateString){
		if(isMobileFlag){
			if(TextUtils.isEmpty(validateString)){
				return false;
			}
			
			if (!TextUtils.isEmpty(validateString)) {
				Pattern p = Pattern.compile("[0-9]+");
				Matcher matcher = p.matcher(validateString);
				if (!matcher.matches()) {
					return false;
				}
			}
			
		}else{
			if(TextUtils.isEmpty(validateString)){
				return false;
			}
			
			if (!TextUtils.isEmpty(validateString)) {
				if (!android.util.Patterns.EMAIL_ADDRESS.matcher(
						validateString).matches()) {
					return false;
				}

			}
		}
		return true;
	}
	
	private void parseJSONFromApi(String mResponseFromApi){
		JSONObject mJSONObject;
		try{
			mJSONObject = new JSONObject(mResponseFromApi);
			JSONObject mJSONObjectData = mJSONObject.getJSONObject("data");
			ApplicationLoader.getPreferences().setProfilePicPath(mJSONObjectData.getString("user_image_path"));
			ApplicationLoader.getPreferences().setStartTime(mJSONObjectData.getString("start_date"));
			ApplicationLoader.getPreferences().setEndTime(mJSONObjectData.getString("end_date"));
			ApplicationLoader.getPreferences().setUserActiveStatus(mJSONObjectData.getString("status"));
			ApplicationLoader.getPreferences().setUserId(mJSONObjectData.getString("user_id"));
		}catch(Exception e){
		}
	}
	
	private void showRoomsJoin(){
		Cursor mCursor = getContentResolver().query(DBConstant.Group_Columns.CONTENT_URI, null, DBConstant.Group_Columns.COLUMN_GROUP_IS_ACTIVE+"=?", new String[]{"1"}, null);
		if(mCursor!=null && mCursor.getCount() > 0){
			mCursor.moveToFirst();
			do {
				TextView mTextView = new TextView(ProfileEditActivity.this);
				mTextView.setText(mCursor.getString(mCursor.getColumnIndex(DBConstant.Group_Columns.COLUMN_GROUP_JABBER_ID)));
				mTextView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
		        ((LinearLayout) mScrollLayout).addView(mTextView);
			} while (mCursor.moveToNext());
		}
	}
	
	public class AsyncUpdateProfile extends AsyncTask<Void, Void, Void>{
		private Context mContext;
		private JSONObject mJSONObject;
		private String mResponseFromApi;
		private boolean isSuccessful = false;
		private ProgressDialog mProgressDialog;
		private String mErrorMessage;
		
		public AsyncUpdateProfile(Context mContext, JSONObject mJSONObject){
			this.mContext = mContext;
			this.mJSONObject = mJSONObject;
		}
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				mResponseFromApi = RestClient.postJSON(
						AppConstants.API.URL, mJSONObject);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				isSuccessful = false;
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
			
			if(BuildVars.DEBUG_VERSION){
				mResponseFromApi = Utilities.readFile("apiProfileEdit.json");
			}
			try {
				JSONObject mJSONObj;
				mJSONObj = new JSONObject(mResponseFromApi);
				isSuccessful = mJSONObj.getBoolean("success");
				if(!isSuccessful){
					mErrorMessage = mJSONObj.getString("message");	
				}else{
					parseJSONFromApi(mResponseFromApi);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			if(isSuccessful){
				Crouton.makeText(ProfileEditActivity.this, "Updated Successfully!", Style.CONFIRM).show();
			}else{
				Crouton.makeText(ProfileEditActivity.this, mErrorMessage, Style.ALERT).show();
			}
			
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			mProgressDialog = new ProgressDialog(mContext);
			mProgressDialog.setMessage("Updating...");
			mProgressDialog.show();
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
