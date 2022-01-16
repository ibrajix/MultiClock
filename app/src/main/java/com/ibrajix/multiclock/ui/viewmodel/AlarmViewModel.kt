package com.ibrajix.multiclock.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.internal.common.CrashlyticsCore
import com.ibrajix.multiclock.database.Alarm
import com.ibrajix.multiclock.database.AlarmDao
import com.ibrajix.multiclock.ui.repository.AlarmRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AlarmViewModel(private val alarmRepository: AlarmRepository) : ViewModel() {


    fun createAlarm(alarm: Alarm){
        viewModelScope.launch {
            alarmRepository.createAlarm(alarm)
        }
    }

    private val _getAllAlarmsResult = MutableSharedFlow<List<Alarm>>()
    val getAllAlarmsResult : SharedFlow<List<Alarm>> = _getAllAlarmsResult

    fun getAllAlarms(alarm: Alarm){
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

}

@Suppress("UNCHECKED_CAST")
class AlarmViewModelFactory(private val alarmRepository: AlarmRepository) : ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AlarmViewModel(alarmRepository) as T
    }
}