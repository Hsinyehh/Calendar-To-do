package com.rita.calendarprooo.data.source

import androidx.lifecycle.MutableLiveData
import com.rita.calendarprooo.data.Check
import com.rita.calendarprooo.data.Plan
import com.rita.calendarprooo.data.User
import com.rita.calendarprooo.data.Result

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

    override suspend fun createPlan(plan: Plan): Result<Boolean> {
        return remoteDataSource.createPlan(plan)
    }

    override suspend fun updatePlan(plan: Plan): Result<Boolean> {
        return remoteDataSource.updatePlan(plan)
    }

    override suspend fun updatePlanByCheck(
        check: Check, checkList:
        MutableLiveData<MutableList<Check>>
    ): Result<Boolean> {
        return remoteDataSource.updatePlanByCheck(check, checkList)
    }

    override suspend fun getCheckAndChangeStatus(
        check: Check, position: Int,
        checkList: MutableLiveData<MutableList<Check>>
    ):
            Result<Boolean> {
        return remoteDataSource.getCheckAndChangeStatus(check, position, checkList)
    }

    override suspend fun getCheckAndRemoveItem(
        check: Check, position: Int,
        checkList: MutableLiveData<MutableList<Check>>
    ):
            Result<Boolean> {
        return remoteDataSource.getCheckAndRemoveItem(check, position, checkList)
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
}