package com.application.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.application.utils.ApplicationLoader;
import com.application.utils.FileLog;

public class NotificationsService extends Service {

	private static final String TAG = NotificationsService.class
			.getSimpleName();

	@Override
	public void onCreate() {
		FileLog.e("tmessages", "service started");
		Log.i(TAG, "servicestarted");
		ApplicationLoader.postInitApplication();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public void onDestroy() {
		FileLog.e("tmessages", "service destroyed");

		if (ApplicationLoader.getPreferences().isPushService()) {
			Intent intent = new Intent("com.application.start");
			sendBroadcast(intent);
		}
	}
}
