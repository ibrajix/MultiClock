package com.ibrajix.multiclock.service

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var coroutineScope: CoroutineScope

    override fun onReceive(context: Context?, intent: Intent?) {
        coroutineScope.launch {
            handleIntents(context, intent)
        }
    }

    private fun handleIntents(context: Context?, intent: Intent?){
        when(intent?.action){

            Intent.ACTION_BOOT_COMPLETED,

            AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED
            -> {
                //reschedule all alarm by getting a list of alarms whose status is true and then for each of them, set the alarm
                startRescheduleAlarmService(context)
            }
            else -> {
                startAlarmService(context)
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

    private fun startAlarmService(context: Context?){

    }

}