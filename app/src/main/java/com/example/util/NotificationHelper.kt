package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object NotificationHelper {

    const val CHANNEL_MOTIVATION = "motivation_channel"
    const val CHANNEL_WARNING = "warning_channel"
    const val CHANNEL_ACHIEVEMENT = "achievement_channel"
    const val CHANNEL_CONSISTENCY = "consistency_channel"

    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channels = listOf(
                NotificationChannel(
                    CHANNEL_MOTIVATION,
                    "Daily Motivation",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Hourly motivating alerts and reminders to keep you focused."
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 300, 200, 300)
                },
                NotificationChannel(
                    CHANNEL_WARNING,
                    "Reality Warnings",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Direct, realistic evaluations of missed deadlines or low productivity."
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 500, 250, 500)
                },
                NotificationChannel(
                    CHANNEL_ACHIEVEMENT,
                    "Milestones & Progress",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Celebrations and praise for hitting your consistency goals."
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 200, 100, 200, 100, 300)
                },
                NotificationChannel(
                    CHANNEL_CONSISTENCY,
                    "Consistency Alerts",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Reminders of consecutive habits and streak statistics."
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 300, 100, 300)
                }
            )

            channels.forEach { channel ->
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    fun sendNotification(
        context: Context,
        id: Int,
        channelId: String,
        title: String,
        message: String
    ) {
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // System default fallback
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(
                when (channelId) {
                    CHANNEL_WARNING, CHANNEL_ACHIEVEMENT -> NotificationCompat.PRIORITY_HIGH
                    else -> NotificationCompat.PRIORITY_DEFAULT
                }
            )
            .setSound(soundUri)
            .setAutoCancel(true)
            .setVibrate(
                when (channelId) {
                    CHANNEL_WARNING -> longArrayOf(0, 500, 250, 500)
                    CHANNEL_ACHIEVEMENT -> longArrayOf(0, 200, 100, 200, 100, 300)
                    else -> longArrayOf(0, 300, 200, 300)
                }
            )

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(id, builder.build())
        } catch (e: SecurityException) {
            // Android 13+ permission missed
            e.printStackTrace()
        }
    }
}
