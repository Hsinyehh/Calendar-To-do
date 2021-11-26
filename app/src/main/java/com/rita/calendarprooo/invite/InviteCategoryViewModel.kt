package com.rita.calendarprooo.invite


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rita.calendarprooo.CalendarProApplication
import com.rita.calendarprooo.R
import com.rita.calendarprooo.data.Invitation
import com.rita.calendarprooo.data.Result
import com.rita.calendarprooo.data.User
import com.rita.calendarprooo.data.source.CalendarRepository
import com.rita.calendarprooo.network.LoadApiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class InviteCategoryViewModel(val repository: CalendarRepository) : ViewModel() {

    var category = MutableLiveData<String>()

    var categoryPosition = MutableLiveData<Int>()

    var user = MutableLiveData<User>()

    var userTobeInvited = MutableLiveData<User>()

    var userUpdate = MutableLiveData<User>()

    var email = MutableLiveData<String>()

    var invitation = MutableLiveData<Invitation>()

    var invitationList = MutableLiveData<MutableList<Invitation>>()

    var isInputBlank = MutableLiveData<Boolean>()

    var isUserNotExist = MutableLiveData<Boolean>()

    var isInvited = MutableLiveData<Boolean>()

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


    fun getUser() {
        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = repository.getUserByEmail(email.value!!)

            userTobeInvited.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    result.data
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                    null
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                    null
                }
                else -> {
                    _error.value = CalendarProApplication.instance.getString(R.string.error)
                    _status.value = LoadApiStatus.ERROR
                    null
                }
            }
            _refreshStatus.value = false
        }
    }


    fun getUserInvited() {
        if (!email.value.isNullOrBlank()) {
            getUser()
        } else {
            isInputBlank.value = true
        }
    }


    fun createInvitation() {
        val list = userTobeInvited.value?.invitationList

        invitation.value = Invitation(
            title = category.value,
            inviter = user.value?.email,
            collaborator = user.value?.categoryList?.get(categoryPosition.value!!)!!.collaborator
        )

        if (list != null) {
            if (list.contains(invitation.value!!)) {
                isInvited.value = true
            } else {
                list.add(invitation.value!!)
            }
        }

        // user update
        val userRenewal = userTobeInvited.value
        if (userRenewal != null) {
            userRenewal.invitationList = list!!
        }
        userUpdate.value = userRenewal

        // invitationList update to start observer
        invitationList.value = list

    }


    fun updateInvitation() {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            when (val result = repository.updateUserExtra(userUpdate.value!!)) {
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


    init {
        invitation.value = null
        invitationList.value = null
        updateSuccess.value = null
    }

}