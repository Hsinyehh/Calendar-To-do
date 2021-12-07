package com.rita.calendarprooo.data


import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Check(
    var title: String? = "",
    var isDone: Boolean = false,
    var done_time: Long? = null,
    var owner: String? = "",
    var doner: String? = "",
    var id: Long = 0L,
    var plan_id: String? = ""
) : Parcelable