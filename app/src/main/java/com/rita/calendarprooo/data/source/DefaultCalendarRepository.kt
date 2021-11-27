package com.rita.calendarprooo.data.source

import androidx.lifecycle.MutableLiveData
import com.rita.calendarprooo.data.*

class DefaultCalendarRepository(
    private val remoteDataSource: CalendarDataSource,
    private val localDataSource: CalendarDataSource
) : CalendarRepository {

    override suspend fun getPlansToday(selectedStartTime: Long, selectedEndTime: Long, user: User):
            Result<List<Plan>>{
        return remoteDataSource.getPlansToday(selectedStartTime, selectedEndTime, user)
    }

    override suspend fun getPlansBeforeToday(selectedStartTime: Long, user: User):
            Result<List<Plan>>{
        return remoteDataSource.getPlansBeforeToday(selectedStartTime, user)
    }

    override fun getLivePlansToday(selectedStartTime: Long, selectedEndTime: Long, user: User)
            : MutableLiveData<List<Plan>> {
        return remoteDataSource.getLivePlansToday(selectedStartTime, selectedEndTime, user)
    }

    override fun getLivePlansBeforeToday(selectedStartTime: Long, user: User):
            MutableLiveData<List<Plan>> {
        return remoteDataSource.getLivePlansBeforeToday(selectedStartTime, user)
    }

    override suspend fun updatePlanForDoneStatus(plan: Plan): Result<Boolean> {
        return remoteDataSource.updatePlanForDoneStatus(plan)
    }

    override suspend fun updatePlanForCheckList(
        plan: Plan, checkList:
        MutableLiveData<MutableList<Check>>
    ): Result<Boolean> {
        return remoteDataSource.updatePlanForCheckList(plan, checkList)
    }

    override suspend fun getPlanByCheck(check: Check): Result<Plan> {
        return remoteDataSource.getPlanByCheck(check)
    }

    override fun getSortLivePlansToday(selectedStartTime: Long, selectedEndTime: Long, user: User,
                                       category: String):
            MutableLiveData<List<Plan>>{
        return remoteDataSource.getSortLivePlansToday(selectedStartTime, selectedEndTime,
            user, category)
    }

    override fun getSortLivePlansBeforeToday(selectedStartTime: Long, user: User, category: String):
            MutableLiveData<List<Plan>>{
        return remoteDataSource.getSortLivePlansBeforeToday(selectedStartTime, user, category)
    }

    override suspend fun createPlan(plan: Plan): Result<Boolean> {
        return remoteDataSource.createPlan(plan)
    }

    override suspend fun updatePlan(plan: Plan): Result<Boolean> {
        return remoteDataSource.updatePlan(plan)
    }

    override suspend fun updatePlanExtra(plan: Plan): Result<Boolean>{
        return remoteDataSource.updatePlanExtra(plan)
    }

    override suspend fun updateUserExtra(user: User): Result<Boolean>{
        return remoteDataSource.updateUserExtra(user)
    }

    override suspend fun getUserByEmail(email: String): Result<User>{
        return remoteDataSource.getUserByEmail(email)
    }

    override fun getUser(id: String): MutableLiveData<User> {
        return remoteDataSource.getUser(id)
    }

    override suspend fun createUser(newUser: User): Result<Boolean> {
        return remoteDataSource.createUser(newUser)
    }

    override suspend fun updateUser(user: User): Result<Boolean>{
        return remoteDataSource.updateUser(user)
    }

    override suspend fun checkUserCreated(user: User): Result<Boolean>{
        return remoteDataSource.checkUserCreated(user)
    }

    override fun getLiveDone(selectedStartTime: Long, selectedEndTime: Long, user: User):
            MutableLiveData<List<Plan>> {
        return remoteDataSource.getLiveDone(selectedStartTime, selectedEndTime, user)
    }

    override fun getLiveInvitations(user: User): MutableLiveData<List<Plan>>{
        return remoteDataSource.getLiveInvitations(user)
    }

    override suspend fun getPlansByInvitation(item: Invitation): Result<MutableList<Plan>>{
        return remoteDataSource.getPlansByInvitation(item)
    }
}