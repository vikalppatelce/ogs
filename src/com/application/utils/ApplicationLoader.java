/*
 * This is the source code of Telegram for Android v. 1.3.2.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013.
 */

package com.application.utils;

import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.application.error.ExceptionHandler;
import com.application.messenger.ConnectionsManager;
import com.application.service.NotificationsService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

@SuppressLint("NewApi")
public class ApplicationLoader extends Application {
	private GoogleCloudMessaging gcm;
	private AtomicInteger msgId = new AtomicInteger();
	private String regid;
	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	public static volatile Context applicationContext = null;
	public static volatile Handler applicationHandler = null;
	public static ApplicationLoader applicationLoader;

	public static SharedPreferences sharedPreferences;
	public static AppPreferences preferences;

	private RequestQueue mRequestQueue;
	private static volatile boolean applicationInited = false;
	public static volatile boolean isScreenOn = false;

	public static final String TAG = ApplicationLoader.class.getSimpleName();

	public static void postInitApplication() {
		// if (applicationInited) {
		// return;
		// }
		// applicationInited = true;
		// UserConfig.loadConfig();
		ConnectionsManager.getInstance().initPushConnection();
		// ApplicationLoader app =
		// (ApplicationLoader)ApplicationLoader.applicationContext;
		// app.initPlayServices();
		// FileLog.e("tmessages", "app initied");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		applicationContext = getApplicationContext();
		applicationHandler = new Handler(applicationContext.getMainLooper());
		applicationLoader = this;
		preferences = new AppPreferences(this);
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		ExceptionHandler.register(applicationLoader);
		if (!TextUtils
				.isEmpty(ApplicationLoader.getPreferences().getJabberId())) {
			applicationContext.startService(new Intent(applicationContext,
					NotificationsService.class));
		}
		 initPlayServices();
		// setUpFacebookSampler();
		// startPushService();
	}

	public static void startPushService() {

		if (ApplicationLoader.getPreferences().isPushService()) {
			applicationContext.startService(new Intent(applicationContext,
					NotificationsService.class));

			if (android.os.Build.VERSION.SDK_INT >= 19) {
				PendingIntent pintent = PendingIntent.getService(
						applicationContext, 0, new Intent(applicationContext,
								NotificationsService.class), 0);
				AlarmManager alarm = (AlarmManager) applicationContext
						.getSystemService(Context.ALARM_SERVICE);
				alarm.cancel(pintent);
			}
		} else {
			stopPushService();
		}
	}

	public static void stopPushService() {
		applicationContext.stopService(new Intent(applicationContext,
				NotificationsService.class));

		PendingIntent pintent = PendingIntent.getService(applicationContext, 0,
				new Intent(applicationContext, NotificationsService.class), 0);
		AlarmManager alarm = (AlarmManager) applicationContext
				.getSystemService(Context.ALARM_SERVICE);
		alarm.cancel(pintent);
	}

	public static Context getApplication() {
		return applicationContext;
	}

	public static AppPreferences getPreferences() {
		return preferences;
	}

	public static SharedPreferences getSharedPreferences() {
		return sharedPreferences;
	}

	public static synchronized ApplicationLoader getInstance() {
		return applicationLoader;
	}

	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}

		return mRequestQueue;
	}

	public <T> void addToRequestQueue(Request<T> req, String tag) {
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
		getRequestQueue().add(req);
	}

	public <T> void addToRequestQueue(Request<T> req) {
		req.setTag(TAG);
		getRequestQueue().add(req);
	}

	public void cancelPendingRequests(Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}

	/*
	 * public void setUpFacebookSampler(){ Permission[] permissions = new
	 * Permission[] { Permission.PUBLIC_PROFILE, Permission.USER_FRIENDS,
	 * Permission.EMAIL };
	 * 
	 * SimpleFacebookConfiguration configuration = new
	 * SimpleFacebookConfiguration.Builder()
	 * .setAppId(ApplicationLoader.getApplication
	 * ().getResources().getString(R.string.facebook_app_id))
	 * .setNamespace("tellus") .setPermissions(permissions)
	 * .setDefaultAudience(SessionDefaultAudience.FRIENDS)
	 * .setAskForAllPermissionsAtOnce(true) .build();
	 * 
	 * SimpleFacebook.setConfiguration(configuration); }
	 */

	/*
	 * public static void startPushService() { SharedPreferences preferences =
	 * applicationContext.getSharedPreferences("Notifications", MODE_PRIVATE);
	 * 
	 * if (preferences.getBoolean("pushService", true)) {
	 * applicationContext.startService(new Intent(applicationContext,
	 * NotificationsService.class));
	 * 
	 * if (android.os.Build.VERSION.SDK_INT >= 19) { Calendar cal =
	 * Calendar.getInstance(); PendingIntent pintent =
	 * PendingIntent.getService(applicationContext, 0, new
	 * Intent(applicationContext, NotificationsService.class), 0); AlarmManager
	 * alarm = (AlarmManager)
	 * applicationContext.getSystemService(Context.ALARM_SERVICE);
	 * alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 30000,
	 * pintent); } } else { stopPushService(); } }
	 * 
	 * public static void stopPushService() { applicationContext.stopService(new
	 * Intent(applicationContext, NotificationsService.class));
	 * 
	 * PendingIntent pintent = PendingIntent.getService(applicationContext, 0,
	 * new Intent(applicationContext, NotificationsService.class), 0);
	 * AlarmManager alarm =
	 * (AlarmManager)applicationContext.getSystemService(Context.ALARM_SERVICE);
	 * alarm.cancel(pintent); }
	 * 
	 * @Override public void onConfigurationChanged(Configuration newConfig) {
	 * super.onConfigurationChanged(newConfig); try {
	 * LocaleController.getInstance().onDeviceConfigurationChange(newConfig);
	 * Utilities.checkDisplaySize(); } catch (Exception e) {
	 * e.printStackTrace(); } }
	 */

	/*
	 * public static void resetLastPauseTime() { if (lastPauseTime != 0 &&
	 * System.currentTimeMillis() - lastPauseTime > 5000) {
	 * ContactsController.getInstance().checkContacts(); } lastPauseTime = 0;
	 * ConnectionsManager.getInstance().applicationMovedToForeground(); }
	 */

	private void initPlayServices() {
		if (checkPlayServices()) {
			gcm = GoogleCloudMessaging.getInstance(this);
			regid = getRegistrationId();

			Log.i(TAG, regid);
			if (regid.length() == 0) {
				registerInBackground();
			} else {
//				sendRegistrationIdToBackend();
			}
		} else {
			Log.i(TAG, "No valid Google Play Services APK found.");
		}
	}

	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			/*
			 * if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
			 * GooglePlayServicesUtil.getErrorDialog(resultCode, this,
			 * PLAY_SERVICES_RESOLUTION_REQUEST).show(); } else { Log.i(TAG,
			 * "This device is not supported."); finish(); }
			 */
			return false;
		}
		return true;
	}

	private String getRegistrationId() {
		final SharedPreferences prefs = getGCMPreferences(applicationContext);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.length() == 0) {
			Log.i(TAG, "Registration not found.");
			return "";
		}
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
				Integer.MIN_VALUE);
		int currentVersion = getAppVersion();
		if (registeredVersion != currentVersion) {
			Log.i(TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}

	private SharedPreferences getGCMPreferences(Context context) {
		return getSharedPreferences(ApplicationLoader.class.getSimpleName(),
				Context.MODE_PRIVATE);
	}

	public static int getAppVersion() {
		try {
			PackageInfo packageInfo = applicationContext.getPackageManager()
					.getPackageInfo(applicationContext.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	public void registerInBackground() {
		AsyncTask<String, String, Boolean> task = new AsyncTask<String, String, Boolean>() {
			@Override
			protected Boolean doInBackground(String... objects) {
				if (gcm == null) {
					gcm = GoogleCloudMessaging.getInstance(applicationContext);
				}
				int count = 0;
				while (count < 100) {
					try {
						count++;
						regid = gcm.register(BuildVars.GCM_SENDER_ID);
						storeRegistrationId(applicationContext, regid);
						if (!ApplicationLoader.getSharedPreferences()
								.getBoolean("isRegisteredToServer", false)) {
//							sendRegistrationIdToBackend();
						} else {
							Log.i("AndroidToServer",
									"Already registered to server");
						}
						return true;
					} catch (Exception e) {
						FileLog.e("tmessages", e);
					}
					try {
						if (count % 20 == 0) {
							Thread.sleep(60000 * 30);
						} else {
							Thread.sleep(5000);
						}
					} catch (InterruptedException e) {
						FileLog.e("tmessages", e);
					}
				}
				return false;
			}
		};

		if (android.os.Build.VERSION.SDK_INT >= 11) {
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null,
					null);
		} else {
			task.execute(null, null, null);
		}
	}

	private void sendRegistrationIdToBackend() {
		TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String currentSIMImsi = mTelephonyMgr.getDeviceId();

		JSONObject jsonObject = getPushNotificationData(currentSIMImsi);
		Log.e("PUSH REGID SERVER---->>>>>>>>>>", jsonObject.toString());
		SendToServerTask sendTask = new SendToServerTask();
		sendTask.execute(new JSONObject[] { jsonObject });
	}

	private JSONObject getPushNotificationData(String currentSIMImsi) {
		JSONObject stringBuffer = new JSONObject();
		try {
			stringBuffer.put("registration_id", getRegistrationId());
			stringBuffer.put("app_version", getAppVersion());
			stringBuffer.put("api_key", ApplicationLoader.getPreferences()
					.getApiKey());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;

	}

	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getGCMPreferences(context);
		int appVersion = getAppVersion();
		FileLog.e("tmessages", "Saving regId on app version " + appVersion);
		Log.i(TAG, "appVersion :" + appVersion);
		Log.i(TAG, "regId :" + regId);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
		ApplicationLoader.getPreferences().setRegistrationId(regId);
	}

	private class SendToServerTask extends AsyncTask<JSONObject, Void, Void> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(JSONObject... params) {
			// TODO Auto-generated method stub
			JSONObject dataToSend = params[0];
			boolean status = false;
			try {
				String jsonStr = RestClient.postJSON(AppConstants.API.URL,
						dataToSend);

				if (jsonStr != null) {
					JSONObject jsonObject = new JSONObject(new String(jsonStr));
					status = jsonObject.getBoolean("success");
					if (status) {
						try {
							// Getting JSON Array node
							// SERVERDEMO
							final SharedPreferences prefs = getGCMPreferences(ApplicationLoader
									.getApplication());
							Log.i(TAG, "Saving regId on server ");
							SharedPreferences.Editor editor = prefs.edit();
							editor.putBoolean("isRegisteredToServer", true);
							editor.commit();

							ApplicationLoader.getPreferences()
									.setRegisteredGCMToServer(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				Log.e("PushServer", jsonStr.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}
	}
}
