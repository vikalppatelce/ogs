/**
 * 
 */
package com.application.ui.activity;

import java.io.IOException;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.application.beans.MessageObject;
import com.application.ui.view.Crouton;
import com.application.ui.view.Style;
import com.application.utils.BuildVars;
import com.application.utils.Utilities;
import com.digitattva.ttogs.R;
import com.flurry.android.FlurryAgent;

/**
 * @author Vikalp Patel(VikalpPatelCE)
 * 
 */
public class MediaPlayerActivity extends ActionBarActivity implements SeekBar.OnSeekBarChangeListener {

	private ImageButton btnPlay;
	private SeekBar songProgressBar;
	private TextView songCurrentDurationLabel;
	private TextView songTotalDurationLabel;
	// Media Player
	private MediaPlayer mp;
	// Handler to update UI timer, progress bar etc,.
	private Handler mHandler = new Handler();;
	private int seekForwardTime = 5000; // 5000 milliseconds
	private int seekBackwardTime = 5000; // 5000 milliseconds

	private Intent mIntent;
	private MessageObject messageObject;
	
	private TextView mActionBarTitle;
	private ImageView mActionBarBack;
	private TextView mActionBarTime;
	private boolean isToStop = false;
	
	private static final String TAG = MediaPlayerActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setWindowFullScreen();
		setContentView(R.layout.activity_audioplayer);
		initUi();
		hideActionBar();
		initCustomActionBar();
		getIntentData();
		initMediaComponents();
		setUiListener();
		startMediaPlayer();
	}

	private void setWindowFullScreen() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	private void initUi() {
		btnPlay = (ImageButton) findViewById(R.id.btnPlay);
		songProgressBar = (SeekBar) findViewById(R.id.songProgressBar);
		songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
		songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);
	}
	
	private void initCustomActionBar(){
		mActionBarTitle = (TextView)findViewById(R.id.actionBarTitle);
		mActionBarBack = (ImageView)findViewById(R.id.actionBarDrawer);
		mActionBarTime = (TextView)findViewById(R.id.actionBarBackArrowOnRight);
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
	

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if(mp!=null){
			try{
				if(mp.isPlaying()){
					mp.stop();
				}
			}catch(Exception e){
				Log.i(TAG, e.toString());
			}
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		onPause();
		finish();
	}
	
	public void destroyThisActivity(){
		if(mp!=null){
			try{
				if(mp.isPlaying()){
					mp.stop();
				}
			}catch(Exception e){
				Log.i(TAG, e.toString());
			}
		}
		finish();
	}

	private void getIntentData() {
		mIntent = getIntent();
		messageObject = mIntent.getParcelableExtra("messageObject");
		mActionBarTitle.setText(messageObject.getUserId());
		mActionBarTime.setText(messageObject.getMessageTime());
	}

	private void initMediaComponents() {
		mp = new MediaPlayer();
		songProgressBar.setOnSeekBarChangeListener(this);
	}

	private void setUiListener() {
		btnPlay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// check for already playing
				if (mp.isPlaying()) {
					if (mp != null) {
						mp.pause();
						// Changing button image to play button
						btnPlay.setImageResource(R.drawable.ic_audio_play);
					}
				} else {
					// Resume song
					if (mp != null) {
						mp.start();
						// Changing button image to pause button
						btnPlay.setImageResource(R.drawable.ic_audio_pause);
						playAudio();
						isToStop = true;
					}
				}

			}
		});
		
		mActionBarBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
	
	/**
	 * Function to play a song
	 * @param songIndex - index of song
	 * */
	private  void  playAudio(){
		// Play song
		try {
        	mp.reset();
			mp.setDataSource(messageObject.getFilePath());
			mp.prepare();
			mp.start();
        	// Changing Button Image to pause image
			btnPlay.setImageResource(R.drawable.ic_audio_pause);
			// set Progress bar values
			songProgressBar.setProgress(0);
			songProgressBar.setMax(100);
			// Updating progress bar
			updateProgressBar();			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Update timer on seekbar
	 * */
	public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);        
    }	
	
	/**
	 * Background Runnable thread
	 * */
	private Runnable mUpdateTimeTask = new Runnable() {
		   public void run() {
			   long totalDuration = mp.getDuration();
			   long currentDuration = mp.getCurrentPosition();
			  
			   // Displaying Total Duration time
			   songTotalDurationLabel.setText(""+Utilities.milliSecondsToTimer(totalDuration));
			   // Displaying time completed playing
			   songCurrentDurationLabel.setText(""+Utilities.milliSecondsToTimer(currentDuration));
			   
			   // Updating progress bar
			   int progress = (int)(Utilities.getProgressPercentage(currentDuration, totalDuration));
			   //Log.d("Progress", ""+progress);
			   songProgressBar.setProgress(progress);

			   if(isToStop){
				   if(songCurrentDurationLabel.getText().toString().compareToIgnoreCase(songTotalDurationLabel.getText().toString()) == 0){
//						Crouton.makeText(MediaPlayerActivity.this, "Stopped!", Style.ALERT).show();
						destroyThisActivity();
					}
			   }
			   // Running this thread after 100 milliseconds
		       mHandler.postDelayed(this, 100);
		   }
		};
		
	/**
	 * 
	 * */
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
		
	}

	/**
	 * When user starts moving the progress handler
	 * */
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// remove message Handler from updating progress bar
		mHandler.removeCallbacks(mUpdateTimeTask);
    }
	
	/**
	 * When user stops moving the progress hanlder
	 * */
	@Override
    public void onStopTrackingTouch(SeekBar seekBar) {
		mHandler.removeCallbacks(mUpdateTimeTask);
		int totalDuration = mp.getDuration();
		int currentPosition = Utilities.progressToTimer(seekBar.getProgress(), totalDuration);
		
		// forward or backward to certain seconds
		mp.seekTo(currentPosition);
		// update timer progress again
		updateProgressBar();
    }
	
	private void startMediaPlayer(){
		btnPlay.performClick();
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
