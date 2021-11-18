package com.rita.calendarprooo.edit

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.rita.calendarprooo.data.Category
import com.rita.calendarprooo.data.Check
import com.rita.calendarprooo.data.Plan
import com.rita.calendarprooo.data.User
import com.rita.calendarprooo.data.source.CalendarRepository
import com.rita.calendarprooo.login.UserManager
import java.text.SimpleDateFormat
import java.util.*


class EditViewModel(plan: Plan, repository: CalendarRepository) : ViewModel() {

    val loadingStatus = MutableLiveData<Boolean?>()

    val currentUser = UserManager.user.value

    var planGet = MutableLiveData<Plan?>()

    var categoryStatus = MutableLiveData<Category?>()

    var categoryPosition = MutableLiveData<Int?>()

    var categoryList = MutableLiveData<MutableList<Category>?>()

    var checkText = MutableLiveData<String?>()

    var title = MutableLiveData<String?>()

    var description = MutableLiveData<String?>()

    var location = MutableLiveData<String?>()

    var newPlan = MutableLiveData<Plan?>()

    var isTodoList = MutableLiveData<Boolean?>()

    var checkList = MutableLiveData<MutableList<Check>?>()

    var start_time = MutableLiveData<Long>()

    var end_time = MutableLiveData<Long>()

    var start_time_detail = MutableLiveData<List<Int>>()

    var end_time_detail = MutableLiveData<List<Int>>()

    var createStatus = MutableLiveData<Boolean?>()

    var editStatus = MutableLiveData<Boolean?>()

    var collaborator = MutableLiveData<MutableList<String>>()

    private val db = Firebase.firestore
    val newPlanRef = db.collection("plan").document()

    val emptyCheckList = mutableListOf<Check>()

    fun toToListModeChanged() {
        Log.i("Rita", "editVM isTodoList ${isTodoList.value}")
        isTodoList.value = isTodoList.value == false
        Log.i("Rita", "editVM isTodoList changed ${isTodoList.value}")
    }

    fun checkListTextCreated() {
        val editCheckList = checkList.value
        Log.i("Rita", "checkListTextCreated()")
        if (planGet.value?.id.isNullOrEmpty()) {
            val newCheck = Check(
                checkText.value, false, 0, "", "", 1,
                newPlanRef.id
            )
            editCheckList?.add(newCheck)
        } else {
            val newCheck = Check(
                checkText.value, false, 0, "", "", 1,
                planGet.value!!.id
            )
            editCheckList?.add(newCheck)
        }
        checkList.value = editCheckList
    }

    fun checkListTextRemoved(position: Int) {
        val listGet = checkList.value
        listGet?.removeAt(position)
        Log.i("Rita", "Edit List removed: $listGet")
        checkList.value = listGet
    }

    fun clearText() {
        checkText.value = ""
    }

    fun createNewPlan() {
        val plan = Plan(
            id = newPlanRef.id,
            title = title.value,
            description = description.value,
            location = location.value,
            start_time = start_time.value,
            end_time = end_time.value,
            start_time_detail = start_time_detail.value,
            end_time_detail = end_time_detail.value,
            category = categoryStatus.value?.name,
            categoryPosition = categoryPosition.value,
            categoryList = categoryList.value,
            checkList = checkList.value,
            isToDoList = isTodoList.value,
            isToDoListDone = false,
            owner = currentUser!!.email,
            owner_name = currentUser!!.name,
            invitation = mutableListOf<String>(),
            collaborator = collaborator.value,
            order_id = 1
        )
        Log.i("Rita", "new plan: $plan")
        newPlan.value = plan
    }

    fun writeNewPlan() {
        // Add a new document with a generated ID
        newPlanRef
            .set(newPlan.value!!)
            .addOnSuccessListener { documentReference ->
                loadingStatus.value = false
                Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: $newPlanRef.id")
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
            }
    }

    fun updatePlan() {
        val planRef = planGet.let { db.collection("plan").document(planGet.value!!.id!!) }
        Log.i("Rita", "updatePlan-planRef: $planRef")
        planRef!!
            .update(
                "title", title.value,
                "description", description.value,
                "location", location.value,
                "start_time", start_time.value,
                "end_time", end_time.value,
                "start_time_detail", start_time_detail.value,
                "end_time_detail", end_time_detail.value,
                "category", categoryStatus.value?.name,
                "categoryPosition", categoryPosition.value,
                "categoryList", categoryList.value,
                "checkList", checkList.value,
                "toDoList", isTodoList.value
            )
            .addOnSuccessListener {
                loadingStatus.value = false
                Log.d(ContentValues.TAG, "successfully updated!")
            }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error updating document", e) }

    }

    fun getCategoryFromUser() {
        Log.i("Rita", "EditVM getCategoryFromUser")
        db.collection("user")
            .whereEqualTo("email", currentUser!!.email)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(ContentValues.TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    for (item in snapshot) {
                        Log.d("Rita", "Current user: $item")
                        val user = item.toObject(User::class.java)
                        categoryList.value = user.categoryList
                        Log.i("Rita", "category:　${categoryList.value}")
                    }
                } else {
                    Log.d(ContentValues.TAG, "Current user: null")
                }
            }
    }

    fun getCategoryFromPlan() {
        Log.i("Rita", "EditVM getCategoryFromPlan")
        db.collection("plan")
            .whereEqualTo("id", "${planGet.value?.id}")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(ContentValues.TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    for (item in snapshot) {
                        Log.d("Rita", "Current plan: $item")
                        val plan = item.toObject(Plan::class.java)
                        categoryList.value = plan.categoryList
                        Log.i("Rita", "category:　${categoryList.value}")
                    }
                } else {
                    Log.d(ContentValues.TAG, "Current user: null")
                }
            }
    }

    fun changeCategory(position: Int, lastPosition: Int) {
        Log.i("Rita", "$lastPosition")
        var categoryListGet = categoryList.value

        //deselected the origin position value
        if (categoryPosition.value != -1) {
            categoryListGet!![categoryPosition.value!!].isSelected = false
        }

        if (lastPosition != -1) {
            categoryListGet!![lastPosition].isSelected = false
        }
        categoryListGet!![position].isSelected = true
        val item = categoryListGet!![position]
        categoryStatus.value = item

        categoryPosition.value = position
        categoryList.value = categoryListGet
    }

    fun convertToStartTimeStamp(dateSelected: String) {
        try {
            val dateSelectedFormat = SimpleDateFormat("dd-MM-yyyy HH:mm").parse(dateSelected)
            Log.i("Rita", "${dateSelectedFormat.time} ")
            start_time.value = dateSelectedFormat.time
        } catch (e: java.text.ParseException) {
            Log.i("Rita", "$e")
        }
    }

    fun convertToEndTimeStamp(dateSelected: String) {
        try {
            val dateSelectedFormat = SimpleDateFormat("dd-MM-yyyy HH:mm").parse(dateSelected)
            Log.i("Rita", "${dateSelectedFormat.time} ")
            end_time.value = dateSelectedFormat.time
            createStatus.value = true
        } catch (e: java.text.ParseException) {
            Log.i("Rita", "$e")
        }
    }

    fun doneConverted() {
        createStatus.value = null
    }


    fun doneNavigated() {
        newPlan.value = null
        planGet.value = null
        editStatus.value = null
    }

    init {
        title.value = plan.title ?: ""
        description.value = plan.description ?: ""
        //location.value = planGet.value.location ?: ""
        start_time.value = plan.start_time ?: null
        end_time.value = plan.end_time ?: null
        start_time_detail.value = plan.start_time_detail ?: null
        end_time_detail.value = plan.end_time_detail ?: null
        newPlan.value = null
        isTodoList.value = plan.isToDoList ?: false
        checkText.value = null
        checkList.value = plan.checkList ?: emptyCheckList
        if (plan?.category == "") {
            categoryStatus.value = Category("", false)
        } else {
            categoryStatus.value = Category(plan.category!!, true)
        }
        categoryPosition.value = plan.categoryPosition
        collaborator.value = mutableListOf(currentUser!!.email)

    }

}