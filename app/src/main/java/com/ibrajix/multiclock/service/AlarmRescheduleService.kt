package com.ibrajix.multiclock.service

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.lifecycle.*
import com.ibrajix.multiclock.database.AlarmDao
import com.ibrajix.multiclock.ui.repository.AlarmRepository
import com.ibrajix.multiclock.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class AlarmRescheduleService : LifecycleService() {

    @Inject
    lateinit var alarmRepository: AlarmRepository

    private val alarmManager: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

    lateinit var alarmPendingIntent: PendingIntent


    override fun onCreate() {
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                alarmRepository.getAlarmWhoseStatusIsTrue.collect {
                    for (alarm in it) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

                            if (alarmManager.canScheduleExactAlarms()) {

                                val broadcastReceiverIntent = Intent(applicationContext, AlarmReceiver::class.java)
                                broadcastReceiverIntent.putExtra(Constants.ALARM_INTENT_TIME, alarm.time)
                                broadcastReceiverIntent.putExtra(Constants.ALARM_INTENT_ID, alarm.id)

                                val newPendingIntent = PendingIntent.getBroadcast(
                                    applicationContext,
                                    alarm.id?:0,
                                    broadcastReceiverIntent,
                                    PendingIntent.FLAG_MUTABLE
                                )

                                //schedule alarm
                                val alarmClockInfo = AlarmManager.AlarmClockInfo(alarm.timeInMilliSecond, null)
                                alarmManager.setAlarmClock(alarmClockInfo, newPendingIntent)

                            }
                            else {
                                //do nothing
                            }
                        }
                        else {

                            //just set alarm
                            val broadcastReceiverIntent = Intent(applicationContext, AlarmReceiver::class.java)
                            broadcastReceiverIntent.putExtra(Constants.ALARM_INTENT_TIME, alarm.time)
                            broadcastReceiverIntent.putExtra(Constants.ALARM_INTENT_ID, alarm.id)

                            val newPendingIntent = PendingIntent.getBroadcast(
                                applicationContext,
                                alarm.id?:0,
                                broadcastReceiverIntent,
                                PendingIntent.FLAG_CANCEL_CURRENT
                            )

                            //ensure the alarm fires even if the device is dozing.
                            val alarmClockInfo = AlarmManager.AlarmClockInfo(alarm.timeInMilliSecond, null)
                            alarmManager.setAlarmClock(alarmClockInfo, newPendingIntent)

                        }
                    }
                }
            }
        }

        return START_STICKY
    }


    override fun onBind(intent: Intent): IBinder? {
        return super.onBind(intent)
    }

}