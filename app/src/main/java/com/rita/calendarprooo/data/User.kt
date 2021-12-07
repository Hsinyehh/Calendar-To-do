package com.rita.calendarprooo.data


import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class User(
    val id: String = "",
    val email: String = "",
    val name: String? = "",
    val photo: String? = "",
    var categoryList: MutableList<Category> = mutableListOf<Category>(),
    var invitationList: MutableList<Invitation> = mutableListOf<Invitation>()
) : Parcelable