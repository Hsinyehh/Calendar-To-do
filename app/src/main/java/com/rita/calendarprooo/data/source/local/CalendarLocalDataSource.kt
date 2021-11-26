package com.rita.calendarprooo.data.source.local

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.rita.calendarprooo.data.Check
import com.rita.calendarprooo.data.Plan
import com.rita.calendarprooo.data.User
import com.rita.calendarprooo.data.source.CalendarDataSource
import com.rita.calendarprooo.data.Result

class CalendarLocalDataSource(val context: Context) : CalendarDataSource {
    override suspend fun getPlansToday(selectedStartTime: Long, selectedEndTime: Long, user: User):
            Result<List<Plan>>{
        TODO("not implemented")
    }

    override suspend fun getPlansBeforeToday(selectedStartTime: Long, user: User):
            Result<List<Plan>>{
        TODO("not implemented")
    }

    override fun getLivePlansToday(selectedStartTime: Long, selectedEndTime: Long, user: User):
            MutableLiveData<List<Plan>> {
        TODO("not implemented")
    }

    override fun getLivePlansBeforeToday(selectedStartTime: Long, user: User):
            MutableLiveData<List<Plan>> {
        TODO("not implemented")
    }

    override suspend fun updatePlanForDoneStatus(plan: Plan): Result<Boolean> {
        TODO("not implemented")
    }

    override suspend fun updatePlanByCheck(
        plan: Plan, checkList: MutableLiveData<MutableList<Check>>
    ): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getPlanByCheck(check: Check): Result<Plan>{
        TODO("Not yet implemented")
    }

    override fun getSortLivePlansToday(selectedStartTime: Long, selectedEndTime: Long, user: User,
                                       category: String):
            MutableLiveData<List<Plan>>{
        TODO("Not yet implemented")
    }

    override fun getSortLivePlansBeforeToday(selectedStartTime: Long, user: User, category: String):
            MutableLiveData<List<Plan>>{
        TODO("Not yet implemented")
    }

    override suspend fun createPlan(plan: Plan): Result<Boolean> {
        TODO("not implemented")
    }

    override suspend fun updatePlan(plan: Plan): Result<Boolean>{
        TODO("not implemented")
    }

    override fun getUser(id: String): MutableLiveData<User> {
        TODO("Not yet implemented")
    }

    override suspend fun createUser(newUser: User): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun updateUser(user: User): Result<Boolean>{
        TODO("Not yet implemented")
    }

    override suspend fun checkUserCreated(user: User): Result<Boolean>{
        TODO("Not yet implemented")
    }

    override fun getLiveDone(selectedStartTime: Long, selectedEndTime: Long, user: User):
            MutableLiveData<List<Plan>> {
        TODO("Not yet implemented")
    }

}