package com.ibrajix.multiclock.ui.fragments

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
import com.ibrajix.multiclock.ui.activities.AlarmClickedActivity
import com.ibrajix.multiclock.ui.adapters.AlarmAdapter
import com.ibrajix.multiclock.ui.viewmodel.AlarmViewModel
import com.ibrajix.multiclock.utils.AlarmUtility.showMaterialDialog
import com.ibrajix.multiclock.utils.AlarmUtility.showPickerAndSetAlarm
import com.ibrajix.multiclock.utils.Constants.ALARM_INTENT_EXTRA
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

    lateinit var pendingIntent: PendingIntent

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

                        //set alarm
                        val activityIntent = Intent(requireContext(), AlarmClickedActivity::class.java)
                        activityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        activityIntent.putExtra(ALARM_INTENT_EXTRA, alarm.time)

                        pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            PendingIntent.getActivity(
                                requireContext(),
                                0,
                                activityIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                            )
                        } else {
                            PendingIntent.getActivity(
                                requireContext(),
                                0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT
                            )
                        }

                        //reschedule alarm
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            //Ensure the alarm fires even if the device is dozing.
                            val alarmClockInfo = AlarmManager.AlarmClockInfo(alarm.timeInMilliSecond, null)
                            alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
                        } else {
                            alarmManager.setExact(
                                AlarmManager.RTC_WAKEUP,
                                alarm.timeInMilliSecond,
                                pendingIntent
                            )
                        }

                    }
                } else {
                    //show a dialog for user to navigate to settings and turn on alarms and reminder
                    showMaterialDialog()
                }
            } else {
                showPickerAndSetAlarm { alarm->

                    alarmViewModel.createAlarm(alarm)

                    //set alarm
                    val activityIntent = Intent(requireContext(), AlarmClickedActivity::class.java)
                    activityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    activityIntent.putExtra(ALARM_INTENT_EXTRA, alarm.time)

                    pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        PendingIntent.getActivity(
                            requireContext(),
                            0,
                            activityIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                    } else {
                        PendingIntent.getActivity(
                            requireContext(),
                            0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT
                        )
                    }

                    //reschedule alarm
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        //Ensure the alarm fires even if the device is dozing.
                        val alarmClockInfo = AlarmManager.AlarmClockInfo(alarm.timeInMilliSecond, null)
                        alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
                    } else {
                        alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            alarm.timeInMilliSecond,
                            pendingIntent
                        )
                    }

                }
            }
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

}