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
import com.ibrajix.multiclock.utils.AlarmUtility.scheduleAlarm
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
                                if(alarm.status == true && alarm.weeklyRecurring == false){
                                    scheduleAlarm(alarm = alarm, context = this@AlarmRescheduleService)
                                }
                            }
                            else {
                                //do nothing or show a toast stating that scheduling has been disabled or turned off
                            }
                        }
                        else {
                            if (alarm.status == true){
                                scheduleAlarm(alarm = alarm, context = this@AlarmRescheduleService)
                            }
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