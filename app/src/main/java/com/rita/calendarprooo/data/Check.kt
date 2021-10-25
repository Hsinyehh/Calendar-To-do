package com.rita.calendarprooo.data

data class Check(
    var title: String?="",
    var isDone: Boolean=false,
    var done_time: Long?=null,
    var owner: String?="",
    var doner: String?="",
    var id: Long=0L
)
