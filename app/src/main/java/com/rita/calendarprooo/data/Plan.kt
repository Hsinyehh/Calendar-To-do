package com.rita.calendarprooo.data

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class Plan(
    var id: String? = null,
    var title: String? = "",
    var description: String? = "",
    var location: String? = "",
    var start_time: Long? = null,
    var end_time: Long? = null,
    var start_time_detail: List<Int>? = null,
    var end_time_detail: List<Int>? = null,
    var alert_time: Long? = null,
    var category: String? = "",
    var categoryPosition: Int? = -1,
    var categoryList:MutableList<Category>? = mutableListOf<Category>(),
    var checkList: MutableList<Check>? = mutableListOf<Check>(),
    var isToDoList:Boolean? = false,
    var isToDoListDone:Boolean = false,
    var done_time: Long? = 0L,
    var doner:String? = "",
    var owner:String? = "",
    var owner_name:String? = "",
    var invitation: MutableList<String>? = mutableListOf<String>(),
    var collaborator: MutableList<String>? = mutableListOf<String>(),
    var order_id:Int? = -1
    ) : Parcelable {
    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(p0: Parcel?, p1: Int) {
        TODO("Not yet implemented")
    }
}

annotation class Parcelize
