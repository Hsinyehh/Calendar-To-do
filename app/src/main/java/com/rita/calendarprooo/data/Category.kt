package com.rita.calendarprooo.data

data class Category(
    val name: String = "",
    var isSelected: Boolean? = false,
    var collaborator: MutableList<String> = mutableListOf<String>()
)
