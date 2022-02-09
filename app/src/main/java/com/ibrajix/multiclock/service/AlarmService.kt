package com.ibrajix.multiclock.service

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.*
import androidx.core.app.NotificationCompat
import com.ibrajix.multiclock.R
import com.ibrajix.multiclock.ui.activities.AlarmClickedActivity
import com.ibrajix.multiclock.utils.Constants.CHANNEL_ID

class AlarmService : Service() {

    private var vibrator: Vibrator? = null

    override fun onCreate() {
        super.onCreate()
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = this.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val activityIntent = Intent(this, AlarmClickedActivity::class.java)

            activityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            val pendingIntent = PendingIntent.getActivity(this, 0, activityIntent, 0)

            val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_bell)
                .setContentTitle(getString(R.string.alarm_running))
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setDefaults(NotificationCompat.DEFAULT_SOUND)
                .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .addAction(R.drawable.ic_stop_watch,getString(R.string.stop), pendingIntent)
                .setContentIntent(pendingIntent)
                .build()

            val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            val r = RingtoneManager.getRingtone(this, notification)
            r.play()


            val pattern = longArrayOf(0, 100, 1000)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
            } else {
                //deprecated in API 26
                vibrator?.vibrate(pattern, 0)
            }


            startForeground(1, notificationBuilder)

        }

        return START_STICKY

    }

    override fun onDestroy() {
        super.onDestroy()
        vibrator?.cancel()
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}