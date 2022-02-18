package com.ibrajix.multiclock.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: Alarm)

    @Transaction
    @Query("SELECT * FROM alarm_table ORDER BY dateCreated DESC")
    fun getAllAlarms(): Flow<List<Alarm>>

    @Update
    suspend fun updateAlarm(alarm: Alarm)

    @Transaction
    @Query("SELECT * FROM alarm_table WHERE id = :id LIMIT 1")
    fun getSingleAlarm(id: Int) : Flow<Alarm>

    @Query("DELETE FROM alarm_table WHERE id = :alarmId")
    suspend fun deleteAlarm(alarmId: Int)

    @Query("UPDATE alarm_table SET status = :status WHERE id = :alarmId")
    suspend fun updateAlarmStatus(status: Boolean, alarmId: Int)

    @Query("UPDATE alarm_table SET vibrate = :vibrate WHERE id = :alarmId")
    suspend fun updateAlarmVibrateStatus(vibrate: Boolean, alarmId: Int)

    @Query("UPDATE alarm_table SET monday = :monday WHERE id = :alarmId")
    suspend fun updateAlarmMonday(monday: Boolean, alarmId: Int)

    @Query("UPDATE alarm_table SET tuesday = :tuesday WHERE id = :alarmId")
    suspend fun updateAlarmTuesday(tuesday: Boolean, alarmId: Int)

    @Query("UPDATE alarm_table SET wednesday = :wednesday WHERE id = :alarmId")
    suspend fun updateAlarmWednesday(wednesday: Boolean, alarmId: Int)

    @Query("UPDATE alarm_table SET thursday = :thursday WHERE id = :alarmId")
    suspend fun updateAlarmThursday(thursday: Boolean, alarmId: Int)

    @Query("UPDATE alarm_table SET friday = :friday WHERE id = :alarmId")
    suspend fun updateAlarmFriday(friday: Boolean, alarmId: Int)

    @Query("UPDATE alarm_table SET saturday = :saturday WHERE id = :alarmId")
    suspend fun updateAlarmSaturday(saturday: Boolean, alarmId: Int)

    @Query("UPDATE alarm_table SET sunday = :sunday WHERE id = :alarmId")
    suspend fun updateAlarmSunday(sunday: Boolean, alarmId: Int)

    @Transaction
    @Query("SELECT * FROM alarm_table WHERE status = :status")
    fun getAlarmWhoseStatusIsTrue(status: Boolean = true) : Flow<List<Alarm>>

}