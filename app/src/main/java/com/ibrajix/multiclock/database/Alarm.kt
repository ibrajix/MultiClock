package com.ibrajix.multiclock.database

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "alarm_table")
data class Alarm (
  @PrimaryKey
  @NonNull
  @ColumnInfo(name = "id")
  val id: Int? = null,
  @ColumnInfo(name = "dateCreated")
  val dateCreated: Long = System.currentTimeMillis(),
  @ColumnInfo(name = "time")
  val time: String,
  @ColumnInfo(name = "hour")
  val hour: Int,
  @ColumnInfo(name = "minute")
  val minute: Int,
  @ColumnInfo(name = "status")
  val status: Boolean? = true,
  @ColumnInfo(name = "vibrate")
  val vibrate: Boolean? = true,
  @ColumnInfo(name = "timeInMilliSecond")
  val timeInMilliSecond: Long,
  @ColumnInfo(name = "monday")
  val monday: Boolean? = false,
  @ColumnInfo(name = "tuesday")
  val tuesday: Boolean? = false,
  @ColumnInfo(name = "wednesday")
  val wednesday: Boolean? = false,
  @ColumnInfo(name = "thursday")
  val thursday: Boolean? = false,
  @ColumnInfo(name = "friday")
  val friday: Boolean? = false,
  @ColumnInfo(name = "saturday")
  val saturday: Boolean? = false,
  @ColumnInfo(name = "sunday")
  val sunday: Boolean? = false
) : Parcelable