package com.ibrajix.multiclock.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.lifecycle.*
import com.ibrajix.multiclock.database.AlarmDao
import com.ibrajix.multiclock.ui.repository.AlarmRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class AlarmRescheduleService : LifecycleService() {

    @Inject
    lateinit var alarmRepository: AlarmRepository

    var alarmManager = this.getSystemService(ALARM_SERVICE) as AlarmManager

    lateinit var alarmPendingIntent: PendingIntent


    override fun onCreate() {
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                alarmRepository.getAlarmWhoseStatusIsTrue.collect {
                    for (alarm in it) {

                        alarmPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            PendingIntent.getBroadcast(
                                this@AlarmRescheduleService,
                                alarm.id?:0,
                                intent!!,
                                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                            )
                        } else {
                            PendingIntent.getBroadcast(
                                this@AlarmRescheduleService,
                                alarm.id?:0, intent!!, PendingIntent.FLAG_UPDATE_CURRENT
                            )
                        }


                        //reschedule alarm
                       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            //Ensure the alarm fires even if the device is dozing.
                            val alarmClockInfo = AlarmManager.AlarmClockInfo(alarm.timeInMilliSecond, null)
                            alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
                        } else {
                            alarmManager.setExact(
                                AlarmManager.RTC_WAKEUP,
                                alarm.timeInMilliSecond,
                                pendingIntent
                            )
                        }*/

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