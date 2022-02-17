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

    @Transaction
    @Query("SELECT * FROM alarm_table WHERE status = :status")
    fun getAlarmWhoseStatusIsTrue(status: Boolean = true) : Flow<List<Alarm>>

}