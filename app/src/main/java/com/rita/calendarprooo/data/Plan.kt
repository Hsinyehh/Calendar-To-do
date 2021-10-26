package com.rita.calendarprooo.data


data class Plan(
    var id : String? = "",
    var title : String? = "",
    var description : String? = "",
    var location : String? = "",
    var start_time : Long? = null,
    var end_time : Long? = null,
    var alert_time : Long? = null,
    var category : String? = "",
    var checkList : List<Check>? = emptyList(),
    var isToDoList:Boolean? = false,
    var isToDoListDone:Boolean = false,
    var owner:String? = "",
    var collaborator:List<String>? = emptyList(),
    var order_id:Int? = -1
    )