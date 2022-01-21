package com.ibrajix.multiclock.utils

import android.content.Context
import androidx.annotation.NonNull
import androidx.annotation.StringRes
import com.ibrajix.multiclock.R
import java.util.concurrent.TimeUnit

object DurationUtility {

    private const val DAYS = 0
    private const val HOURS = 1
    private const val MINUTES = 2
    private const val SECONDS = 3
    const val MILLIS = 4

    /** Return a string representing the duration, formatted in hours and minutes.
     * TODO: Need to adapt this to represent all time fields eventually
     * TODO: Since this is primarirly used for alarm set toasts, you should make different methods for
     * different use cases. E.g. Timer's duration should have its own method.
     * TODO: Then, rename this method to something about alarm toasts.  */
    fun showAlarmToast(context: Context, millis: Long, abbreviate: Boolean): String {
        val fields = breakdown(millis)
        var numDays = fields[DAYS]
        var numHours = fields[HOURS]
        var numMins = fields[MINUTES]
        var numSecs = fields[SECONDS] // only considered for rounding of minutes
        if (numSecs >= 31) {
            numMins++
            numSecs = 0 // Not totally necessary since it won't be considered any more
            if (numMins == 60L) {
                numHours++
                numMins = 0
                if (numHours == 24L) {
                    numDays++
                    numHours = 0
                }
            }
        }
        val res: Int = if (abbreviate) {
            getAbbreviatedStringRes(numDays, numHours, numMins)
        } else {
            getStringRes(numDays, numHours, numMins)
        }
        return context.getString(res, numDays, numHours, numMins)
    }

    /**
     * Equivalent to
     * [ breakdown(millis, TimeUnit.MILLISECONDS, true)][.breakdown],
     * which rounds milliseconds. Callers who use this are probably not
     * concerned about displaying the milliseconds value.
     */
    private fun breakdown(millis: Long): LongArray {
        return breakdown(millis, TimeUnit.MILLISECONDS, true)
    }
    /**
     * Returns a breakdown of a given time into its values
     * in hours, minutes, seconds and milliseconds.
     * @param t the time to break down
     * @param unit the [TimeUnit] the given time is expressed in
     * @param roundMillis whether rounding of milliseconds is desired
     * @return a `long[]` of the values in hours, minutes, seconds
     * and milliseconds in that order
     */
    /**
     * Equivalent to
     * [breakdown(t, unit, false)][.breakdown],
     * i.e. does not round milliseconds.
     */
    @JvmOverloads
    fun breakdown(t: Long, @NonNull unit: TimeUnit, roundMillis: Boolean = false): LongArray {
        var days = unit.toDays(t)
        var hours = unit.toHours(t) % 24
        var minutes = unit.toMinutes(t) % 60
        var seconds = unit.toSeconds(t) % 60
        var msecs = unit.toMillis(t) % 1000
        if (roundMillis) {
            if (msecs >= 500) {
                seconds++
                msecs = 0
                if (seconds == 60L) {
                    minutes++
                    seconds = 0
                    if (minutes == 60L) {
                        hours++
                        minutes = 0
                        if (hours == 24L) {
                            days++
                            hours = 0
                        }
                    }
                }
            }
        }
        return longArrayOf(days, hours, minutes, seconds, msecs)
    }

    @StringRes
    private fun getStringRes(numDays: Long, numHours: Long, numMins: Long): Int {
        val res: Int =
            when(numDays){
                0L -> {
                    when(numHours){
                        0L -> {
                            when(numMins){
                                0L -> R.string.less_than_one_minute
                                1L -> R.string.minute
                                else -> R.string.minutes
                            }
                        }
                        1L -> {
                            when(numMins){
                                0L -> R.string.hour
                                1L -> R.string.hour_and_minute
                                else ->  R.string.hour_and_minutes
                            }
                        }
                        else -> {
                            when(numMins){
                                0L -> R.string.hours
                                1L -> R.string.hours_and_minute
                                else -> R.string.hours_and_minutes
                            }
                        }
                    }
                }
                1L -> {
                    when(numHours){
                        0L -> {
                            when(numMins){
                                0L -> R.string.day
                                1L -> R.string.day_and_minute
                                else -> R.string.day_and_minutes
                            }
                            when(numMins){
                                0L -> R.string.day
                                1L -> R.string.day_and_minute
                                else -> R.string.day_and_minutes
                            }
                        }
                        1L -> {
                            when(numMins){
                                0L -> R.string.day_and_hour
                                1L -> R.string.day_hour_and_minute
                                else -> R.string.day_hour_and_minutes
                            }
                        }
                        else -> {
                            when(numMins){
                                0L -> R.string.day_and_hours
                                1L -> R.string.day_hours_and_minute
                                else -> R.string.day_hours_and_minutes
                            }
                        }
                    }
                }
                else ->
                {
                    when(numHours){
                        0L -> {
                            when(numMins){
                                0L -> R.string.days
                                1L -> R.string.days_and_minute
                                else -> R.string.days_and_minutes
                            }
                        }
                        1L -> {
                            when(numMins){
                                0L ->  R.string.days_and_hour
                                1L -> R.string.days_hour_and_minute
                                else ->  R.string.days_hour_and_minutes
                            }
                        }
                        else -> {
                            when(numMins){
                                0L ->  R.string.days_and_hours
                                1L -> R.string.days_hours_and_minute
                                else ->  R.string.days_hours_and_minutes
                            }
                        }
                    }
                }
            }
        return res
    }

    @StringRes
    private fun getAbbreviatedStringRes(numDays: Long, numHours: Long, numMins: Long): Int {
        val res: Int = if (numDays == 0L) {
            if (numHours == 0L) {
                if (numMins == 0L) {
                    R.string.abbrev_less_than_one_minute
                } else {
                    R.string.abbrev_minutes
                }
            } else {
                if (numMins == 0L) {
                    R.string.abbrev_hours
                } else {
                    R.string.abbrev_hours_and_minutes
                }
            }
        } else {
            if (numHours == 0L) {
                if (numMins == 0L) {
                    R.string.abbrev_days
                } else {
                    R.string.abbrev_days_and_minutes
                }
            } else {
                if (numMins == 0L) {
                    R.string.abbrev_days_and_hours
                } else {
                    R.string.abbrev_days_hours_and_minutes
                }
            }
        }
        return res
    }
}