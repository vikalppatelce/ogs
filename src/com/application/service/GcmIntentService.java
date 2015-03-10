package com.application.service;

import org.json.JSONObject;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmIntentService extends IntentService {
    
	public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    
    static final String TAG = "GCMService";
    static final String MESSAGE_TYPE_REQUEST = "pending_request";
    static final String MESSAGE_TYPE_APPROVAL = "approval";
    String notificationContent = null;
    String notificationTitle = null;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

//        sendNotification("Received: " + extras.toString());
        Log.i(TAG, "Received: " + extras.toString());
        
        
        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) 
            {
//                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) 
            {
//                sendNotification("Deleted messages on server: " +extras.toString());
            // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) 
            {
                // This loop represents the service doing some work.
                for (int i=0; i<5; i++) {
                    Log.i(TAG, "Working... " + (i+1)+ "/5 @ " + SystemClock.elapsedRealtime());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                }
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.
                Log.i(TAG, "Received: " + extras.toString());
            }
            else if (MESSAGE_TYPE_REQUEST.equalsIgnoreCase(messageType)) 
            {
                // This loop represents the service doing some work.                
                Log.i(TAG, "Working..." + SystemClock.elapsedRealtime());
				try {
					String message = extras.getString("message");
					JSONObject msgJsonObject = new JSONObject(message);
				} 
				catch (Exception e){
				}
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.
                Log.i(TAG, "Received: " + extras.toString());
            }
            else if (MESSAGE_TYPE_APPROVAL.equalsIgnoreCase(messageType)) 
            {
                // This loop represents the service doing some work.
                
                Log.i(TAG, "Working..." + SystemClock.elapsedRealtime());
				try {
//					sendNotification("Received: " + extras.toString());
					String message = extras.getString("message");
					JSONObject msgJsonObject = new JSONObject(message);
				} 
				catch (Exception e){
				}
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());
                
                // Post notification of received message.
//                sendNotification("New Notification", notificationContent, "notification");
                Log.i(TAG, "Received: " + extras.toString());
            }
            
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        WakefulBroadcastReceiver.completeWakefulIntent(intent);
    }
}
