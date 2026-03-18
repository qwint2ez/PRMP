package com.example.calculator.services

import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        println("FCM Token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        remoteMessage.notification?.let {
            showNotification(it.title, it.body)
        }
    }

    private fun showNotification(title: String?, body: String?) {
        val channelId = "calculator_notifications"
        val notificationId = System.currentTimeMillis().toInt()

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Калькулятор уведомления",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Уведомления от калькулятора"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title ?: "Калькулятор")
            .setContentText(body ?: "Новое уведомление")
            .setSmallIcon(R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }
}