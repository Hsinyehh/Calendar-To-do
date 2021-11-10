package com.rita.calendarprooo.data.source

import androidx.lifecycle.MutableLiveData
import com.rita.calendarprooo.data.Check
import com.rita.calendarprooo.data.Plan
import com.rita.calendarprooo.data.User
import com.rita.calendarprooo.data.Result

interface CalendarDataSource {
    fun getLivePlansFromToday(selectedStartTime: Long,selectedEndTime: Long,user: User):
            MutableLiveData<List<Plan>>

    fun getLivePlansBeforeToday(selectedStartTime:Long, user:User):
            MutableLiveData<List<Plan>>

    suspend fun createPlan(plan: Plan): Result<Boolean>

    suspend fun updatePlan(plan: Plan): Result<Boolean>

    suspend fun updatePlanByCheck(check: Check,
                                  checkList: MutableLiveData<MutableList<Check>>): Result<Boolean>

    suspend fun getCheckAndChangeStatus(
        check: Check,
        checkList: Int,
        checkList1: MutableLiveData<MutableList<Check>>
    ): Result<Boolean>

    suspend fun getCheckAndRemoveItem(
        check: Check,
        checkList: Int,
        checkList1: MutableLiveData<MutableList<Check>>
    ): Result<Boolean>

    fun getUser (id:String) : MutableLiveData<User>

    suspend fun createUser(newUser: User): Result<Boolean>

    fun getLiveDone(selectedStartTime: Long,selectedEndTime: Long,user: User):
            MutableLiveData<List<Plan>>
}