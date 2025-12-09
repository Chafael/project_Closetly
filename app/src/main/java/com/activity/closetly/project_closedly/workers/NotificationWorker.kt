package com.activity.closetly.project_closedly.workers

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.activity.closetly.project_closedly.MainActivity
import com.activity.closetly.project_closedly.R
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

class NotificationWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        showNotification()
        scheduleNextNotification()
        return Result.success()
    }

    private fun scheduleNextNotification() {
        val nextWorkRequest = androidx.work.OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(30, java.util.concurrent.TimeUnit.SECONDS)
            .build()

        androidx.work.WorkManager.getInstance(applicationContext)
            .enqueue(nextWorkRequest)
    }

    private fun showNotification() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val messages = listOf(
            "No olvides armar tu outfit de hoy",
            "¿Ya elegiste qué ponerte? Tu estilo espera",
            "Hora de brillar: Organiza tu look del día",
            "Tu armario tiene infinitas posibilidades para hoy",
            "Inspírate y crea tu outfit perfecto ahora",
            "Dale vida a tu estilo hoy con un nuevo look"
        )
        val message = messages.random()

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, "closetly_reminders")
            .setSmallIcon(R.drawable.app_logo)
            .setContentTitle("Closetly")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(1001, builder.build())
        }
    }
}
