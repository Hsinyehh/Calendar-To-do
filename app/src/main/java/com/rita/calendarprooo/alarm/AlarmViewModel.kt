package com.rita.calendarprooo.alarm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rita.calendarprooo.data.Plan

class AlarmViewModel : ViewModel() {
    val plan = MutableLiveData<Plan>()

    val alarmTime = MutableLiveData<Long>()

    init {
        alarmTime.value = null
    }
}