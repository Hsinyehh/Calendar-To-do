package com.rita.calendarprooo.invite

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rita.calendarprooo.data.Plan


class InviteViewModel : ViewModel() {

    var plan = MutableLiveData<Plan>()

    var email = MutableLiveData<String>()

}