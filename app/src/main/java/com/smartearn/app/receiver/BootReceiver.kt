package com.smartearn.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.smartearn.app.service.BackgroundService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON"
        ) {
            // Restart background service after reboot
            BackgroundService.start(context)
        }
    }
}