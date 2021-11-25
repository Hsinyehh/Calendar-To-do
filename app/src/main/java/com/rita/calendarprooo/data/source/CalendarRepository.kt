package com.rita.calendarprooo.data.source

import androidx.lifecycle.MutableLiveData
import com.rita.calendarprooo.data.Check
import com.rita.calendarprooo.data.Plan
import com.rita.calendarprooo.data.User
import com.rita.calendarprooo.data.Result

interface CalendarRepository {
    suspend fun getPlansToday(selectedStartTime: Long, selectedEndTime: Long, user: User):
    Result<List<Plan>>

    suspend fun getPlansBeforeToday(selectedStartTime: Long, user: User):
            Result<List<Plan>>

    fun getLivePlansToday(selectedStartTime: Long, selectedEndTime: Long, user: User):
            MutableLiveData<List<Plan>>

    fun getLivePlansBeforeToday(selectedStartTime: Long, user: User):
            MutableLiveData<List<Plan>>

    suspend fun createPlan(plan: Plan): Result<Boolean>

    suspend fun updatePlanForDoneStatus(plan: Plan): Result<Boolean>

    suspend fun updatePlanByCheck(
        plan: Plan,
        checkList: MutableLiveData<MutableList<Check>>
    ): Result<Boolean>

    suspend fun getPlanByCheck(check: Check): Result<Plan>

    fun getUser(id: String): MutableLiveData<User>

    suspend fun createUser(newUser: User): Result<Boolean>

    suspend fun updateUser(user: User): Result<Boolean>

    suspend fun checkUserCreated(user: User): Result<Boolean>

    fun getLiveDone(selectedStartTime: Long, selectedEndTime: Long, user: User):
            MutableLiveData<List<Plan>>
}