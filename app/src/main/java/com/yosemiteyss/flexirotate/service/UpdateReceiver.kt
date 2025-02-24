package com.yosemiteyss.flexirotate.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class UpdateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            context.startService(Intent(context, FoldStateRotateService::class.java))
        }
    }
}