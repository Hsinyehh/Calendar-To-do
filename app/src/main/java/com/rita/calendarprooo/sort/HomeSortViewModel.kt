package com.rita.calendarprooo.sort

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.rita.calendarprooo.data.Category
import com.rita.calendarprooo.data.Check
import com.rita.calendarprooo.data.Plan
import com.rita.calendarprooo.data.User
import com.rita.calendarprooo.data.source.CalendarRepository
import com.rita.calendarprooo.ext.convertToTimeStamp
import com.rita.calendarprooo.ext.getToday
import com.rita.calendarprooo.login.UserManager
import java.util.*

class HomeSortViewModel(val repository: CalendarRepository) : ViewModel() {

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

    var listFromToday = MutableLiveData<List<Plan>>()

    var listBeforeToday = MutableLiveData<List<Plan>>()

    var readListFromToday = MutableLiveData<List<Plan>>()

    var readListBeforeToday = MutableLiveData<List<Plan>>()

    var selectedStartTime = MutableLiveData<Long>()

    var selectedEndTime = MutableLiveData<Long>()

    var scheduleViewList = MutableLiveData<MutableList<Boolean>>()

    var todoViewList = MutableLiveData<MutableList<Boolean>>()

    var doneViewList = MutableLiveData<MutableList<Boolean>>()

    var startToGetViewList = MutableLiveData<Boolean>()

    var startToGetViewListForTodoMode = MutableLiveData<Boolean>()

    var startToGetViewListForDoneMode = MutableLiveData<Boolean>()

    // the variable is used after viewList is created so that the recyclerView can show
    var getViewListAlready = MutableLiveData<Boolean>()

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

    fun doneNavigated() {
        _navigateToEdit.value = null
        _navigateToEditByPlan.value = null
        _navigateToInvite.value = null
        _navigateToInviteCategory.value = null
    }

    fun readPlanFromToday() {
        Log.i("Rita", "readPlanFromToday user: ${currentUser.value}")
        // reGet viewList
        getViewListAlready.value = null

        //plan's start-time from today
        currentUser.value?.let {
            db.collection("plan")
                .whereArrayContains("collaborator", it.email!!)
                .whereEqualTo("category", categoryStatus.value)
                .whereGreaterThanOrEqualTo("start_time", selectedStartTime.value!!)
                .whereLessThanOrEqualTo("start_time", selectedEndTime.value!!)
                .get()
                .addOnSuccessListener { result ->
                    val list = mutableListOf<Plan>()
                    for (document in result) {
                        Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                        val plan = document.toObject(Plan::class.java)
                        list.add(plan)
                    }
                    Log.i("Rita", "list: $list")
                    readListFromToday.value = list
                }
                .addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error getting documents.", exception)
                }
        }
    }

    fun readPlanBeforeToday() {
        Log.i("Rita", "readPlanBeforeToday user: ${currentUser.value}")
        //plan's start-time before today
        currentUser.value?.let {
            db.collection("plan")
                .whereArrayContains("collaborator", it.email!!)
                .whereEqualTo("category", categoryStatus.value)
                .whereLessThan("start_time", selectedStartTime.value!!)
                .get()
                .addOnSuccessListener { result ->
                    val listBefore = mutableListOf<Plan>()
                    for (document in result) {
                        Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                        val plan = document.toObject(Plan::class.java)
                        listBefore.add(plan)
                    }
                    Log.i("Rita", "listBeforeToday:　$listBefore")

                    val filteredList = listBefore
                        .filter { it -> it.end_time!! >= selectedStartTime.value!! }

                    Log.i("Rita", "filtered listBeforeToday:　$filteredList")

                    readListBeforeToday.value = filteredList
                }
                .addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error getting documents.", exception)
                }
        }
    }

    fun readPlanInTotal() {
        var list = readListFromToday.value?.toMutableList()
        var listBefore = readListBeforeToday.value?.toMutableList()
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
            _scheduleList.value = list.filter { it -> it.isToDoList == false }
            _todoList.value = list.filter { it -> it.isToDoList == true && !it.isToDoListDone }
            _doneList.value = list.filter { it -> it.isToDoListDone }
            startToGetViewList.value = true
        }
    }

    fun doneGetViewList() {
        startToGetViewList.value = null
    }

    fun startToGetViewListForTodo() {
        startToGetViewListForTodoMode.value = true
        startToGetViewListForDoneMode.value = true
    }

    fun getViewList() {
        Log.i("Rita", "getViewList")
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
        }
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

    fun changeScheduleView(position: Int) {
        var list = scheduleViewList.value
        var status = list?.get(position)

        Log.i("Rita", "ScheduleView- $position -$status")
        status = status != true
        list?.set(position, status)
        Log.i("Rita", "ScheduleView changed- $position -$status")

        scheduleViewList.value = list

    }

    fun changeTodoView(position: Int) {
        var list = todoViewList.value
        var status = list?.get(position)
        Log.i("Rita", "todoViewList- ${todoViewList.value}")

        Log.i("Rita", "todoView- $position - $status")
        status = status != true
        list?.set(position, status)
        Log.i("Rita", "todoView changed- $position - $status")

        todoViewList.value = list
        Log.i("Rita", "todoViewList changed- ${todoViewList.value}")
    }

    fun changeDoneView(position: Int) {
        var list = doneViewList.value
        var status = list?.get(position)
        Log.i("Rita", "doneViewList- ${doneViewList.value}")

        Log.i("Rita", "doneView- $position - $status")
        status = status != true
        list?.set(position, status)
        Log.i("Rita", "doneView changed- $position - $status")

        doneViewList.value = list

        Log.i("Rita", "doneViewList changed- ${doneViewList.value}")

    }

    fun getCheckAndChangeStatus(item: Check, position: Int) {
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
        var plan: Plan? = null
        planRef!!.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(ContentValues.TAG, "DocumentSnapshot data: ${document.data}")
                    plan = document.toObject(Plan::class.java)
                    if (plan != null) {
                        plan!!.checkList!![position] = item
                        checkList.value = plan!!.checkList
                        Log.i("Rita", " getCheckList-itemUpdate as $item")
                        //Store isDone status
                        writeCheckItemStatus(item)
                    }
                } else {
                    Log.d(ContentValues.TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "get failed with ", exception)
            }
    }

    fun getCheckAndRemoveItem(item: Check, position: Int) {
        val planRef = item.plan_id?.let { db.collection("plan").document(it) }
        planRef!!.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(ContentValues.TAG, "DocumentSnapshot data: ${document.data}")
                    val plan = document.toObject(Plan::class.java)
                    if (plan != null) {
                        plan.checkList!!.removeAt(position)
                        checkList.value = plan.checkList
                        Log.i("Rita", " getCheckList-itemRemoved as $item")
                        //Store isDone status
                        writeCheckItemStatus(item)
                    }
                } else {
                    Log.d(ContentValues.TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "get failed with ", exception)
            }
    }

    private fun writeCheckItemStatus(item: Check) {
        val planRef = item.plan_id?.let { db.collection("plan").document(it) }
        Log.i("Rita", "writeCheckItemDone-planRef: $planRef")
        planRef!!
            .update("checkList", checkList.value)
            .addOnSuccessListener {
                Log.d(
                    ContentValues.TAG,
                    "DocumentSnapshot successfully updated!"
                )
            }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error updating document", e) }
    }

    fun readPlanOnChanged() {
        Log.i("Rita", "readPlanOnChanged user: ${currentUser.value}")
        currentUser.value?.let {
            db.collection("plan")
                .whereArrayContains("collaborator", it.email!!)
                .whereEqualTo("category", categoryStatus.value)
                .whereGreaterThanOrEqualTo("start_time", selectedStartTime.value!!)
                .whereLessThanOrEqualTo("start_time", selectedEndTime.value!!)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.w(ContentValues.TAG, "Listen failed.", e)
                        return@addSnapshotListener
                    }
                    if (snapshot != null && !snapshot.isEmpty) {
                        val list = mutableListOf<Plan>()
                        for (item in snapshot) {
                            Log.d("Rita", "Current data: $item")
                            val plan = item.toObject(Plan::class.java)
                            list.add(plan!!)
                        }
                        Log.i("Rita", "list onChanged:　$list")
                        listFromToday.value = list
                    } else {
                        Log.d(ContentValues.TAG, "Current data: null")
                    }
                }
        }
        //plan's start-time before today
        currentUser.value?.let {
            db.collection("plan")
                .whereArrayContains("collaborator", it.email!!)
                .whereEqualTo("category", categoryStatus.value)
                .whereLessThan("start_time", selectedStartTime.value!!)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.w(ContentValues.TAG, "Listen failed.", e)
                        return@addSnapshotListener
                    }
                    if (snapshot != null && !snapshot.isEmpty) {
                        val listBefore = mutableListOf<Plan>()
                        for (item in snapshot) {
                            Log.d("Rita", "Current data Before: $item")
                            val plan = item.toObject(Plan::class.java)
                            if (plan.start_time!! < selectedStartTime.value!!) {
                                listBefore.add(plan!!)
                            }
                        }
                        val filteredList = listBefore
                            .filter { it -> it.end_time!! >= selectedStartTime.value!! }
                        Log.i("Rita", "listBeforeToday onChanged:　$filteredList")
                        listBeforeToday.value = filteredList
                    } else {
                        Log.d(ContentValues.TAG, "Current data: null")
                    }
                }
        }
    }

    fun getTotalList() {
        Log.i("Rita", "getTotalList listFromToday - ${listFromToday.value}")
        Log.i("Rita", "getTotalList readListBeforeToday - ${readListBeforeToday.value}")
        var list = listFromToday.value!!.toMutableList()
        readListBeforeToday.value?.let { list?.addAll(it) }

        _scheduleList.value = list?.filter { it -> it.isToDoList == false }
        _todoList.value =
            list?.filter { it -> it.isToDoList == true && !it.isToDoListDone }
        _doneList.value =
            list?.filter { it -> it.isToDoListDone }
    }

    fun getTotalListBefore() {
        Log.i("Rita", "getTotalListBefore readList - ${readListFromToday.value}")
        Log.i("Rita", "getTotalListBefore listBefore - ${listBeforeToday.value}")
        var list = readListFromToday.value?.toMutableList()
        listBeforeToday.value?.let { list?.addAll(it) }

        _scheduleList.value = list?.filter { it -> it.isToDoList == false }
        _todoList.value =
            list?.filter { it -> it.isToDoList == true && !it.isToDoListDone }
        _doneList.value =
            list?.filter { it -> it.isToDoListDone }
    }

    fun getPlanAndChangeStatus(item: Plan) {
        val planRef = item.id?.let { db.collection("plan").document(it) }
        planRef!!
            .update(
                "toDoListDone", item.isToDoListDone,
                "done_time", item.done_time,
                "doner", item.doner
            )
            .addOnSuccessListener {
                Log.d(
                    ContentValues.TAG,
                    "DocumentSnapshot successfully updated!"
                )
            }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error updating document", e) }
    }


    fun selectedTimeSet(date: String) {
        val timeList = convertToTimeStamp(date)
        selectedStartTime.value = timeList?.get(0)
        selectedEndTime.value = timeList?.get(1)
    }

    fun getUserData(userId: String) {
        Log.d("Rita", "userId: $userId")
        currentUser = repository.getUser(userId)
        UserManager.user = repository.getUser(userId)
    }

    fun changeCategory(position: Int, lastPosition: Int) {
        Log.i("Rita", "$lastPosition")
        var categoryListGet = categoryList.value

        //deselected the origin position value
        if (categoryPosition.value != -1) {
            categoryListGet!![categoryPosition.value!!].isSelected = false
        }

        if (lastPosition != -1) {
            categoryListGet!![lastPosition].isSelected = false
        }
        categoryListGet!![position].isSelected = true
        val item = categoryListGet!![position]
        categoryStatus.value = item.name

        categoryPosition.value = position
        categoryList.value = categoryListGet
    }

    fun initCategory(user: User) {
        categoryList.value = user.categoryList
        categoryPosition.value = 0
        //init position is 0
        var categoryListGet = categoryList.value
        categoryListGet!![0].isSelected = true
        val item = categoryListGet!![0]
        categoryStatus.value = item.name
        categoryList.value = categoryListGet

    }

    init {
        _navigateToEdit.value = null
        selectedTimeSet(getToday())
        UserManager.userToken?.let { getUserData(it) }

    }
}