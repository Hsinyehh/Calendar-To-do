package com.rita.calendarprooo.home

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.rita.calendarprooo.CalendarProApplication
import com.rita.calendarprooo.R
import com.rita.calendarprooo.data.Check
import com.rita.calendarprooo.data.Plan
import com.rita.calendarprooo.data.Result
import com.rita.calendarprooo.data.User
import com.rita.calendarprooo.data.source.CalendarRepository
import com.rita.calendarprooo.ext.convertToTimeStamp
import com.rita.calendarprooo.ext.getToday
import com.rita.calendarprooo.login.UserManager
import com.rita.calendarprooo.network.LoadApiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

class HomeViewModel(val repository: CalendarRepository) : ViewModel() {

    // loading animation
    val loadingStatus = MutableLiveData<Boolean?>()

    var currentUser = MutableLiveData<User>()

    private var _navigateToEdit = MutableLiveData<Boolean>()
    val navigateToEdit: LiveData<Boolean>
        get() = _navigateToEdit

    private var _navigateToEditByPlan = MutableLiveData<Plan>()
    val navigateToEditByPlan: LiveData<Plan>
        get() = _navigateToEditByPlan

    private var _navigateToInvite = MutableLiveData<Plan>()
    val navigateToInvite: LiveData<Plan>
        get() = _navigateToInvite

    private var _navigateToAlarm = MutableLiveData<Plan>()
    val navigateToAlarm: LiveData<Plan>
        get() = _navigateToAlarm

    private var _scheduleList = MutableLiveData<List<Plan>>()
    val scheduleList: LiveData<List<Plan>>
        get() = _scheduleList

    private var _todoList = MutableLiveData<List<Plan>>()
    val todoList: LiveData<List<Plan>>
        get() = _todoList

    private var _doneList = MutableLiveData<List<Plan>>()
    val doneList: LiveData<List<Plan>>
        get() = _doneList

    var checkList = MutableLiveData<MutableList<Check>>()

    //Firebase
    private val db = Firebase.firestore

    var selectedStartTime = MutableLiveData<Long>()

    var selectedEndTime = MutableLiveData<Long>()

    var plansToday = MutableLiveData<List<Plan>>()

    var plansBeforeToday = MutableLiveData<List<Plan>>()

    var livePlansToday = MutableLiveData<List<Plan>>()

    var livePlansBeforeToday = MutableLiveData<List<Plan>>()

    var scheduleViewList = MutableLiveData<MutableList<Boolean>>()

    var todoViewList = MutableLiveData<MutableList<Boolean>>()

    var doneViewList = MutableLiveData<MutableList<Boolean>>()

    var startToGetViewList = MutableLiveData<Boolean>()

    var startToGetViewListForTodoMode = MutableLiveData<Boolean>()

    var startToGetViewListForDoneMode = MutableLiveData<Boolean>()

    // the variable is used after viewList is created so that the recyclerView can show
    var getViewListAlready = MutableLiveData<Boolean>()

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


    fun swapCheckListItem(start: Int, end: Int) {
        val todoListGet = _todoList.value
        Collections.swap(todoListGet, start, end)
        _todoList.value = todoListGet
    }

    fun startNavigateToEdit() {
        _navigateToEdit.value = true
    }

    fun startNavigateToEditByPlan(plan: Plan) {
        _navigateToEditByPlan.value = plan
    }

    fun startNavigateToInvite(plan: Plan) {
        _navigateToInvite.value = plan
    }

    fun startNavigateToAlarm(plan: Plan) {
        _navigateToAlarm.value = plan
    }

    fun doneNavigated() {
        _navigateToEdit.value = null
        _navigateToEditByPlan.value = null
        _navigateToInvite.value = null
        _navigateToAlarm.value = null
    }

    fun getPlansToday() {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = currentUser.value?.let {
                repository.getPlansToday(selectedStartTime.value!!, selectedEndTime.value!!, it)
            }
                 plansToday.value = when (result) {
                is Result.Success -> {
                    // reGet viewList
                    getViewListAlready.value = null
                    loadingStatus.value = true

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

    fun getPlansBeforeToday() {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = currentUser.value?.let {
                repository.getPlansBeforeToday(selectedStartTime.value!!, it)
            }

            plansBeforeToday.value = when (result) {
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

    fun getTotalPlans() {

        var list = plansToday.value?.toMutableList()
        val listBefore = plansBeforeToday.value?.toMutableList()

        if (list != null) {
            if (listBefore != null) {
                list.addAll(listBefore)
            }
        } else {
            if (listBefore != null) {
                list = listBefore
            }
        }
        if (list != null) {
            _scheduleList.value = list.filter {  it.isToDoList == false }
            _todoList.value = list.filter { it.isToDoList == true && !it.isToDoListDone }
            _doneList.value = list.filter { it.isToDoListDone }
            startToGetViewList.value = true
        }
    }


    fun startToGetViewListForTodo() {
        startToGetViewListForTodoMode.value = true
        startToGetViewListForDoneMode.value = true
    }

    // create list to store detail showing/hiding status
    fun getViewList() {

        val list = mutableListOf<Boolean>()
        val todoList = mutableListOf<Boolean>()
        val doneList = mutableListOf<Boolean>()
        val size = _scheduleList.value?.size
        val todoSize = _todoList.value?.size
        val doneSize = _doneList.value?.size

        if (size != null && size > 0) {
            for (i in 1..size) {
                list.add(false)
            }
            scheduleViewList.value = list
            Log.i("Rita","getViewList scheduleViewList: ${scheduleViewList.value}")
        }
        if (todoSize != null && todoSize > 0) {
            for (i in 1..todoSize) {
                todoList.add(false)
            }
            todoViewList.value = todoList
            Log.i("Rita","getViewList todoViewList: ${todoViewList.value}")
        }
        if (doneSize != null && doneSize > 0) {
            for (i in 1..doneSize) {
                doneList.add(false)
            }
            doneViewList.value = doneList
            Log.i("Rita","getViewList doneViewList: ${doneViewList.value}")
        }
        getViewListAlready.value = true
    }

    fun getViewListForTodoMode() {

        val todoList = mutableListOf<Boolean>()
        val doneList = mutableListOf<Boolean>()
        val todoSize = _todoList.value?.size
        val doneSize = _doneList.value?.size

        if (todoSize != null && todoSize > 0) {
            for (i in 1..todoSize) {
                todoList.add(false)
            }
            todoViewList.value = todoList
        }
        if (doneSize != null && doneSize > 0) {
            for (i in 1..doneSize) {
                doneList.add(false)
            }
            doneViewList.value = doneList
        }

        Log.i("Rita", "getViewListForTodoMode  todo- ${todoViewList.value}")
        Log.i("Rita", "getViewListForTodoMode  done- ${doneViewList.value}")
    }

    // change detail showing/hiding status
    fun changeScheduleView(position: Int) {
        val list = scheduleViewList.value
        var status = list?.get(position)

        status = status != true
        list?.set(position, status)

        scheduleViewList.value = list

    }

    fun changeTodoView(position: Int) {
        val list = todoViewList.value
        var status = list?.get(position)

        status = status != true
        list?.set(position, status)

        todoViewList.value = list
        Log.i("Rita", "todoViewList changed- ${todoViewList.value}")
    }

    fun changeDoneView(position: Int) {
        val list = doneViewList.value
        var status = list?.get(position)

        status = status != true
        list?.set(position, status)

        doneViewList.value = list
        Log.i("Rita", "doneViewList changed- ${doneViewList.value}")

    }

    fun getCheckAndChangeStatus(item: Check, position: Int) {
        loadingStatus.value = true

        if (item.isDone) {
            item.isDone = false
            item.done_time = null
            item.doner = null
        } else if (!item.isDone) {
            item.isDone = true
            item.done_time = Calendar.getInstance().timeInMillis
            item.doner = currentUser.value?.name
        }

        val planRef = item.plan_id?.let { db.collection("plan").document(it) }

        planRef!!.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val plan = document.toObject(Plan::class.java)
                    if (plan != null) {
                        plan.checkList!![position] = item
                        checkList.value = plan.checkList
                        Log.i("Rita", " getCheckList-itemUpdate as $item")

                        // Store isDone status
                        updateCheckList(item)
                    }
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    fun getCheckAndRemoveItem(item: Check, position: Int) {

        loadingStatus.value = true

        val planRef = item.plan_id?.let { db.collection("plan").document(it) }

        planRef!!.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    val plan = document.toObject(Plan::class.java)
                    if (plan != null) {
                        plan.checkList!!.removeAt(position)
                        checkList.value = plan.checkList
                        Log.i("Rita", " getCheckList-itemRemoved as $item")

                        // Store isDone status
                        updateCheckList(item)
                    }
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    private fun updateCheckList(item: Check) {
        val planRef = item.plan_id?.let { db.collection("plan").document(it) }
        Log.i("Rita", "writeCheckItemDone-planRef: $planRef")
        planRef!!
            .update("checkList", checkList.value)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully updated!")
            }
            .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
    }


    fun getLivePlans() {
        if(selectedStartTime.value!=null && selectedEndTime.value != null
            && currentUser.value!=null)
        {
            livePlansToday = repository.getLivePlansToday(
                selectedStartTime.value!!, selectedEndTime.value!!, currentUser.value!!)

            livePlansBeforeToday = repository.getLivePlansBeforeToday(
                selectedStartTime.value!!, currentUser.value!!)
        }
    }



    fun getTotalLivePlans() {

        val list = livePlansToday.value?.toMutableList()

        livePlansBeforeToday.value?.let { list?.addAll(it) }

        _scheduleList.value = list?.filter { it -> it.isToDoList == false }
        _todoList.value =
            list?.filter { it -> it.isToDoList == true && !it.isToDoListDone }
        _doneList.value =
            list?.filter { it -> it.isToDoListDone }

    }


    fun getPlanAndChangeStatus(item: Plan) {
        val planRef = item.id?.let { db.collection("plan").document(it) }

        planRef!!
            .update("toDoListDone", item.isToDoListDone,
                "done_time", item.done_time,
                "doner", item.doner)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
    }


    fun selectedTimeSet(date: String) {
        val timeList = convertToTimeStamp(date)
        selectedStartTime.value = timeList?.get(0)
        selectedEndTime.value = timeList?.get(1)
    }

    private fun getUserData(userId: String) {
        Log.d("Rita", "userId: $userId")
        currentUser = repository.getUser(userId)
        UserManager.user = repository.getUser(userId)
    }

    init {
        loadingStatus.value = true
        _navigateToEdit.value = null
        selectedTimeSet(getToday())
        UserManager.userToken?.let { getUserData(it) }
    }


}