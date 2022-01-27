package com.ibrajix.multiclock.utils

import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.ibrajix.multiclock.R
import com.ibrajix.multiclock.database.Alarm
import dev.shreyaspatil.MaterialDialog.MaterialDialog
import java.util.*
import java.util.Calendar.SATURDAY
import java.util.Calendar.SUNDAY
import java.util.concurrent.TimeUnit
import kotlin.math.min

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


    fun Fragment.showPickerAndSetAlarm(callback :(Alarm) -> Unit) {

        val materialTimePicker: MaterialTimePicker = MaterialTimePicker.Builder()
            .setTitleText(requireContext().getString(R.string.select_time))
            .setHour(12)
            .setMinute(10)
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .build()

        materialTimePicker.show(parentFragmentManager, getString(R.string.alarm))

        materialTimePicker.addOnPositiveButtonClickListener {

            val pickedHour: Int = materialTimePicker.hour
            val pickedMinute: Int = materialTimePicker.minute

            val formattedTime: String = when {
                pickedHour > 12 -> {
                    if (pickedMinute < 10) {
                        "${materialTimePicker.hour - 12}:0${materialTimePicker.minute}pm"
                    } else {
                        "${materialTimePicker.hour - 12}:${materialTimePicker.minute}pm"
                    }
                }
                pickedHour == 12 -> {
                    if (pickedMinute < 10) {
                        "${materialTimePicker.hour}:0${materialTimePicker.minute}pm"
                    } else {
                        "${materialTimePicker.hour}:${materialTimePicker.minute}pm"
                    }
                }
                pickedHour == 0 -> {
                    if (pickedMinute < 10) {
                        "${materialTimePicker.hour + 12}:0${materialTimePicker.minute}am"
                    } else {
                        "${materialTimePicker.hour + 12}:${materialTimePicker.minute}am"
                    }
                }
                else -> {
                    if (pickedMinute < 10) {
                        "${materialTimePicker.hour}:0${materialTimePicker.minute}am"
                    } else {
                        "${materialTimePicker.hour}:${materialTimePicker.minute}am"
                    }
                }
            }

            val alarm = Alarm(
                time = formattedTime,
                hour = pickedHour,
                minute = pickedMinute
            )

            callback(alarm)

            Toast.makeText(requireContext(), getString(R.string.alarm_set_for, DurationUtility.showAlarmToast(requireContext(), ringsIn(pickedHour, pickedMinute), false)), Toast.LENGTH_LONG).show()
        }

    }

    //show material dialog
    @RequiresApi(Build.VERSION_CODES.S)
    fun Fragment.showMaterialDialog(){

        val mDialog = MaterialDialog.Builder(requireActivity())
            .setTitle(requireContext().getString(R.string.need_permission))
            .setMessage(requireContext().getString(R.string.permission_helper))
            .setCancelable(false)
            .setAnimation(R.raw.permission)
            .setPositiveButton(
                requireContext().getString(R.string.allow)
            ) { dialogInterface, which ->
                //on click, navigate to settings screen
                val intent = Intent().apply {
                    action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                }
                startActivity(intent)
            }
            .setNegativeButton(
                requireContext().getString(R.string.later)
            ){dialogInterface, which ->
                //on click cancel
                dialogInterface.dismiss()
            }
            .build()
        mDialog.show()

    }

}