package com.rita.calendarprooo.result

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.PieEntry
import com.rita.calendarprooo.data.Plan
import com.rita.calendarprooo.data.User
import com.rita.calendarprooo.data.source.CalendarRepository
import com.rita.calendarprooo.ext.convertToTimeStamp
import com.rita.calendarprooo.ext.getToday
import com.rita.calendarprooo.login.UserManager

class ResultViewModel(val repository: CalendarRepository) : ViewModel() {

    var currentUser = MutableLiveData<User>()

    var readListFromToday = MutableLiveData<List<Plan>>()

    var readListBeforeToday = MutableLiveData<List<Plan>>()

    var selectedStartTime = MutableLiveData<Long>()

    var selectedEndTime = MutableLiveData<Long>()

    private var _todoList = MutableLiveData<List<Plan>>()
    val todoList : LiveData<List<Plan>>
        get() = _todoList

    val todoListSize : LiveData<Int> = Transformations.map(todoList){
        var size = 0
        if(!todoList.value.isNullOrEmpty()){
            size = todoList.value?.size!!
        }
        size
    }

    private var _doneList = MutableLiveData<List<Plan>>()
    val doneList : LiveData<List<Plan>>
        get() = _doneList

    val doneListSize : LiveData<Int> = Transformations.map(doneList){
        var size = 0
        if(!doneList.value.isNullOrEmpty()){
            size = doneList.value?.size!!
        }
        size
    }

    var categoryForDoneList = MutableLiveData<MutableMap<String, Float>>()

    var pieEntryList = MutableLiveData<MutableList<PieEntry>>()


    fun readPlanFromToday(){
        Log.i("Rita","1. readPlanFromToday ${readListFromToday.value }")
        readListFromToday = repository.getLivePlansFromToday(selectedStartTime.value!!,
            selectedEndTime.value!!,UserManager.user.value!!)

        //readListFromToday.value = list.value
    }

    fun readPlanBeforeToday(){
        Log.i("Rita","1. readPlanBeforeToday ${readListBeforeToday.value }")
        readListBeforeToday = repository.getLivePlansBeforeToday(selectedStartTime.value!!,
            UserManager.user.value!!)
        Log.i("Rita","2. readPlanBeforeToday ${readListBeforeToday.value }")
        //readListBeforeToday.value = list.value
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
            _todoList.value = list.filter { it -> it.isToDoList==true && !it.isToDoListDone }
            _doneList.value= list.filter { it ->  it.isToDoListDone }
        }
    }

    fun countForCategory(list: List<Plan>){
        var categoryMap = mutableMapOf<String, Float>()
        if(!list.isNullOrEmpty()){
            for(item in list){
                if(!categoryMap.containsKey(item.category)){
                    categoryMap["${item.category}"] = 1F
                }
                else{
                    val count = categoryMap["${item.category}"]
                    count!!.plus(1F)
                }
            }
        }
        categoryForDoneList.value = categoryMap

    }

    fun selectedTimeSet(date: String){
        val timeList = convertToTimeStamp(date)
        selectedStartTime.value = timeList?.get(0)
        selectedEndTime.value = timeList?.get(1)
    }

    fun initPieEntryList(){
        pieEntryList.value = mutableListOf<PieEntry>(PieEntry(100F,"None"))
    }


    init{
        Log.i("Rita", "${UserManager.user.value}")

        initPieEntryList()

        selectedTimeSet(getToday())

    }

}