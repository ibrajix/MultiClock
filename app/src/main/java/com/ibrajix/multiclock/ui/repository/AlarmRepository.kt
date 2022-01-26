package com.ibrajix.multiclock.ui.repository

import com.ibrajix.multiclock.database.Alarm
import com.ibrajix.multiclock.database.AlarmDao
import com.ibrajix.multiclock.database.Database
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AlarmRepository @Inject constructor(private val alarmDao: AlarmDao) {

    suspend fun createAlarm(alarm: Alarm) = alarmDao.insertAlarm(alarm)

    val getAllAlarms : Flow<List<Alarm>> get() = alarmDao.getAllAlarms()

    suspend fun deleteSingleAlarm(id: Int) = alarmDao.deleteAlarm(id)

    suspend fun updateAlarmStatus(status: Boolean, alarmId: Int) =  alarmDao.updateAlarmStatus(status, alarmId)

}