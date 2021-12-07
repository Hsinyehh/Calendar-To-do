package com.rita.calendarprooo.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Invitation(
    var title: String = "",
    var inviter: String = "",
    var collaborator: MutableList<String> = mutableListOf<String>(),
) : Parcelable
