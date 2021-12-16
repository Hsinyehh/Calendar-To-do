package com.rita.calendarprooo.home


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rita.calendarprooo.CalendarProApplication
import com.rita.calendarprooo.R
import com.rita.calendarprooo.Util.Logger
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

    private val _navigateToEdit = MutableLiveData<Boolean>()
    val navigateToEdit: LiveData<Boolean>
        get() = _navigateToEdit

    private val _navigateToEditByPlan = MutableLiveData<Plan>()
    val navigateToEditByPlan: LiveData<Plan>
        get() = _navigateToEditByPlan

    private val _navigateToInvite = MutableLiveData<Plan>()
    val navigateToInvite: LiveData<Plan>
        get() = _navigateToInvite

    private val _navigateToAlarm = MutableLiveData<Plan>()
    val navigateToAlarm: LiveData<Plan>
        get() = _navigateToAlarm

    private val _scheduleList = MutableLiveData<List<Plan>>()
    val scheduleList: LiveData<List<Plan>>
        get() = _scheduleList

    private val _todoList = MutableLiveData<List<Plan>>()
    val todoList: LiveData<List<Plan>>
        get() = _todoList

    private val _doneList = MutableLiveData<List<Plan>>()
    val doneList: LiveData<List<Plan>>
        get() = _doneList

    val selectedStartTime = MutableLiveData<Long>()

    val selectedEndTime = MutableLiveData<Long>()

    val plansToday = MutableLiveData<List<Plan>>()

    val plansBeforeToday = MutableLiveData<List<Plan>>()

    var livePlansToday = MutableLiveData<List<Plan>>()

    var livePlansBeforeToday = MutableLiveData<List<Plan>>()

    val livePlansReset = MutableLiveData<Boolean>()

    val scheduleViewList = MutableLiveData<MutableList<Boolean>>()

    val todoViewList = MutableLiveData<MutableList<Boolean>>()

    val doneViewList = MutableLiveData<MutableList<Boolean>>()

    val startToGetViewList = MutableLiveData<Boolean>()

    val startToGetViewListForTodoMode = MutableLiveData<Boolean>()

    val startToGetViewListForDoneMode = MutableLiveData<Boolean>()

    // the variable is used after viewList is created so that the recyclerView can show
    val getViewListAlready = MutableLiveData<Boolean>()

    // update
    private val checkListUpdate = MutableLiveData<MutableList<Check>>()

    private val checkUpdate = MutableLiveData<Check>()

    private val positionUpdate = MutableLiveData<Int>()

    val planUpdate = MutableLiveData<Plan>()

    val isCheckDoneChanged = MutableLiveData<Boolean>()

    val isCheckRemoved = MutableLiveData<Boolean>()

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
            _scheduleList.value = list.filter { it.isToDoList == false }
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
    private fun createViewList(planList: List<Plan>): MutableList<Boolean> {
        val list = mutableListOf<Boolean>()
        val size = planList.size

        if (size > 0) {
            for (i in 1..size) {
                list.add(false)
            }
        }

        Logger.i("createViewList: $list")
        return list
    }


    fun getViewList() {
        scheduleViewList.value = createViewList(_scheduleList.value!!)
        todoViewList.value = createViewList(_todoList.value!!)
        doneViewList.value = createViewList(_doneList.value!!)

        Logger.i("getViewList scheduleViewList: ${scheduleViewList.value}")
        Logger.i("getViewList todoViewList: ${todoViewList.value}")
        Logger.i("getViewList doneViewList: ${doneViewList.value}")

        getViewListAlready.value = true
    }


    fun getViewListForTodoMode() {
        todoViewList.value = createViewList(_todoList.value!!)
        doneViewList.value = createViewList(_doneList.value!!)

        Logger.i("getViewListForTodoMode  todo- ${todoViewList.value}")
        Logger.i("getViewListForTodoMode  done- ${doneViewList.value}")
    }


    // change detail showing/hiding status
    fun changeScheduleView(position: Int) {
        val list = scheduleViewList.value
        if (list != null) {
            if(list.lastIndex >= position ) {
                var status = list.get(position)

                status = status != true
                list.set(position, status)

                scheduleViewList.value = list
            }
        }
    }


    fun changeTodoView(position: Int) {
        val list = todoViewList.value
        if (list != null) {
            if (list.lastIndex >= position) {
                var status = list.get(position)

                status = status != true
                list.set(position, status)

                todoViewList.value = list
                Logger.i("todoViewList changed- ${todoViewList.value}")
            }
        }
    }


    fun changeDoneView(position: Int) {
        val list = doneViewList.value
        if (list != null) {
            if (list.lastIndex >= position) {
                var status = list.get(position)

                status = status != true
                list.set(position, status)

                doneViewList.value = list
                Logger.i("doneViewList changed- ${doneViewList.value}")
            }
        }
    }


    // update check
    private fun getPlanByCheck(check: Check) {

        coroutineScope.launch {
            _status.value = LoadApiStatus.LOADING
            val result = currentUser.value?.let { repository.getPlanByCheck(check) }

            planUpdate.value = when (result) {
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


    fun updatePlanByCheck(plan: Plan) {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            when (val result = repository.updatePlanForCheckList(plan, checkListUpdate)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    Logger.i("home VM updatePlanByCheck: $result")
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                    Logger.i("home VM updatePlanByCheck: $result")
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                    Logger.i("home VM updatePlanByCheck: $result")
                }
                else -> {
                    _error.value =
                        CalendarProApplication.instance.getString(R.string.Error)
                    _status.value = LoadApiStatus.ERROR
                }
            }
        }
    }


    //  update check - change check done status
    private fun changeDoneStatus(check: Check): Check {
        if (check.isDone) {
            check.isDone = false
            check.done_time = null
            check.doner = null
        } else if (!check.isDone) {
            check.isDone = true
            check.done_time = Calendar.getInstance().timeInMillis
            check.doner = currentUser.value?.name
        }
        return check
    }


    // update check - change check done status step1 - checkAdapter
    fun changeCheckDoneStatus(check: Check, position: Int) {
        // init
        loadingStatus.value = true
        isCheckDoneChanged.value = true
        positionUpdate.value = position

        checkUpdate.value = changeDoneStatus(check)

        // get Plan for new checkList
        getPlanByCheck(checkUpdate.value!!)
    }


    // update check - change check done status step2 - fragment
    fun renewCheckDoneStatus(plan: Plan): Plan {
        // Plan's checkList renewal
        plan.checkList!![positionUpdate.value!!] = checkUpdate.value!!

        // checkListUpdated renewal
        checkListUpdate.value = plan.checkList

        Logger.i("renewDoneForCheckList checkList: ${checkListUpdate.value}")

        return plan
    }


    // update check - remove check step1 - checkAdapter
    fun removeCheck(check: Check, position: Int) {
        Logger.i("removeCheck check: $check, position: $position")
        loadingStatus.value = true
        isCheckRemoved.value = true
        positionUpdate.value = position
        checkUpdate.value = check

        // get Plan for new checkList
        getPlanByCheck(check)
    }


    // update check - remove check step2 - checkAdapter
    fun renewCheckRemoval(plan: Plan): Plan {
        plan.checkList!!.removeAt(positionUpdate.value!!)
        checkListUpdate.value = plan.checkList

        return plan
    }


    fun doneUpdated() {
        isCheckDoneChanged.value = null
        isCheckRemoved.value = null
    }


    fun getLivePlans() {
        if (selectedStartTime.value != null && selectedEndTime.value != null
            && currentUser.value != null
        ) {

            livePlansToday = repository.getLivePlansToday(
                selectedStartTime.value!!, selectedEndTime.value!!, currentUser.value!!
            )

            livePlansBeforeToday = repository.getLivePlansBeforeToday(
                selectedStartTime.value!!, currentUser.value!!
            )

            livePlansReset.value = true
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


    fun updatePlanDoneStatus(plan: Plan) {

        coroutineScope.launch {
            _status.value = LoadApiStatus.LOADING

            when (val result = repository.updatePlanForDoneStatus(plan)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    Logger.i("home VM updatePlanDoneStatus: $result")
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                    Logger.i("home VM updatePlanDoneStatus: $result")
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                    Logger.i("home VM updatePlanDoneStatus: $result")
                }
                else -> {
                    _error.value =
                        CalendarProApplication.instance.getString(R.string.Error)
                    _status.value = LoadApiStatus.ERROR
                }
            }
        }
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