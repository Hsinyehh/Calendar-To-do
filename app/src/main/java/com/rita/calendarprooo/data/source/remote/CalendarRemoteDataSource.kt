package com.rita.calendarprooo.data.source.remote

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.rita.calendarprooo.CalendarProApplication
import com.rita.calendarprooo.R
import com.rita.calendarprooo.Util.Logger
import com.rita.calendarprooo.data.*
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
                    Logger.i("fb - getPlansToday result.size"+ task.result.size())
                    val list = mutableListOf<Plan>()
                    for (document in task.result!!) {
                        Logger.i("fb - getPlansToday doc"+ document.id + " =>"  + document.data)

                        val plan = document.toObject(Plan::class.java)
                        list.add(plan)
                    }
                    continuation.resume(Result.Success(list))
                } else {
                    task.exception?.let {
                       Logger.w(
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
                    Logger.i("fb - getPlansBeforeToday result.size"+ task.result.size())

                    for (document in task.result!!) {
                        val plan = document.toObject(Plan::class.java)
                        list.add(plan)
                    }
                    Logger.i("fb - getPlansBeforeToday listBeforeToday:　$list")

                    val filteredList = list
                        .filter {  it.end_time!! >= selectedStartTime}

                    Logger.i("fb - getPlansBeforeToday filtered listBeforeToday:　$filteredList")

                    continuation.resume(Result.Success(filteredList))
                } else {
                    task.exception?.let {

                       Logger.w(
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
                Logger.i("Today SnapshotListener user email:${user.email} ")
                e?.let {
                    Logger.w(
                        "[${this::class.simpleName}] Error getting documents. ${it.message}"
                    )
                }
                val list = mutableListOf<Plan>()
                for (item in snapshot!!) {
                    Logger.i("plan:　${item.data}")
                    val plan = item.toObject(Plan::class.java)
                    list.add(plan)
                }
                Logger.i("list onChanged:　$list")
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
                Logger.i("Before SnapshotListener user email:${user.email} ")

                e?.let {
                    Logger.w(
                        "[${this::class.simpleName}] Error getting documents. ${it.message}"
                    )
                }
                val list = mutableListOf<Plan>()
                for (item in snapshot!!) {
                    val plan = item.toObject(Plan::class.java)
                    list.add(plan)
                }
                Logger.i("list onChanged:　$list")
                val filteredList = list.filter { it.end_time!! >= selectedStartTime }
                livedata.value = filteredList
            }
        return livedata
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
                    Logger.d( "DocumentSnapshot successfully updated!")
                    continuation.resume(Result.Success(true))
                }
                .addOnFailureListener {
                    Logger.w( "Error updating document: $it")
                    continuation.resume(Result.Error(it))
                }
        }

    override suspend fun updatePlanForCheckList(plan: Plan, checkList: MutableLiveData<MutableList<Check>>
    ): Result<Boolean> = suspendCoroutine { continuation ->

        val planRef = plan.id?.let {
            FirebaseFirestore.getInstance()
                .collection(PATH_PLAN).document(it)
        }

        planRef!!
            .update("checkList", checkList.value)
            .addOnSuccessListener {
                Logger.d( "DocumentSnapshot successfully updated!")
                continuation.resume(Result.Success(true))
            }
            .addOnFailureListener {
                Logger.w( "Error updating document: $it")
                continuation.resume(Result.Error(it))
            }
    }

    override suspend fun getPlanByCheck(check: Check):
            Result<Plan> = suspendCoroutine { continuation ->

        Logger.i("fb - getPlanByCheck check: $check")

        val planRef = check.plan_id?.let {
            FirebaseFirestore.getInstance().collection(PATH_PLAN).document(it)
        }

        planRef!!.get()
            .addOnSuccessListener { document ->
                val plan : Plan?
                Logger.i("fb - getPlanByCheck doc"+ document.data)
                if (document != null) {
                    plan = document.toObject(Plan::class.java)
                    if(plan != null) {
                        continuation.resume(Result.Success(plan))
                    }
                }
            }
            .addOnFailureListener { exception ->
                Logger.d( "get failed with: $exception")
                continuation.resume(Result.Error(exception))
            }
    }

    override fun getSortLivePlansToday(selectedStartTime: Long, selectedEndTime: Long, user: User,
                              category: String):
            MutableLiveData<List<Plan>>{
        val livedata = MutableLiveData<List<Plan>>()
        FirebaseFirestore.getInstance()
            .collection(PATH_PLAN)
            .whereArrayContains("collaborator", user.email)
            .whereEqualTo("category", category)
            .whereGreaterThanOrEqualTo("start_time", selectedStartTime)
            .whereLessThanOrEqualTo("start_time", selectedEndTime)
            .addSnapshotListener { snapshot, e ->
                Logger.i("Today SnapshotListener user email:${user.email} ")
                e?.let {
                    Logger.w(
                        "[${this::class.simpleName}] Error getting documents. ${it.message}"
                    )
                }
                val list = mutableListOf<Plan>()
                for (item in snapshot!!) {
                    Logger.i("plan:　${item.data}")
                    val plan = item.toObject(Plan::class.java)
                    list.add(plan)
                }
                Logger.i("list onChanged:　$list")
                livedata.value = list
            }
        return livedata
    }

    override fun getSortLivePlansBeforeToday(selectedStartTime: Long, user: User, category: String):
            MutableLiveData<List<Plan>>{
        val livedata = MutableLiveData<List<Plan>>()
        FirebaseFirestore.getInstance()
            .collection(PATH_PLAN)
            .whereArrayContains("collaborator", user.email)
            .whereEqualTo("category", category)
            .whereLessThanOrEqualTo("start_time", selectedStartTime)
            .addSnapshotListener { snapshot, e ->
                Logger.i("Before SnapshotListener user email:${user.email} ")

                e?.let {
                    Logger.w(
                        "[${this::class.simpleName}] Error getting documents. ${it.message}"
                    )
                }
                val list = mutableListOf<Plan>()
                for (item in snapshot!!) {
                    val plan = item.toObject(Plan::class.java)
                    list.add(plan)
                }
                Logger.i("list onChanged:　$list")
                val filteredList = list.filter { it.end_time!! >= selectedStartTime }
                livedata.value = filteredList
            }
        return livedata
    }

    override suspend fun createPlan(plan: Plan): Result<Boolean> =
        suspendCoroutine { continuation ->
            val newPlanRef = plan.id?.let {
                FirebaseFirestore.getInstance().collection(PATH_PLAN).document(it)
            }

            newPlanRef!!
                .set(plan)
                .addOnSuccessListener {
                    Logger.d( "DocumentSnapshot added with ID: $newPlanRef.id")
                    continuation.resume(Result.Success(true))
                }
                .addOnFailureListener {
                    Logger.w( "Error adding document: $it")
                    continuation.resume(Result.Error(it))
                }
        }

    override suspend fun updatePlan(plan: Plan): Result<Boolean> =
        suspendCoroutine { continuation ->
            val planRef = plan.id?.let {
                FirebaseFirestore.getInstance().collection(PATH_PLAN).document(it)
            }

            planRef!!
                .update(
                    "title", plan.title,
                    "description", plan.description,
                    "location", plan.location,
                    "start_time", plan.start_time,
                    "end_time", plan.end_time,
                    "start_time_detail", plan.start_time_detail,
                    "end_time_detail", plan.end_time_detail,
                    "category", plan.category,
                    "categoryPosition", plan.categoryPosition,
                    "categoryList", plan.categoryList,
                    "checkList", plan.checkList,
                    "toDoList", plan.isToDoList)
                .addOnSuccessListener {
                    Logger.d( "DocumentSnapshot successfully updated!")
                    continuation.resume(Result.Success(true))
                }
                .addOnFailureListener {
                    Logger.w( "Error updating document: $it")
                    continuation.resume(Result.Error(it))
                }
        }

    override suspend fun updatePlanExtra(plan: Plan): Result<Boolean> =
        suspendCoroutine { continuation ->
            val planRef = plan.id?.let {
                FirebaseFirestore.getInstance().collection(PATH_PLAN).document(it)
            }

            planRef!!
                .update("invitation", plan.invitation,
                "categoryList", plan.categoryList,
                    "collaborator", plan.collaborator)
                .addOnSuccessListener {
                    Logger.d( "DocumentSnapshot successfully updated!")
                    continuation.resume(Result.Success(true))
                }
                .addOnFailureListener {
                    Logger.w( "Error updating document: $it")
                    continuation.resume(Result.Error(it))
                }
    }

    override suspend fun updateUserExtra(user: User): Result<Boolean> =
        suspendCoroutine { continuation ->
            val userRef = user.email.let {
                FirebaseFirestore.getInstance().collection(PATH_USER).document(it)
            }
            Logger.i("updateUser - user: $user")
            Logger.i("updateUser - userRef: $userRef")

            userRef
                .update("invitationList", user.invitationList,
                    "categoryList", user.categoryList)
                .addOnSuccessListener {
                    Logger.d( "DocumentSnapshot successfully updated!")
                    continuation.resume(Result.Success(true))
                }
                .addOnFailureListener {
                    Logger.w( "Error updating document: $it")
                    continuation.resume(Result.Error(it))
                }

    }

    override suspend fun getUserByEmail(email: String): Result<User> =
        suspendCoroutine { continuation ->

        Logger.i("fb - getUserByEmail email: $email")

        val userRef = FirebaseFirestore.getInstance().collection(PATH_USER).document(email)

        userRef.get()
            .addOnSuccessListener { document ->
                Logger.i("fb - getUserByEmail doc "+ document.data)
                if (document != null) {
                    val user = document.toObject(User::class.java)
                    if(user != null) {
                        continuation.resume(Result.Success(user))
                    }else{
                        val nullUser = User()
                        continuation.resume(Result.Success(nullUser))
                    }
                }
            }
            .addOnFailureListener { exception ->
                Logger.d( "get failed with: $exception")
                continuation.resume(Result.Error(exception))
            }
    }

    override fun getUser(id: String): MutableLiveData<User> {
        val liveData = MutableLiveData<User>()

        FirebaseFirestore.getInstance()
            .collection(PATH_USER)
            .whereEqualTo("id", id)
            .addSnapshotListener { snapshot, exception ->
                Logger.i("getUser snapshot ${snapshot!!.documents}")
                exception?.let {
                    Logger.w( "[${this::class.simpleName}] Error getting documents. ${it.message}")
                }
                for (document in snapshot) {
                    val user = document.toObject(User::class.java)
                    liveData.value = user
                }
            }
        Logger.i("getUser: ${liveData.value}")
        return liveData
    }

    override suspend fun createUser(newUser: User): Result<Boolean> =
        suspendCoroutine { continuation ->
            val db = FirebaseFirestore.getInstance()
            Logger.i("createUser - newUser: $newUser")
            Logger.i("createUser - ref: ${db.collection(PATH_USER).document(newUser.email)}")

            db.collection(PATH_USER).document(newUser.email)
                .set(newUser)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Logger.i("createUser Success Task $newUser")
                        continuation.resume(Result.Success(true))
                    } else {
                        task.exception?.let {
                            Logger.w(
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
        Logger.i("updateUser - user: $user")
        Logger.i("updateUser - userRef: $userRef")

        userRef
            .update("id", user.id,
                "photo",user.photo, "name",user.name)
            .addOnSuccessListener {
                Logger.d( "DocumentSnapshot successfully updated!")
                continuation.resume(Result.Success(true))
            }
            .addOnFailureListener {
                Logger.w( "Error updating document: $it")
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
                    Logger.i("checkUserCreated size: ${task.result.size()}")

                    val list = mutableListOf<User>()
                    for (document in task.result!!) {
                        Log.d("Rita",document.id + " => " + document.data)
                        val userGet = document.toObject(User::class.java)
                        list.add(userGet)
                    }

                    val isUserExisted: Boolean?
                    if (list.size > 0) {
                        isUserExisted = true
                    } else {
                        isUserExisted = false
                        Logger.d( "No such document")
                    }
                    Logger.i("checkUserCreated - $isUserExisted")

                    continuation.resume(Result.Success(isUserExisted))
                } else {
                    task.exception?.let {
                        Logger.d( "[${this::class.simpleName}] Error getting documents. ${it.message}")
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
                Logger.i("Today SnapshotListener user email:${user.email} ")
                e?.let {
                    Logger.w(
                        "[${this::class.simpleName}] Error getting documents. ${it.message}"
                    )
                }
                val list = mutableListOf<Plan>()
                if (snapshot != null) {
                    for (item in snapshot) {
                        Logger.i("plan:　${item.data}")
                        val plan = item.toObject(Plan::class.java)
                        list.add(plan)
                    }
                }
                Logger.i("list onChanged:　$list")
                livedata.value = list
            }
        Logger.i("livedata:　${livedata.value}")
        return livedata
    }

    override fun getLiveInvitations(user: User): MutableLiveData<List<Plan>>{
        val livedata = MutableLiveData<List<Plan>>()

        FirebaseFirestore.getInstance()
            .collection(PATH_PLAN)
            .whereArrayContains("invitation", user.email)
            .addSnapshotListener { snapshot, e ->
                e?.let {
                    Logger.w(
                        "[${this::class.simpleName}] Error getting documents. ${it.message}"
                    )
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    val list = mutableListOf<Plan>()
                    for (item in snapshot) {
                        val plan = item.toObject(Plan::class.java)
                        list.add(plan)
                    }
                    Logger.i("Invitation list onChanged:　$list")
                    livedata.value = list
                } else {
                    val nullList = mutableListOf<Plan>()
                    Logger.d( "Current data: null: $nullList")
                    livedata.value = nullList
                }
            }
        return livedata
    }

    override suspend fun getPlansByInvitation(item: Invitation):
            Result<MutableList<Plan>> = suspendCoroutine { continuation ->

        FirebaseFirestore.getInstance()
            .collection(PATH_PLAN)
            .whereArrayContains("collaborator", item.inviter)
            .whereEqualTo("category", item.title)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Logger.i("fb - getPlansByInvitation result.size"+ task.result.size())
                    val list = mutableListOf<Plan>()
                    for (document in task.result!!) {
                        Logger.i("fb - getPlansByInvitation doc"+ document.id + " =>"  + document.data)

                        val plan = document.toObject(Plan::class.java)
                        list.add(plan)
                    }
                    continuation.resume(Result.Success(list))
                } else {
                    task.exception?.let {
                       Logger.w(
                            "[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(CalendarProApplication.instance.getString(R.string.error)))
                }
            }
    }

}