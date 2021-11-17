package com.rita.calendarprooo.invite

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.rita.calendarprooo.data.Invitation
import com.rita.calendarprooo.data.User
import com.rita.calendarprooo.data.source.CalendarRepository


class InviteCategoryViewModel(val repository: CalendarRepository) : ViewModel() {

    var category = MutableLiveData<String>()

    var categoryPosition = MutableLiveData<Int>()

    var user = MutableLiveData<User>()

    var userTobeInvited = MutableLiveData<User>()

    var email = MutableLiveData<String>()

    var invitation = MutableLiveData<Invitation>()

    var invitationList = MutableLiveData<MutableList<Invitation>>()

    var isInvited = MutableLiveData<Boolean>()

    //var updatePlan = MutableLiveData<Boolean>()

    var updateSuccess = MutableLiveData<Boolean>()

    //Firebase
    private val db = Firebase.firestore


    fun createInvitation() {
        var list = userTobeInvited.value?.invitationList
        invitation.value = Invitation(
            title = category.value,
            inviter = user.value?.email,
            collaborator = user.value?.categoryList?.get(categoryPosition.value!!)!!.collaborator
        )

        if (list != null) {
            if (list!!.contains(invitation.value!!)) {
                isInvited.value = true
            } else {
                list!!.add(invitation.value!!)
            }
        }

        invitationList.value = list

    }


    fun getTheUser() {
        email.value?.let {
            db.collection("user")
                .whereEqualTo("email", it)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                        val user = document.toObject(User::class.java)
                        userTobeInvited.value = user
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error getting documents.", exception)
                }
        }
    }

    fun updateInvitation(list: MutableList<Invitation>) {
        val userRef = userTobeInvited.value?.let {
            db.collection("user").document(it.email!!)
        }
        Log.i("Rita", "updateInvitation-userRef: $userRef")
        userRef!!
            .update(
                "invitationList", list
            )
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "successfully updated!")
                updateSuccess.value = true
            }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error updating document", e) }

    }


    fun doneWritten() {
        invitation.value = null
        isInvited.value = null
        updateSuccess.value = null
    }

    init {
        invitation.value = null
        invitationList.value = null
        updateSuccess.value = null
    }

}