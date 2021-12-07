package com.rita.calendarprooo.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Category(
    val name: String = "",
    var isSelected: Boolean? = false,
    var collaborator: MutableList<String> = mutableListOf<String>()
) : Parcelable
