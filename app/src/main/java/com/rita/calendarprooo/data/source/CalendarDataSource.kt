package com.rita.calendarprooo.data.source

import androidx.lifecycle.MutableLiveData
import com.rita.calendarprooo.data.*

interface CalendarDataSource {

    suspend fun getPlansToday(selectedStartTime: Long, selectedEndTime: Long, user: User):
            Result<List<Plan>>

    suspend fun getPlansBeforeToday(selectedStartTime: Long, user: User):
            Result<List<Plan>>

    fun getLivePlansToday(selectedStartTime: Long, selectedEndTime: Long, user: User):
            MutableLiveData<List<Plan>>

    fun getLivePlansBeforeToday(selectedStartTime: Long, user: User):
            MutableLiveData<List<Plan>>

    suspend fun updatePlanForDoneStatus(plan: Plan): Result<Boolean>

    suspend fun updatePlanForCheckList(
        plan: Plan,
        checkList: MutableLiveData<MutableList<Check>>
    ): Result<Boolean>

    suspend fun getPlanByCheck(check: Check): Result<Plan>

    fun getSortLivePlansToday(
        selectedStartTime: Long, selectedEndTime: Long, user: User,
        category: String
    ):
            MutableLiveData<List<Plan>>

    fun getSortLivePlansBeforeToday(selectedStartTime: Long, user: User, category: String):
            MutableLiveData<List<Plan>>

    suspend fun createPlan(plan: Plan): Result<Boolean>

    suspend fun updatePlan(plan: Plan): Result<Boolean>

    suspend fun updatePlanExtra(plan: Plan): Result<Boolean>

    suspend fun updateUserExtra(user: User): Result<Boolean>

    suspend fun getUserByEmail(email: String): Result<User>

    fun getUser(id: String): MutableLiveData<User>

    suspend fun createUser(newUser: User): Result<Boolean>

    suspend fun updateUser(user: User): Result<Boolean>

    suspend fun checkUserCreated(user: User): Result<Boolean>

    fun getLiveDone(selectedStartTime: Long, selectedEndTime: Long, user: User):
            MutableLiveData<List<Plan>>

    fun getLiveInvitations(user: User): MutableLiveData<List<Plan>>

    suspend fun getPlansByInvitation(item: Invitation): Result<MutableList<Plan>>
}