package com.ibrajix.multiclock.ui.repository

import com.ibrajix.multiclock.database.Alarm
import com.ibrajix.multiclock.database.AlarmDao
import com.ibrajix.multiclock.database.Database
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AlarmRepository @Inject constructor(private val alarmDao: AlarmDao) {

    suspend fun createAlarm(alarm: Alarm) = alarmDao.insertAlarm(alarm)

    val getAllAlarms : Flow<List<Alarm>> get() = alarmDao.getAllAlarms()

    fun getSingleAlarm(alarmId: Int) = alarmDao.getSingleAlarm(alarmId)

    val getAlarmWhoseStatusIsTrue : Flow<List<Alarm>> get() = alarmDao.getAlarmWhoseStatusIsTrue()

    suspend fun deleteSingleAlarm(id: Int) = alarmDao.deleteAlarm(id)

    suspend fun updateAlarmStatus(status: Boolean, alarmId: Int) =  alarmDao.updateAlarmStatus(status, alarmId)

    suspend fun updateAlarmVibrateStatus(status: Boolean, alarmId: Int) =  alarmDao.updateAlarmVibrateStatus(status, alarmId)

    suspend fun updateAlarmMonday(status: Boolean, alarmId: Int) =  alarmDao.updateAlarmMonday(status, alarmId)

    suspend fun updateAlarmTuesday(status: Boolean, alarmId: Int) =  alarmDao.updateAlarmTuesday(status, alarmId)

    suspend fun updateAlarmWednesday(status: Boolean, alarmId: Int) =  alarmDao.updateAlarmWednesday(status, alarmId)

    suspend fun updateAlarmThursday(status: Boolean, alarmId: Int) =  alarmDao.updateAlarmThursday(status, alarmId)

    suspend fun updateAlarmFriday(status: Boolean, alarmId: Int) =  alarmDao.updateAlarmFriday(status, alarmId)

    suspend fun updateAlarmSaturday(status: Boolean, alarmId: Int) =  alarmDao.updateAlarmSaturday(status, alarmId)

    suspend fun updateAlarmSunday(status: Boolean, alarmId: Int) =  alarmDao.updateAlarmSunday(status, alarmId)

}