package com.rita.calendarprooo.invitation

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.rita.calendarprooo.data.Category
import com.rita.calendarprooo.data.Invitation
import com.rita.calendarprooo.data.Plan
import com.rita.calendarprooo.data.User
import com.rita.calendarprooo.data.source.CalendarRepository
import com.rita.calendarprooo.login.UserManager

class InvitationViewModel(val repository: CalendarRepository) : ViewModel() {

    val loadingStatus = MutableLiveData<Boolean?>()

    var user = MutableLiveData<User>()

    var invitationList = MutableLiveData<MutableList<Plan>>()

    var invitationForCategoryList = MutableLiveData<MutableList<Invitation>>()

    var invitationListSize: LiveData<Int> = Transformations.map(invitationList) {
        var size = 0
        if (!it.isNullOrEmpty()) {
            size = it.size
        }
        size
    }

    var invitationForCategoryListSize: LiveData<Int> =
        Transformations.map(invitationForCategoryList) {
            var size = 0
            if (!it.isNullOrEmpty()) {
                size = it.size
            }
            size
        }

    // Category
    var isAccepted = MutableLiveData<Boolean>()

    var invitationAccepted = MutableLiveData<Invitation>()

    var invitationListUpdated = MutableLiveData<MutableList<Invitation>>()

    var plans = MutableLiveData<MutableList<Plan>>()

    var addCollaboratorForPlan = MutableLiveData<Boolean>()

    var updatePlan = MutableLiveData<Boolean>()

    var startToUpdate = MutableLiveData<Boolean>()

    val categoryListTobeUpdated = MutableLiveData<MutableList<Category>>()

    val updateCategories = MutableLiveData<Boolean>()

    val updateCategoriesForUser = MutableLiveData<Boolean>()

    var updateSuccess = MutableLiveData<Boolean>()


    // Firebase
    private val db = Firebase.firestore

    fun readInvitation() {
        db.collection("plan")
            .whereArrayContains("invitation", user.value!!.email)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(ContentValues.TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    val list = mutableListOf<Plan>()
                    for (item in snapshot) {
                        val plan = item.toObject(Plan::class.java)
                        list.add(plan)
                    }
                    Log.i("Rita", "Invitation list onChanged:　$list")
                    invitationList.value = list
                } else {
                    val nullList = mutableListOf<Plan>()
                    Log.d(ContentValues.TAG, "Current data: null: $nullList")
                    invitationList.value = nullList
                }
            }
    }

    fun acceptOrDeclineInvitation(plan: Plan, isAccepted: Boolean) {

        loadingStatus.value = true
        // update plan collaborator & invitation
        val invitationGet = plan.invitation
        val collaboratorGet = plan.collaborator

        val indexRemoved = invitationGet?.indexOf(user.value!!.email)
        if (indexRemoved != null && indexRemoved >= 0) {
            invitationGet.removeAt(indexRemoved)
        }
        if (isAccepted) {
            collaboratorGet?.add(user.value!!.email)
        }

        val planRef = db.collection("plan").document(plan.id!!)
        Log.i("Rita", "updatePlan-planRef: $planRef")

        planRef
            .update(
                "invitation", invitationGet,
                "collaborator", collaboratorGet,
            )
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "successfully updated!")
                loadingStatus.value = false
            }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error updating document", e) }
    }


    // update category

    fun updateInvitation(item: Invitation, accepted: Boolean) {
        loadingStatus.value = true
        isAccepted.value = accepted

        invitationAccepted.value = item
        val title = item.title
        val inviter = item.inviter
        val invitationList = user.value!!.invitationList
        val indexRemoved =
            invitationList.indexOfFirst { it.title == title && it.inviter == inviter }

        if (indexRemoved >= 0) {
            invitationList.removeAt(indexRemoved)
        }

        invitationListUpdated.value = invitationList
    }


    fun updateInvitationList(list: MutableList<Invitation>) {
        val userRef =
            db.collection("user").document(user.value!!.email)

        userRef
            .update("invitationList", list)
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "successfully updated!")

                if (isAccepted.value == true) {
                    startToUpdate.value = true
                }
            }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error updating document", e) }

    }


    // get All plans with query for the category
    fun getPlans() {
        Log.i("Rita", "readPlans user: ${user.value}")
        val item = invitationAccepted.value

        item?.let {
            db.collection("plan")
                .whereArrayContains("collaborator", it.inviter!!)
                .whereEqualTo("category", it.title)
                .get()
                .addOnSuccessListener { result ->
                    val list = mutableListOf<Plan>()
                    for (document in result) {
                        Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                        val plan = document.toObject(Plan::class.java)
                        list.add(plan)
                    }
                    Log.i("Rita", "list: $list")
                    plans.value = list
                    addCollaboratorForPlan.value = true
                }
                .addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error getting documents.", exception)
                }
        }
    }

    fun addCollaboratorForPlan() {
        val list = plans.value
        if (list != null) {
            for (item in list) {
                if (!item.collaborator!!.contains(user.value?.email)) {
                    user.value?.email?.let { item.collaborator!!.add(it) }
                }
            }
        }
        plans.value = list
        updatePlan.value = true
    }


    // update collaborator for All plans with loop in fragment
    fun updatePlan(plan: Plan) {
        val planRef = plan.let { db.collection("plan").document(plan.id!!) }

        planRef
            .update(
                "collaborator", plan.collaborator,
            )
            .addOnSuccessListener { Log.d(ContentValues.TAG, "successfully updated!") }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error updating document", e) }
    }

    // update user's categoryList
    fun updateCategories() {

        val item = invitationAccepted.value
        val title = item!!.title
        val categoryList = user.value!!.categoryList
        val index = categoryList.indexOfFirst { it.name == title }
        Log.i("Rita", "updateCategories index:　$index")
        if (index == -1) {
            val categoryAdded = Category(
                name = title!!,
                isSelected = false,
                mutableListOf<String>(user.value!!.email)
            )
            categoryList.add(categoryAdded)
            Log.i("Rita", "updateCategories categoryAdded: $categoryAdded")
            Log.i(
                "Rita", "updateCategories categoryListTobeUpdated: " +
                        " ${categoryListTobeUpdated.value}"
            )

            categoryListTobeUpdated.value = categoryList
            updateCategoriesForUser.value = true
        } else {
            updateCategoriesForUser.value = false
            loadingStatus.value = false
        }


    }

    fun updateCategoriesForUser() {
        val userRef = user.value?.let { db.collection("user").document(it.email) }
        Log.i("Rita", "updateUserCategories - userRef: $userRef")
        userRef!!
            .update("categoryList", categoryListTobeUpdated.value)
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "successfully updated!")
                loadingStatus.value = false
            }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error updating document", e) }
    }


    fun doneWritten() {
        invitationAccepted.value = null
        addCollaboratorForPlan.value = null
        updatePlan.value = null
        startToUpdate.value = null
        updateSuccess.value = null
        categoryListTobeUpdated.value = null
        updateCategories.value = null
        updateCategoriesForUser.value = null
    }


    private fun getUserData(userId: String) {
        Log.d("Rita", "userId: $userId")
        user = repository.getUser(userId)
        UserManager.user = repository.getUser(userId)
    }


    init {

        invitationList.value = null
        addCollaboratorForPlan.value = null
        updatePlan.value = null
        updateSuccess.value = null
        categoryListTobeUpdated.value = null
        updateCategories.value = null
        updateCategoriesForUser.value = null

        UserManager.userToken?.let { getUserData(it) }
    }

}