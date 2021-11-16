package com.rita.calendarprooo.addcategory

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.rita.calendarprooo.data.Category
import com.rita.calendarprooo.data.Plan
import com.rita.calendarprooo.data.User
import com.rita.calendarprooo.login.UserManager

class AddCategoryViewModel : ViewModel() {

    val currentUser = UserManager.user.value

    var categoryList = MutableLiveData<MutableList<Category>>()

    var unselectedCategoryList = MutableLiveData<MutableList<Category>>()

    var categoryListFromUser = MutableLiveData<MutableList<String>>()

    var categoryAdded = MutableLiveData<String>()

    var planGet = MutableLiveData<Plan>()

    var startToCreate = MutableLiveData<Boolean>()

    var startToPrepare = MutableLiveData<Boolean>()

    var startToUpdate = MutableLiveData<Boolean>()

    var startToNavigate = MutableLiveData<Boolean>()


    fun onclickToCreate() {
        startToCreate.value = true
    }

    //Firebase
    private val db = Firebase.firestore

    fun prepareForCategory() {
        if (categoryAdded.value == "") {
            startToUpdate.value = false
            Log.i("Rita", "Can't update the category because it's null")
        } else {
            Log.i("Rita", "prepareForCategory categoryList ${categoryList.value}")
            val list = categoryList.value
            val newCategory = Category("${categoryAdded.value}", false)
            list?.add(newCategory)
            categoryList.value = list

            Log.i("Rita", "prepareForCategory categoryList changed ${categoryList.value}")
            //start to update
            startToUpdate.value = true
        }
    }

    //if the plan is at edited Status
    fun getCategoryFromThePlan() {
        db.collection("plan")
            .whereEqualTo("id", "${planGet.value?.id}")
            .get()
            .addOnSuccessListener { result ->
                for (item in result) {
                    Log.d("Rita", "Current plan: $item")
                    val plan = item.toObject(Plan::class.java)
                    categoryList.value = plan.categoryList
                    Log.i("Rita", "getCategoryFromThePlan - category:　${categoryList.value}")
                    startToPrepare.value = true
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }
    }


    //if the plan is at created Status
    fun getCategoryFromUser(isCreated: Boolean) {
        db.collection("user")
            .whereEqualTo("email", currentUser!!.email)
            .get()
            .addOnSuccessListener { result ->
                for (item in result) {
                    Log.d("Rita", "Current user: $item")
                    val user = item.toObject(User::class.java)
                    categoryListFromUser.value = user.categoryList?.let { convertToStringList(it) }
                    if (isCreated) {
                        categoryList.value = user.categoryList
                        Log.i("Rita", "getCategoryFromUser - category:　${categoryList.value}")
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }
    }

    //Both Conditions Needs the function below
    fun updateUser() {
        val userRef = db.collection("user").document(currentUser!!.id)
        Log.i("Rita", "updateUser-Ref: $userRef")
        userRef!!
            .update(
                "categoryList", unselectedCategoryList.value
            )
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "User successfully updated!")
                startToNavigate.value = true
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error updating document", e)
            }
    }

    //only for edit status
    fun updateThePlan() {
        val planRef = db.collection("plan").document("${planGet.value?.id}")
        Log.i("Rita", "updatePlan-Ref: $planRef")
        planRef!!
            .update(
                "categoryList", categoryList.value
            )
            .addOnSuccessListener { Log.d(ContentValues.TAG, "Plan successfully updated!") }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error updating document", e)
            }
    }

    fun convertToStringList(list: List<Category>): MutableList<String> {
        var stringList = mutableListOf<String>()
        for (item in list) {
            stringList.add(item.name)
        }
        return stringList
    }

    fun convertToUnselectedList(list: List<Category>) {
        var newList = mutableListOf<Category>()
        for (item in list) {
            item.isSelected = false
            newList.add(item)
        }
        unselectedCategoryList.value = newList
    }

    fun getPlanFromUserFirst() {
        if (planGet.value?.id == "") {
            getCategoryFromUser(true)
        } else {
            getCategoryFromUser(false)
        }
    }


    init {
        startToCreate.value = null
        startToPrepare.value = null
        startToUpdate.value = null
        startToNavigate.value = null
    }


}