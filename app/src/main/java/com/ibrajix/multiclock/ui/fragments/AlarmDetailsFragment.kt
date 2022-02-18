package com.ibrajix.multiclock.ui.fragments

import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.ibrajix.multiclock.R
import com.ibrajix.multiclock.databinding.FragmentAlarmDetailsBinding
import com.ibrajix.multiclock.ui.viewmodel.AlarmViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_alarm_details.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AlarmDetailsFragment : Fragment() {

    private var _binding: FragmentAlarmDetailsBinding? = null
    private val binding get() = _binding!!

    private val alarmViewModel: AlarmViewModel by viewModels()
    val args: AlarmDetailsFragmentArgs by navArgs()

    var mondayAlarm: Boolean = false
    var tuesdayAlarm: Boolean = false
    var wednesdayAlarm: Boolean = false
    var thursdayAlarm: Boolean = false
    var fridayAlarm: Boolean = false
    var saturdayAlarm: Boolean = false
    var sundayAlarm: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAlarmDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView(){
        setView()
        handleClicks()
        checkAlarmRecurringEvent()
    }

    private fun setView(){

        binding.txtAlarmTime.text = args.alarm.time

        val alarmTone: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val ringtoneAlarm = RingtoneManager.getRingtone(requireContext(), alarmTone)
        binding.txtRingtone.text = ringtoneAlarm.getTitle(requireContext())

    }

    private fun checkAlarmRecurringEvent(){

        alarmViewModel.getAllAlarms()

        //observe/collect
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                alarmViewModel.getAllAlarmsResult.collect { alarms->
                  for (alarm in alarms){
                      if (alarm.monday == true){
                          binding.monday.background = ContextCompat.getDrawable(requireContext(), R.drawable.circular_shape_filled)
                          mondayAlarm = true
                      }
                      else {
                          binding.monday.background = ContextCompat.getDrawable(requireContext(), R.drawable.circular_shape_not_filled)
                      }
                      if (alarm.tuesday == true){
                          binding.tuesday.background = ContextCompat.getDrawable(requireContext(), R.drawable.circular_shape_filled)
                          tuesdayAlarm = true
                      }
                      else {
                          binding.tuesday.background = ContextCompat.getDrawable(requireContext(), R.drawable.circular_shape_not_filled)
                      }
                      if (alarm.wednesday == true){
                          binding.wednesday.background = ContextCompat.getDrawable(requireContext(), R.drawable.circular_shape_filled)
                          wednesdayAlarm = true
                      }
                      else {
                          binding.wednesday.background = ContextCompat.getDrawable(requireContext(), R.drawable.circular_shape_not_filled)
                      }
                      if (alarm.thursday == true){
                          binding.thursday.background = ContextCompat.getDrawable(requireContext(), R.drawable.circular_shape_filled)
                          thursdayAlarm = true
                      }
                      else {
                          binding.thursday.background = ContextCompat.getDrawable(requireContext(), R.drawable.circular_shape_not_filled)
                      }
                      if (alarm.friday == true){
                          binding.friday.background = ContextCompat.getDrawable(requireContext(), R.drawable.circular_shape_filled)
                          fridayAlarm = true
                      }
                      else {
                          binding.friday.background = ContextCompat.getDrawable(requireContext(), R.drawable.circular_shape_not_filled)
                      }
                      if (alarm.saturday == true){
                          binding.saturday.background = ContextCompat.getDrawable(requireContext(), R.drawable.circular_shape_filled)
                          saturdayAlarm = true
                      }
                      else {
                          binding.saturday.background = ContextCompat.getDrawable(requireContext(), R.drawable.circular_shape_not_filled)
                      }
                      if (alarm.sunday == true){
                          binding.sunday.background = ContextCompat.getDrawable(requireContext(), R.drawable.circular_shape_filled)
                          sundayAlarm = true
                      }
                      else {
                          binding.sunday.background = ContextCompat.getDrawable(requireContext(), R.drawable.circular_shape_not_filled)
                      }
                  }
                }
            }
        }

    }

    private fun handleClicks(){

        //on click back
        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }

        /*handle alarm date clicks*/
        binding.monday.setOnClickListener {

            mondayAlarm = if (mondayAlarm){
                args.alarm.id?.let { it1 -> alarmViewModel.updateAlarmMonday(false, it1) }
                false
            } else {
                args.alarm.id?.let { it1 -> alarmViewModel.updateAlarmMonday(true, it1) }
                true
            }

            if(binding.monday.background.constantState?.equals(ContextCompat.getDrawable(requireContext(), R.drawable.circular_shape_not_filled)?.constantState) == false){
                binding.monday.background = ContextCompat.getDrawable(requireContext(), R.drawable.circular_shape_not_filled)
            }
            else{
                binding.monday.background = ContextCompat.getDrawable(requireContext(), R.drawable.circular_shape_filled)
            }

        }

        binding.tuesday.setOnClickListener {

           tuesdayAlarm = if (tuesdayAlarm){
                args.alarm.id?.let { it1 -> alarmViewModel.updateAlarmTuesday(false, it1) }
                false
            } else {
                args.alarm.id?.let { it1 -> alarmViewModel.updateAlarmTuesday(true, it1) }
                true
            }

            if(binding.tuesday.background.constantState?.equals(ContextCompat.getDrawable(requireContext(), R.drawable.circular_shape_not_filled)?.constantState) == false){
                //do something if this is the correct drawable
                binding.tuesday.background = ContextCompat.getDrawable(requireContext(), R.drawable.circular_shape_not_filled)
            }
            else{
                binding.tuesday.background = ContextCompat.getDrawable(requireContext(), R.drawable.circular_shape_filled)
            }

        }

        binding.wednesday.setOnClickListener {

            wednesdayAlarm = if (wednesdayAlarm){
                args.alarm.id?.let { it1 -> alarmViewModel.updateAlarmWednesday(false, it1) }
                false
            } else {
                args.alarm.id?.let { it1 -> alarmViewModel.updateAlarmWednesday(true, it1) }
                true
            }

            if(binding.wednesday.background.constantState?.equals(ContextCompat.getDrawable(requireContext(), R.drawable.circular_shape_not_filled)?.constantState) == false){
                //do something if this is the correct drawable
                binding.wednesday.background = ContextCompat.getDrawable(requireContext(), R.drawable.circular_shape_not_filled)
            }
            else{
                binding.wednesday.background = ContextCompat.getDrawable(requireContext(), R.drawable.circular_shape_filled)
            }

        }

        binding.thursday.setOnClickListener {

            thursdayAlarm = if (thursdayAlarm){
                args.alarm.id?.let { it1 -> alarmViewModel.updateAlarmThursday(false, it1) }
                false
            } else {
                args.alarm.id?.let { it1 -> alarmViewModel.updateAlarmThursday(true, it1) }
                true
            }

            if(binding.thursday.background.constantState?.equals(ContextCompat.getDrawable(requireContext(), R.drawable.circular_shape_not_filled)?.constantState) == false){
                //do something if this is the correct drawable
                binding.thursday.background = ContextCompat.getDrawable(requireContext(), R.drawable.circular_shape_not_filled)
            }
            else{
                binding.thursday.background = ContextCompat.getDrawable(requireContext(), R.drawable.circular_shape_filled)
            }
        }

        binding.friday.setOnClickListener {

            fridayAlarm = if (fridayAlarm){
                args.alarm.id?.let { it1 -> alarmViewModel.updateAlarmFriday(false, it1) }
                false
            } else {
                args.alarm.id?.let { it1 -> alarmViewModel.updateAlarmFriday(true, it1) }
                true
            }

            if(binding.friday.background.constantState?.equals(ContextCompat.getDrawable(requireContext(), R.drawable.circular_shape_not_filled)?.constantState) == false){
                //do something if this is the correct drawable
                binding.friday.background = ContextCompat.getDrawable(requireContext(), R.drawable.circular_shape_not_filled)
            }
            else{
                binding.friday.background = ContextCompat.getDrawable(requireContext(), R.drawable.circular_shape_filled)
            }

        }

        binding.saturday.setOnClickListener {

            saturdayAlarm = if (saturdayAlarm){
                args.alarm.id?.let { it1 -> alarmViewModel.updateAlarmSaturday(false, it1) }
                false
            } else {
                args.alarm.id?.let { it1 -> alarmViewModel.updateAlarmSaturday(true, it1) }
                true
            }

            if(binding.saturday.background.constantState?.equals(ContextCompat.getDrawable(requireContext(), R.drawable.circular_shape_not_filled)?.constantState) == false){
                //do something if this is the correct drawable
                binding.saturday.background = ContextCompat.getDrawable(requireContext(), R.drawable.circular_shape_not_filled)
            }
            else{
                binding.saturday.background = ContextCompat.getDrawable(requireContext(), R.drawable.circular_shape_filled)
            }
        }

        binding.sunday.setOnClickListener {

            sundayAlarm = if (sundayAlarm){
                args.alarm.id?.let { it1 -> alarmViewModel.updateAlarmSunday(false, it1) }
                false
            } else {
                args.alarm.id?.let { it1 -> alarmViewModel.updateAlarmSunday(true, it1) }
                true
            }

            if(binding.sunday.background.constantState?.equals(ContextCompat.getDrawable(requireContext(), R.drawable.circular_shape_not_filled)?.constantState) == false){
                //do something if this is the correct drawable
                binding.sunday.background = ContextCompat.getDrawable(requireContext(), R.drawable.circular_shape_not_filled)
            }
            else{
                binding.sunday.background = ContextCompat.getDrawable(requireContext(), R.drawable.circular_shape_filled)
            }
        }

    }

}