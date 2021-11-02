package com.rita.calendarprooo.invitation

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.rita.calendarprooo.data.Plan

class InvitationViewModel : ViewModel() {

    //Firebase
    private val db = Firebase.firestore

    fun readInvitation(){
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
                    //listFromToday.value = list
                } else {
                    Log.d(ContentValues.TAG, "Current data: null")
                }
            }
    }

    init {
        readInvitation()
    }

}