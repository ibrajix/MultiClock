package com.ibrajix.multiclock.ui.activities

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.ibrajix.multiclock.R
import com.ibrajix.multiclock.databinding.ActivityAlarmClickedBinding
import com.ibrajix.multiclock.service.AlarmReceiver
import com.ibrajix.multiclock.service.AlarmService
import com.ibrajix.multiclock.utils.Constants
import com.ibrajix.multiclock.utils.Constants.ALARM_INTENT_ID
import com.ibrajix.multiclock.utils.Constants.ALARM_INTENT_TIME
import com.ibrajix.multiclock.utils.Constants.SNOOZE_TIME
import com.ibrajix.multiclock.utils.UiUtility
import java.util.*

class AlarmClickedActivity : AppCompatActivity() {

    lateinit var binding: ActivityAlarmClickedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_alarm_clicked)

        showContent()

        handleClicks()
    }

    private fun showContent(){

        val textAlarmExtra = intent.getStringExtra(ALARM_INTENT_TIME)
        binding.txtAlarmTime.text = textAlarmExtra

    }

    private fun handleClicks(){

        //on click snooze
        binding.btnSnooze.setOnClickListener {

            val alarmManager: AlarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.add(Calendar.MINUTE, SNOOZE_TIME)

            val broadcastReceiverIntent = Intent(this, AlarmReceiver::class.java)
            broadcastReceiverIntent.putExtra(ALARM_INTENT_TIME, UiUtility.getCurrentTime())

            val pendingIntent =
                PendingIntent.getBroadcast(
                    this,
                    intent.getIntExtra(ALARM_INTENT_ID, 0),
                    broadcastReceiverIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )


            //ensure the alarm fires even if the device is dozing.
            val alarmClockInfo = AlarmManager.AlarmClockInfo(calendar.timeInMillis, null)
            alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)

            val intentService = Intent(applicationContext, AlarmService::class.java)
            applicationContext.stopService(intentService)

            Toast.makeText(this, getString(R.string.alarm_snoozed), Toast.LENGTH_LONG).show()
            finish()

        }

        //on click stop
        binding.btnStop.setOnClickListener {

            //perform action stop
            val intentService = Intent(applicationContext, AlarmService::class.java)
            applicationContext?.stopService(intentService)
            finish()

        }

    }

}