package com.ibrajix.multiclock.service

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import com.ibrajix.multiclock.R
import com.ibrajix.multiclock.database.Alarm
import com.ibrajix.multiclock.utils.Constants
import kotlinx.coroutines.CoroutineScope
import java.util.*
import javax.inject.Inject

class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var coroutineScope: CoroutineScope

    var broadcastCode = 0

    override fun onReceive(context: Context?, intent: Intent?) {

        when (intent?.action) {

            Intent.ACTION_BOOT_COMPLETED, AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED -> {
                startRescheduleAlarmService(context)
            }

            Constants.ACTION_SNOOZE -> {
               snoozeAlarm(context, intent)
            }

            else -> {
                startAlarmService(context, intent)
            }
        }
    }

    private fun startRescheduleAlarmService(context: Context?){
        val intent = Intent(context, AlarmRescheduleService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context?.startForegroundService(intent)
        } else {
            context?.startService(intent)
        }
    }

    private fun startAlarmService(context: Context?, passedIntent: Intent?){
        val intentService = Intent(context, AlarmService::class.java)
        intentService.putExtra(Constants.ALARM_INTENT_EXTRA, passedIntent?.getStringExtra(Constants.ALARM_INTENT_EXTRA))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context?.startForegroundService(intentService)
        } else {
            context?.startService(intentService)
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun snoozeAlarm(context: Context?, intent: Intent?){

        broadcastCode++

        val alarmManager: AlarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.add(Calendar.MINUTE, 10)


        val broadcastReceiverIntent = Intent(context, AlarmReceiver::class.java)
        broadcastReceiverIntent.putExtra(Constants.ALARM_INTENT_EXTRA, calendar.time)

        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getBroadcast(
                context,
                broadcastCode,
                broadcastReceiverIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getBroadcast(
                context,
                broadcastCode,
                broadcastReceiverIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        //reschedule alarm
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //ensure the alarm fires even if the device is dozing.
            val alarmClockInfo = AlarmManager.AlarmClockInfo(calendar.timeInMillis, null)
            alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }

        val intentService = Intent(context.applicationContext, AlarmService::class.java)
        context.applicationContext.stopService(intentService)

        Toast.makeText(context, context.getString(R.string.alarm_snoozed), Toast.LENGTH_LONG).show()

    }

}