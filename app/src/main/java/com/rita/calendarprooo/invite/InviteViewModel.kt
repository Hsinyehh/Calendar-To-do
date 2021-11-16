package com.rita.calendarprooo.invite

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.rita.calendarprooo.data.Plan


class InviteViewModel : ViewModel() {

    var plan = MutableLiveData<Plan>()

    var email = MutableLiveData<String>()

    var invitation = MutableLiveData<MutableList<String>>()

    var isInvited = MutableLiveData<Boolean>()

    var isCollaborator = MutableLiveData<Boolean>()

    var updateSuccess = MutableLiveData<Boolean>()

    //Firebase
    private val db = Firebase.firestore

    fun createInvitation() {
        val invitationGet = plan.value?.invitation
        val collaboratorGet = plan.value?.collaborator

        val isInvitedCheck: Boolean? = invitationGet?.contains(email.value) ?: null
        val isCollaboratorCheck: Boolean? = collaboratorGet?.contains(email.value) ?: null

        if (isInvitedCheck == false && isCollaboratorCheck == false) {
            email.value?.let { invitationGet?.add(it) }
            invitation.value = invitationGet
        } else if (isInvitedCheck == true) {
            isInvited.value = isInvitedCheck
        } else if (isCollaboratorCheck == true) {
            isCollaborator.value = isCollaboratorCheck
        }
    }

    fun writeInvitation() {
        val planRef = plan.value?.id?.let { db.collection("plan").document(it) }
        planRef!!
            .update(
                "invitation", invitation.value,
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

}