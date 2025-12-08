package com.activity.closetly.project_closedly.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.activity.closetly.project_closedly.R

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        val channelId = "outfit_reminders"
        val channel = NotificationChannel(
            channelId,
            "Recordatorios de Outfit",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager.createNotificationChannel(channel)
        
        val messages = listOf(
            "No olvides armar tu outfit de hoy",
            "Es hora de elegir tu look perfecto",
            "Prepara tu outfit para hoy",
            "Crea tu estilo del día",
            "Diseña tu outfit ideal"
        )
        
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Closetly")
            .setContentText(messages.random())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
