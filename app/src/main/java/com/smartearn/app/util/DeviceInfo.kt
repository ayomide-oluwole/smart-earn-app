package com.smartearn.app.util

import android.content.Context
import android.os.Build
import android.provider.Settings

object DeviceInfo {
    fun getDeviceId(context: Context): String {
        return Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: "unknown"
    }

    fun getDeviceModel(): String {
        return "${Build.MANUFACTURER} ${Build.MODEL}"
    }

    fun getAndroidVersion(): String {
        return Build.VERSION.RELEASE
    }
}