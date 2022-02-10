package com.ibrajix.multiclock.service

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.EXTRA_NOTIFICATION_ID
import androidx.core.content.ContextCompat
import com.ibrajix.multiclock.R
import com.ibrajix.multiclock.ui.activities.AlarmClickedActivity
import com.ibrajix.multiclock.utils.Constants
import com.ibrajix.multiclock.utils.Constants.ACTION_SNOOZE
import com.ibrajix.multiclock.utils.Constants.ALARM_INTENT_EXTRA
import com.ibrajix.multiclock.utils.Constants.ALARM_NOTIFICATION_ID
import com.ibrajix.multiclock.utils.Constants.ALARM_SERVICE_REQUEST_CODE
import com.ibrajix.multiclock.utils.Constants.CHANNEL_ID


class AlarmService : Service() {

    private var vibrator: Vibrator? = null

    override fun onCreate() {

        super.onCreate()

        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = this.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }

    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


            val activityIntent = Intent(this, AlarmClickedActivity::class.java)

            activityIntent.putExtra(ALARM_INTENT_EXTRA, intent?.getStringExtra(ALARM_INTENT_EXTRA))
            activityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK


            val snoozeIntent = Intent(this, AlarmReceiver::class.java).apply {
                action = ACTION_SNOOZE
                putExtra(EXTRA_NOTIFICATION_ID, ALARM_NOTIFICATION_ID)
                putExtra(ALARM_INTENT_EXTRA, intent?.getStringExtra(ALARM_INTENT_EXTRA))
            }

            @SuppressLint("UnspecifiedImmutableFlag")
            val snoozePendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.getBroadcast(
                    this,
                    ALARM_SERVICE_REQUEST_CODE,
                    snoozeIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            } else {
                PendingIntent.getBroadcast(
                    this,
                    ALARM_SERVICE_REQUEST_CODE,
                    snoozeIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            }


            @SuppressLint("UnspecifiedImmutableFlag")
            val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.getActivity(
                    this,
                    ALARM_SERVICE_REQUEST_CODE,
                    activityIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            } else {
                PendingIntent.getActivity(
                    this,
                    ALARM_SERVICE_REQUEST_CODE,
                    activityIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            }

            val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_bell)
                .setColor(ContextCompat.getColor(this, R.color.main_pink))
                .setContentTitle(getString(R.string.alarm))
                .setAutoCancel(false)
                .setOngoing(true)
                .setAllowSystemGeneratedContextualActions(false)
                .setFullScreenIntent(pendingIntent, true)
                .setContentText(intent?.getStringExtra(Constants.ALARM_INTENT_EXTRA))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .addAction(0, getString(R.string.snooze), snoozePendingIntent)
                .addAction(0, getString(R.string.stop), pendingIntent)
                .setContentIntent(pendingIntent)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .build()

            val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            val r = RingtoneManager.getRingtone(this, notification)
            r.play()


            val pattern = longArrayOf(1500, 800, 800, 800)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(pattern, 0)
            }

            startForeground(ALARM_NOTIFICATION_ID, notificationBuilder)

        }

        return START_STICKY

    }

    override fun onDestroy() {
        vibrator?.cancel()
        Log.e("served", "Service Stopped")
        super.onDestroy()
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}