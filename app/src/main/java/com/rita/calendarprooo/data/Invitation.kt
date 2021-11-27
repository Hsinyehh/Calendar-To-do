package com.rita.calendarprooo.data

data class Invitation(
    var title: String = "",
    var inviter: String = "",
    var collaborator: MutableList<String> = mutableListOf<String>(),
    )
