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

    fun deleteAlarm(id: Int){
        viewModelScope.launch {
            alarmRepository.deleteSingleAlarm(id)
        }
    }

    fun updateAlarmStatus(status: Boolean, alarmId: Int){
        viewModelScope.launch {
            alarmRepository.updateAlarmStatus(status, alarmId)
        }
    }

}