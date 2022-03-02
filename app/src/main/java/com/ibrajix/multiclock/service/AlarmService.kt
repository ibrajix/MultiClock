package com.ibrajix.multiclock.service

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.*
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.EXTRA_NOTIFICATION_ID
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import androidx.core.content.ContextCompat
import com.ibrajix.multiclock.R
import com.ibrajix.multiclock.ui.activities.AlarmClickedActivity
import com.ibrajix.multiclock.utils.AlarmUtility.repeatAlarm
import com.ibrajix.multiclock.utils.Constants
import com.ibrajix.multiclock.utils.Constants.ACTION_SNOOZE
import com.ibrajix.multiclock.utils.Constants.ACTION_STOP
import com.ibrajix.multiclock.utils.Constants.ALARM_INTENT_ID
import com.ibrajix.multiclock.utils.Constants.ALARM_INTENT_TIME
import com.ibrajix.multiclock.utils.Constants.ALARM_NOTIFICATION_ID
import com.ibrajix.multiclock.utils.Constants.ALARM_SERVICE_REQUEST_CODE
import com.ibrajix.multiclock.utils.Constants.CHANNEL_ID
import com.ibrajix.multiclock.utils.UiUtility.getCurrentTime


class AlarmService : Service() {

    private var vibrator: Vibrator? = null

    private var notification: Uri? = null
    private var ringtone: Ringtone? = null

    override fun onCreate() {

        super.onCreate()

        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                this.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        //start alarm for the next day again

        val getPassedIntentForWeeklyAlarm = intent?.getBooleanExtra(Constants.IS_ALARM_WEEKLY_REPEATING, false)

        if (getPassedIntentForWeeklyAlarm == false){
             //do normal repeating of daily alarm
            repeatAlarm(alarmTime = intent.getStringExtra(ALARM_INTENT_TIME), alarmId = intent.getStringExtra(ALARM_INTENT_ID), context = this)
        }

        //activity intent - when notification is clicked
        val activityIntent = Intent(this, AlarmClickedActivity::class.java)
        activityIntent.putExtra(ALARM_INTENT_TIME, intent?.getStringExtra(ALARM_INTENT_TIME))
        activityIntent.putExtra(ALARM_INTENT_ID, intent?.getStringExtra(ALARM_INTENT_ID))
        activityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        // this pending intent is called when the notification is called
        val pendingIntent =
            PendingIntent.getActivity(
                this,
                ALARM_SERVICE_REQUEST_CODE,
                activityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )


        val snoozeIntent = Intent(this, AlarmReceiver::class.java).apply {
            action = ACTION_SNOOZE
            putExtra(EXTRA_NOTIFICATION_ID, ALARM_NOTIFICATION_ID)
            putExtra(ALARM_INTENT_TIME, getCurrentTime())
            putExtra(ALARM_INTENT_ID, intent?.getStringExtra(ALARM_INTENT_ID))
        }

        //snooze action intent, - when snooze button is called
        val snoozePendingIntent =
            PendingIntent.getBroadcast(
                this,
                ALARM_SERVICE_REQUEST_CODE,
                snoozeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        val stopIntent = Intent(this, AlarmReceiver::class.java).apply {
            action = ACTION_STOP
            putExtra(EXTRA_NOTIFICATION_ID, ALARM_NOTIFICATION_ID)
            putExtra(ALARM_INTENT_TIME, intent?.getStringExtra(ALARM_INTENT_TIME))
            putExtra(ALARM_INTENT_ID, intent?.getStringExtra(ALARM_INTENT_ID))
        }


        //stop action intent, - when stop button is called
        val stopPendingIntent =
            PendingIntent.getBroadcast(
                this,
                ALARM_SERVICE_REQUEST_CODE,
                stopIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_bell)
            .setColor(ContextCompat.getColor(this, R.color.main_pink))
            .setContentTitle(getString(R.string.alarm))
            .setAutoCancel(false)
            .setOngoing(true)
            .setVisibility(VISIBILITY_PUBLIC)
            .setAllowSystemGeneratedContextualActions(false)
            .setFullScreenIntent(pendingIntent, true)
            .setContentText(intent?.getStringExtra(ALARM_INTENT_TIME))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(0, getString(R.string.snooze), snoozePendingIntent)
            .addAction(0, getString(R.string.stop), stopPendingIntent)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .build()

        notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ringtone = RingtoneManager.getRingtone(this, notification)
        ringtone?.play()

        val pattern = longArrayOf(1500, 800, 800, 800)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(pattern, 0)
        }

        startForeground(ALARM_NOTIFICATION_ID, notificationBuilder)

        return START_STICKY

    }

    override fun onDestroy() {
        vibrator?.cancel()
        ringtone?.stop()
        super.onDestroy()
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}