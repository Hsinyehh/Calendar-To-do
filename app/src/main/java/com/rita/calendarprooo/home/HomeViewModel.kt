package com.rita.calendarprooo.home

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.rita.calendarprooo.data.Check
import com.rita.calendarprooo.data.Plan
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel() : ViewModel() {

    private var _navigateToEdit = MutableLiveData<Boolean>()
    val navigateToEdit : LiveData<Boolean>
        get() = _navigateToEdit

    private var _navigateToEditByPlan = MutableLiveData<Plan>()
    val navigateToEditByPlan : LiveData<Plan>
        get() = _navigateToEditByPlan

    private var _navigateToInvite = MutableLiveData<Plan>()
    val navigateToInvite : LiveData<Plan>
        get() = _navigateToInvite

    private var _scheduleList = MutableLiveData<List<Plan>>()
    val scheduleList : LiveData<List<Plan>>
        get() = _scheduleList

    private var _todoList = MutableLiveData<List<Plan>>()
    val todoList : LiveData<List<Plan>>
        get() = _todoList

    private var _doneList = MutableLiveData<List<Plan>>()
    val doneList : LiveData<List<Plan>>
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

    fun swapCheckListItem(start:Int , end:Int){
        val todoListGet=_todoList.value
        Collections.swap(todoListGet,start,end)
        _todoList.value=todoListGet
    }

    fun startNavigateToEdit(){
        _navigateToEdit.value=true
    }

    fun startNavigateToEditByPlan(plan:Plan){
        _navigateToEditByPlan.value=plan
    }

    fun startNavigateToInvite(plan:Plan){
        _navigateToInvite.value=plan
    }

    fun doneNavigated(){
        _navigateToEdit.value=null
        _navigateToEditByPlan.value=null
        _navigateToInvite.value=null
    }

    fun readPlanFromToday(){
        val list = mutableListOf<Plan>()

        //plan's start-time from today
        db.collection("plan")
            .whereEqualTo("owner","lisa@gmail.com")
            .whereGreaterThanOrEqualTo("start_time", selectedStartTime.value!!)
            .whereLessThanOrEqualTo("start_time", selectedEndTime.value!!)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    val plan= document.toObject(Plan::class.java)
                    list.add(plan)
                }
                Log.i("Rita","list: $list")
                readListFromToday.value = list
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    fun readPlanBeforeToday(){
        val listBefore = mutableListOf<Plan>()
        //plan's start-time before today
        db.collection("plan")
            .whereEqualTo("owner","lisa@gmail.com")
            .whereLessThanOrEqualTo("start_time", selectedStartTime.value!!)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    val plan= document.toObject(Plan::class.java)
                    listBefore.add(plan)
                }
                Log.i("Rita","listBeforeToday:　$listBefore")

                val filteredList = listBefore
                    .filter { it -> it.end_time!! >= selectedStartTime.value!! }

                Log.i("Rita","filtered listBeforeToday:　$filteredList")

                readListBeforeToday.value = filteredList
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    fun readPlanInTotal(){
        var list = readListFromToday.value?.toMutableList()
        var listBefore = readListBeforeToday.value?.toMutableList()
        if(list!= null){
            if (listBefore != null) {
                list.addAll(listBefore)
            }
        }else{
            if (listBefore != null) {
                list = listBefore
            }
        }
        if (list != null) {
            _scheduleList.value = list.filter { it -> it.isToDoList==false }
            _todoList.value = list.filter { it -> it.isToDoList==true && !it.isToDoListDone }
            _doneList.value= list.filter { it ->  it.isToDoListDone }
            startToGetViewList.value = true
        }
    }

    fun doneGetViewList(){
        startToGetViewList.value = null
    }

    fun startToGetViewListForTodo(){
        startToGetViewListForTodoMode.value = true
        startToGetViewListForDoneMode.value = true
    }

    fun getViewList(){
        Log.i("Rita","getViewList")
        val list = mutableListOf<Boolean>()
        val todoList = mutableListOf<Boolean>()
        val doneList = mutableListOf<Boolean>()
        val size = _scheduleList.value?.size
        val todoSize = _todoList.value?.size
        val doneSize = _doneList.value?.size

        if(size!=null && size>0){
            for(i in 1..size){
                list.add(false)
            }
            scheduleViewList.value = list
        }
        if(todoSize!=null && todoSize>0){
            for(i in 1..todoSize){
                todoList.add(false)
            }
            todoViewList.value = todoList
        }
        if(doneSize!=null && doneSize>0){
            for(i in 1..doneSize){
                doneList.add(false)
            }
            doneViewList.value = doneList
        }
    }

    fun getViewListForTodoMode(){

        val todoList = mutableListOf<Boolean>()
        val doneList = mutableListOf<Boolean>()
        val todoSize = _todoList.value?.size
        val doneSize = _doneList.value?.size

        if(todoSize!=null && todoSize>0){
            for(i in 1..todoSize){
                todoList.add(false)
            }
            todoViewList.value = todoList
        }
        if(doneSize!=null && doneSize>0){
            for(i in 1..doneSize){
                doneList.add(false)
            }
            doneViewList.value = doneList
        }

        Log.i("Rita","getViewListForTodoMode  todo- ${todoViewList.value}")
        Log.i("Rita","getViewListForTodoMode  done- ${doneViewList.value}")
    }

    fun changeScheduleView(position: Int){
        var list = scheduleViewList.value
        var status = list?.get(position)

        Log.i("Rita","ScheduleView- $position -$status")
        status = status != true
        list?.set(position,status)
        Log.i("Rita","ScheduleView changed- $position -$status")

        scheduleViewList.value = list

    }

    fun changeTodoView(position: Int){
        var list = todoViewList.value
        var status = list?.get(position)
        Log.i("Rita","todoViewList- ${todoViewList.value}")

        Log.i("Rita","todoView- $position - $status")
        status = status != true
        list?.set(position,status)
        Log.i("Rita","todoView changed- $position - $status")

        todoViewList.value = list
        Log.i("Rita","todoViewList changed- ${todoViewList.value}")
    }

    fun changeDoneView(position: Int){
        var list = doneViewList.value
        var status = list?.get(position)
        Log.i("Rita","doneViewList- ${doneViewList.value}")

        Log.i("Rita","doneView- $position - $status")
        status = status != true
        list?.set(position,status)
        Log.i("Rita","doneView changed- $position - $status")

        doneViewList.value = list

        Log.i("Rita","doneViewList changed- ${doneViewList.value}")

    }

    fun getCheckAndChangeStatus(item:Check, position:Int) {
        val planRef = item.plan_id?.let { db.collection("plan").document(it) }
        var plan : Plan? = null
        planRef!!.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    plan= document.toObject(Plan::class.java)
                    if (plan != null) {
                        plan!!.checkList!![position]=item
                        checkList.value = plan!!.checkList
                        Log.i("Rita"," getCheckList-itemUpdate as $item")
                        //Store isDone status
                        writeCheckItemStatus(item)
                    }
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    fun getCheckAndRemoveItem(item:Check, position:Int){
        val planRef = item.plan_id?.let { db.collection("plan").document(it) }
        planRef!!.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    val plan= document.toObject(Plan::class.java)
                    if (plan != null) {
                        plan.checkList!!.removeAt(position)
                        checkList.value = plan.checkList
                        Log.i("Rita"," getCheckList-itemRemoved as $item")
                        //Store isDone status
                        writeCheckItemStatus(item)
                    }
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    private fun writeCheckItemStatus(item:Check){
        val planRef = item.plan_id?.let { db.collection("plan").document(it) }
        Log.i("Rita","writeCheckItemDone-planRef: $planRef")
        planRef!!
            .update("checkList",checkList.value)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
    }

    fun readPlanOnChanged(){
        db.collection("plan")
            .whereEqualTo("owner","lisa@gmail.com")
            .whereGreaterThanOrEqualTo("start_time", selectedStartTime.value!!)
            .whereLessThanOrEqualTo("start_time", selectedEndTime.value!!)
            .addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }
            if (snapshot != null && !snapshot.isEmpty) {
                val list = mutableListOf<Plan>()
                Log.d(TAG, "Current data: ")
                for (item in snapshot) {
                    Log.d("Rita", item.toString())
                    val plan= item.toObject(Plan::class.java)
                    list.add(plan!!)
                }
                Log.i("Rita", "list onChanged:　$list")
                listFromToday.value = list
            } else {
                Log.d(TAG, "Current data: null")
            }
        }
        //plan's start-time before today
        db.collection("plan")
            .whereEqualTo("owner","lisa@gmail.com")
            .whereLessThanOrEqualTo("start_time", selectedStartTime.value!!)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    val listBefore = mutableListOf<Plan>()
                    Log.d(TAG, "Current data: ")
                    for (item in snapshot) {
                        Log.d("Rita", item.toString())
                        val plan = item.toObject(Plan::class.java)
                        listBefore.add(plan!!)
                    }
                    Log.i("Rita", "listBeforeToday onChanged:　$listBefore")
                    val filteredList = listBefore
                        .filter { it -> it.end_time!! >= selectedStartTime.value!! }
                    listBeforeToday.value = filteredList
                } else {
                    Log.d(TAG, "Current data: null")
                }
            }
    }

    fun getTotalList(){
        Log.i("Rita","getTotalList listFromToday - ${listFromToday.value}")
        Log.i("Rita","getTotalList readListBeforeToday - ${readListBeforeToday.value}")
        var list = listFromToday.value!!.toMutableList()
        readListBeforeToday.value?.let { it1 -> list?.addAll(it1) }

        _scheduleList.value = list?.filter { it -> it.isToDoList == false }
        _todoList.value =
            list?.filter { it -> it.isToDoList == true && !it.isToDoListDone }
        _doneList.value =
            list?.filter { it ->  it.isToDoListDone }
    }

    fun getTotalListBefore(){
        Log.i("Rita","getTotalList list - ${readListFromToday.value}")
        Log.i("Rita","getTotalList listBefore - ${listBeforeToday.value}")
        var list = readListFromToday.value?.toMutableList()
        listBeforeToday.value?.let { list?.addAll(it) }

        _scheduleList.value = list?.filter { it -> it.isToDoList == false }
        _todoList.value =
            list?.filter { it -> it.isToDoList == true && !it.isToDoListDone }
        _doneList.value =
            list?.filter { it ->  it.isToDoListDone }
    }

    fun getPlanAndChangeStatus(item:Plan) {
        val planRef = item.id?.let { db.collection("plan").document(it) }
        planRef!!
            .update("toDoListDone", item.isToDoListDone)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
    }

    fun convertToTimeStamp(dateSelected:String){
        try {
            val startTime= "$dateSelected 00:00"
            val endTime= "$dateSelected 23:59"

            val startDateSelectedFormat = SimpleDateFormat("dd-MM-yyyy HH:mm").parse(startTime)
            val endDateSelectedFormat = SimpleDateFormat("dd-MM-yyyy HH:mm").parse(endTime)
            Log.i("Rita", "convertToTimeStamp: ${startDateSelectedFormat.time} ")

            selectedStartTime.value = startDateSelectedFormat.time
            selectedEndTime.value = endDateSelectedFormat.time
        }
        catch(e:java.text.ParseException){
            Log.i("Rita","$e")
        }
    }

    fun getToday() : String {
        val df = SimpleDateFormat("dd-MM-yyyy");
        val today = df.format(Calendar.getInstance().getTime())
        Log.i("Rita","getToday() - $today")
        return today
    }

    init {
        _navigateToEdit.value = null
        convertToTimeStamp(getToday())
    }


}