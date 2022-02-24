package com.ibrajix.multiclock.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import com.ibrajix.multiclock.R
import com.ibrajix.multiclock.utils.Constants.ACTION_SNOOZE
import com.ibrajix.multiclock.utils.Constants.ACTION_STOP
import com.ibrajix.multiclock.utils.Constants.ALARM_INTENT_ID
import com.ibrajix.multiclock.utils.Constants.ALARM_INTENT_TIME
import com.ibrajix.multiclock.utils.Constants.SNOOZE_TIME
import kotlinx.coroutines.CoroutineScope
import java.util.*
import javax.inject.Inject

class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var coroutineScope: CoroutineScope


    override fun onReceive(context: Context?, intent: Intent?) {

        when (intent?.action) {

            Intent.ACTION_BOOT_COMPLETED, AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED -> {
                startRescheduleAlarmService(context)
            }

            ACTION_SNOOZE -> {
               snoozeAlarm(context, intent)
            }

            ACTION_STOP -> {
                stopAlarm(context, intent)
            }

            else -> {
                startAlarmService(context, intent)
            }
        }

    }

    private fun stopAlarm(context: Context?, intent: Intent?){
        val intentService = Intent(context?.applicationContext, AlarmService::class.java)
        context?.applicationContext?.stopService(intentService)
    }

    private fun startRescheduleAlarmService(context: Context?){
        //reschedule alarm
        val intent = Intent(context, AlarmRescheduleService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context?.startForegroundService(intent)
        } else {
            context?.startService(intent)
        }
    }

    private fun startAlarmService(context: Context?, passedIntent: Intent?){
        val intentService = Intent(context, AlarmService::class.java)
        intentService.putExtra(ALARM_INTENT_TIME, passedIntent?.getStringExtra(ALARM_INTENT_TIME))
        intentService.putExtra(ALARM_INTENT_ID, passedIntent?.getStringExtra(ALARM_INTENT_ID))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context?.startForegroundService(intentService)
        } else {
            context?.startService(intentService)
        }
    }

    private fun snoozeAlarm(context: Context?, intent: Intent?){

        val alarmManager: AlarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.add(Calendar.MINUTE, SNOOZE_TIME)

        val broadcastReceiverIntent = Intent(context, AlarmReceiver::class.java)
        broadcastReceiverIntent.putExtra(ALARM_INTENT_TIME, intent?.getStringExtra(ALARM_INTENT_TIME))

        val pendingIntent =
            intent?.let {
                PendingIntent.getBroadcast(
                    context,
                    it.getIntExtra(ALARM_INTENT_ID, 0),
                    broadcastReceiverIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            }


        //ensure the alarm fires even if the device is dozing.
        val alarmClockInfo = AlarmManager.AlarmClockInfo(calendar.timeInMillis, null)
        alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)

        val intentService = Intent(context.applicationContext, AlarmService::class.java)
        context.applicationContext.stopService(intentService)

        Toast.makeText(context, context.getString(R.string.alarm_snoozed), Toast.LENGTH_LONG).show()

    }

}