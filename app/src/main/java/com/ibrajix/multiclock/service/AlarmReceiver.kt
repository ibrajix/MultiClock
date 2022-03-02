package com.ibrajix.multiclock.service

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.ibrajix.multiclock.utils.Constants.ACTION_SNOOZE
import com.ibrajix.multiclock.utils.Constants.ACTION_STOP
import com.ibrajix.multiclock.utils.Constants.ALARM_INTENT_ID
import com.ibrajix.multiclock.utils.Constants.ALARM_INTENT_TIME
import com.ibrajix.multiclock.utils.Constants.IS_ALARM_WEEKLY_REPEATING
import java.util.*

class AlarmReceiver : BroadcastReceiver() {

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
        intentService.putExtra(IS_ALARM_WEEKLY_REPEATING, passedIntent?.getBooleanExtra(IS_ALARM_WEEKLY_REPEATING, false))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context?.startForegroundService(intentService)
        } else {
            context?.startService(intentService)
        }
    }

    private fun snoozeAlarm(context: Context?, intent: Intent?){
        snoozeAlarm(context, intent)
    }


   /* private fun alarmIsToday(intent: Intent): Boolean {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        val today = calendar[Calendar.DAY_OF_WEEK]
        when (today) {
            Calendar.MONDAY -> {
                return intent.getBooleanExtra(
                    AlarmBroadcastReceiver.MONDAY,
                    false
                )
            }
            Calendar.TUESDAY -> {
                return intent.getBooleanExtra(
                     AlarmBroadcastReceiver.TUESDAY,
                    false
                )
            }
            Calendar.WEDNESDAY -> {
                return intent.getBooleanExtra(
                     AlarmBroadcastReceiver.WEDNESDAY,
                    false
                )
            }
            Calendar.THURSDAY -> {
                return intent.getBooleanExtra(
                     AlarmBroadcastReceiver.THURSDAY,
                    false
                )
            }
            Calendar.FRIDAY -> {
                return intent.getBooleanExtra(
                     AlarmBroadcastReceiver.FRIDAY,
                    false
                )
            }
            Calendar.SATURDAY -> {
                return intent.getBooleanExtra(
                    AlarmBroadcastReceiver.SATURDAY,
                    false
                )
            }
            Calendar.SUNDAY -> {
                return intent.getBooleanExtra(
                    AlarmBroadcastReceiver.SUNDAY,
                    false
                )
            }
        }
        return false
    }*/

}