package com.rita.calendarprooo.invite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rita.calendarprooo.CalendarProApplication
import com.rita.calendarprooo.R
import com.rita.calendarprooo.data.Plan
import com.rita.calendarprooo.data.Result
import com.rita.calendarprooo.data.source.CalendarRepository
import com.rita.calendarprooo.network.LoadApiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class InviteViewModel(val repository: CalendarRepository) : ViewModel() {

    var plan = MutableLiveData<Plan>()

    var email = MutableLiveData<String>()

    var invitation = MutableLiveData<MutableList<String>>()

    var isInvited = MutableLiveData<Boolean>()

    var isCollaborator = MutableLiveData<Boolean>()

    var updateSuccess = MutableLiveData<Boolean>()

    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String>()

    val error: LiveData<String>
        get() = _error

    // status for the loading icon of swl
    private val _refreshStatus = MutableLiveData<Boolean>()

    val refreshStatus: LiveData<Boolean>
        get() = _refreshStatus

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)


    fun createInvitation() {
        val invitationGet = plan.value?.invitation
        val collaboratorGet = plan.value?.collaborator

        val isInvitedCheck: Boolean? = invitationGet?.contains(email.value)
        val isCollaboratorCheck: Boolean? = collaboratorGet?.contains(email.value)

        if (isInvitedCheck == false && isCollaboratorCheck == false) {
            email.value?.let { invitationGet.add(it) }

            // update plan
            val planUpdate = plan.value
            planUpdate!!.invitation = invitationGet
            plan.value = planUpdate

            // update invitation to start observer
            invitation.value = invitationGet

        } else if (isInvitedCheck == true) {
            isInvited.value = isInvitedCheck
        } else if (isCollaboratorCheck == true) {
            isCollaborator.value = isCollaboratorCheck
        }

    }


    fun updateInvitation() {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            when (val result = repository.updatePlanExtra(plan.value!!)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    updateSuccess.value = true
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                }
                else -> {
                    _error.value =
                        CalendarProApplication.instance.getString(R.string.Error)
                    _status.value = LoadApiStatus.ERROR
                }
            }
        }
    }


    fun doneUpdate() {
        invitation.value = null
        isInvited.value = null
        updateSuccess.value = null
    }

}