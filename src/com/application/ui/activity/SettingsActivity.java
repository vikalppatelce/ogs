/**
 * 
 */
package com.application.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.ui.view.Crouton;
import com.application.ui.view.Style;
import com.application.utils.ApplicationLoader;
import com.application.utils.BuildVars;
import com.digitattva.ttogs.R;
import com.flurry.android.FlurryAgent;

/**
 * @author Vikalp Patel(VikalpPatelCE)
 * 
 */
public class SettingsActivity extends ActionBarActivity {

	private static final String TAG = SettingsActivity.class.getSimpleName();
	
	private TextView mActionBarTitle;
	private ImageView mActionBarBack;
	private TextView mActionBarRight;

	private CheckBox mDefaultGalleryCheckBox;
	private CheckBox mDefaultPlayerCheckBox;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setWindowFullScreen();
		setContentView(R.layout.activity_settings);
		initUi();
		initCustomActionBar();
		hideActionBar();
		setUiListener();
		setValueFromPreferences();
	}

	private void setWindowFullScreen() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	private void initUi() {
		mDefaultGalleryCheckBox = (CheckBox) findViewById(R.id.settingsGalleryCB);
		mDefaultPlayerCheckBox = (CheckBox) findViewById(R.id.settingsPlayerCB);
	}

	private void initCustomActionBar() {
		mActionBarTitle = (TextView) findViewById(R.id.actionBarTitle);
		mActionBarBack = (ImageView) findViewById(R.id.actionBarDrawer);
		mActionBarRight = (TextView) findViewById(R.id.actionBarBackArrowOnRight);
		
		mActionBarRight.setVisibility(View.GONE);
		mActionBarTitle.setText("Settings");
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
	
	
	private void setValueFromPreferences(){
		if(ApplicationLoader.getPreferences().isDefaultGallery()){
			mDefaultGalleryCheckBox.setChecked(true);
		}else{
			mDefaultGalleryCheckBox.setChecked(false);
		}
		
		if(ApplicationLoader.getPreferences().isDefaultMusicPlayer()){
			mDefaultPlayerCheckBox.setChecked(true);
		}else{
			mDefaultPlayerCheckBox.setChecked(false);
		}
	}

	private void setUiListener() {
		mDefaultGalleryCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton checkBox,
							boolean isChecked) {
						// TODO Auto-generated method stub
						ApplicationLoader.getPreferences().isDefaultGallery(
								isChecked);
						showUpdatePreferences();
					}
				});

		mDefaultPlayerCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						ApplicationLoader.getPreferences()
								.isDefaultMusicPlayer(isChecked);
						showUpdatePreferences();
					}
				});
		
		mActionBarBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
			finish();	
			}
		});
	}
	
	
	private void showUpdatePreferences(){
		Crouton.makeText(SettingsActivity.this, "Preferences has been saved!", Style.CONFIRM).show();
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
