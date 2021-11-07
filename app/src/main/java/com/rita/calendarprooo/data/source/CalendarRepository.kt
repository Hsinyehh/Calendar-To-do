package com.rita.calendarprooo.data.source

import androidx.lifecycle.MutableLiveData
import com.rita.calendarprooo.data.Check
import com.rita.calendarprooo.data.Plan
import com.rita.calendarprooo.data.User
import com.rita.calendarprooo.data.Result

interface CalendarRepository {
    fun getLivePlansFromToday(selectedStartTime: Long,selectedEndTime: Long,user: User):
            MutableLiveData<MutableList<Plan>>

    fun getLivePlansBeforeToday(selectedStartTime:Long, user:User):
            MutableLiveData<MutableList<Plan>>

    suspend fun createPlan(plan: Plan): Result<Boolean>

    suspend fun updatePlan(plan: Plan): Result<Boolean>

    suspend fun updatePlanByCheck(check: Check,
                                  checkList: MutableLiveData<MutableList<Check>>): Result<Boolean>

    suspend fun getCheckAndChangeStatus(check:Check, position:Int,
                                        checkList: MutableLiveData<MutableList<Check>>): Result<Boolean>

    suspend fun getCheckAndRemoveItem(check:Check, position:Int,
                                      checkList: MutableLiveData<MutableList<Check>>): Result<Boolean>

    fun getUser (id:String) : MutableLiveData<User>

    suspend fun createUser(newUser: User): Result<Boolean>
}