package com.rita.calendarprooo.data.source.remote

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.rita.calendarprooo.data.Check
import com.rita.calendarprooo.data.Plan
import com.rita.calendarprooo.data.User
import com.rita.calendarprooo.data.Result
import com.rita.calendarprooo.data.source.CalendarDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


object CalendarRemoteDataSource : CalendarDataSource {

    private const val PATH_PLAN = "plan"

    override fun getLivePlansFromToday(selectedStartTime: Long,selectedEndTime: Long ,user: User):
            MutableLiveData<MutableList<Plan>> {
        var livedata = MutableLiveData<MutableList<Plan>>()
        FirebaseFirestore.getInstance()
            .collection(PATH_PLAN)
            .whereEqualTo("owner","lisa@gmail.com")
            .whereGreaterThanOrEqualTo("start_time", selectedStartTime)
            .whereLessThanOrEqualTo("start_time", selectedEndTime)
            .addSnapshotListener { snapshot, e ->
                Log.i("Rita","addSnapshotListener detect")

                e?.let {
                    Log.i("Rita",
                        "[${this::class.simpleName}] Error getting documents. ${it.message}")
                }

                val list = mutableListOf<Plan>()
                for (item in snapshot!!) {
                    //Log.d("Rita", item.toString())
                    val plan= item.toObject(Plan::class.java)
                    list.add(plan!!)
                }
                Log.i("Rita", "list onChanged:　$list")
                livedata.value = list
            }
        return livedata
    }



    override fun getLivePlansBeforeToday(selectedStartTime:Long, user:User):
            MutableLiveData<MutableList<Plan>> {
        var livedata = MutableLiveData<MutableList<Plan>>()
        FirebaseFirestore.getInstance()
            .collection(PATH_PLAN)
            .whereEqualTo("owner","lisa@gmail.com")
            .whereLessThanOrEqualTo("start_time", selectedStartTime)
            .addSnapshotListener { snapshot, e ->
                Log.i("Rita","addSnapshotListener detect")

                e?.let {
                    Log.i("Rita",
                        "[${this::class.simpleName}] Error getting documents. ${it.message}")
                }
                val list = mutableListOf<Plan>()
                for (item in snapshot!!) {
                    //Log.d("Rita", item.toString())
                    val plan= item.toObject(Plan::class.java)
                    list.add(plan!!)
                }
                Log.i("Rita", "list onChanged:　$list")
                val filteredList = list.filter { it -> it.end_time!! >= selectedStartTime }
                livedata.value = filteredList.toMutableList()
            }
        return livedata
    }

    override suspend fun createPlan(plan: Plan): Result<Boolean> = suspendCoroutine{ continuation ->
        val newPlanRef = FirebaseFirestore.getInstance().collection("plan").document()
        newPlanRef
            .set(plan)
            .addOnSuccessListener { documentReference ->
                Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: $newPlanRef.id")
                continuation.resume(Result.Success(true))
            }
            .addOnFailureListener {
                Log.w(ContentValues.TAG, "Error adding document", it)
                continuation.resume(Result.Error(it))
            }
    }

    override suspend fun updatePlan(plan: Plan): Result<Boolean>
    = suspendCoroutine{ continuation ->
        val planRef = plan.id?.let {
            FirebaseFirestore.getInstance().collection(PATH_PLAN).document(it) }
        planRef!!
            .update("isToDoListDone", plan.isToDoListDone)
            .addOnSuccessListener{
                Log.d(ContentValues.TAG, "DocumentSnapshot successfully updated!")
                continuation.resume(Result.Success(true))
            }
            .addOnFailureListener {
                Log.w(ContentValues.TAG, "Error updating document", it)
                continuation.resume(Result.Error(it))
            }
    }

    override suspend fun updatePlanByCheck(check: Check,
                                           checkList: MutableLiveData<MutableList<Check>>):
            Result<Boolean>
    = suspendCoroutine{ continuation ->
        val planRef = check.plan_id?.let {
            FirebaseFirestore.getInstance()
            .collection(PATH_PLAN).document(it) }
        Log.i("Rita","writeCheckItemDone-planRef: $planRef")
        planRef!!
            .update("checkList",checkList.value)
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "DocumentSnapshot successfully updated!")
                continuation.resume(Result.Success(true))
            }
            .addOnFailureListener {
                Log.w(ContentValues.TAG, "Error updating document", it)
                continuation.resume(Result.Error(it))
            }
    }

    override suspend fun getCheckAndChangeStatus(check:Check, position:Int,
                                                 checkList:MutableLiveData<MutableList<Check>>):
            Result<Boolean>
    = suspendCoroutine{ continuation ->
        val planRef = check.plan_id?.let {
            FirebaseFirestore.getInstance().collection(PATH_PLAN).document(it) }
        var plan : Plan? = null
        planRef!!.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(ContentValues.TAG, "DocumentSnapshot data: ${document.data}")
                    plan= document.toObject(Plan::class.java)
                    if (plan != null) {
                        plan!!.checkList!![position] = check
                        checkList.value = plan!!.checkList
                        Log.i("Rita"," getCheckList-itemUpdate as $check")
                        //Store isDone status
                        //updatePlanByCheck(check, checkList)
                    }
                } else {
                    Log.d(ContentValues.TAG, "No such document")
                }
                continuation.resume(Result.Success(true))
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "get failed with ", exception)
                continuation.resume(Result.Error(exception))
            }
    }

    override suspend fun getCheckAndRemoveItem(check:Check, position:Int,
                                               checkList: MutableLiveData<MutableList<Check>>):
            Result<Boolean> = suspendCoroutine{ continuation ->
        val planRef = check.plan_id?.let {
            FirebaseFirestore.getInstance().collection(PATH_PLAN).document(it) }

        planRef!!.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(ContentValues.TAG, "DocumentSnapshot data: ${document.data}")
                    val plan= document.toObject(Plan::class.java)
                        plan!!.checkList!!.removeAt(position)
                        checkList.value = plan.checkList
                        Log.i("Rita"," getCheckList-itemRemoved as $check")
                        //Store isDone status
                        //updatePlanByCheck(item)
                } else {
                    Log.d(ContentValues.TAG, "No such document")
                }
                continuation.resume(Result.Success(true))
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "get failed with ", exception)
            }
    }
}