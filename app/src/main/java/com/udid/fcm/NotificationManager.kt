package com.udid.fcm

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.udid.R
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class NotificationManager(private val mCtx: Context) {
    private val ID_BIG_NOTIFICATION = 98
    private val ID_SMALL_NOTIFICATION = 99
    private val CHANNEL_ID = "silvertouchcare"

    @SuppressLint("ServiceCast")
    fun showSmallNotification(title: String?, message: String?, intent: Intent?) {
        val i = System.currentTimeMillis().toInt()
        var resultPendingIntent: PendingIntent? = null
        resultPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                mCtx,ID_SMALL_NOTIFICATION * i,
                intent,
                PendingIntent.FLAG_MUTABLE
            )
        } else {
            PendingIntent.getActivity(
                mCtx,ID_SMALL_NOTIFICATION * i,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        val mBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(mCtx, CHANNEL_ID)
        val notification: Notification = mBuilder
            .setTicker(title)
            .setWhen(0)
            .setAutoCancel(true)
            .setContentIntent(resultPendingIntent)
            .setContentTitle(title)
//            .setSmallIcon(R.drawable.ic_app_logo)
//            .setLargeIcon(BitmapFactory.decodeResource(mCtx.resources, R.drawable.ic_app_logo))
            .setContentText(message)
            .setDefaults(Notification.DEFAULT_VIBRATE)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .build()
        notification.flags = notification.flags or Notification.FLAG_AUTO_CANCEL
        val notificationManager =
            mCtx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(ID_SMALL_NOTIFICATION * i,
            notification
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(CHANNEL_ID, "HRMS", importance)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.lockscreenVisibility = Notification.DEFAULT_LIGHTS
            if (notificationManager != null) mBuilder.setChannelId(CHANNEL_ID)
            mBuilder.build()
            // mBuilder.priority = NotificationManager.IMPORTANCE_DEFAULT
            notificationManager.createNotificationChannel(notificationChannel)
            notificationManager.notify(ID_SMALL_NOTIFICATION * i, notification)
        }
    }

    //The method will return Bitmap from an image URL
    private fun getBitmapFromURL(strURL: String): Bitmap? {
        return try {
            val url = URL(strURL)
            val connection =
                url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}