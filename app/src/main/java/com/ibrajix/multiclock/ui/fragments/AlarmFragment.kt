package com.ibrajix.multiclock.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.ibrajix.multiclock.R
import com.ibrajix.multiclock.database.Alarm
import com.ibrajix.multiclock.database.Database
import com.ibrajix.multiclock.databinding.FragmentAlarmBinding
import com.ibrajix.multiclock.ui.adapters.AlarmAdapter
import com.ibrajix.multiclock.ui.viewmodel.AlarmViewModel
import com.ibrajix.multiclock.utils.AlarmUtility.ringsIn
import com.ibrajix.multiclock.utils.DurationUtility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AlarmFragment : Fragment() {

    private lateinit var  binding: FragmentAlarmBinding

    @Inject
    lateinit var database: Database

    lateinit var alarmAdapter: AlarmAdapter

    private val alarmViewModel: AlarmViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_alarm, container, false)
        binding.lifecycleOwner = this
        binding.alarmViewModel = alarmViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initItems()
        setUpClickListeners()
        setUpObserver()
    }


    private fun setUpObserver(){

        alarmViewModel.getAllAlarms()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                alarmViewModel.getAllAlarmsResult.collect {
                    Timber.d(it.toString())
                    alarmAdapter.submitList(it)
                    if(it.isEmpty()){
                        binding.txtNoAlarm.visibility = View.VISIBLE
                    }
                    else {
                        binding.txtNoAlarm.visibility = View.GONE
                    }
                }
            }
        }

    }

    private fun initItems(){

        alarmAdapter = AlarmAdapter(onClickListener = AlarmAdapter.OnAlarmClickListener{

        })

        AlarmAdapter.AlarmViewHolder.setOnAlarmChangeStatusListener { alarm, status ->
            alarm.id?.let { alarmViewModel.updateAlarmStatus(status, it) }
        }

        binding.rcvAlarms.apply {
            adapter = alarmAdapter
        }

    }


    private fun setUpClickListeners(){

        binding.appBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.about -> {
                    val action = AlarmFragmentDirections.actionAlarmFragmentToAboutFragment()
                    findNavController().navigate(action)
                    true
                }
                else -> false
            }
        }

        binding.floatingActionButton.setOnClickListener {

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

                //add alarm to room database
                val alarm = Alarm(
                    time = formattedTime,
                    hour = pickedHour,
                    minute = pickedMinute
                )

                alarmViewModel.createAlarm(alarm = alarm)

                Toast.makeText(requireContext(), getString(R.string.alarm_set_for, DurationUtility.showAlarmToast(requireContext(), ringsIn(pickedHour, pickedMinute), false)), Toast.LENGTH_LONG).show()

            }
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

}