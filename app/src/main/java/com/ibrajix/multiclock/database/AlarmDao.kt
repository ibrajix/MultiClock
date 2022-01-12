package com.ibrajix.multiclock.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToFavorite(alarm: Alarm)

    @Transaction
    @Query("SELECT * FROM alarm_table ORDER BY id DESC")
    fun getAllAlarms(): Flow<List<Alarm>>

    @Update
    suspend fun updateAlarm(alarm: Alarm)

    @Query("DELETE FROM alarm_table WHERE id = :alarmId")
    suspend fun deleteAlarm(alarmId: Int)

}