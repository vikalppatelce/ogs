/**
 * 
 */
package com.application.ui.activity;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.application.beans.City;
import com.application.beans.Group;
import com.chat.ttogs.R;
import com.flurry.android.FlurryAgent;
import com.application.ui.view.Crouton;
import com.application.ui.view.Style;
import com.application.utils.AppConstants;
import com.application.utils.ApplicationLoader;
import com.application.utils.BuildVars;
import com.application.utils.ImageCompression;
import com.application.utils.RequestBuilder;
import com.application.utils.RestClient;
import com.application.utils.Utilities;

/**
 * @author Vikalp Patel(VikalpPatelCE)
 * 
 */
public class RegistrationActivity extends ActionBarActivity {

	private EditText mEditDisplayName;
	private EditText mEditMobileNumber;
	private EditText mEditEmailAddress;
	private TextView mSpinnerCity;
	private TextView mSpinnerGroup;
	
	private Button mBtnRegister;
	private TextView mTextViewSkip;
	private TextView mTextViewCall;
	private TextView mTextViewTerms;

	private ArrayList<City> mArrayListCity;
	private ArrayList<Group> mArrayListGroup;

	private ArrayAdapter mCityAdapter;
	private ArrayAdapter mGroupAdapter;
	
	private String mCityName;
	private String mGroupName;
	private String mCityId;
	private String mGroupId;
	
	private static final String TAG = RegistrationActivity.class.getSimpleName();
	
	private static final int GET_CITY = 10001;
	private static final int GET_GROUP= 10002; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setWindowFullScreen();
		setContentView(R.layout.activity_registration);
		initUi();
		hideActionBar();
		setUiListener();
	}

	private void setWindowFullScreen() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	private void initUi() {
		mEditDisplayName = (EditText) findViewById(R.id.displayName);
		mEditMobileNumber = (EditText) findViewById(R.id.mobileNumber);
		mEditEmailAddress = (EditText) findViewById(R.id.emailAddress);

		mBtnRegister = (Button) findViewById(R.id.registerButton);

//		mSpinnerCity = (Spinner) findViewById(R.id.city);
//		mSpinnerGroup = (Spinner) findViewById(R.id.group);

		mSpinnerCity = (TextView) findViewById(R.id.city);
		mSpinnerGroup = (TextView) findViewById(R.id.group);
		
		mTextViewSkip = (TextView) findViewById(R.id.skip);
		mTextViewCall = (TextView) findViewById(R.id.call);
		
		mTextViewTerms = (TextView)findViewById(R.id.terms);
		
		mTextViewTerms.setText(Html.fromHtml(getResources().getString(R.string.str_activity_registration_terms)));
		
		mTextViewCall.setText(Html.fromHtml(getResources().getString(R.string.str_activity_registration_call)));
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
		mBtnRegister.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (validateFields()) {
					if (Utilities.isInternetConnected()) {
						new AysncTaskRegistration(
								buildJSONRequestForRegistration(),true).execute();
					} else {
						Crouton.makeText(
								RegistrationActivity.this,
								getResources().getString(
										R.string.no_internet_connection),
								Style.ALERT).show();
					}
				}
			}
		});
		
		mSpinnerCity.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				Intent mIntent = new Intent(RegistrationActivity.this, RegistrationCitySelectActivity.class);
				startActivityForResult(mIntent, GET_CITY);
			}
		});
		
		mSpinnerGroup.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				Intent mIntent = new Intent(RegistrationActivity.this, RegistrationGroupSelectActivity.class);
				startActivityForResult(mIntent, GET_GROUP);
			}
		});
		
		mTextViewCall.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Uri number = Uri.parse("tel:+919867944455");
		        Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
		        startActivity(callIntent);	
			}
		});
		
		mTextViewSkip.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (Utilities.isInternetConnected()) {
					new AysncTaskRegistration(
							buildJSONRequestForSkip(),false).execute();
				} else {
					Crouton.makeText(
							RegistrationActivity.this,
							getResources().getString(
									R.string.no_internet_connection),
							Style.ALERT).show();
				}
			}
		});
		
		
		mTextViewTerms.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent mIntent = new Intent(RegistrationActivity.this, TermsAndConditionActivity.class);
				startActivity(mIntent);
			}
		});
	}

	private void getSpinnerData() {
		if (Utilities.isInternetConnected()) {
			new AysncTaskCityGroups(buildJSONRequestForRegistration())
					.execute();
		} else {
			Crouton.makeText(RegistrationActivity.this,
					getResources().getString(R.string.no_internet_connection),
					Style.ALERT).show();
		}
	}

	private boolean validateFields() {
		if (TextUtils.isEmpty(mEditDisplayName.getText().toString())) {
			Crouton.makeText(RegistrationActivity.this,
					"Please Enter Valid Display Name", Style.ALERT).show();
			return false;
		}
		
		if (!TextUtils.isEmpty(mEditEmailAddress.getText().toString())) {
			if (!android.util.Patterns.EMAIL_ADDRESS.matcher(
					mEditEmailAddress.getText().toString()).matches()) {
				Crouton.makeText(RegistrationActivity.this,
						"Please Enter Valid Email Address", Style.ALERT).show();
				return false;
			}
		}

		if (TextUtils.isEmpty(mEditMobileNumber.getText().toString())) {
			Crouton.makeText(RegistrationActivity.this,
					"Please Enter Valid Mobile Number", Style.ALERT).show();
			return false;
		}
		
		if (TextUtils.isEmpty(mCityId)) {
			Crouton.makeText(RegistrationActivity.this,
					"Please Select City", Style.ALERT).show();
			return false;
		}
		
		if (TextUtils.isEmpty(mGroupId)) {
			Crouton.makeText(RegistrationActivity.this,
					"Please Select Group", Style.ALERT).show();
			return false;
		}
		

		if (!TextUtils.isEmpty(mEditMobileNumber.getText().toString())) {
			Pattern p = Pattern.compile("[0-9]+");
			Matcher matcher = p.matcher(mEditMobileNumber.getText().toString());
			if (!matcher.matches()) {
				Crouton.makeText(RegistrationActivity.this,
						"Please Enter Valid Mobile Number", Style.ALERT).show();
				return false;
			}
			
			if(mEditMobileNumber.getText().toString().length() != 10){
				Crouton.makeText(RegistrationActivity.this,
						"Please Enter 10 Digit Valid Mobile Number", Style.ALERT).show();
				return false;
			}
		}
		
		return true;
	}

	private JSONObject buildJSONRequestForRegistration() {
		return RequestBuilder.getPostRegistrationData(mEditDisplayName
				.getText().toString(), mEditEmailAddress.getText().toString(),
				Utilities.getDeviceId(), Utilities.getDeviceName(), Utilities
						.getSDKVersion(), Utilities.getApplicationVersion(),
				mCityId, mGroupId, mEditMobileNumber
						.getText().toString());
	}
	
	private JSONObject buildJSONRequestForSkip(){
		return RequestBuilder.getPostRegistrationSkipData();
	}
	

	private void onSuccessfulRegistration(final String mResponseFromApi) {
		setDefaultValueToPreferences();
		parseJSONFromApi(mResponseFromApi);
		ApplicationLoader.getPreferences().setUserRegistrationTime(String.valueOf(System.currentTimeMillis()));
		Utilities.searchQueue.postRunnable(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				NTPUDPClient timeClient = new NTPUDPClient();
			    InetAddress inetAddress;
				try {
						inetAddress = InetAddress.getByName(AppConstants.API.TIME_SERVER);
						TimeInfo timeInfo = timeClient.getTime(inetAddress);
						ApplicationLoader.getPreferences().setUserRegistrationTime(String.valueOf(timeInfo.getMessage().getTransmitTimeStamp().getTime()));
				}catch(Exception e){
				}
				Log.i(TAG, "userid : "+ApplicationLoader.getPreferences().getUserId());
				if(!TextUtils.isEmpty(ApplicationLoader.getPreferences().getUserId())){
					Intent mIntent = new Intent(RegistrationActivity.this,GroupSelectActivity.class);
					startActivity(mIntent);
					finish();	
				}
			}
		});
	}
	
	private void setDefaultValueToPreferences(){
		ApplicationLoader.getPreferences().setDisplayName(mEditDisplayName.getText().toString());
		ApplicationLoader.getPreferences().setDisplayName(mEditMobileNumber.getText().toString());
		ApplicationLoader.getPreferences().setDisplayName(mEditEmailAddress.getText().toString());
	}
	
	private void parseJSONFromApi(String mResponseFromApi){
		try{
			Log.i(TAG, mResponseFromApi);
			JSONObject mJSONObject = new JSONObject(mResponseFromApi);
			ApplicationLoader.getPreferences().setJabberId(mJSONObject.getString("jid"));
			ApplicationLoader.getPreferences().setUserId(mJSONObject.getString("user_id"));
			ApplicationLoader.getPreferences().setStartTime(mJSONObject.getString("start_date"));
			ApplicationLoader.getPreferences().setEndTime(mJSONObject.getString("end_date"));
			ApplicationLoader.getPreferences().setProfilePicPath(mJSONObject.getString("user_image_path"));
			ApplicationLoader.getPreferences().setUserActiveStatus(mJSONObject.getString("status"));
			ApplicationLoader.getPreferences().setUserNumber(mJSONObject.getString("mobile_no"));
			ApplicationLoader.getPreferences().setDisplayName(mJSONObject.getString("display_name"));
			ApplicationLoader.getPreferences().setUserCity(mJSONObject.getString("city"));
			ApplicationLoader.getPreferences().setUserCityId(mCityId);
			ApplicationLoader.getPreferences().setUserGroupId(mGroupId);
			ApplicationLoader.getPreferences().setUserEmailId(mJSONObject.getString("email"));
			ApplicationLoader.getPreferences().setUserService(mJSONObject.getString("service"));
		}catch(Exception e){
		}
	}

	public class AysncTaskRegistration extends AsyncTask<Void, Void, Void> {
		private ProgressDialog mProgress;
		private boolean isSuccessfulRegistration = false;
		private JSONObject mJSONObjectToSend;
		private String mErrorMessage;
		private String mResponseFromApi;
		private boolean isFromRegister;

		public AysncTaskRegistration(JSONObject mJSONObjectToSend, boolean isFromRegister) {
			super();
			// TODO Auto-generated constructor stub
			this.mJSONObjectToSend = mJSONObjectToSend;
			this.isFromRegister = isFromRegister;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			mProgress = new ProgressDialog(RegistrationActivity.this);
			if(isFromRegister){
				mProgress.setMessage("Registration");	
			}else{
				mProgress.setMessage("Please wait");
			}
			mProgress.show();
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
						AppConstants.API.URL,
						mJSONObjectToSend);
				JSONObject mJSONObject = new JSONObject(mResponseFromApi);
				isSuccessfulRegistration = mJSONObject.getBoolean("success");
				if(!isSuccessfulRegistration){
					mErrorMessage = mJSONObject.getString("error");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch(Exception e){
				e.printStackTrace();
				try {
					mErrorMessage = RequestBuilder.getPostFailMessage().getString("error");
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (mProgress != null) {
				mProgress.dismiss();
			}
			
			if(BuildVars.DEBUG_VERSION){
				mResponseFromApi = Utilities.readFile("apiRegistration.json");
				JSONObject mJSONObject;
				try {
					mJSONObject = new JSONObject(mResponseFromApi);
					isSuccessfulRegistration = mJSONObject.getBoolean("success");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(isSuccessfulRegistration){
				onSuccessfulRegistration(mResponseFromApi);
			}else{
				if(isFromRegister){
					if(!TextUtils.isEmpty(mErrorMessage)){
						Crouton.makeText(RegistrationActivity.this, mErrorMessage, Style.ALERT).show();
					}else{
						Crouton.makeText(RegistrationActivity.this, "Please try again!", Style.ALERT).show();
					}						
				}else{
					Crouton.makeText(RegistrationActivity.this, "Please Register first!", Style.ALERT).show();
				}
			}
		}
	}

	/*private void onSuccessfulCityGroup() {
		List<String> mListCity = getAllCityName();
		String[] mArrCity = mListCity.toArray(new String[mListCity.size()]);
		mCityAdapter = new CustomArrayAdapter<CharSequence>(
				RegistrationActivity.this, mArrCity);
		mCityAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinnerCity.setAdapter(mCityAdapter);

		List<String> mListGroup = getAllGroupName();
		String[] mArrGroup = mListCity.toArray(new String[mListGroup.size()]);
		mGroupAdapter = new CustomArrayAdapter<CharSequence>(
				RegistrationActivity.this, mArrGroup);
		mGroupAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinnerGroup.setAdapter(mGroupAdapter);
	}*/

	private List<String> getAllCityName() {
		List<String> mListCityName = new ArrayList<String>();
		for (int i = 0; i < mArrayListCity.size(); i++) {
			mListCityName.add(mArrayListCity.get(i).getCityName());
		}
		return mListCityName;
	}

	private List<String> getAllGroupName() {
		List<String> mListGroupName = new ArrayList<String>();
		for (int i = 0; i < mArrayListGroup.size(); i++) {
			mListGroupName.add(mArrayListGroup.get(i).getGroupName());
		}
		return mListGroupName;
	}

	private void parseJSONCityGroup(String mResponseFromApi) {
		mArrayListCity = new ArrayList<City>();
		mArrayListGroup = new ArrayList<Group>();

		if (BuildVars.DEBUG_VERSION) {
			mResponseFromApi = Utilities.readFile("apiCityGroup.json");
		} else {

		}
		try {
			JSONObject mJSONObject = new JSONObject(mResponseFromApi);
			JSONArray mJSONArrayCity = mJSONObject.getJSONArray("cities");
			JSONArray mJSONArrayGroup = mJSONObject.getJSONArray("groups");
			for (int i = 0; i < mJSONArrayCity.length(); i++) {
				City obj = new City();
				JSONObject mJSONObjectCity = mJSONArrayCity.getJSONObject(i);
				obj.setCityId(mJSONObjectCity.getString("city_id"));
				obj.setCityName(mJSONObjectCity.getString("city_name"));
				mArrayListCity.add(obj);
			}

			for (int i = 0; i < mJSONArrayGroup.length(); i++) {
				Group obj = new Group();
				JSONObject mJSONObjectGroup = mJSONArrayGroup.getJSONObject(i);
				obj.setGroupId(mJSONObjectGroup.getString("group_id"));
				obj.setGroupName(mJSONObjectGroup.getString("group_name"));
				mArrayListGroup.add(obj);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public class AysncTaskCityGroups extends AsyncTask<Void, Void, Void> {
		private ProgressDialog mProgress;
		private boolean isSuccessfulCityGroup = false;
		private JSONObject mJSONObjectToSend;

		public AysncTaskCityGroups(JSONObject mJSONObjectToSend) {
			super();
			// TODO Auto-generated constructor stub
			this.mJSONObjectToSend = mJSONObjectToSend;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			mProgress = new ProgressDialog(RegistrationActivity.this);
			mProgress.setMessage("Please wait");
			mProgress.show();
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
				String responseFromApi = RestClient.postJSON(
						AppConstants.API.URL,
						mJSONObjectToSend);
				parseJSONCityGroup(responseFromApi);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (mProgress != null) {
				mProgress.dismiss();
//				onSuccessfulCityGroup();
			}
		}
	}

	static class CustomArrayAdapter<T> extends ArrayAdapter<T> {
		public CustomArrayAdapter(Context ctx, T[] objects) {
			super(ctx, R.layout.layout_spinner, objects);
		}

		// other constructors

		@Override
		public TextView getView(int position, View convertView, ViewGroup parent) {
			TextView v = (TextView) super
					.getView(position, convertView, parent);
			v.setPadding(15, 15, 0, 15);
			return v;
		}

		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {
			View view = super.getView(position, convertView, parent);
			TextView text = (TextView) view.findViewById(R.id.textSpinner);
			text.setPadding(15, 15, 0, 15);
			return view;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, responseCode, intent);
		if(requestCode==GET_CITY){
			if(responseCode== Activity.RESULT_OK){
				mCityId = intent.getStringExtra("city_id");
				mCityName = intent.getStringExtra("city_name");
				updateSpinnerCityUi();
				if(BuildVars.DEBUG_VERSION){
					Log.i(TAG, intent.getStringExtra("city_id"));
					Log.i(TAG, intent.getStringExtra("city_name"));
				}
			}
		}
		
		if(requestCode==GET_GROUP){
			if(responseCode== Activity.RESULT_OK){
				mGroupId = intent.getStringExtra("group_id");
				mGroupName = intent.getStringExtra("group_name");
				updateSpinnerGroupUi();
				if(BuildVars.DEBUG_VERSION){
					Log.i(TAG, intent.getStringExtra("group_id"));
					Log.i(TAG, intent.getStringExtra("group_name"));
				}
			}
		}
	}
	
	private void updateSpinnerCityUi(){
		mSpinnerCity.setText(mCityName);
	}
	
	private void updateSpinnerGroupUi(){
		mSpinnerGroup.setText(mGroupName);
	}
	
	private void checkForRegistrationId(){
		if(TextUtils.isEmpty(ApplicationLoader.getPreferences().getRegistrationId())){
			ApplicationLoader mAppLoader = new ApplicationLoader();
			mAppLoader.registerInBackground();
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
