/**
 * 
 */
package com.application.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;

import com.application.ui.view.Crouton;
import com.application.ui.view.Style;
import com.application.utils.BuildVars;
import com.digitattva.ttogs.R;
import com.flurry.android.FlurryAgent;

/**
 * @author Vikalp Patel(VikalpPatelCE)
 * 
 */
public class SplashActivity extends ActionBarActivity {

	private static final String TAG = SplashActivity.class.getSimpleName();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setWindowFullScreen();
		setContentView(R.layout.activity_splash);
		initUi();
		hideActionBar();
		redirectToAdScreen();
//		showUserNumber();
	}

	private void initUi() {
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

	private void redirectToAdScreen() {
		Thread timer = new Thread() {
			public void run() {
				try {
					sleep(2000);
					intentToAdScreen();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
				}
			}
		};
		timer.start();
	}

	private void intentToAdScreen() {
			Intent mIntent = new Intent(SplashActivity.this, AdsActivity.class);
			startActivity(mIntent);
			finish();
	}

	private void setWindowFullScreen() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	public void showUserNumber(){
		TelephonyManager tMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		String mPhoneNumber = tMgr.getLine1Number();
		String mString=mPhoneNumber;
		if(!TextUtils.isEmpty(mString)){
			Crouton.makeText(SplashActivity.this, mString, Style.CONFIRM).show();	
		}else{
			Crouton.makeText(SplashActivity.this, "Your number ain't store in SIM card!", Style.CONFIRM).show();
		}
		
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
