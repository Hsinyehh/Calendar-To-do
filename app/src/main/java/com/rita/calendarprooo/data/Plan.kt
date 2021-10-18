package com.rita.calendarprooo.data


data class Plan(
    var id : Long,
    var title : String?,
    var description : String?,
    var location : String?,
    var start_time : Long?,
    var end_time : Long?,
    var alert_time : Long?,
    var category : String?,
    var checkList : List<Check>?,
    var isToDoList:Boolean?,
    var isToDoListDone:Boolean=false,
    var owner:String?,
    var collaborator:List<String>?
    )
