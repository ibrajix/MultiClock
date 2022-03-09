package com.ibrajix.multiclock.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.ibrajix.multiclock.R
import com.ibrajix.multiclock.database.Alarm
import com.ibrajix.multiclock.database.DeviceSound
import com.ibrajix.multiclock.service.AlarmReceiver
import com.ibrajix.multiclock.service.AlarmService
import com.ibrajix.multiclock.utils.Constants.ALARM_FRIDAY
import com.ibrajix.multiclock.utils.Constants.ALARM_MONDAY
import com.ibrajix.multiclock.utils.Constants.ALARM_SATURDAY
import com.ibrajix.multiclock.utils.Constants.ALARM_SUNDAY
import com.ibrajix.multiclock.utils.Constants.ALARM_THURSDAY
import com.ibrajix.multiclock.utils.Constants.ALARM_TUESDAY
import com.ibrajix.multiclock.utils.Constants.ALARM_WEDNESDAY
import com.ibrajix.multiclock.utils.Constants.IS_ALARM_WEEKLY_REPEATING
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

    private fun ringsIn(hour: Int, minutes: Int): Long {
        return ringsAt(hour, minutes) - System.currentTimeMillis()
    }

    fun Fragment.showPickerAndSetAlarm(callback :(Alarm) -> Unit) {

        val hour = Calendar.getInstance()[Calendar.HOUR_OF_DAY]
        val minute = Calendar.getInstance()[Calendar.MINUTE]

        val materialTimePicker: MaterialTimePicker = MaterialTimePicker.Builder()
            .setTitleText(requireContext().getString(R.string.select_time))
            .setHour(hour)
            .setMinute(minute)
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .build()

        materialTimePicker.show(parentFragmentManager, getString(R.string.alarm))

        materialTimePicker.addOnPositiveButtonClickListener {

            val pickedHour: Int = materialTimePicker.hour
            val pickedMinute: Int = materialTimePicker.minute

            val calendar = Calendar.getInstance()
            calendar[Calendar.HOUR_OF_DAY] = pickedHour
            calendar[Calendar.MINUTE] = pickedMinute
            calendar[Calendar.SECOND] = 0
            calendar[Calendar.MILLISECOND] = 0


            // if alarm time has already passed, increment day by 1
            if (calendar.timeInMillis <= System.currentTimeMillis()) {
                calendar[Calendar.DAY_OF_MONTH] = calendar[Calendar.DAY_OF_MONTH] + 1
            }


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

            val id = Random().nextInt(Integer.MAX_VALUE)

            val alarm = Alarm(
                id = id,
                time = formattedTime,
                hour = pickedHour,
                minute = pickedMinute,
                timeInMilliSecond = calendar.timeInMillis,
            )

            callback(alarm)

            Toast.makeText(requireContext(), getString(R.string.alarm_set_for, DurationUtility.showAlarmToast(requireContext(), ringsIn(pickedHour, pickedMinute), false)), Toast.LENGTH_LONG).show()

        }

    }

    fun Fragment.showPickerAndUpdateAlarm(alarm: Alarm, callback :(Alarm) -> Unit) {

        val materialTimePicker: MaterialTimePicker = MaterialTimePicker.Builder()
            .setTitleText(requireContext().getString(R.string.select_time))
            .setHour(alarm.hour)
            .setMinute(alarm.minute)
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .build()

        materialTimePicker.show(parentFragmentManager, getString(R.string.alarm))

        materialTimePicker.addOnPositiveButtonClickListener {

            val pickedHour: Int = materialTimePicker.hour
            val pickedMinute: Int = materialTimePicker.minute

            val calendar = Calendar.getInstance()
            calendar[Calendar.HOUR_OF_DAY] = pickedHour
            calendar[Calendar.MINUTE] = pickedMinute
            calendar[Calendar.SECOND] = 0
            calendar[Calendar.MILLISECOND] = 0


            // if alarm time has already passed, increment day by 1
            if (calendar.timeInMillis <= System.currentTimeMillis()) {
                calendar[Calendar.DAY_OF_MONTH] = calendar[Calendar.DAY_OF_MONTH] + 1
            }


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

            val alarmSet = Alarm(
                id = alarm.id,
                time = formattedTime,
                hour = pickedHour,
                minute = pickedMinute,
                timeInMilliSecond = calendar.timeInMillis,
            )

            callback(alarmSet)

            Toast.makeText(requireContext(), getString(R.string.alarm_set_for, DurationUtility.showAlarmToast(requireContext(), ringsIn(pickedHour, pickedMinute), false)), Toast.LENGTH_LONG).show()

        }

    }

    fun scheduleAlarm(alarm: Alarm, context: Context){

        val calendar = Calendar.getInstance()
        calendar[Calendar.HOUR_OF_DAY] = alarm.hour
        calendar[Calendar.MINUTE] = alarm.minute
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0


        // if alarm time has already passed, increment day by 1
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar[Calendar.DAY_OF_MONTH] = calendar[Calendar.DAY_OF_MONTH] + 1
        }

        val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val broadcastReceiverIntent = Intent(context, AlarmReceiver::class.java)
        broadcastReceiverIntent.putExtra(Constants.ALARM_INTENT_TIME, alarm.time)
        broadcastReceiverIntent.putExtra(Constants.ALARM_INTENT_ID, alarm.id)

        val newPendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id?:0,
            broadcastReceiverIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        //schedule alarm
        val alarmClockInfo = AlarmManager.AlarmClockInfo(calendar.timeInMillis, null)
        alarmManager.setAlarmClock(alarmClockInfo, newPendingIntent)

    }


    fun rescheduleAlarm(alarm: Alarm, context: Context){

        val calendar = Calendar.getInstance()
        calendar[Calendar.HOUR_OF_DAY] = alarm.hour
        calendar[Calendar.MINUTE] = alarm.minute
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0


        // if alarm time has already passed, increment day by 1
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar[Calendar.DAY_OF_MONTH] = calendar[Calendar.DAY_OF_MONTH] + 1
        }

        val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager


        val broadcastReceiverIntent = Intent(context, AlarmReceiver::class.java)
        broadcastReceiverIntent.putExtra(Constants.ALARM_INTENT_TIME, alarm.time)
        broadcastReceiverIntent.putExtra(Constants.ALARM_INTENT_ID, alarm.id)

        val newPendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id?:0,
            broadcastReceiverIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        //schedule alarm
        val alarmClockInfo = AlarmManager.AlarmClockInfo(calendar.timeInMillis, null)
        alarmManager.setAlarmClock(alarmClockInfo, newPendingIntent)

    }

    fun scheduleWeeklyAlarm(alarm: Alarm, dayOfWeek: Int, context: Context) {

        val calendar = Calendar.getInstance()
        calendar[Calendar.DAY_OF_WEEK] = dayOfWeek
        calendar[Calendar.MINUTE] = alarm.minute
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0

        //if alarm time has already passed, increment day by the following week
        if (calendar.timeInMillis < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 7)
        }

        val alarmManager: AlarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val broadcastReceiverIntent = Intent(context, AlarmReceiver::class.java)
        broadcastReceiverIntent.putExtra(Constants.ALARM_INTENT_TIME, alarm.time)
        broadcastReceiverIntent.putExtra(Constants.ALARM_INTENT_ID, alarm.id)
        broadcastReceiverIntent.putExtra(IS_ALARM_WEEKLY_REPEATING, true)

        val newPendingIntent = PendingIntent.getBroadcast(
            context,
            dayOfWeek,
            broadcastReceiverIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        //schedule alarm
        val alarmClockInfo = AlarmManager.AlarmClockInfo(calendar.timeInMillis, null)
        alarmManager.setAlarmClock(alarmClockInfo, newPendingIntent)
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            24 * 60 * 60 * 1000,
            newPendingIntent
        )

        val dayOfTheWeek: String = getDayOfTheWeekInString(calendar.get(Calendar.DAY_OF_WEEK))
        Toast.makeText(
            context,
            context.getString(R.string.alarm_set_for_weekday, dayOfTheWeek),
            Toast.LENGTH_LONG
        ).show()

    }

    fun repeatAlarm(alarmTime: String?, alarmId: String?, context: Context){

        val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager


        val broadcastReceiverIntent = Intent(context, AlarmReceiver::class.java)
        broadcastReceiverIntent.putExtra(Constants.ALARM_INTENT_TIME, alarmTime)
        broadcastReceiverIntent.putExtra(Constants.ALARM_INTENT_ID, alarmId)


        val newPendingIntent = alarmId?.let {
            PendingIntent.getBroadcast(
                context,
                it.toInt(),
                broadcastReceiverIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        val interval = System.currentTimeMillis() + AlarmManager.INTERVAL_DAY

        //schedule alarm
        val alarmClockInfo = AlarmManager.AlarmClockInfo(interval, null)
        alarmManager.setAlarmClock(alarmClockInfo, newPendingIntent)

    }

    fun cancelAlarm(alarm: Alarm, context: Context) {

        val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val broadcastReceiverIntent = Intent(context, AlarmReceiver::class.java)

        val newPendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id?:0,
            broadcastReceiverIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        newPendingIntent.cancel()

        alarmManager.cancel(newPendingIntent)

    }


    fun cancelWeeklyAlarm(alarm: Alarm, context: Context, dayOfWeek: Int) {

        val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val broadcastReceiverIntent = Intent(context, AlarmReceiver::class.java)

        val newPendingIntent = PendingIntent.getBroadcast(
            context,
            dayOfWeek,
            broadcastReceiverIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.cancel(newPendingIntent)

    }

    fun snoozeAlarm(context: Context, intent: Intent){

        val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.add(Calendar.MINUTE, Constants.SNOOZE_TIME)

        val broadcastReceiverIntent = Intent(context, AlarmReceiver::class.java)
        broadcastReceiverIntent.putExtra(
            Constants.ALARM_INTENT_TIME, intent.getStringExtra(
                Constants.ALARM_INTENT_TIME
            ))

        val pendingIntent =
            intent.let {
                PendingIntent.getBroadcast(
                    context,
                    it.getIntExtra(Constants.ALARM_INTENT_ID, 0),
                    broadcastReceiverIntent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            }


        //ensure the alarm fires even if the device is dozing.
        val alarmClockInfo = AlarmManager.AlarmClockInfo(calendar.timeInMillis, null)
        alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)

        val intentService = Intent(context.applicationContext, AlarmService::class.java)
        context.applicationContext.stopService(intentService)

        Toast.makeText(context, context.getString(R.string.alarm_snoozed), Toast.LENGTH_LONG).show()

    }

    //get recurring days
    fun getRecurringDays(monday: Boolean?, tuesday: Boolean?, wednesday: Boolean?, thursday: Boolean?,
    friday: Boolean?, saturday: Boolean?, sunday: Boolean?) : String{
        var days = ""
        if (monday == true) {
            days += "Mon "
        }
        if (tuesday == true) {
            days += "Tue "
        }
        if (wednesday == true) {
            days += "Wed "
        }
        if (thursday == true) {
            days += "Thur "
        }
        if (friday == true) {
            days += "Fri "
        }
        if (saturday == true) {
            days += "Sat "
        }
        if (sunday == true) {
            days += "Sun "
        }
        return days
    }

    fun getDayOfTheWeekInString(value: Int): String {
        var day = ""
        when (value) {
            1 -> day = "Sunday"
            2 -> day = "Monday"
            3 -> day = "Tuesday"
            4 -> day = "Wednesday"
            5 -> day = "Thursday"
            6 -> day = "Friday"
            7 -> day = "Saturday"
        }
        return day
    }


    //check if alarm is on (weekly)
    fun checkWeeklyAlarmStatusAndCancel(alarm: Alarm, context: Context) {
        if (alarm.monday == true){
            cancelWeeklyAlarm(alarm, context, ALARM_MONDAY)
        }
        if (alarm.tuesday == true){
            cancelWeeklyAlarm(alarm, context, ALARM_TUESDAY)
        }
        if (alarm.wednesday == true){
            cancelWeeklyAlarm(alarm, context, ALARM_WEDNESDAY)
        }
        if (alarm.thursday == true){
            cancelWeeklyAlarm(alarm, context, ALARM_THURSDAY)
        }
        if (alarm.friday == true){
            cancelWeeklyAlarm(alarm, context, ALARM_FRIDAY)
        }
        if (alarm.saturday == true){
            cancelWeeklyAlarm(alarm, context, ALARM_SATURDAY)
        }
        if (alarm.sunday == true){
            cancelWeeklyAlarm(alarm, context, ALARM_SUNDAY)
        }
    }

    fun getNotificationSounds(context: Context): ArrayList<DeviceSound> {

        val manager = RingtoneManager(context)
        manager.setType(RingtoneManager.TYPE_ALARM)
        val cursor = manager.cursor

        val list: ArrayList<DeviceSound> = ArrayList()

        while (cursor.moveToNext()) {

            val id = cursor.getString(RingtoneManager.ID_COLUMN_INDEX)
            val title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX)
            val uri = cursor.getString(RingtoneManager.URI_COLUMN_INDEX)

            list.add(
                DeviceSound(
                name = title,
                id = id,
                uri = "$uri/$id",
            ))
        }

        return list
    }


}