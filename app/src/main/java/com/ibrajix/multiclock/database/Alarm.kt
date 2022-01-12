package com.ibrajix.multiclock.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarm_table")
data class Alarm(
  @PrimaryKey(autoGenerate = true)
  val id: Int,
  val time: String,
  val status: Boolean,
  val vibrate: Boolean
)