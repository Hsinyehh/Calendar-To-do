package com.rita.calendarprooo.data

data class Check(
    var title: String? = "",
    var isDone: Boolean = false,
    var done_time: Long? = null,
    var owner: String? = "",
    var doner: String? = "",
    var id: Long = 0L,
    var plan_id: String? = ""
) {
    fun toHashMap(data: Check) {
        val data = hashMapOf(
            "title" to data.title,
            "isDone" to data.isDone,
            "done_time" to data.done_time,
            "owner" to data.owner,
            "doner" to data.doner,
            "id" to data.id
        )
    }
}
