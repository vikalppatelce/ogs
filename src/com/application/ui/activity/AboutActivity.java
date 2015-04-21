/**
 * 
 */
package com.application.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.TextView;

import com.application.utils.BuildVars;
import com.chat.ttogs.R;
import com.flurry.android.FlurryAgent;

/**
 * @author Vikalp Patel(VikalpPatelCE)
 * 
 */
public class AboutActivity extends ActionBarActivity {

	private static final String TAG = AboutActivity.class.getSimpleName();
	
	private TextView actionBarTitle;
	
	private TextView mAboutText;
	private WebView mWebView;
	private WebView mWebView2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setWindowFullScreen();
		setContentView(R.layout.activity_about);
		hideActionBar();
		initUi();
		setUiListener();
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
	
	private void setUiListener(){
		mWebView2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Uri number = Uri.parse("tel:+919867944455");
		        Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
		        startActivity(callIntent);
			}
		});
	}
	
	private void initUi(){
//		mAboutText = (TextView)findViewById(R.id.aboutText);
		
		actionBarTitle = (TextView)findViewById(R.id.actionBarTitle);
		mWebView = (WebView)findViewById(R.id.webView);
		mWebView2 = (WebView)findViewById(R.id.webView2);
		mWebView.setBackgroundColor(0x00000000);
		mWebView2.setBackgroundColor(0x00000000);
		actionBarTitle.setText("About Us");
//		mAboutText.setText(Html.fromHtml(getResources().getString(R.string.about)));
		setWebData();
	}
	
	private void setWebData(){
		String text= "Tours and Travels Operator Guide Service (TTOGS) is a private, mobile app provided to the industry professionals to connect with each other. The service aims to improve to the quality of services provided in the industry by providing peer guidance to the users. </br></br>The brotherhood of the operators is very friendly and aims to provide assistance like leads and timely support, for the mutual benefit. You can contact us to become a member of this service.</br></br>";
		String htmlText = "<html><body style=\"text-align:justify\"><font size=\"4\" color=\"#FFFFFF\"> %s </font></body></Html>";
		
		String text1= "To advertise in the app, contact us on <br/><font size=\"4\" color=\"0000FF\"><u>+91 9867944455.<u/></font></br>";
		String htmlText1 = "<html><body style=\"text-align:justify\"><font size=\"4\" color=\"#FFFFFF\"> %s </font></body></Html>";
		
		mWebView.loadData(String.format(htmlText, text), "text/html", "utf-8");
		mWebView2.loadData(String.format(htmlText1, text1), "text/html", "utf-8");
	}

	private void setWindowFullScreen() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
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
