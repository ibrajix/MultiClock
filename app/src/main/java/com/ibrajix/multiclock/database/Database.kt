package com.ibrajix.multiclock.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Alarm::class], version = 4)
abstract class Database : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao
}