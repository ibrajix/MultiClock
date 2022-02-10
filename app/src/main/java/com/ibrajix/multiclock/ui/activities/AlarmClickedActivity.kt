package com.ibrajix.multiclock.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.ibrajix.multiclock.R
import com.ibrajix.multiclock.databinding.ActivityAlarmClickedBinding
import com.ibrajix.multiclock.utils.Constants.ALARM_INTENT_EXTRA

class AlarmClickedActivity : AppCompatActivity() {

    lateinit var binding: ActivityAlarmClickedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_alarm_clicked)

        showContent()

    }

    private fun showContent(){

        val textAlarmExtra = intent.getStringExtra(ALARM_INTENT_EXTRA)
        binding.txtAlarmTime.text = textAlarmExtra

    }
}