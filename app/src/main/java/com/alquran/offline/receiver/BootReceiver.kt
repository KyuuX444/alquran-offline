package com.alquran.offline.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.alquran.offline.QuranApplication
import com.alquran.offline.notification.NotificationScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_MY_PACKAGE_REPLACED
        ) {
            val pendingResult = goAsync()
            val app = context.applicationContext as QuranApplication
            val repository = app.repository

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val enabled = repository.notificationEnabled.first()
                    val hour = repository.notificationHour.first()
                    val minute = repository.notificationMinute.first()

                    if (enabled) {
                        NotificationScheduler.scheduleDailyNotification(context, hour, minute)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
