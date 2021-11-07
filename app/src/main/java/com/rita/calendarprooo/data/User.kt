package com.rita.calendarprooo.data

import android.net.Uri

data class User (
    val id : String = "",
    val email : String = "",
    val name : String? = "",
    val photo : String? = "",
    val categoryList : MutableList<Category> = mutableListOf<Category>()
)