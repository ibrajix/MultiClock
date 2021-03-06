package com.ibrajix.multiclock.ui.fragments

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.Context
import android.os.Build
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
import com.ibrajix.multiclock.R
import com.ibrajix.multiclock.databinding.FragmentAlarmBinding
import com.ibrajix.multiclock.ui.adapters.AlarmAdapter
import com.ibrajix.multiclock.ui.viewmodel.AlarmViewModel
import com.ibrajix.multiclock.utils.AlarmUtility.cancelAlarm
import com.ibrajix.multiclock.utils.AlarmUtility.cancelWeeklyAlarm
import com.ibrajix.multiclock.utils.AlarmUtility.checkWeeklyAlarmStatusAndCancel
import com.ibrajix.multiclock.utils.AlarmUtility.scheduleAlarm
import com.ibrajix.multiclock.utils.AlarmUtility.showPickerAndSetAlarm
import com.ibrajix.multiclock.utils.UiUtility.showMaterialDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AlarmFragment : Fragment() {

    private lateinit var binding: FragmentAlarmBinding

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


    private fun setUpObserver() {

        alarmViewModel.getAllAlarms()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                alarmViewModel.getAllAlarmsResult.collect {
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

        alarmAdapter = AlarmAdapter(onClickListener = AlarmAdapter.OnAlarmClickListener { alarm->

            //move to alarm details fragment
            val action = AlarmFragmentDirections.actionAlarmFragmentToAlarmDetailsFragment(alarm)
            findNavController().navigate(action)

        })

        binding.rcvAlarms.apply {
            adapter = alarmAdapter
        }

        AlarmAdapter.AlarmViewHolder.setOnAlarmChangeStatusListener { alarm, status ->
            alarm.id?.let { alarmViewModel.updateAlarmStatus(status, it) }
            //if status is true, set alarm
            if (status && alarm.weeklyRecurring == false) {
                scheduleAlarm(alarm = alarm, context = requireContext())
            } else {

                //remove alarm
                cancelAlarm(alarm = alarm, context = requireContext())

                //cancel weekly alarm
                if (alarm.weeklyRecurring == true){
                    checkWeeklyAlarmStatusAndCancel(alarm, requireContext())
                }
            }

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
                        scheduleAlarm(alarm = alarm, context = requireContext())
                    }
                } else {
                    //show a dialog for user to navigate to settings and turn on alarms and reminder
                    showMaterialDialog(title = getString(R.string.need_permission), message = getString(R.string.permission_helper), anim = R.raw.permission)
                }
            } else {
                showPickerAndSetAlarm { alarm->
                    alarmViewModel.createAlarm(alarm)
                    scheduleAlarm(alarm = alarm, context = requireContext())
                }
            }
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

}