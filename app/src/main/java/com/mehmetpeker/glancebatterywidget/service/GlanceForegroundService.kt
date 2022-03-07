package com.mehmetpeker.glancebatterywidget.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.core.app.NotificationCompat
import com.mehmetpeker.glancebatterywidget.GlanceBatteryWidgetReceiver


@ExperimentalUnitApi
class GlanceForegroundService : Service() {
    private val channelId = "GlanceWidgetChannelId"
    private val serviceId = 1907
    private var glanceBatteryWidgetReceiver:GlanceBatteryWidgetReceiver? = null
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (glanceBatteryWidgetReceiver == null)
            glanceBatteryWidgetReceiver = GlanceBatteryWidgetReceiver()

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_BATTERY_CHANGED)
        }
        this.registerReceiver(glanceBatteryWidgetReceiver, filter)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotificationChannel()
        }


        val notification = NotificationCompat.Builder(this, channelId)
        notification.priority = NotificationCompat.PRIORITY_MIN
        startForeground(serviceId, notification.build())
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        glanceBatteryWidgetReceiver?.let {
            this.unregisterReceiver(glanceBatteryWidgetReceiver)
        }
        super.onDestroy()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
       val channel = NotificationChannel(
            channelId,
            channelId,
            NotificationManager.IMPORTANCE_LOW
        )
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }
}