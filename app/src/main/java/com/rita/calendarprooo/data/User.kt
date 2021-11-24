package com.rita.calendarprooo.data


import android.os.Parcel
import android.os.Parcelable

@Parcelize
data class User (
    val id : String = "",
    val email : String = "",
    val name : String? = "",
    val photo : String? = "",
    val categoryList : MutableList<Category> = mutableListOf<Category>(),
    val invitationList : MutableList<Invitation> = mutableListOf<Invitation>()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString(),
        TODO("categoryList"),
        TODO("invitationList")
    )

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(p0: Parcel?, p1: Int) {
        TODO("Not yet implemented")
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}