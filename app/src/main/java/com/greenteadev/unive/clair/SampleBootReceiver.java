package com.greenteadev.unive.clair;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.greenteadev.unive.clair.data.SyncService;
import com.greenteadev.unive.clair.util.AndroidComponentUtil;

/**
 * Created by Hitech95 on 31/01/2018.
 */

public class SampleBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            AndroidComponentUtil.toggleComponent(context, this.getClass(), false);
            context.startService(SyncService.getStartIntent(context));
        }
    }
}
