package com.ibrajix.multiclock.ui.fragments

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.ibrajix.multiclock.R
import com.ibrajix.multiclock.database.Database
import com.ibrajix.multiclock.databinding.FragmentAlarmBinding
import com.ibrajix.multiclock.service.AlarmReceiver
import com.ibrajix.multiclock.ui.adapters.AlarmAdapter
import com.ibrajix.multiclock.ui.viewmodel.AlarmViewModel
import com.ibrajix.multiclock.utils.AlarmUtility.showMaterialDialog
import com.ibrajix.multiclock.utils.AlarmUtility.showPickerAndSetAlarm
import com.ibrajix.multiclock.utils.Constants.ALARM_INTENT_ID
import com.ibrajix.multiclock.utils.Constants.ALARM_INTENT_TIME
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class AlarmFragment : Fragment() {

    private lateinit var binding: FragmentAlarmBinding

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


    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    private fun setUpObserver() {

        alarmViewModel.getAllAlarms()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                alarmViewModel.getAllAlarmsResult.collect {
                    Timber.d(it.toString())
                    alarmAdapter.submitList(it)
                    if (it.isEmpty()) {
                        binding.txtNoAlarm.visibility = View.VISIBLE
                    } else {
                        binding.txtNoAlarm.visibility = View.GONE
                    }
                }
            }
        }

    }

    private fun initItems() {

        alarmAdapter = AlarmAdapter(onClickListener = AlarmAdapter.OnAlarmClickListener {

        })

        AlarmAdapter.AlarmViewHolder.setOnAlarmChangeStatusListener { alarm, status ->
            alarm.id?.let { alarmViewModel.updateAlarmStatus(status, it) }
            //if status is true, set alarm
            if (status) {

            } else {
                //remove alarm
            }
        }

        binding.rcvAlarms.apply {
            adapter = alarmAdapter
        }

    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun setUpClickListeners() {

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

            val alarmManager: AlarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

                if (alarmManager.canScheduleExactAlarms()) {

                    showPickerAndSetAlarm { alarm ->

                        alarmViewModel.createAlarm(alarm)

                        val broadcastReceiverIntent = Intent(requireContext(), AlarmReceiver::class.java)
                        broadcastReceiverIntent.putExtra(ALARM_INTENT_TIME, alarm.time)
                        broadcastReceiverIntent.putExtra(ALARM_INTENT_ID, alarm.id)

                        val newPendingIntent = PendingIntent.getBroadcast(
                            requireContext(),
                            alarm.id?:0,
                            broadcastReceiverIntent,
                            PendingIntent.FLAG_MUTABLE
                        )

                        //schedule alarm
                        val alarmClockInfo = AlarmManager.AlarmClockInfo(alarm.timeInMilliSecond, null)
                        alarmManager.setAlarmClock(alarmClockInfo, newPendingIntent)

                    }
                } else {

                    //show a dialog for user to navigate to settings and turn on alarms and reminder
                    showMaterialDialog()

                }
            } else {

                showPickerAndSetAlarm { alarm->

                    alarmViewModel.createAlarm(alarm)

                    //set alarm
                    val broadcastReceiverIntent = Intent(requireContext(), AlarmReceiver::class.java)
                    broadcastReceiverIntent.putExtra(ALARM_INTENT_TIME, alarm.time)
                    broadcastReceiverIntent.putExtra(ALARM_INTENT_ID, alarm.id)

                    val newPendingIntent = PendingIntent.getBroadcast(
                        requireContext(),
                        alarm.id?:0,
                        broadcastReceiverIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                    )

                    //ensure the alarm fires even if the device is dozing.
                    val alarmClockInfo = AlarmManager.AlarmClockInfo(alarm.timeInMilliSecond, null)
                    alarmManager.setAlarmClock(alarmClockInfo, newPendingIntent)

                }
            }
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

}