package com.alquran.offline.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.alquran.offline.QuranApplication
import com.alquran.offline.R
import com.alquran.offline.notification.NotificationScheduler
import com.alquran.offline.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HadithNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        val app = context.applicationContext as QuranApplication
        val repository = app.repository

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val enabled = repository.notificationEnabled.first()
                val hour = repository.notificationHour.first()
                val minute = repository.notificationMinute.first()

                if (enabled) {
                    val hadith = repository.getTodayHadith()
                    showNotification(context, hadith.id, hadith.judul, hadith.teksId, hadith.sumber)
                    // Reschedule for next day
                    NotificationScheduler.scheduleDailyNotification(context, hour, minute)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun showNotification(
        context: Context,
        hadithId: Int,
        judul: String,
        teksId: String,
        sumber: String
    ) {
        NotificationScheduler.createNotificationChannel(context)

        val clickIntent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse("alquran://hadith?id=$hadithId")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            hadithId,
            clickIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val snippet = if (teksId.length > 150) "${teksId.take(150)}..." else teksId
        val fullBody = "\"$snippet\"\n— $sumber"

        val notification = NotificationCompat.Builder(context, NotificationScheduler.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_quran)
            .setContentTitle("📖 Hadits Hari Ini: $judul")
            .setContentText(fullBody)
            .setStyle(NotificationCompat.BigTextStyle().bigText("\"$teksId\"\n\n— $sumber"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = NotificationManagerCompat.from(context)
        try {
            if (notificationManager.areNotificationsEnabled()) {
                notificationManager.notify(NotificationScheduler.NOTIFICATION_ID, notification)
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}
