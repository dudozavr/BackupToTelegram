package com.mindorks.framework.backuptotelegram.extentions

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import com.mindorks.framework.backuptotelegram.R

suspend fun CoroutineWorker.setForegroundWithForegroundInfo(
    context: Context,
    notificationId: Int,
    notificationIdString: String,
    channelName: String,
    title: String,
    body: String
) {
    setForeground(
        createForegroundInfo(
            context,
            notificationId,
            notificationIdString,
            channelName,
            title,
            body
        )
    )
}

private fun createForegroundInfo(
    context: Context,
    notificationId: Int,
    notificationIdString: String,
    channelName: String,
    title: String,
    body: String
): ForegroundInfo {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        createChannel(context, notificationIdString, channelName)
    }

    val notification = NotificationCompat.Builder(context, notificationIdString)
        .setContentTitle(title)
        .setTicker(title)
        .setContentText(body)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setOngoing(true)
        .build()

    return ForegroundInfo(notificationId, notification)
}

@RequiresApi(Build.VERSION_CODES.O)
private fun createChannel(context: Context, notificationIdString: String, channelName: String) {
    (context.getSystemService(Context.NOTIFICATION_SERVICE) as
            NotificationManager).createNotificationChannel(
        NotificationChannel(
            notificationIdString,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
    )
}