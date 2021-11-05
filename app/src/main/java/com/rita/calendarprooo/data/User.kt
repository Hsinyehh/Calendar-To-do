package com.rita.calendarprooo.data

data class User (
    val id : String = "",
    val email : String = "",
    val name : String? = "",
    val image : String? = "",
    val categoryList : MutableList<Category> = mutableListOf<Category>()
)