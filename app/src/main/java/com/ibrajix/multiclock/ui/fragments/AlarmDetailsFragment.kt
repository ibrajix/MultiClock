package com.ibrajix.multiclock.ui.fragments

import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.media.RingtoneManager
import android.net.Uri
import android.os.*
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
import com.ibrajix.multiclock.R
import com.ibrajix.multiclock.databinding.FragmentAlarmDetailsBinding
import com.ibrajix.multiclock.ui.viewmodel.AlarmViewModel
import com.ibrajix.multiclock.utils.AlarmUtility.cancelAlarm
import com.ibrajix.multiclock.utils.AlarmUtility.cancelWeeklyAlarm
import com.ibrajix.multiclock.utils.AlarmUtility.getRecurringDays
import com.ibrajix.multiclock.utils.AlarmUtility.scheduleWeeklyAlarm
import com.ibrajix.multiclock.utils.AlarmUtility.showConfirmationDeleteDialog
import com.ibrajix.multiclock.utils.Constants.ALARM_FRIDAY
import com.ibrajix.multiclock.utils.Constants.ALARM_MONDAY
import com.ibrajix.multiclock.utils.Constants.ALARM_SATURDAY
import com.ibrajix.multiclock.utils.Constants.ALARM_SUNDAY
import com.ibrajix.multiclock.utils.Constants.ALARM_THURSDAY
import com.ibrajix.multiclock.utils.Constants.ALARM_TUESDAY
import com.ibrajix.multiclock.utils.Constants.ALARM_WEDNESDAY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@Suppress("DEPRECATION")
@AndroidEntryPoint
class AlarmDetailsFragment : Fragment() {

    private var _binding: FragmentAlarmDetailsBinding? = null
    private val binding get() = _binding!!
    private var vibrator: Vibrator? = null


    private val alarmViewModel: AlarmViewModel by viewModels()
    private val args: AlarmDetailsFragmentArgs by navArgs()

    var mondayAlarm: Boolean = false
    var tuesdayAlarm: Boolean = false
    var wednesdayAlarm: Boolean = false
    var thursdayAlarm: Boolean = false
    var fridayAlarm: Boolean = false
    var saturdayAlarm: Boolean = false
    var sundayAlarm: Boolean = false
    var isVibrateChecked: Boolean? = null

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initView(){

        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                requireContext().getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            requireContext().getSystemService(VIBRATOR_SERVICE) as Vibrator
        }

        setView()
        handleClicks()
        checkAlarmRecurringEvent()
    }

    private fun setView(){

        binding.txtAlarmTime.text = args.alarm.time
        binding.switchBtnVibrate.isChecked = args.alarm.vibrate?:true

        val alarmTone: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val ringtoneAlarm = RingtoneManager.getRingtone(requireContext(), alarmTone)
        binding.txtRingtone.text = ringtoneAlarm.getTitle(requireContext())

    }

    private fun checkAlarmRecurringEvent(){

        args.alarm.id?.let { alarmViewModel.getSingleAlarm(it) }

        //observe/collect
        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                alarmViewModel.getSingleAlarmResult.collect { alarm->
                    isVibrateChecked = alarm.vibrate

                   val recurring = getRecurringDays(alarm.monday, alarm.tuesday, alarm.wednesday, alarm.thursday, alarm.friday, alarm.saturday, alarm.sunday)

                    binding.txtSchedule.text = recurring

                    if (alarm.monday == true || alarm.tuesday == true || alarm.wednesday == true || alarm.thursday == true
                        || alarm.friday == true || alarm.saturday == true || alarm.sunday == true
                    ){
                        alarm.id?.let { alarmViewModel.updateAlarmWeeklyRecurring(true, it) }
                    }

                    if (alarm.monday == true){
                        binding.monday.background = ContextCompat.getDrawable(requireContext(), R.drawable.circular_shape_filled)
                        mondayAlarm = true
                    }
                    else {
                        binding.monday.background = ContextCompat.getDrawable(requireContext(), R.drawable.circular_shape_not_filled)
                        mondayAlarm = false
                    }
                    if (alarm.tuesday == true){
                        binding.tuesday.background = ContextCompat.getDrawable(requireContext(), R.drawable.circular_shape_filled)
                        tuesdayAlarm = true
                    }
                    else {
                        binding.tuesday.background = ContextCompat.getDrawable(requireContext(), R.drawable.circular_shape_not_filled)
                        tuesdayAlarm = false
                    }
                    if (alarm.wednesday == true){
                        binding.wednesday.background = ContextCompat.getDrawable(requireContext(), R.drawable.circular_shape_filled)
                        wednesdayAlarm = true
                    }
                    else {
                        binding.wednesday.background = ContextCompat.getDrawable(requireContext(), R.drawable.circular_shape_not_filled)
                        wednesdayAlarm = false
                    }
                    if (alarm.thursday == true){
                        binding.thursday.background = ContextCompat.getDrawable(requireContext(), R.drawable.circular_shape_filled)
                        thursdayAlarm = true
                    }
                    else {
                        binding.thursday.background = ContextCompat.getDrawable(requireContext(), R.drawable.circular_shape_not_filled)
                        thursdayAlarm = false
                    }
                    if (alarm.friday == true){
                        binding.friday.background = ContextCompat.getDrawable(requireContext(), R.drawable.circular_shape_filled)
                        fridayAlarm = true
                    }
                    else {
                        binding.friday.background = ContextCompat.getDrawable(requireContext(), R.drawable.circular_shape_not_filled)
                        fridayAlarm = false
                    }
                    if (alarm.saturday == true){
                        binding.saturday.background = ContextCompat.getDrawable(requireContext(), R.drawable.circular_shape_filled)
                        saturdayAlarm = true
                    }
                    else {
                        binding.saturday.background = ContextCompat.getDrawable(requireContext(), R.drawable.circular_shape_not_filled)
                        saturdayAlarm = false
                    }
                    if (alarm.sunday == true){
                        binding.sunday.background = ContextCompat.getDrawable(requireContext(), R.drawable.circular_shape_filled)
                        sundayAlarm = true
                    }
                    else {
                        binding.sunday.background = ContextCompat.getDrawable(requireContext(), R.drawable.circular_shape_not_filled)
                        sundayAlarm = false
                    }

                }
            }
        }

    }

    private fun handleClicks(){

        //on click delete alarm
        binding.txtDelete.setOnClickListener {
            showConfirmationDeleteDialog(alarm = args.alarm, getString(R.string.delete_alarm_title), getString(R.string.delete_alarm_message_helper), callback = {
                //on click delete
                //remove/delete from room database
                args.alarm.id?.let { it1 -> alarmViewModel.deleteAlarm(it1) }
                //cancel all pending alarm to be scheduled
                cancelAlarm(alarm = args.alarm, requireContext())
                //cancel weekly alarm if scheduled

                //go back to main fragment
                findNavController().popBackStack()
            })
        }

        //on click back
        binding.imgBack.setOnClickListener {
            findNavController().popBackStack()
        }

        /*handle alarm date clicks*/
        binding.monday.setOnClickListener {

            mondayAlarm = if (mondayAlarm){
                args.alarm.id?.let { it1 -> alarmViewModel.updateAlarmMonday(false, it1) }
                cancelWeeklyAlarm(args.alarm, requireContext(), ALARM_MONDAY)
                false
            } else {
                args.alarm.id?.let { it1 -> alarmViewModel.updateAlarmMonday(true, it1) }
                scheduleWeeklyAlarm(args.alarm, ALARM_MONDAY, requireContext())
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
                cancelWeeklyAlarm(args.alarm, requireContext(), ALARM_TUESDAY)
                false
            } else {
                args.alarm.id?.let { it1 -> alarmViewModel.updateAlarmTuesday(true, it1) }
                scheduleWeeklyAlarm(args.alarm, ALARM_TUESDAY, requireContext())
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
                cancelWeeklyAlarm(args.alarm, requireContext(), ALARM_WEDNESDAY)
                false
            } else {
                args.alarm.id?.let { it1 -> alarmViewModel.updateAlarmWednesday(true, it1) }
                scheduleWeeklyAlarm(args.alarm, ALARM_WEDNESDAY, requireContext())
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
                cancelWeeklyAlarm(args.alarm, requireContext(), ALARM_THURSDAY)
                false
            } else {
                args.alarm.id?.let { it1 -> alarmViewModel.updateAlarmThursday(true, it1) }
                scheduleWeeklyAlarm(args.alarm, ALARM_THURSDAY, requireContext())
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
                cancelWeeklyAlarm(args.alarm, requireContext(), ALARM_FRIDAY)
                false
            } else {
                args.alarm.id?.let { it1 -> alarmViewModel.updateAlarmFriday(true, it1) }
                scheduleWeeklyAlarm(args.alarm, ALARM_FRIDAY, requireContext())
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
                cancelWeeklyAlarm(args.alarm, requireContext(), ALARM_SATURDAY)
                false
            } else {
                args.alarm.id?.let { it1 -> alarmViewModel.updateAlarmSaturday(true, it1) }
                scheduleWeeklyAlarm(args.alarm, ALARM_SATURDAY, requireContext())
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
                cancelWeeklyAlarm(args.alarm, requireContext(), ALARM_SUNDAY)
                false
            } else {
                args.alarm.id?.let { it1 -> alarmViewModel.updateAlarmSunday(true, it1) }
                scheduleWeeklyAlarm(args.alarm, ALARM_SUNDAY, requireContext())
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

        //on click switch button
        binding.switchBtnVibrate.setOnCheckedChangeListener { compoundButton, isChecked ->

            if (isVibrateChecked == true && !isChecked){
                binding.switchBtnVibrate.isChecked = false
                isVibrateChecked = false
                args.alarm.id?.let { alarmViewModel.updateAlarmVibrateStatus(alarmId = it, status = false) }
            }
            else {

                binding.switchBtnVibrate.isChecked = true
                isVibrateChecked = true

                args.alarm.id?.let { alarmViewModel.updateAlarmVibrateStatus(alarmId = it, status = true) }

                //make that vibrate sound
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(
                        VibrationEffect.createOneShot(
                            500,
                            VibrationEffect.DEFAULT_AMPLITUDE
                        )
                    )
                } else {
                    //deprecated in API 26
                    vibrator?.vibrate(500)
                }

            }

        }

    }

}