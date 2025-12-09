package com.activity.closetly.project_closedly

import android.app.Application
import com.activity.closetly.project_closedly.data.remote.CloudinaryService
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class ClosetlyApplication : Application() {

    @Inject
    lateinit var cloudinaryService: CloudinaryService

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()

        cloudinaryService.initialize(
            cloudName = "dnsxwuqhw",
            apiKey = "587713743338794",
            apiSecret = "_ptH-7JCnd0Nc7-0a1rYEkyj36c"
        )
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val name = "Closetly Reminders"
            val descriptionText = "Recordatorios diarios de outfit"
            val importance = android.app.NotificationManager.IMPORTANCE_HIGH
            val channel = android.app.NotificationChannel("closetly_reminders", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: android.app.NotificationManager =
                getSystemService(android.content.Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}