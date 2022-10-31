package com.example.chatapp

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class CounterNotificationService(
    private val context: Context
)
    /* { private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun showNotification(message: String?, senderName: String?){
        val activityIntent = Intent(context, MainActivity::class.java)
        val activityPendingIntent = PendingIntent.getActivity(
            context,
            1,
            activityIntent,
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )

        val incrementInent = PendingIntent.getBroadcast(
            context,
            2,
            Intent(context, CounterNotificationService::class.java),
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )

        val notification = NotificationCompat.Builder(context, COUNTER_CHANNEL_ID)
            .setSmallIcon(R.drawable.upscaledmetislogo)
            .setContentTitle(senderName)
            .setPriority(NotificationManager.IMPORTANCE_MAX)
            .setContentText(message)
            .setContentIntent(activityPendingIntent)
            .setDefaults(Notification.DEFAULT_ALL)
            .build()


        notificationManager.notify(1, notification)

    }

    companion object{
        const val COUNTER_CHANNEL_ID = "counter_channel"
    }
} */