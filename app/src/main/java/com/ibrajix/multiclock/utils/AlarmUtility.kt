package com.ibrajix.multiclock.utils

import java.util.*
import java.util.Calendar.SATURDAY
import java.util.Calendar.SUNDAY
import java.util.concurrent.TimeUnit

object AlarmUtility {

    private val recurringDays = BooleanArray(7)

    private fun hasRecurrence(): Boolean {
        return numRecurringDays() > 0
    }

    fun ringsAt(hour: Int, minutes: Int): Long {

        // ====================================================
        // Always with respect to the current date and time
        val calendar: Calendar = GregorianCalendar()
        calendar[Calendar.HOUR_OF_DAY] = hour
        calendar[Calendar.MINUTE] = minutes
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        var baseRingTime = calendar.timeInMillis
        return if (!hasRecurrence()) {
            if (baseRingTime <= System.currentTimeMillis()) {
                // The specified time has passed for today
                baseRingTime += TimeUnit.DAYS.toMillis(1)
            }
            baseRingTime
        } else {
            // Compute the ring time just for the next closest recurring day.
            // Remember that day constants defined in the Calendar class are
            // not zero-based like ours, so we have to compensate with an offset
            // of magnitude one, with the appropriate sign based on the situation.
            val weekdayToday = calendar[Calendar.DAY_OF_WEEK]
            var numDaysFromToday = -1
            for (i in weekdayToday..Calendar.SATURDAY) {
                if (isRecurring(i - 1 /*match up with our day constant*/)) {
                    if (i == weekdayToday) {
                        if (baseRingTime > System.currentTimeMillis()) {
                            // The normal ring time has not passed yet
                            numDaysFromToday = 0
                            break
                        }
                    } else {
                        numDaysFromToday = i - weekdayToday
                        break
                    }
                }
            }

            // Not computed yet
            if (numDaysFromToday < 0) {
                for (i in Calendar.SUNDAY until weekdayToday) {
                    if (isRecurring(i - 1 /*match up with our day constant*/)) {
                        numDaysFromToday = Calendar.SATURDAY - weekdayToday + i
                        break
                    }
                }
            }

            // Still not computed yet. The only recurring day is weekdayToday,
            // and its normal ring time has already passed.
            if (numDaysFromToday < 0 && isRecurring(weekdayToday - 1)
                && baseRingTime <= System.currentTimeMillis()
            ) {
                numDaysFromToday = 7
            }
            check(numDaysFromToday >= 0) { "How did we get here?" }
            baseRingTime + TimeUnit.DAYS.toMillis(numDaysFromToday.toLong())
        }
    }

    private fun numRecurringDays(): Int {
        var count = 0
        for (b in recurringDays) if (b) count++
        return count
    }

    private fun isRecurring(day: Int): Boolean {
        checkDay(day)
        return recurringDays[day]
    }

    private fun checkDay(day: Int) {
        require(!(day < SUNDAY || day > SATURDAY)) { "Invalid day of week: $day" }
    }

    fun ringsIn(hour: Int, minutes: Int): Long {
        return ringsAt(hour, minutes) - System.currentTimeMillis()
    }

}