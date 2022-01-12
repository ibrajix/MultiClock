package com.ibrajix.multiclock.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Alarm::class], version = 1)
abstract class Db : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao
}