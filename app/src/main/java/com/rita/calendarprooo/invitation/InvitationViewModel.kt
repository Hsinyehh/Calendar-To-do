package com.rita.calendarprooo.invitation

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.rita.calendarprooo.data.Plan

class InvitationViewModel : ViewModel() {

    var invitationList = MutableLiveData<MutableList<Plan>>()

    //Firebase
    private val db = Firebase.firestore

    private fun readInvitation(){
        db.collection("plan")
            .whereArrayContains("invitation","lisa@gmail.com")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(ContentValues.TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    val list = mutableListOf<Plan>()
                    for (item in snapshot) {
                        val plan= item.toObject(Plan::class.java)
                        list.add(plan!!)
                    }
                    Log.i("Rita", "Invitation list onChanged:　$list")
                    invitationList.value = list
                } else {
                    Log.d(ContentValues.TAG, "Current data: null")
                }
            }
    }

    fun acceptOrDeclineInvitation(plan: Plan, isAccepted: Boolean){
        //update plan collaborator & invitation
        val invitationGet = plan.invitation
        val collaboratorGet = plan.collaborator

        val indexRemoved = invitationGet?.indexOf("lisa@gmail.com")
        if (indexRemoved != null) {
            invitationGet?.removeAt(indexRemoved)
        }
        if(isAccepted){
            collaboratorGet?.add("lisa@gmail.com")
        }

        val planRef =  db.collection("plan").document(plan.id!!)
        Log.i("Rita", "updatePlan-planRef: $planRef")
        planRef!!
            .update(
                "invitation", invitationGet,
                "collaborator", collaboratorGet,)
            .addOnSuccessListener { Log.d(ContentValues.TAG, "successfully updated!") }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error updating document", e) }
    }



    init {
        readInvitation()
    }

}