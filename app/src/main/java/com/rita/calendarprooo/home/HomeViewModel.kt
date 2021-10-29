package com.rita.calendarprooo.home

import android.content.ContentValues.TAG
import android.util.Log
import androidx.annotation.Nullable
import androidx.lifecycle.*
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Transaction
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.rita.calendarprooo.data.Check
import com.rita.calendarprooo.data.Plan
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel : ViewModel() {

    private var _navigateToEdit = MutableLiveData<Boolean>()
    val navigateToEdit : LiveData<Boolean>
        get() = _navigateToEdit

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

    var listTotal = MutableLiveData<List<Plan>>()



    var Listtest : LiveData<List<Plan>> = Transformations.map(listTotal){
        var listFiltered = mutableListOf<Plan>()
        listTotal.value?.let{
            listFiltered = it.filter { it.isToDoList == false  } as MutableList<Plan>
        }
        listFiltered
    }

    var selectedStartTime = MutableLiveData<Long>()

    var selectedEndTime = MutableLiveData<Long>()

    fun swapCheckListItem(start:Int , end:Int){
        val todoListGet=_todoList.value
        Collections.swap(todoListGet,start,end)
        _todoList.value=todoListGet
    }


    fun startNavigateToEdit(){
        _navigateToEdit.value=true
    }

    fun doneNavigated(){
        _navigateToEdit.value=null
    }


    fun readPlan(){
        val list = mutableListOf<Plan>()
        val listBeforeToday = mutableListOf<Plan>()

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
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }

        //plan's start-time before today
        db.collection("plan")
            .whereEqualTo("owner","lisa@gmail.com")
            .whereLessThanOrEqualTo("start_time", selectedStartTime.value!!)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    val plan= document.toObject(Plan::class.java)
                    listBeforeToday.add(plan)
                }
                Log.i("Rita","listBeforeToday:　$listBeforeToday")

                val filteredList = listBeforeToday
                    .filter { it -> it.end_time!! >= selectedStartTime.value!! }

                list.addAll(filteredList)

                _scheduleList.value = list.filter { it -> it.isToDoList==false }
                _todoList.value = list.filter { it -> it.isToDoList==true && !it.isToDoListDone }
                _doneList.value= list.filter { it ->  it.isToDoListDone }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }

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
                    val listBeforeToday = mutableListOf<Plan>()
                    Log.d(TAG, "Current data: ")
                    for (item in snapshot) {
                        Log.d("Rita", item.toString())
                        val plan = item.toObject(Plan::class.java)
                        listBeforeToday.add(plan!!)
                    }
                    Log.i("Rita", "listBeforeToday onChanged:　$listBeforeToday")
                    val filteredList = listBeforeToday
                        .filter { it -> it.end_time!! >= selectedStartTime.value!! }
                    listFromToday.value = filteredList
                } else {
                    Log.d(TAG, "Current data: null")
                }
            }
    }

    fun getTotalList(){
        listFromToday.value?.let{
            var list = it.toMutableList()
            listBeforeToday.value?.let { it1 -> list?.addAll(it1) }
            //listTotal.value = list

            _scheduleList.value = list.filter { it -> it.isToDoList == false }
            _todoList.value =
                list.filter { it -> it.isToDoList == true && !it.isToDoListDone }
            _doneList.value =
                list.filter { it ->  it.isToDoListDone }
        }
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