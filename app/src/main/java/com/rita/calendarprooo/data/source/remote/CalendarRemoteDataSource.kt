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

    override suspend fun getPlansToday(selectedStartTime: Long, selectedEndTime: Long, user: User):
            Result<List<Plan>> = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance()
            .collection(PATH_PLAN)
            .whereArrayContains("collaborator", user.email)
            .whereGreaterThanOrEqualTo("start_time", selectedStartTime)
            .whereLessThanOrEqualTo("start_time", selectedEndTime)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i("Rita","fb - getPlansToday result.size"+ task.result.size())
                    val list = mutableListOf<Plan>()
                    for (document in task.result!!) {
                        Log.i("Rita","fb - getPlansToday doc"+ document.id + " =>"  + document.data)

                        val plan = document.toObject(Plan::class.java)
                        list.add(plan)
                    }
                    continuation.resume(Result.Success(list))
                } else {
                    task.exception?.let {
                        Log.w("Rita",
                            "[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(CalendarProApplication.instance.getString(R.string.error)))
                }
            }
    }

    override suspend fun getPlansBeforeToday(selectedStartTime: Long, user: User):
            Result<List<Plan>> = suspendCoroutine { continuation ->

        FirebaseFirestore.getInstance()
            .collection(PATH_PLAN)
            .whereArrayContains("collaborator", user.email)
            .whereLessThan("start_time", selectedStartTime)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val list = mutableListOf<Plan>()
                    Log.i("Rita","fb - getPlansBeforeToday result.size"+ task.result.size())

                    for (document in task.result!!) {
                        val plan = document.toObject(Plan::class.java)
                        list.add(plan)
                    }
                    Log.i("Rita", "fb - getPlansBeforeToday listBeforeToday:　$list")

                    val filteredList = list
                        .filter {  it.end_time!! >= selectedStartTime}

                    Log.i("Rita", "fb - getPlansBeforeToday filtered listBeforeToday:　$filteredList")

                    continuation.resume(Result.Success(filteredList))
                } else {
                    task.exception?.let {

                        Log.w("Rita",
                            "[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(CalendarProApplication.instance.getString(R.string.error)))
                }
            }
    }

    override fun getLivePlansToday(selectedStartTime: Long, selectedEndTime: Long, user: User):
            MutableLiveData<List<Plan>> {
        val livedata = MutableLiveData<List<Plan>>()
        FirebaseFirestore.getInstance()
            .collection(PATH_PLAN)
            .whereArrayContains("collaborator", user.email)
            .whereGreaterThanOrEqualTo("start_time", selectedStartTime)
            .whereLessThanOrEqualTo("start_time", selectedEndTime)
            .addSnapshotListener { snapshot, e ->
                Log.i("Rita", "Today SnapshotListener user email:${user.email} ")
                e?.let {
                    Log.i(
                        "Rita",
                        "[${this::class.simpleName}] Error getting documents. ${it.message}"
                    )
                }
                val list = mutableListOf<Plan>()
                for (item in snapshot!!) {
                    Log.i("Rita", "plan:　${item.data}")
                    val plan = item.toObject(Plan::class.java)
                    list.add(plan)
                }
                Log.i("Rita", "list onChanged:　$list")
                livedata.value = list
            }
        return livedata
    }

    override fun getLivePlansBeforeToday(selectedStartTime: Long, user: User):
            MutableLiveData<List<Plan>> {
        val livedata = MutableLiveData<List<Plan>>()
        FirebaseFirestore.getInstance()
            .collection(PATH_PLAN)
            .whereArrayContains("collaborator", user.email)
            .whereLessThanOrEqualTo("start_time", selectedStartTime)
            .addSnapshotListener { snapshot, e ->
                Log.i("Rita", "Before SnapshotListener user email:${user.email} ")

                e?.let {
                    Log.i(
                        "Rita",
                        "[${this::class.simpleName}] Error getting documents. ${it.message}"
                    )
                }
                val list = mutableListOf<Plan>()
                for (item in snapshot!!) {
                    val plan = item.toObject(Plan::class.java)
                    list.add(plan)
                }
                Log.i("Rita", "list onChanged:　$list")
                val filteredList = list.filter { it.end_time!! >= selectedStartTime }
                livedata.value = filteredList
            }
        return livedata
    }

    override suspend fun createPlan(plan: Plan): Result<Boolean> =
        suspendCoroutine { continuation ->
            val newPlanRef = FirebaseFirestore.getInstance().collection("plan").document()
            newPlanRef
                .set(plan)
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot added with ID: $newPlanRef.id")
                    continuation.resume(Result.Success(true))
                }
                .addOnFailureListener {
                    Log.w(TAG, "Error adding document", it)
                    continuation.resume(Result.Error(it))
                }
        }

    override suspend fun updatePlanForDoneStatus(plan: Plan): Result<Boolean> =
        suspendCoroutine { continuation ->
            val planRef = plan.id?.let {
                FirebaseFirestore.getInstance().collection(PATH_PLAN).document(it)
            }

            planRef!!
                .update("toDoListDone", plan.isToDoListDone,
                    "done_time", plan.done_time,
                    "doner", plan.doner)
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot successfully updated!")
                    continuation.resume(Result.Success(true))
                }
                .addOnFailureListener {
                    Log.w(TAG, "Error updating document", it)
                    continuation.resume(Result.Error(it))
                }
        }

    override suspend fun updatePlanByCheck(
        plan: Plan,
        checkList: MutableLiveData<MutableList<Check>>
    ): Result<Boolean> = suspendCoroutine { continuation ->

        val planRef = plan.id?.let {
            FirebaseFirestore.getInstance()
                .collection(PATH_PLAN).document(it)
        }

        planRef!!
            .update("checkList", checkList.value)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully updated!")
                continuation.resume(Result.Success(true))
            }
            .addOnFailureListener {
                Log.w(TAG, "Error updating document", it)
                continuation.resume(Result.Error(it))
            }
    }

    override suspend fun getPlanByCheck(
        check: Check): Result<Plan> = suspendCoroutine { continuation ->

        val planRef = check.plan_id?.let {
            FirebaseFirestore.getInstance().collection(PATH_PLAN).document(it)
        }

        planRef!!.get()
            .addOnSuccessListener { document ->
                var plan : Plan?
                if (document != null) {
                    plan = document.toObject(Plan::class.java)!!
                    continuation.resume(Result.Success(plan))
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
                continuation.resume(Result.Error(exception))
            }
    }

    override fun getUser(id: String): MutableLiveData<User> {
        val liveData = MutableLiveData<User>()

        FirebaseFirestore.getInstance()
            .collection(PATH_USER)
            .whereEqualTo("id", id)
            .addSnapshotListener { snapshot, exception ->
                Log.d(TAG, "getUser snapshot ${snapshot!!.documents}")
                exception?.let {
                    Log.d(TAG, "[${this::class.simpleName}] Error getting documents. ${it.message}")
                }

                for (document in snapshot) {
                    val user = document.toObject(User::class.java)
                    liveData.value = user
                }
            }
        return liveData
    }

    override suspend fun createUser(newUser: User): Result<Boolean> =
        suspendCoroutine { continuation ->
            val db = FirebaseFirestore.getInstance()
            Log.i("Rita", "createUser - newUser: $newUser")
            Log.i("Rita", "createUser - ref: ${db.collection(PATH_USER).document(newUser.email)}")

            db.collection(PATH_USER).document(newUser.email)
                .set(newUser)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.i("Rita", "createUser Success Task $newUser")
                        continuation.resume(Result.Success(true))
                    } else {
                        task.exception?.let {

                            Log.i(
                                "Rita",
                                "[${this::class.simpleName}] Error getting documents. ${it.message}"
                            )
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail(CalendarProApplication.instance.getString(R.string.Error)))
                    }
                }
        }

    override suspend fun updateUser(user: User): Result<Boolean> =
    suspendCoroutine { continuation ->
        val userRef = user.email.let {
            FirebaseFirestore.getInstance().collection(PATH_USER).document(it)
        }
        Log.i("Rita", "updateUser - user: $user")
        Log.i("Rita", "updateUser - userRef: $userRef")

        userRef
            .update("id", user.id,
                "photo",user.photo, "name",user.name)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully updated!")
                continuation.resume(Result.Success(true))
            }
            .addOnFailureListener {
                Log.w(TAG, "Error updating document", it)
                continuation.resume(Result.Error(it))
            }
    }

    override suspend fun checkUserCreated(user: User): Result<Boolean>
     = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance()
            .collection(PATH_USER)
            .whereEqualTo("email", user.email)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i("Rita","checkUserCreated")
                    val list = mutableListOf<User>()
                    for (document in task.result!!) {
                        Log.d("Rita",document.id + " => " + document.data)
                        val user = document.toObject(User::class.java)
                        list.add(user)
                    }
                    val isUserExisted: Boolean?
                    if (list.size > 0) {
                        isUserExisted = true
                    } else {
                        isUserExisted = false
                        Log.d(TAG, "No such document")
                    }
                    Log.i("Rita","checkUserCreated - $isUserExisted")
                    continuation.resume(Result.Success(isUserExisted))
                } else {
                    task.exception?.let {
                        Log.d(TAG, "[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(CalendarProApplication.instance.getString(R.string.error)))
                }
            }
    }

    override fun getLiveDone(selectedStartTime: Long, selectedEndTime: Long, user: User):
            MutableLiveData<List<Plan>> {
        val livedata = MutableLiveData<List<Plan>>()
        FirebaseFirestore.getInstance()
            .collection(PATH_PLAN)
            .whereArrayContains("collaborator", user.email)
            .whereGreaterThanOrEqualTo("done_time", selectedStartTime)
            .whereLessThanOrEqualTo("done_time", selectedEndTime)
            .addSnapshotListener { snapshot, e ->
                Log.i("Rita", "Today SnapshotListener user email:${user.email} ")
                e?.let {
                    Log.i(
                        "Rita",
                        "[${this::class.simpleName}] Error getting documents. ${it.message}"
                    )
                }
                val list = mutableListOf<Plan>()
                if (snapshot != null) {
                    for (item in snapshot) {
                        Log.i("Rita", "plan:　${item.data}")
                        val plan = item.toObject(Plan::class.java)
                        list.add(plan)
                    }
                }
                Log.i("Rita", "list onChanged:　$list")
                livedata.value = list
            }
        Log.i("Rita", "livedata:　${livedata.value}")
        return livedata
    }
}