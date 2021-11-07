package com.rita.calendarprooo.invitation

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.rita.calendarprooo.data.Plan
import com.rita.calendarprooo.login.UserManager

class InvitationViewModel : ViewModel() {

    val currentUser = UserManager.user.value

    var invitationList = MutableLiveData<MutableList<Plan>>()

    var invitationListSize : LiveData<Int> =Transformations.map(invitationList){
        it.size
    }


    //Firebase
    private val db = Firebase.firestore

   fun readInvitation(){
       var list = mutableListOf<Plan>()

        db.collection("plan")
            .whereArrayContains("invitation",currentUser!!.email)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(ContentValues.TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    for (item in snapshot) {
                        val plan= item.toObject(Plan::class.java)
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

    fun acceptOrDeclineInvitation(plan: Plan, isAccepted: Boolean){
        //update plan collaborator & invitation
        val invitationGet = plan.invitation
        val collaboratorGet = plan.collaborator

        val indexRemoved = invitationGet?.indexOf(currentUser!!.email)
        if (indexRemoved != null && indexRemoved >= 0 ) {
            invitationGet?.removeAt(indexRemoved)
        }
        if(isAccepted){
            collaboratorGet?.add(currentUser!!.email)
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
        //readInvitation()
    }

}