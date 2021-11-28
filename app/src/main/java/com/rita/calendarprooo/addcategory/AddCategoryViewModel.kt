package com.rita.calendarprooo.addcategory

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rita.calendarprooo.CalendarProApplication
import com.rita.calendarprooo.R
import com.rita.calendarprooo.data.Category
import com.rita.calendarprooo.data.Plan
import com.rita.calendarprooo.data.Result
import com.rita.calendarprooo.data.User
import com.rita.calendarprooo.data.source.CalendarRepository
import com.rita.calendarprooo.login.UserManager
import com.rita.calendarprooo.network.LoadApiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AddCategoryViewModel(val repository: CalendarRepository) : ViewModel() {

    var currentUser = MutableLiveData<User>()

    var categoryListForAutoInput = MutableLiveData<MutableList<String>>()

    var categoryList = MutableLiveData<MutableList<Category>>()

    var categoryAdded = MutableLiveData<String>()

    var planGet = MutableLiveData<Plan>()

    private var userUpdate = MutableLiveData<User>()

    // check if the plan is created or edited
    var isPlanCreated = MutableLiveData<Boolean>()

    var startToCreate = MutableLiveData<Boolean>()

    var startToPrepare = MutableLiveData<Boolean>()

    var startToUpdate = MutableLiveData<Boolean>()

    var startToNavigate = MutableLiveData<Boolean>()

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


    fun onclickToCreate() {
        startToCreate.value = true
    }


    fun getCategoryFromUserFirst() {
        if (planGet.value?.id == "") {
            isPlanCreated.value = true
            getCategoryFromUser(true)
        } else {
            isPlanCreated.value = false
            getCategoryFromUser(false)
        }
    }


    // if the plan is at created Status, then give categoryList value
    private fun getCategoryFromUser(isCreated: Boolean) {
        categoryListForAutoInput.value = convertToStringList(currentUser.value!!.categoryList)
        if (isCreated) {
            categoryList.value = currentUser.value!!.categoryList
            Log.i("Rita", "getCategoryFromUser - category:　${categoryList.value}")
        }
    }


    fun prepareForCategory() {
        if (categoryAdded.value.isNullOrBlank()) {
            startToUpdate.value = false
        } else {
            val newCategory = Category("${categoryAdded.value}", false)
            val list = categoryList.value
            val userRenewal = currentUser.value

            list?.add(newCategory)
            userRenewal?.categoryList?.add(newCategory)

            categoryList.value = list
            userUpdate.value = userRenewal

            startToUpdate.value = true
        }
    }


    // if the plan is at edited Status, then give categoryList value
    fun getCategoryFromPlan() {
        categoryList.value = planGet.value?.categoryList
        startToPrepare.value = true
    }


    // Both Conditions Needs the function below
    fun updateUser() {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            when (val result = repository.updateUserExtra(userUpdate.value!!)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    startToNavigate.value = true
                    Log.i("Rita", "add VM updateUser $result")
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                    Log.i("Rita", "add VM updateUser: $result")
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                    Log.i("Rita", "add VM updateUser: $result")
                }
                else -> {
                    _error.value =
                        CalendarProApplication.instance.getString(R.string.Error)
                    _status.value = LoadApiStatus.ERROR
                }
            }
        }
    }


    // only for edit status
    fun updatePlan() {

        // update plan for categoryList
        val planRenewal = planGet.value
        planRenewal!!.categoryList = categoryList.value

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            when (val result = repository.updatePlanExtra(planRenewal)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    Log.i("Rita", "add VM updatePlan: $result")
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                    Log.i("Rita", "add VM updatePlan: $result")
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                    Log.i("Rita", "add VM updatePlan: $result")
                }
                else -> {
                    _error.value =
                        CalendarProApplication.instance.getString(R.string.Error)
                    _status.value = LoadApiStatus.ERROR
                }
            }
        }
    }


    private fun convertToStringList(list: List<Category>): MutableList<String> {
        val stringList = mutableListOf<String>()

        for (item in list) {
            stringList.add(item.name)
        }
        return stringList
    }


    fun doneNavigated() {
        startToCreate.value = null
        startToPrepare.value = null
        startToUpdate.value = null
        startToNavigate.value = null
    }


    private fun getUserData(userId: String) {
        Log.d("Rita", "userId: $userId")
        currentUser = repository.getUser(userId)
        UserManager.user = repository.getUser(userId)
    }


    init {
        UserManager.userToken?.let { getUserData(it) }

        startToCreate.value = null
        startToPrepare.value = null
        startToUpdate.value = null
        startToNavigate.value = null
    }


}