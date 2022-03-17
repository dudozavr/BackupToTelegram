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
    channelName: String,
    title: String,
    body: String
) {
    setForeground(createForegroundInfo(context, channelName, title, body))
}

private const val NOTIFICATION_ID = 0
private const val NOTIFICATION_ID_STRING = "0"

private fun createForegroundInfo(
    context: Context,
    channelName: String,
    title: String,
    body: String
): ForegroundInfo {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        createChannel(context, channelName)
    }

    val notification = NotificationCompat.Builder(context, NOTIFICATION_ID_STRING)
        .setContentTitle(title)
        .setTicker(title)
        .setContentText(body)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setOngoing(true)
        .build()

    return ForegroundInfo(NOTIFICATION_ID, notification)
}

@RequiresApi(Build.VERSION_CODES.O)
private fun createChannel(context: Context, channelName: String) {
    (context.getSystemService(Context.NOTIFICATION_SERVICE) as
            NotificationManager).createNotificationChannel(
        NotificationChannel(
            NOTIFICATION_ID_STRING,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
    )
}