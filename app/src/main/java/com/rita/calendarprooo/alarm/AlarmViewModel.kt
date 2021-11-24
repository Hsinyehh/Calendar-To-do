package com.rita.calendarprooo.alarm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rita.calendarprooo.data.Plan

class AlarmViewModel : ViewModel() {
    var plan = MutableLiveData<Plan>()

    var alarmTime = MutableLiveData<Long>()

    init {
        alarmTime.value = null
    }
}