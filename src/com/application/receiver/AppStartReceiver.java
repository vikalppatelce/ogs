/*
 * This is the source code of Telegram for Android v. 1.4.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2014.
 */

package com.application.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.application.utils.AndroidUtilities;
import com.application.utils.ApplicationLoader;

public class AppStartReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                ApplicationLoader.startPushService();
            }
        });
    }
}
