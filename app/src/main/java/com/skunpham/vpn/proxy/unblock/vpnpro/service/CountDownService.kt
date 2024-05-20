package com.skunpham.vpn.proxy.unblock.vpnpro.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.*
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.skunpham.vpn.proxy.unblock.vpnpro.R
import com.skunpham.vpn.proxy.unblock.vpnpro.core.Constants

class CountDownService : Service() {
    companion object{
        private const val TAG = "CountDownService"
        const val ACTION_DISCONNECT_FROM_SERVICE = "action_disconnect"
        private const val CHANNEL_ID = "vpn_time_remain"
    }

    private var notificationManagerCompat: NotificationManagerCompat? = null
    private var handler: Handler? = null
    private var runnable: Runnable? = null
    private val notificationId = 10001


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        handler = Handler(Looper.myLooper()!!)
        runnable = object : Runnable {
            override fun run() {
                Constants.remainTime--
                updateTimeNotification(Constants.remainTime.toInt())
                handler?.postDelayed(this, 1000)
            }
        }
        handler?.postDelayed(runnable!!, 1000)
        createNotificationChannel()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelNotification()
    }

    private fun createNotificationChannel() {
        notificationManagerCompat = NotificationManagerCompat.from(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "TrustedVPN Time",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "This is channel of TrustedVPN"
            val manager = this.getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(channel)
        }
    }

    private fun updateTimeNotification(time: Int) {
        if (time <= 0) {
            sendMessageDisconnect()
            stopSelf()
            return
        }
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_home_app)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.time_remaining, convertSecondsToHMmSs(time.toLong())))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .setVibrate(null)
            .build()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            //todo truongpa
            notificationManagerCompat?.notify(notificationId, notification)
        }
    }

    override fun stopService(name: Intent?): Boolean {
        cancelNotification()
        return super.stopService(name)
    }

    private fun cancelNotification() {
        notificationManagerCompat!!.cancelAll()
        if (handler != null && runnable != null) {
            handler!!.removeCallbacks(runnable!!)
        }
    }

    private fun sendMessageDisconnect() {
        val intent = Intent(ACTION_DISCONNECT_FROM_SERVICE)
        intent.setPackage(packageName)
        applicationContext.sendBroadcast(intent)
    }

    private fun convertSecondsToHMmSs(seconds: Long): String {
        val s = seconds % 60
        val m = seconds / 60 % 60
        val h = seconds / (60 * 60) % 24
        return if (h < 10) {
            String.format("0%d:%02d:%02d", h, m, s)
        } else {
            String.format("%d:%02d:%02d", h, m, s)
        }
    }
}