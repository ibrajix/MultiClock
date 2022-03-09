package com.ibrajix.multiclock.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.internal.common.CrashlyticsCore
import com.ibrajix.multiclock.database.Alarm
import com.ibrajix.multiclock.database.AlarmDao
import com.ibrajix.multiclock.ui.repository.AlarmRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor (private val alarmRepository: AlarmRepository) : ViewModel() {


    fun createAlarm(alarm: Alarm){
        viewModelScope.launch {
            alarmRepository.createAlarm(alarm)
        }
    }

    private val _getAllAlarmsResult = MutableSharedFlow<List<Alarm>>()
    val getAllAlarmsResult : SharedFlow<List<Alarm>> = _getAllAlarmsResult

    private val _getSingleAlarmResult = MutableSharedFlow<Alarm>()
    val getSingleAlarmResult : SharedFlow<Alarm> = _getSingleAlarmResult

    private val _getAllAlarmsWhoseStatusIsTrue = MutableSharedFlow<List<Alarm>>()
    val getAllAlarmsWhoseStatusIsTrue : SharedFlow<List<Alarm>> = _getAllAlarmsWhoseStatusIsTrue

    fun getAllAlarms(){
        viewModelScope.launch {
            alarmRepository.getAllAlarms
                .catch {e->
                    FirebaseCrashlytics.getInstance().log(e.toString())
                }
                .collect {
                    _getAllAlarmsResult.emit(it)
                }
        }
    }

    fun getAllAlarmsWhoseStatusIsTrue(){
        viewModelScope.launch {
            alarmRepository.getAlarmWhoseStatusIsTrue
                .catch { e->
                    FirebaseCrashlytics.getInstance().log(e.toString())
                }
                .collect {
                    _getAllAlarmsWhoseStatusIsTrue.emit(it)
                }
        }
    }

    fun deleteAlarm(id: Int){
        viewModelScope.launch {
            alarmRepository.deleteSingleAlarm(id)
        }
    }

    fun getSingleAlarm(alarmId: Int) {

        viewModelScope.launch {
            alarmRepository.getSingleAlarm(alarmId)

                .catch { e->
                    FirebaseCrashlytics.getInstance().log(e.toString())
                }
                .collect {
                    _getSingleAlarmResult.emit(it)
                }
        }

    }

    fun updateAlarmVibrateStatus(status: Boolean, alarmId: Int){
        viewModelScope.launch {
            alarmRepository.updateAlarmVibrateStatus(status, alarmId)
        }
    }

    fun updateAlarmStatus(status: Boolean, alarmId: Int){
        viewModelScope.launch {
            alarmRepository.updateAlarmStatus(status, alarmId)
        }
    }

    fun updateAlarmMonday(status: Boolean, alarmId: Int){
        viewModelScope.launch {
            alarmRepository.updateAlarmMonday(status, alarmId)
        }
    }

    fun updateAlarmTuesday(status: Boolean, alarmId: Int){
        viewModelScope.launch {
            alarmRepository.updateAlarmTuesday(status, alarmId)
        }
    }

    fun updateAlarmWednesday(status: Boolean, alarmId: Int){
        viewModelScope.launch {
            alarmRepository.updateAlarmWednesday(status, alarmId)
        }
    }

    fun updateAlarmThursday(status: Boolean, alarmId: Int){
        viewModelScope.launch {
            alarmRepository.updateAlarmThursday(status, alarmId)
        }
    }

    fun updateAlarmFriday(status: Boolean, alarmId: Int){
        viewModelScope.launch {
            alarmRepository.updateAlarmFriday(status, alarmId)
        }
    }

    fun updateAlarmSaturday(status: Boolean, alarmId: Int){
        viewModelScope.launch {
            alarmRepository.updateAlarmSaturday(status, alarmId)
        }
    }

    fun updateAlarmSunday(status: Boolean, alarmId: Int){
        viewModelScope.launch {
            alarmRepository.updateAlarmSunday(status, alarmId)
        }
    }

    fun updateAlarmWeeklyRecurring(status: Boolean, alarmId: Int){
        viewModelScope.launch {
            alarmRepository.updateAlarmWeeklyRecurring(status, alarmId)
        }
    }

    fun updateAlarm(alarm: Alarm){
        viewModelScope.launch {
            alarmRepository.updateAlarm(alarm)
        }
    }

}