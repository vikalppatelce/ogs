package com.application.ui.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.application.service.FetchAddressIntentService;
import com.application.service.GPSTracker;
import com.application.ui.activity.MotherPagerActivity;
import com.application.ui.view.ButtonFloat;
import com.application.ui.view.RippleBackground;
import com.application.utils.AppConstants;
import com.application.utils.ApplicationLoader;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.chat.ttogs.R;

public class FragmentAttendance extends Fragment implements
		ConnectionCallbacks, OnConnectionFailedListener {

	private Button mBtnFetchLocation;
	private RippleBackground mRippleBackground;
	private Button mBtnFetchLocationStop;

	/*
	 * GPSTracker class
	 */
	private GPSTracker mGPSTracker;

	private double mGPSLatitude;
	private double mGPSLongitude;

	private double mNetworkLatitude;
	private double mNetworkLongitude;
	private static final String TAG = FragmentAttendance.class.getSimpleName();
	
	private Handler mHandler;
	private Runnable mRunnable;

	/*
	 * Google Api Client
	 */

	protected static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
	protected static final String LOCATION_ADDRESS_KEY = "location-address";

	// flag for GPS status
	private boolean isGPSEnabled = false;

	// flag for network status
	private boolean isNetworkEnabled = false;

	// Declaring a Location Manager
	private LocationManager locationManager;

	/**
	 * Provides the entry point to Google Play services.
	 */
	protected GoogleApiClient mGoogleApiClient;

	/**
	 * Represents a geographical location.
	 */
	protected Location mLastLocation;

	private boolean isResume = false;

	/**
	 * Tracks whether the user has requested an address. Becomes true when the
	 * user requests an address and false when the address (or an error message)
	 * is delivered. The user requests an address by pressing the Fetch Address
	 * button. This may happen before GoogleApiClient connects. This activity
	 * uses this boolean to keep track of the user's intent. If the value is
	 * true, the activity tries to fetch the address as soon as GoogleApiClient
	 * connects.
	 */
	protected boolean mAddressRequested;

	/**
	 * The formatted location address.
	 */
	protected String mAddressOutput;

	/**
	 * Receiver registered with this activity to get the response from
	 * FetchAddressIntentService.
	 */
	private AddressResultReceiver mResultReceiver;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_attendance, container,
				false);
		initUi(view);
		return view;
	}

	private void initUi(View view) {
		mBtnFetchLocation = (Button) view
				.findViewById(R.id.activity_attendance_btn_fetch_location);
		mBtnFetchLocationStop = (Button) view
				.findViewById(R.id.activity_attendance_btn_fetch_location_stop);
		mRippleBackground = (RippleBackground) view
				.findViewById(R.id.rippleBackground);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setUiEventListener();
	}
	
	private void startRippleAnimation() {
		mRippleBackground.startRippleAnimation();
	}

	private void stopRippleAnimation() {
		try{
			mRippleBackground.stopRippleAnimation();
			mRippleBackground.stopRippleAnimationForceFully();
		}catch(Exception e){
			Log.i(TAG, e.toString());
		}
	}

	private void setUiEventListener() {
		mBtnFetchLocationStop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mRippleBackground.isRippleAnimationRunning()){
					stopRippleAnimation();	
				}
			}
		});
		
		mBtnFetchLocation.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getUserLocation();
				getUserGoogleApiLocation();
				startRippleAnimation();
			}
		});
	}

	private void getUserLocation() {
		mGPSTracker = new GPSTracker(getActivity());
		// check if GPS enabled
		if (mGPSTracker.canGetLocation()) {
			mGPSLatitude = mGPSTracker.getGPSLatitude();
			mGPSLongitude = mGPSTracker.getGPSLongitude();

			mNetworkLatitude = mGPSTracker.getNetworkLatitude();
			mNetworkLongitude = mGPSTracker.getNetworkLongitude();

			mHandler = new Handler();
			
			mRunnable = new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					mBtnFetchLocationStop.performClick();
					mHandler.postDelayed(this, 3000);
				}
			};
			
			mRunnable.run();
			
			
			Toast.makeText(
					getActivity(),
					"Your GPS Location is - \nLat: " + mGPSLatitude
							+ "\nLong: " + mGPSLongitude
							+ "\nYour Network Location is - \nLat: "
							+ mNetworkLatitude + "\nLong: " + mNetworkLongitude
							+ "\n Address : "
							+ mGPSTracker.getLocationAddress(),
					Toast.LENGTH_LONG).show();
		} else {
			// can't get location
			// GPS or Network is not enabled
			// Ask user to enable GPS/network in settings
			mGPSTracker.showSettingsAlert();
		}
	}

	/*
	 * Google Api Client
	 */

	/**
	 * Builds a GoogleApiClient. Uses {@code #addApi} to request the
	 * LocationServices API.
	 */
	protected synchronized void buildGoogleApiClient() {
		mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API).build();
	}

	/**
	 * Runs when a GoogleApiClient object successfully connects.
	 */
	@Override
	public void onConnected(Bundle connectionHint) {
		// Gets the best and most recent location currently available, which may
		// be null
		// in rare cases when a location is not available.
		mLastLocation = LocationServices.FusedLocationApi
				.getLastLocation(mGoogleApiClient);
		if (mLastLocation != null) {
			// Determine whether a Geocoder is available.
			if (!Geocoder.isPresent()) {
				Toast.makeText(getActivity(), R.string.no_geocoder_available,
						Toast.LENGTH_LONG).show();
				return;
			}
			// It is possible that the user presses the button to get the
			// address before the
			// GoogleApiClient object successfully connects. In such a case,
			// mAddressRequested
			// is set to true, but no attempt is made to fetch the address (see
			// fetchAddressButtonHandler()) . Instead, we start the intent
			// service here if the
			// user has requested an address, since we now have a connection to
			// GoogleApiClient.
			if (mAddressRequested) {
				startIntentService();
			}
		}
	}

	/**
	 * Creates an intent, adds location data to it as an extra, and starts the
	 * intent service for fetching an address.
	 */
	protected void startIntentService() {
		// Create an intent for passing to the intent service responsible for
		// fetching the address.
		Intent intent = new Intent(getActivity(),
				FetchAddressIntentService.class);

		// Pass the result receiver as an extra to the service.
		intent.putExtra(AppConstants.GPS.RECEIVER, mResultReceiver);

		// Pass the location data as an extra to the service.
		intent.putExtra(AppConstants.GPS.LOCATION_DATA_EXTRA, mLastLocation);

		// Start the service. If the service isn't already running, it is
		// instantiated and started
		// (creating a process for it if needed); if it is running then it
		// remains running. The
		// service kills itself automatically once all intents are processed.
		getActivity().startService(intent);
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// Refer to the javadoc for ConnectionResult to see what error codes
		// might be returned in
		// onConnectionFailed.
		Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
				+ result.getErrorCode());
	}

	@Override
	public void onConnectionSuspended(int cause) {
		// The connection to Google Play services was lost for some reason. We
		// call connect() to
		// attempt to re-establish the connection.
		Log.i(TAG, "Connection suspended");
		mGoogleApiClient.connect();
	}

	@Override
	public void onStart() {
		super.onStart();
		buildGoogleApiClient();
		mGoogleApiClient.connect();
	}

	@Override
	public void onStop() {
		super.onStop();
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	/**
	 * Shows a toast with the given text.
	 */
	protected void showToast(String text) {
		Toast.makeText(ApplicationLoader.getApplication(), text,
				Toast.LENGTH_SHORT).show();
	}

	/**
	 * Receiver for data sent from FetchAddressIntentService.
	 */
	class AddressResultReceiver extends ResultReceiver {
		public AddressResultReceiver(Handler handler) {
			super(handler);
		}

		/**
		 * Receives data sent from FetchAddressIntentService and updates the UI
		 * in MainActivity.
		 */
		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {

			// Display the address string or an error message sent from the
			// intent service.
			mAddressOutput = resultData
					.getString(AppConstants.GPS.RESULT_DATA_KEY);
			displayAddressOutput();

			// Show a toast message if an address was found.
			if (resultCode == AppConstants.GPS.SUCCESS_RESULT) {
				showToast(getString(R.string.address_found));
			}

			// Reset. Enable the Fetch Address button and stop showing the
			// progress bar.
			mAddressRequested = false;
		}
	}

	/**
	 * Updates the address in the UI.
	 */
	protected void displayAddressOutput() {
		showToast(mAddressOutput);
	}

	private void isGPSAndNetworkEnable() {
		locationManager = (LocationManager) getActivity().getSystemService(
				android.content.Context.LOCATION_SERVICE);
		// getting GPS status
		isGPSEnabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		// getting network status
		isNetworkEnabled = locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}

	private void getUserGoogleApiLocation() {
		isGPSAndNetworkEnable();
		// We only start the service to fetch the address if GoogleApiClient is
		// connected.
		if (isGPSEnabled && isNetworkEnabled) {
			if (mGoogleApiClient.isConnected()/* && mLastLocation != null */) {
				startIntentService();
			}
			// If GoogleApiClient isn't connected, we process the user's request
			// by setting
			// mAddressRequested to true. Later, when GoogleApiClient connects,
			// we launch the service to
			// fetch the address. As far as the user is concerned, pressing the
			// Fetch Address button
			// immediately kicks off the process of getting the address.
			mAddressRequested = true;
		} else {
			showSettingsAlert(ApplicationLoader.getApplication());
		}
	}

	/**
	 * Function to show settings alert dialog
	 * */
	public void showSettingsAlert(final Context mContext) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

		// Setting Dialog Title
		alertDialog.setTitle("GPS is settings");

		// Setting Dialog Message
		alertDialog
				.setMessage("GPS is not enabled. Do you want to go to settings menu?");

		// Setting Icon to Dialog
		// alertDialog.setIcon(R.drawable.delete);

		// On pressing Settings button
		alertDialog.setPositiveButton("Settings",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(
								Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						mContext.startActivity(intent);
					}
				});

		// on pressing cancel button
		alertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		// Showing Alert Message
		alertDialog.show();
	}
}
