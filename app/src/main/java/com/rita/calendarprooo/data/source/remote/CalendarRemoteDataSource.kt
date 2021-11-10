package com.rita.calendarprooo.data.source.remote

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.rita.calendarprooo.CalendarProApplication
import com.rita.calendarprooo.R
import com.rita.calendarprooo.data.Check
import com.rita.calendarprooo.data.Plan
import com.rita.calendarprooo.data.User
import com.rita.calendarprooo.data.Result
import com.rita.calendarprooo.data.source.CalendarDataSource
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


object CalendarRemoteDataSource : CalendarDataSource {

    private const val PATH_PLAN = "plan"
    private const val PATH_USER = "user"

    override fun getLivePlansFromToday(selectedStartTime: Long,selectedEndTime: Long, user: User):
            MutableLiveData<List<Plan>> {
        var livedata = MutableLiveData<List<Plan>>()
        FirebaseFirestore.getInstance()
            .collection(PATH_PLAN)
            .whereArrayContains("collaborator", user.email)
            .whereGreaterThanOrEqualTo("start_time", selectedStartTime)
            .whereLessThanOrEqualTo("start_time", selectedEndTime)
            .addSnapshotListener { snapshot, e ->
                Log.i("Rita","Today SnapshotListener user email:${user.email} ")
                e?.let {
                    Log.i("Rita",
                        "[${this::class.simpleName}] Error getting documents. ${it.message}")
                }
                val list = mutableListOf<Plan>()
                for (item in snapshot!!) {
                    Log.i("Rita", "plan:　${item.data}")
                    val plan= item.toObject(Plan::class.java)
                    list.add(plan!!)
                }
                Log.i("Rita", "list onChanged:　$list")
                livedata.value = list
            }
        return livedata
    }



    override fun getLivePlansBeforeToday(selectedStartTime:Long, user:User):
            MutableLiveData<List<Plan>> {
        var livedata = MutableLiveData<List<Plan>>()
        FirebaseFirestore.getInstance()
            .collection(PATH_PLAN)
            .whereArrayContains("collaborator", user.email)
            .whereLessThanOrEqualTo("start_time", selectedStartTime)
            .addSnapshotListener { snapshot, e ->
                Log.i("Rita","Before SnapshotListener user email:${user.email} ")

                e?.let {
                    Log.i("Rita",
                        "[${this::class.simpleName}] Error getting documents. ${it.message}")
                }
                val list = mutableListOf<Plan>()
                for (item in snapshot!!) {
                    val plan= item.toObject(Plan::class.java)
                    list.add(plan!!)
                }
                Log.i("Rita", "list onChanged:　$list")
                val filteredList = list.filter { it -> it.end_time!! >= selectedStartTime }
                livedata.value = filteredList
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

    override fun getUser (id: String) : MutableLiveData<User>{
        val liveData = MutableLiveData<User>()

        FirebaseFirestore.getInstance()
            .collection(PATH_USER)
            .whereEqualTo("id", id)
            .addSnapshotListener { snapshot, exception ->
                Log.d(TAG, "getUser snapshot ${snapshot!!.documents}")
                exception?.let {
                    Log.d(TAG, "[${this::class.simpleName}] Error getting documents. ${it.message}")
                }

                for (document in snapshot!!) {
                    val user = document.toObject(User::class.java)
                    liveData.value = user
                }
            }
        return liveData
    }

    override suspend fun createUser(newUser: User): Result<Boolean> = suspendCoroutine { continuation ->
        val db = FirebaseFirestore.getInstance()

        db.collection(PATH_USER).document(newUser.id)
            .set(newUser)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i("Rita","Create: $newUser")
                    continuation.resume(Result.Success(true))
                } else {
                    task.exception?.let {

                        Log.i("Rita","[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(CalendarProApplication.instance.getString(R.string.Error)))
                }
            }
    }

    override fun getLiveDone(selectedStartTime: Long,selectedEndTime: Long,user: User):
            MutableLiveData<List<Plan>>{
        var livedata = MutableLiveData<List<Plan>>()
        FirebaseFirestore.getInstance()
            .collection(PATH_PLAN)
            .whereArrayContains("collaborator", user.email)
            .whereGreaterThanOrEqualTo("done_time", selectedStartTime)
            .whereLessThanOrEqualTo("done_time", selectedEndTime)
            .addSnapshotListener { snapshot, e ->
                Log.i("Rita","Today SnapshotListener user email:${user.email} ")
                e?.let {
                    Log.i("Rita",
                        "[${this::class.simpleName}] Error getting documents. ${it.message}")
                }
                val list = mutableListOf<Plan>()
                if (snapshot != null) {
                    for (item in snapshot) {
                        Log.i("Rita", "plan:　${item.data}")
                        val plan= item.toObject(Plan::class.java)
                        list.add(plan!!)
                    }
                }
                Log.i("Rita", "list onChanged:　$list")
                livedata.value = list
            }
        return livedata
    }
}