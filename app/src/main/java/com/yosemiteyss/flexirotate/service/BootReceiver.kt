package com.yosemiteyss.flexirotate.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootReceiver", "onReceive: ACTION_BOOT_COMPLETED")
            context.startService(Intent(context, FoldStateRotateService::class.java))
        }
    }
}