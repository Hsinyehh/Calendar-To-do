package com.rita.calendarprooo.sort

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rita.calendarprooo.CalendarProApplication
import com.rita.calendarprooo.R
import com.rita.calendarprooo.data.Category
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

class HomeSortViewModel(val repository: CalendarRepository) : ViewModel() {

    // loading animation
    val loadingStatus = MutableLiveData<Boolean?>()

    var currentUser = MutableLiveData<User>()

    var categoryStatus = MutableLiveData<String>()

    var categoryPosition = MutableLiveData<Int?>()

    var categoryList = MutableLiveData<MutableList<Category>?>()

    private var _navigateToEdit = MutableLiveData<Boolean>()
    val navigateToEdit: LiveData<Boolean>
        get() = _navigateToEdit

    private var _navigateToEditByPlan = MutableLiveData<Plan>()
    val navigateToEditByPlan: LiveData<Plan>
        get() = _navigateToEditByPlan

    private var _navigateToInvite = MutableLiveData<Plan>()
    val navigateToInvite: LiveData<Plan>
        get() = _navigateToInvite

    private var _navigateToInviteCategory = MutableLiveData<Boolean>()
    val navigateToInviteCategory: LiveData<Boolean>
        get() = _navigateToInviteCategory

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

    var selectedStartTime = MutableLiveData<Long>()

    var selectedEndTime = MutableLiveData<Long>()

    var plansToday = MutableLiveData<List<Plan>>()

    var plansBeforeToday = MutableLiveData<List<Plan>>()

    var livePlansToday = MutableLiveData<List<Plan>>()

    var livePlansBeforeToday = MutableLiveData<List<Plan>>()

    var livePlansReset = MutableLiveData<Boolean>()

    // store detail showing/hiding status
    var scheduleViewList = MutableLiveData<MutableList<Boolean>>()

    var todoViewList = MutableLiveData<MutableList<Boolean>>()

    var doneViewList = MutableLiveData<MutableList<Boolean>>()

    var startToGetViewList = MutableLiveData<Boolean>()

    var startToGetViewListForTodoMode = MutableLiveData<Boolean>()

    var startToGetViewListForDoneMode = MutableLiveData<Boolean>()

    // the variable is used after viewList is created so that the recyclerView can show
    var getViewListAlready = MutableLiveData<Boolean>()

    // update
    private var checkListUpdate = MutableLiveData<MutableList<Check>>()

    private var checkUpdate = MutableLiveData<Check>()

    private var positionUpdate = MutableLiveData<Int>()

    var planUpdate = MutableLiveData<Plan>()

    var isCheckDoneChanged = MutableLiveData<Boolean>()

    var isCheckRemoved = MutableLiveData<Boolean>()

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


    fun startNavigateToInviteCategory() {
        _navigateToInviteCategory.value = true
    }


    fun startNavigateToAlarm(plan: Plan) {
        _navigateToAlarm.value = plan
    }


    fun doneNavigated() {
        _navigateToEdit.value = null
        _navigateToEditByPlan.value = null
        _navigateToInvite.value = null
        _navigateToInviteCategory.value = null
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
                    result.data.filter { it.category == categoryStatus.value }
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
                    result.data.filter { it.category == categoryStatus.value }
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

        loadingStatus.value = false
    }

    fun doneGetViewList() {
        startToGetViewList.value = null
    }


    fun startToGetViewListForTodo() {
        startToGetViewListForTodoMode.value = true
        startToGetViewListForDoneMode.value = true
    }


    private fun createViewList(planList: List<Plan>): MutableList<Boolean>{
        val list = mutableListOf<Boolean>()
        val size = planList.size

        if (size > 0) {
            for (i in 1..size) {
                list.add(false)
            }
        }

        Log.i("Rita", "createViewList: $list")
        return list
    }


    fun getViewList() {
        scheduleViewList.value = createViewList(_scheduleList.value!!)
        todoViewList.value = createViewList(_todoList.value!!)
        doneViewList.value = createViewList(_doneList.value!!)

        Log.i("Rita", "getViewList scheduleViewList: ${scheduleViewList.value}")
        Log.i("Rita", "getViewList todoViewList: ${todoViewList.value}")
        Log.i("Rita", "getViewList doneViewList: ${doneViewList.value}")

        getViewListAlready.value = true
    }


    fun getViewListForTodoMode() {
        todoViewList.value = createViewList(_todoList.value!!)
        doneViewList.value = createViewList(_doneList.value!!)

        Log.i("Rita", "getViewListForTodoMode  todo- ${todoViewList.value}")
        Log.i("Rita", "getViewListForTodoMode  done- ${doneViewList.value}")
    }


    fun changeScheduleView(position: Int) {
        val list = scheduleViewList.value
        var status = list?.get(position)

        status = status != true
        list?.set(position, status)

        scheduleViewList.value = list
        Log.i("Rita", "scheduleViewList changed: ${scheduleViewList.value}")
    }


    fun changeTodoView(position: Int) {
        val list = todoViewList.value
        var status = list?.get(position)

        status = status != true
        list?.set(position, status)

        todoViewList.value = list
        Log.i("Rita", "todoViewList changed: ${todoViewList.value}")
    }


    fun changeDoneView(position: Int) {
        val list = doneViewList.value
        var status = list?.get(position)

        status = status != true
        list?.set(position, status)

        doneViewList.value = list

        Log.i("Rita", "doneViewList changed: ${doneViewList.value}")

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

            when (val result = repository.updatePlanByCheck(plan, checkListUpdate)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    Log.i("Rita","home VM updatePlanByCheck: $result")
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                    Log.i("Rita","home VM updatePlanByCheck: $result")
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                    Log.i("Rita","home VM updatePlanByCheck: $result")
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
    private fun changeDoneStatus(check: Check): Check{
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
    fun renewCheckDoneStatus(plan: Plan): Plan{
        // Plan's checkList renewal
        plan.checkList!![positionUpdate.value!!] = checkUpdate.value!!

        // checkListUpdated renewal
        checkListUpdate.value = plan.checkList

        Log.i("Rita", "renewDoneForCheckList checkList: ${checkListUpdate.value}")

        return plan
    }


    // update check - remove check step1 - checkAdapter
    fun removeCheck(check: Check, position: Int) {
        Log.i("Rita","removeCheck check: $check, position: $position")
        loadingStatus.value = true
        isCheckRemoved.value = true
        positionUpdate.value = position
        checkUpdate.value = check

        // get Plan for new checkList
        getPlanByCheck(check)
    }


    // update check - remove check step2 - checkAdapter
    fun renewCheckRemoval(plan: Plan): Plan{
        plan.checkList!!.removeAt(positionUpdate.value!!)
        checkListUpdate.value = plan.checkList

        return plan
    }


    fun doneUpdated(){
        isCheckDoneChanged.value = null
        isCheckRemoved.value = null
    }


    fun getLivePlans() {
        if (selectedStartTime.value != null && selectedEndTime.value != null
            && currentUser.value != null) {

            livePlansToday = repository.getSortLivePlansToday(
                selectedStartTime.value!!, selectedEndTime.value!!, currentUser.value!!,
                categoryStatus.value!!
            )

            livePlansBeforeToday = repository.getSortLivePlansBeforeToday(
                selectedStartTime.value!!, currentUser.value!!, categoryStatus.value!!
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
                    Log.i("Rita","home VM updatePlanDoneStatus: $result")
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                    Log.i("Rita","home VM updatePlanDoneStatus: $result")
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                    Log.i("Rita","home VM updatePlanDoneStatus: $result")
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
        categoryStatus.value = item.name

        categoryPosition.value = position
        categoryList.value = categoryListGet
    }


    fun initCategory(user: User) {
        categoryList.value = user.categoryList
        categoryPosition.value = 0

        // init position is 0
        val categoryListGet = categoryList.value
        categoryListGet!![0].isSelected = true
        val item = categoryListGet[0]

        categoryStatus.value = item.name
        categoryList.value = categoryListGet
    }


    init {
        loadingStatus.value = true
        _navigateToEdit.value = null
        selectedTimeSet(getToday())
        UserManager.userToken?.let { getUserData(it) }
    }
}