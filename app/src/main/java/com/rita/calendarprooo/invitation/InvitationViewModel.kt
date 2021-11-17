package com.rita.calendarprooo.invitation

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.rita.calendarprooo.data.Invitation
import com.rita.calendarprooo.data.Plan
import com.rita.calendarprooo.data.User
import com.rita.calendarprooo.data.source.CalendarRepository
import com.rita.calendarprooo.login.UserManager

class InvitationViewModel(val repository: CalendarRepository) : ViewModel() {

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
    var invitationAccepted = MutableLiveData<Invitation>()

    var plans = MutableLiveData<MutableList<Plan>>()

    var addCollaboratorForPlan = MutableLiveData<Boolean>()

    var updatePlan = MutableLiveData<Boolean>()

    var getUsers = MutableLiveData<Boolean>()

    var userList = MutableLiveData<MutableList<User>>()

    var addCollaboratorForUser = MutableLiveData<Boolean>()

    var updateCategoryForUser = MutableLiveData<Boolean>()

    var updateSuccess = MutableLiveData<Boolean>()


    //Firebase
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
                    var list = mutableListOf<Plan>()
                    for (item in snapshot) {
                        val plan = item.toObject(Plan::class.java)
                        list.add(plan!!)
                    }
                    Log.i("Rita", "Invitation list onChanged:　$list")
                    invitationList.value = list
                } else {
                    var nullList = mutableListOf<Plan>()
                    Log.d(ContentValues.TAG, "Current data: null: $nullList")
                    invitationList.value = nullList
                }
            }
    }

    fun acceptOrDeclineInvitation(plan: Plan, isAccepted: Boolean) {
        //update plan collaborator & invitation
        val invitationGet = plan.invitation
        val collaboratorGet = plan.collaborator

        val indexRemoved = invitationGet?.indexOf(user.value!!.email)
        if (indexRemoved != null && indexRemoved >= 0) {
            invitationGet?.removeAt(indexRemoved)
        }
        if (isAccepted) {
            collaboratorGet?.add(user.value!!.email)
        }

        val planRef = db.collection("plan").document(plan.id!!)
        Log.i("Rita", "updatePlan-planRef: $planRef")
        planRef!!
            .update(
                "invitation", invitationGet,
                "collaborator", collaboratorGet,
            )
            .addOnSuccessListener { Log.d(ContentValues.TAG, "successfully updated!") }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error updating document", e) }
    }


    // get All plans with query for the category
    fun getPlans(item: Invitation) {
        Log.i("Rita", "readPlans user: ${user.value}")

        item.let {
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
        Log.i("Rita", "updatePlan-planRef: $planRef")
        planRef!!
            .update(
                "collaborator", plan.collaborator,
            )
            .addOnSuccessListener { Log.d(ContentValues.TAG, "successfully updated!") }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error updating document", e) }
    }


    // update Users' category's collaborator
    fun getUsers() {
        val collaboratorFromCategory = invitationAccepted.value?.collaborator!!
        Log.i("Rita", "collaboratorFromCategory: $collaboratorFromCategory")

        val list = mutableListOf<User>()
        for (user in collaboratorFromCategory) {
            db.collection("user")
                .whereEqualTo("email", user)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val user = document.toObject(User::class.java)
                        if (!list.contains(user)) {
                            list.add(user)
                        }
                    }
                    Log.i("Rita", "list: $list")
                }
                .addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error getting documents.", exception)
                }
        }
        userList.value = list
        addCollaboratorForUser.value = true
    }


    fun addCollaboratorForUser() {
        val list = userList.value
        if (list != null) {
            for (userGet in list) {
                val category = invitationAccepted.value?.title
                val email = user.value?.email
                val index = userGet.categoryList.indexOfFirst { it.name == category }
                val categoryFromUser = userGet.categoryList[index]

                if (!categoryFromUser.collaborator!!.contains(email)) {
                    email?.let { userGet.categoryList[index].collaborator.add(it) }
                }
            }
        }
        Log.i("Rita", "addCollaboratorForUser- $list")
        userList.value = list
    }

    fun updateCategoryForUser(user: User) {
        val userRef = user.let { db.collection("user").document(it.email!!) }
        Log.i("Rita", "updatePlan-planRef: $userRef")
        userRef!!
            .update(
                "category", user.categoryList,
            )
            .addOnSuccessListener { Log.d(ContentValues.TAG, "successfully updated!") }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error updating document", e) }

    }


    fun doneWritten() {

        addCollaboratorForPlan.value = null
        updatePlan.value = null
        getUsers.value = null
        addCollaboratorForUser.value = null
        updateCategoryForUser.value = null
        updateSuccess.value = null
    }


    private fun getUserData(userId: String) {
        Log.d("Rita", "userId: $userId")
        user = repository.getUser(userId)
        UserManager.user = repository.getUser(userId)
    }


    init {
        //readInvitation()
        invitationList.value = null
        addCollaboratorForPlan.value = null
        updatePlan.value = null
        getUsers.value = null
        addCollaboratorForUser.value = null
        updateCategoryForUser.value = null
        updateSuccess.value = null

        UserManager.userToken?.let { getUserData(it) }
    }

}