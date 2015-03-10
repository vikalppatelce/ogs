/**
 * 
 */
package com.application.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.application.service.NotificationsService;
import com.application.service.OfflinePullMessageService;
import com.application.utils.ApplicationLoader;

/**
 * @author Vikalp Patel(VikalpPatelCE)
 * 
 */
public class NetworkReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo activeWifiInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo activeHighNetInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE_HIPRI);

		ComponentName comp = new ComponentName(context.getPackageName(),
				NotificationsService.class.getName());

		boolean isMobileConnected = activeNetInfo != null
				&& activeNetInfo.isConnectedOrConnecting();

		boolean isHighSpeedConnected = activeHighNetInfo != null
				&& activeHighNetInfo.isConnectedOrConnecting();

		boolean isWifiConnected = activeWifiInfo != null
				&& activeWifiInfo.isConnectedOrConnecting();
		if (isWifiConnected || isMobileConnected || isHighSpeedConnected) {
			if (!TextUtils.isEmpty(ApplicationLoader.getPreferences()
					.getJabberId())) {
				// context.stopService((intent.setComponent(comp)));
				// context.startService((intent.setComponent(comp)));
				
				/*
				 * XMPP : Connected Services
				 */
				
				context.stopService(new Intent(context,NotificationsService.class));
				context.startService(new Intent(context,NotificationsService.class));

				/*
				 * Receive : Offline Lost Message Packets
				 */
				context.startService(new Intent(context,OfflinePullMessageService.class));
			}
		}
	}
}
