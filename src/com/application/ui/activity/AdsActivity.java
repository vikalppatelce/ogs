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

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.beans.Banner;
import com.application.utils.AppConstants;
import com.application.utils.ApplicationLoader;
import com.application.utils.BuildVars;
import com.application.utils.RequestBuilder;
import com.application.utils.RestClient;
import com.application.utils.Utilities;
import com.chat.ttogs.R;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.model.UrlTileProvider;
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
public class AdsActivity extends ActionBarActivity {

	private ImageView mAdsSponsorLogoIv;
	private ImageView mAdsSponsorBigIv;
	private ImageView mAdsSponsorSmallIv;
	private TextView mAdsTitleTv;

	private AdView mAdView;
	
	private ImageLoader mImageLoader;
	private DisplayImageOptions mDisplayImageOptions;

	private static final String TAG = AdsActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setWindowFullScreen();
		setContentView(R.layout.activity_ads);
		initUi();
		setUniversalImageLoader();
		hideActionBar();
		getDataFromApi();
		loadAds();
		redirectToGroupScreen();
	}

	private void initUi() {
		mAdsSponsorLogoIv = (ImageView) findViewById(R.id.actionBarAds);
		mAdsSponsorSmallIv = (ImageView) findViewById(R.id.activityAdsSmallImageView);
		mAdsSponsorBigIv = (ImageView) findViewById(R.id.activityAdsFullImageView);
		mAdsTitleTv = (TextView)findViewById(R.id.activityAdsTitle);
		mAdView = (AdView)findViewById(R.id.adView);
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

	private void hideActionBar() {
		try {
			if (Build.VERSION.SDK_INT < 11) {
				getSupportActionBar().hide();
			}
		} catch (Exception e) {
			Log.i(TAG, e.toString());
		}
	}

	private void getDataFromApi(){
		if(Utilities.isInternetConnected()){
			new AsyncGetAds(buildJSONRequestForAds()).execute();
		}
	}
	 /** Called when leaving the activity */
    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    /** Called when returning to the activity */
    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
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
    
	private void loadAds() {
		if(!BuildVars.DEBUG_VERSION){
			try{
				final ArrayList<Banner> mArrayListBanner= retrieveArrayListFromPreferencesUsingGSON();
				if (mArrayListBanner != null && mArrayListBanner.size() > 0) {
					/*if (System.currentTimeMillis() > Utilities
							.getMilliSecondsFromData(Integer.parseInt(mArrayListBanner.get(0).getBannerStartDate().substring(0, 4)),
									Integer.parseInt(mArrayListBanner.get(0).getBannerStartDate().substring(5, 7)),
									Integer.parseInt( mArrayListBanner.get(0).getBannerStartDate().substring(8, 10)))
							&&
							System.currentTimeMillis() < Utilities
							.getMilliSecondsFromData(Integer.parseInt(mArrayListBanner.get(0).getBannerEndDate().substring(0, 4)),
									Integer.parseInt(mArrayListBanner.get(0).getBannerEndDate().substring(5, 7)),
									Integer.parseInt( mArrayListBanner.get(0).getBannerEndDate().substring(8, 10))))*/
//						if(Utilities.isToShowBannerAds(Utilities.getSystemDateYYYYMMDD(), mArrayListBanner.get(0).getBannerStartDate(), mArrayListBanner.get(0).getBannerEndDate()))
					if (mArrayListBanner.get(0).isBannerIsAdsOn())
						{
//						mImageLoader.displayImage(mArrayListBanner.get(0).getBannerImage(), mAdsSponsorBigIv);
						mAdsTitleTv.setText(mArrayListBanner.get(0).getBannerCompanyName());
						if(new File(AppConstants.ADS_DIRECTORY_PATH+Utilities.getFileNameFromPath(mArrayListBanner.get(0).getBannerImage())).exists()){
							mAdsSponsorBigIv.setImageBitmap(BitmapFactory.decodeFile(AppConstants.ADS_DIRECTORY_PATH+Utilities.getFileNameFromPath(mArrayListBanner.get(0).getBannerImage())));
						}else{
							mImageLoader.displayImage(mArrayListBanner.get(0).getBannerImage(), mAdsSponsorBigIv, new ImageLoadingListener() {
								
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
									Utilities.writeBitmapToSDCardAdsImages(mBitmap, Utilities.getFileNameFromPath(mArrayListBanner.get(0).getBannerImage()));
								}
								
								@Override
								public void onLoadingCancelled(String arg0, View arg1) {
									// TODO Auto-generated method stub
								}
							});
						}
					}else{
					mAdsSponsorBigIv.setVisibility(View.GONE);
					mAdsTitleTv.setText("");
					mAdView.setVisibility(View.VISIBLE);
					 AdRequest adRequest = new AdRequest.Builder()
	                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
	                    .build();
					 //Start loading the ad in the background.
					 mAdView.loadAd(adRequest);
					}
				}else{
					mAdsSponsorBigIv.setVisibility(View.GONE);
					mAdsTitleTv.setText("");
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

	private void redirectToGroupScreen() {
		Thread timer = new Thread() {
			public void run() {
				try {
					sleep(3000);
					intentToGroupScreen();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
				}
			}
		};
		timer.start();
	}

	private void intentToGroupScreen() {
		if (TextUtils.isEmpty(ApplicationLoader.getPreferences().getJabberId())) {
			Intent mIntent = new Intent(AdsActivity.this,RegistrationActivity.class);
			startActivity(mIntent);
			finish();
		} else {
			Intent mIntent = new Intent(AdsActivity.this,GroupSelectActivity.class);
			startActivity(mIntent);
			finish();
		}
	}

	private void setWindowFullScreen() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	private JSONObject buildJSONRequestForAds(){
		return RequestBuilder.getPostAds();
	}
	
	private void parseFromJSONApi(String mResponseFromApi){
		JSONObject mJSONObject;
		ArrayList<Banner> mArrayListBanner = new ArrayList<Banner>();
		try {
			mJSONObject = new JSONObject(mResponseFromApi);
			if(mJSONObject.getBoolean("success")){
				JSONObject mJSONObjectData = mJSONObject.getJSONObject("data");
				JSONArray mJSONArray = mJSONObjectData.getJSONArray("banners");
				for (int i = 0; i < mJSONArray.length(); i++) {
					JSONObject mJSONObjBanner = mJSONArray.getJSONObject(i);
					 Banner obj = new Banner();
					 obj.setBannerId(mJSONObjBanner.getString("banner_id"));
					 obj.setBannerName(mJSONObjBanner.getString("banner_name"));
					 obj.setBannerPosition(mJSONObjBanner.getString("position_id"));
					 obj.setBannerStartDate(mJSONObjBanner.getString("start_date"));
					 obj.setBannerEndDate(mJSONObjBanner.getString("end_date"));
					 obj.setBannerImage(mJSONObjBanner.getString("filepath"));
					 obj.setBannerCompanyName(mJSONObjBanner.getString("company_name"));
					 obj.setBannerDescription(mJSONObjBanner.getString("description"));
					 obj.setBannerIsAdsOn(mJSONObjBanner.getBoolean("is_ads_on"));
					 mArrayListBanner.add(obj);
				}
				saveArrayListInPreferencesUsingGSON(mArrayListBanner);
				loadAds();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(Exception e){
			
		}
	}
	
	private void saveArrayListInPreferencesUsingGSON(ArrayList<Banner> mArrayListBanner){
		//Set the values
		Gson gson = new Gson();
		String jsonText = gson.toJson(mArrayListBanner);
		ApplicationLoader.getPreferences().setAdsListBannerObject(jsonText);
	}
	
	private ArrayList<Banner> retrieveArrayListFromPreferencesUsingGSON(){
		Gson gson = new Gson();
		String json = ApplicationLoader.getPreferences().getAdsListBannerObject();
		Type type = new TypeToken<ArrayList<Banner>>() {}.getType();
		ArrayList<Banner> mArrayListBanner = gson.fromJson(json, type);
		return mArrayListBanner;
	}
	
	public class AsyncGetAds extends AsyncTask<Void, Void, Void>{

		private String mResponseFromApi;
		private JSONObject mJSONObject;

		public AsyncGetAds(JSONObject mJSONObject){
			this.mJSONObject = mJSONObject;
		}
		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			try {
				mResponseFromApi = RestClient.postJSON(AppConstants.API.URL,
						mJSONObject);
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
			if(BuildVars.DEBUG_VERSION){
				mResponseFromApi = Utilities.readFile("apiAds.json");
			}
			parseFromJSONApi(mResponseFromApi);
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
