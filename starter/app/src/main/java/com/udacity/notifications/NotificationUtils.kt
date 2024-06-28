package com.udacity.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.udacity.DetailActivity
import com.udacity.DownloadStatus
import com.udacity.R

private const val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(
    fileName: String,
    status: DownloadStatus,
    applicationContext: Context
) {
    val contentIntent = Intent(applicationContext, DetailActivity::class.java)
    contentIntent
        .putExtra(Keys.FileName, fileName)
        .putExtra(Keys.Status, status)
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )


    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.download_notification_channel_id)
    )
        .setSmallIcon(R.drawable.download_image)
        .setContentTitle(
            applicationContext
                .getString(R.string.notification_title)
        )
        .setContentText(applicationContext.getString(R.string.notification_description))
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        // To support devices running API level 25 or lower
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    notify(NOTIFICATION_ID, builder.build())

}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}