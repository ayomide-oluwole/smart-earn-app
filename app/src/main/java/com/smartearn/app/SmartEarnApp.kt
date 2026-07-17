package com.smartearn.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class SmartEarnApp : Application() {
    companion object {
        const val CHANNEL_ID = "smart_earn_service"
        lateinit var instance: SmartEarnApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Smart Earn Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Background sync service"
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}