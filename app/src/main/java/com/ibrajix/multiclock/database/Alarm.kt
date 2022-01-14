package com.ibrajix.multiclock.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarm_table")
data class Alarm(
  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name = "id")
  val id: Int,
  @ColumnInfo(name = "time")
  val time: String,
  @ColumnInfo(name = "status")
  val status: Boolean,
  @ColumnInfo(name = "vibrate")
  val vibrate: Boolean
)