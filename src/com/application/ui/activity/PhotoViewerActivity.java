/**
 * 
 */
package com.application.ui.activity;

import java.io.File;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.beans.MessageObject;
import com.application.ui.view.TouchImageView;
import com.application.utils.BuildVars;
import com.digitattva.ttogs.R;
import com.flurry.android.FlurryAgent;

/**
 * @author Vikalp Patel(VikalpPatelCE)
 * 
 */
public class PhotoViewerActivity extends ActionBarActivity {

	private TouchImageView mTouchImageView;
	private Intent mIntent;
	private MessageObject messageObject;
	private TextView mActionBarTitle;
	private ImageView mActionBarBack;
	private TextView mActionBarTime;
	private static final String TAG = PhotoViewerActivity.class.getSimpleName();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setWindowFullScreen();
		setContentView(R.layout.activity_photoviewer);
		initUi();
		initCustomActionBar();
		hideActionBar();
		getIntentData();
		setUiListener();
	}

	private void setWindowFullScreen() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
	
	private void initUi(){
		mTouchImageView = (TouchImageView)findViewById(R.id.activityPhotoViewerImageView);	
	}
	
	private void initCustomActionBar(){
		mActionBarTitle = (TextView)findViewById(R.id.actionBarTitle);
		mActionBarBack = (ImageView)findViewById(R.id.actionBarDrawer);
		mActionBarTime = (TextView)findViewById(R.id.actionBarBackArrowOnRight);
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
		mIntent = getIntent();
		messageObject = mIntent.getParcelableExtra("messageObject");
		mTouchImageView.setImageURI(Uri.fromFile(new File(messageObject.getFilePath())));
		mActionBarTitle.setText(messageObject.getUserId());
		mActionBarTime.setText(messageObject.getMessageTime());
	}
	
	private void setUiListener(){
		mActionBarBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
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
