package com.rita.calendarprooo.invitation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.rita.calendarprooo.CalendarProApplication
import com.rita.calendarprooo.R
import com.rita.calendarprooo.Util.Logger
import com.rita.calendarprooo.data.*
import com.rita.calendarprooo.data.source.CalendarRepository
import com.rita.calendarprooo.login.UserManager
import com.rita.calendarprooo.network.LoadApiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class InvitationViewModel(val repository: CalendarRepository) : ViewModel() {

    // loading animation
    val loadingStatus = MutableLiveData<Boolean?>()

    var user = MutableLiveData<User>()

    // Plan
    var invitationList = MutableLiveData<List<Plan>>()

    var invitationListReset = MutableLiveData<Boolean>()

    var invitationForCategoryList = MutableLiveData<MutableList<Invitation>>()

    var invitationListSize = MutableLiveData<Int>()

    // Category
    var invitationForCategoryListSize: LiveData<Int> =
        Transformations.map(invitationForCategoryList) {
            var size = 0
            if (!it.isNullOrEmpty()) {
                size = it.size
            }
            size
        }

    var isAccepted = MutableLiveData<Boolean>()

    var invitationAccepted = MutableLiveData<Invitation>()

    var invitationListUpdated = MutableLiveData<MutableList<Invitation>>()

    var plans = MutableLiveData<MutableList<Plan>>()

    var plansUpdate = MutableLiveData<MutableList<Plan>>()

    var startToUpdate = MutableLiveData<Boolean>()

    val categoryListTobeUpdated = MutableLiveData<MutableList<Category>>()

    val renewCategories = MutableLiveData<Boolean>()

    val updateCategoriesForUser = MutableLiveData<Boolean>()

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


    fun convertToSize(list: List<Plan>) {
        invitationListSize.value = list.size
    }


    fun getInvitations() {
        invitationList = repository.getLiveInvitations(user.value!!)

        invitationListReset.value = true
    }


    fun updatePlan(plan: Plan) {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            when (val result = repository.updatePlanExtra(plan)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    loadingStatus.value = false
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


    fun updateUser(user: User) {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            when (val result = repository.updateUserExtra(user)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    loadingStatus.value = false
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


    fun acceptOrDeclineInvitation(plan: Plan, isAccepted: Boolean) {
        loadingStatus.value = true
        // update plan collaborator & invitation
        val invitationUpdate = plan.invitation
        val collaboratorUpdate = plan.collaborator

        val indexRemoved = invitationUpdate?.indexOf(user.value!!.email)
        if (indexRemoved != null && indexRemoved >= 0) {
            invitationUpdate.removeAt(indexRemoved)
        }
        if (isAccepted) {
            collaboratorUpdate?.add(user.value!!.email)
        }

        plan.invitation = invitationUpdate
        plan.collaborator = collaboratorUpdate

        updatePlan(plan)

    }


    // update category

    fun renewInvitation(item: Invitation, accepted: Boolean) {
        loadingStatus.value = true
        isAccepted.value = accepted

        invitationAccepted.value = item
        val title = item.title
        val inviter = item.inviter
        val invitationList = user.value!!.invitationList
        val indexRemoved =
            invitationList.indexOfFirst { it.title == title && it.inviter == inviter }

        if (indexRemoved >= 0) {
            invitationList.removeAt(indexRemoved)
        }

        invitationListUpdated.value = invitationList
    }


    fun updateUserForInvitationList(list: MutableList<Invitation>) {
        val userRenewal = user.value
        userRenewal!!.invitationList = list

        updateUser(userRenewal)

        if (isAccepted.value == true) {
            startToUpdate.value = true
        }
    }


    // get All plans with query for the category
    fun getPlans() {
        val item = invitationAccepted.value

        coroutineScope.launch {
            _status.value = LoadApiStatus.LOADING

            val result = item?.let { repository.getPlansByInvitation(it) }

            plans.value = when (result) {
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


    fun addCollaboratorForPlan() {
        val list = plans.value

        if (list != null) {
            for (item in list) {
                if (!item.collaborator!!.contains(user.value?.email)) {
                    user.value?.email?.let { item.collaborator!!.add(it) }
                }
            }
        }

        plansUpdate.value = list
    }


    // update collaborator for All plans with loop in fragment
    fun updateCollaboratorForPlans(list: MutableList<Plan>) {
        for (plan in list) {
            updatePlan(plan)
        }
        renewCategories.value = true
    }


    // update user's categoryList
    fun renewCategories() {
        val item = invitationAccepted.value
        val title = item!!.title
        val categoryList = user.value!!.categoryList
        val index = categoryList.indexOfFirst { it.name == title }

        // if the category isn't in User's categoryList, then update it
        if (index == -1) {
            val categoryAdded = Category(
                name = title,
                isSelected = false,
                mutableListOf<String>(user.value!!.email)
            )
            categoryList.add(categoryAdded)

            categoryListTobeUpdated.value = categoryList
            updateCategoriesForUser.value = true

        } else {
            updateCategoriesForUser.value = false
            loadingStatus.value = false
        }

    }


    fun updateCategoriesForUser() {
        val userRenewal = user.value
        userRenewal!!.categoryList = categoryListTobeUpdated.value!!

        updateUser(userRenewal)

        loadingStatus.value = false

    }


    fun doneWritten() {
        invitationAccepted.value = null
        startToUpdate.value = null
        updateSuccess.value = null
        categoryListTobeUpdated.value = null
        renewCategories.value = null
        updateCategoriesForUser.value = null
    }


    private fun getUserData(userId: String) {
        user = repository.getUser(userId)
        UserManager.user = repository.getUser(userId)
    }


    init {

        invitationList.value = null
        updateSuccess.value = null
        categoryListTobeUpdated.value = null
        renewCategories.value = null
        updateCategoriesForUser.value = null

        UserManager.userToken?.let { getUserData(it) }
    }

}