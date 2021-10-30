package com.rita.calendarprooo.data

import android.os.Parcel
import android.os.Parcelable

@Parcelize
data class Check(
    var title: String? = "",
    var isDone: Boolean = false,
    var done_time: Long? = null,
    var owner: String? = "",
    var doner: String? = "",
    var id: Long = 0L,
    var plan_id: String? = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readString(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeByte(if (isDone) 1 else 0)
        parcel.writeValue(done_time)
        parcel.writeString(owner)
        parcel.writeString(doner)
        parcel.writeLong(id)
        parcel.writeString(plan_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Check> {
        override fun createFromParcel(parcel: Parcel): Check {
            return Check(parcel)
        }

        override fun newArray(size: Int): Array<Check?> {
            return arrayOfNulls(size)
        }
    }
}
