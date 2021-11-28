package com.rita.calendarprooo.edit

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.rita.calendarprooo.CalendarProApplication
import com.rita.calendarprooo.R
import com.rita.calendarprooo.data.*
import com.rita.calendarprooo.data.source.CalendarRepository
import com.rita.calendarprooo.ext.stringToTimestamp
import com.rita.calendarprooo.login.UserManager
import com.rita.calendarprooo.network.LoadApiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class EditViewModel(plan: Plan, val repository: CalendarRepository) : ViewModel() {

    val loadingStatus = MutableLiveData<Boolean?>()

    var currentUser = UserManager.user

    var updatedUser = MutableLiveData<User>()

    var planGet = MutableLiveData<Plan?>()

    var categoryStatus = MutableLiveData<Category?>()

    var categoryPosition = MutableLiveData<Int?>()

    var categoryList = MutableLiveData<MutableList<Category>?>()

    var checkText = MutableLiveData<String?>()

    var id = MutableLiveData<String?>()

    var title = MutableLiveData<String?>()

    var description = MutableLiveData<String?>()

    var location = MutableLiveData<String?>()

    var newPlan = MutableLiveData<Plan?>()

    var isTodoList = MutableLiveData<Boolean?>()

    var checkList = MutableLiveData<MutableList<Check>?>()

    var start_time = MutableLiveData<Long>()

    var end_time = MutableLiveData<Long>()

    var start_time_detail = MutableLiveData<List<Int>>()

    var end_time_detail = MutableLiveData<List<Int>>()

    var doneConverted= MutableLiveData<Boolean?>()

    var editStatus = MutableLiveData<Boolean?>()

    var collaborator = MutableLiveData<MutableList<String>>()

    private val db = Firebase.firestore

    private val newPlanRef = db.collection("plan").document()

    private val emptyCheckList = mutableListOf<Check>()

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


    fun toToListModeChanged() {
        isTodoList.value = isTodoList.value == false
    }


    fun checkListTextCreated() {
        val editCheckList = checkList.value
        Log.i("Rita", "checkListTextCreated()")

        if (planGet.value?.id.isNullOrEmpty()) {
            val newCheck = Check(
                checkText.value, false, 0, "", "", 1,
                newPlanRef.id
            )
            editCheckList?.add(newCheck)
        } else {
            val newCheck = Check(
                checkText.value, false, 0, "", "", 1,
                planGet.value!!.id
            )
            editCheckList?.add(newCheck)
        }

        checkList.value = editCheckList
    }


    fun checkListTextRemoved(position: Int) {
        val listGet = checkList.value
        listGet?.removeAt(position)
        Log.i("Rita", "Edit List removed: $listGet")
        checkList.value = listGet
    }


    fun clearText() {
        checkText.value = ""
    }


    fun prepareNewPlan() {
        val plan = Plan(
            id = newPlanRef.id,
            title = title.value,
            description = description.value,
            location = location.value,
            start_time = start_time.value,
            end_time = end_time.value,
            start_time_detail = start_time_detail.value,
            end_time_detail = end_time_detail.value,
            category = categoryStatus.value?.name,
            categoryPosition = categoryPosition.value,
            categoryList = categoryList.value,
            checkList = checkList.value,
            isToDoList = isTodoList.value,
            isToDoListDone = false,
            owner = currentUser.value!!.email,
            owner_name = currentUser.value!!.name,
            invitation = mutableListOf<String>(),
            collaborator = collaborator.value,
            order_id = 1
        )
        Log.i("Rita", "new plan: $plan")
        newPlan.value = plan
    }


    fun preparePlan() {
        val plan = Plan(
            id = id.value,
            title = title.value,
            description = description.value,
            location = location.value,
            start_time = start_time.value,
            end_time = end_time.value,
            start_time_detail = start_time_detail.value,
            end_time_detail = end_time_detail.value,
            category = categoryStatus.value?.name,
            categoryPosition = categoryPosition.value,
            categoryList = categoryList.value,
            checkList = checkList.value,
            isToDoList = isTodoList.value
        )
        Log.i("Rita", "new plan: $plan")
        newPlan.value = plan
    }


    fun updatePlan(plan: Plan) {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            when (val result = repository.updatePlan(plan)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    loadingStatus.value = false
                    Log.i("Rita","home VM updatePlan: $result")
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                    Log.i("Rita","home VM updatePlan: $result")
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                    Log.i("Rita","home VM updatePlan: $result")
                }
                else -> {
                    _error.value =
                        CalendarProApplication.instance.getString(R.string.Error)
                    _status.value = LoadApiStatus.ERROR
                }
            }
        }
    }


    fun createPlan(plan: Plan) {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            when (val result = repository.createPlan(plan)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    loadingStatus.value = false
                    Log.i("Rita","home VM createPlan: $result")
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                    Log.i("Rita","home VM createPlan: $result")
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                    Log.i("Rita","home VM createPlan: $result")
                }
                else -> {
                    _error.value =
                        CalendarProApplication.instance.getString(R.string.Error)
                    _status.value = LoadApiStatus.ERROR
                }
            }
        }
    }


    fun changeCategory(position: Int, lastPosition: Int) {
        Log.i("Rita", "$lastPosition")

        val categoryListGet = categoryList.value

        // deselected the origin position value
        if (categoryPosition.value != -1) {
            categoryListGet!![categoryPosition.value!!].isSelected = false
        }

        if (lastPosition != -1) {
            categoryListGet!![lastPosition].isSelected = false
        }

        categoryListGet!![position].isSelected = true
        val item = categoryListGet[position]
        categoryStatus.value = item
        categoryPosition.value = position
        categoryList.value = categoryListGet
    }


    fun convertToTimestamp(startDateSelected: String, endDateSelected: String){
        start_time.value = stringToTimestamp(startDateSelected)
        end_time.value = stringToTimestamp(endDateSelected)
        doneConverted.value = true
    }


    fun doneConverted() {
        doneConverted.value = null
    }


    fun doneNavigated() {
        newPlan.value = null
        planGet.value = null
        editStatus.value = null
    }


    private fun getUserData(){
        updatedUser = repository.getUser(UserManager.userToken!!)
    }


    fun getCategoryFromUser() {
        if(editStatus.value != true) {
            categoryList.value = updatedUser.value?.categoryList
        }
    }


    fun getCategoryFromPlan(plan: Plan){
        if (!plan.categoryList.isNullOrEmpty()) {
            categoryList.value = plan.categoryList
        }
    }


    fun initPlanExtra(plan: Plan){
        //recognize as edit rather than created a plan
        editStatus.value = true
        id.value = plan.id
        location.value = plan.location
    }


    init {

        getUserData()

        title.value = plan.title ?: ""
        description.value = plan.description ?: ""
        start_time.value = plan.start_time
        end_time.value = plan.end_time
        start_time_detail.value = plan.start_time_detail
        end_time_detail.value = plan.end_time_detail
        newPlan.value = null
        isTodoList.value = plan.isToDoList ?: false
        checkText.value = null
        checkList.value = plan.checkList ?: emptyCheckList
        if (plan.category == "") {
            categoryStatus.value = Category("", false)
        } else {
            categoryStatus.value = Category(plan.category!!, true)
        }
        categoryPosition.value = plan.categoryPosition
        collaborator.value = mutableListOf(currentUser.value!!.email)
    }

}