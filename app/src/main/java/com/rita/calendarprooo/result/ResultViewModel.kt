package com.rita.calendarprooo.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.PieEntry
import com.rita.calendarprooo.Util.Logger
import com.rita.calendarprooo.data.Plan
import com.rita.calendarprooo.data.User
import com.rita.calendarprooo.data.source.CalendarRepository
import com.rita.calendarprooo.ext.convertToTimeStamp
import com.rita.calendarprooo.ext.getToday
import com.rita.calendarprooo.login.UserManager

class ResultViewModel(val repository: CalendarRepository) : ViewModel() {

    val currentUser = MutableLiveData<User>()

    var listToday = MutableLiveData<List<Plan>>()

    var listBeforeToday = MutableLiveData<List<Plan>>()

    val selectedStartTime = MutableLiveData<Long>()

    val selectedEndTime = MutableLiveData<Long>()

    private val _todoList = MutableLiveData<List<Plan>>()
    val todoList: LiveData<List<Plan>>
        get() = _todoList

    val todoListSize: LiveData<Int> = Transformations.map(todoList) {
        var size = 0
        if (!todoList.value.isNullOrEmpty()) {
            size = todoList.value?.size!!
        }
        size
    }

    private var _doneList = MutableLiveData<List<Plan>>()
    val doneList: LiveData<List<Plan>>
        get() = _doneList

    val doneListReset = MutableLiveData<Boolean>()

    val categoryForDoneList = MutableLiveData<MutableMap<String, Float>>()

    val pieEntryList = MutableLiveData<MutableList<PieEntry>>()


    fun doneReset() {
        doneListReset.value = null
    }


    fun getPlansToday() {
        listToday = repository.getLivePlansToday(
            selectedStartTime.value!!,
            selectedEndTime.value!!, UserManager.user.value!!
        )
    }


    fun getPlansBeforeToday() {
        listBeforeToday = repository.getLivePlansBeforeToday(
            selectedStartTime.value!!,
            UserManager.user.value!!
        )
    }


    fun getPlansInTotal() {
        var list = listToday.value?.toMutableList()
        val listBefore = listBeforeToday.value?.toMutableList()

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
            _todoList.value = list.filter { it -> it.isToDoList == true && !it.isToDoListDone }
        }
    }


    fun getDone() {
        _doneList = repository.getLiveDone(
            selectedStartTime.value!!,
            selectedEndTime.value!!, UserManager.user.value!!
        )

        doneListReset.value = true
    }


    fun countForCategory(list: List<Plan>) {
        val categoryMap = mutableMapOf<String, Float>()

        Logger.i("countForCategory list: $list")

        if (!list.isNullOrEmpty()) {
            for (item in list) {
                if (!categoryMap.containsKey(item.category)) {
                    categoryMap["${item.category}"] = 1F

                    Logger.i("countForCategory count: ${categoryMap["${item.category}"]}")
                } else {
                    var count = categoryMap["${item.category}"]!!.toInt()
                    count += 1
                    categoryMap["${item.category}"] = count.toFloat()

                    Logger.i("countForCategory count - contain: ${categoryMap["${item.category}"]}")
                }
            }
        } else {
            categoryMap["No Done Task"] = 1F
        }

        Logger.i("countForCategory map: $categoryMap")

        categoryForDoneList.value = categoryMap

    }


    fun selectedTimeSet(date: String) {
        val timeList = convertToTimeStamp(date)
        selectedStartTime.value = timeList?.get(0)
        selectedEndTime.value = timeList?.get(1)
    }


    init {
        selectedTimeSet(getToday())
        getDone()
    }

}