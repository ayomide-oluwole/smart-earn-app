package com.smartearn.app.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.smartearn.app.MainActivity
import com.smartearn.app.R
import com.smartearn.app.SmartEarnApp
import kotlinx.coroutines.*

class BackgroundService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, SmartEarnApp.CHANNEL_ID)
            .setContentTitle("Smart Earn")
            .setContentText("Sync is active")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()

        startForeground(1, notification)

        // Periodic heartbeat to keep service alive
        serviceScope.launch {
            while (isActive) {
                delay(60_000L) // Every minute
                // Heartbeat — service is alive
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        // Restart the service
        val intent = Intent(this, BackgroundService::class.java)
        startService(intent)
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, BackgroundService::class.java)
            context.startForegroundService(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, BackgroundService::class.java)
            context.stopService(intent)
        }
    }
}