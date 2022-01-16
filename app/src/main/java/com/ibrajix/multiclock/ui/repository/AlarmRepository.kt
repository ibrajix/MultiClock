package com.ibrajix.multiclock.ui.repository

import com.ibrajix.multiclock.database.Alarm
import com.ibrajix.multiclock.database.AlarmDao
import com.ibrajix.multiclock.database.Database
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AlarmRepository @Inject constructor(private val alarmDatabase: Database) {

    suspend fun createAlarm(alarm: Alarm) = alarmDatabase.alarmDao().insertAlarm(alarm)

    val getAllAlarms : Flow<List<Alarm>> get() = alarmDatabase.alarmDao().getAllAlarms()

    suspend fun deleteSingleAlarm(id: Int) = alarmDatabase.alarmDao().deleteAlarm(id)

}