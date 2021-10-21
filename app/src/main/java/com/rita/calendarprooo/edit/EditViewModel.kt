package com.rita.calendarprooo.edit

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rita.calendarprooo.data.Category
import com.rita.calendarprooo.data.Check
import com.rita.calendarprooo.data.Plan


class EditViewModel : ViewModel() {

    var categoryStatus= MutableLiveData<Category?>()

    var checkText= MutableLiveData<String?>()

    var title= MutableLiveData<String?>()

    var description= MutableLiveData<String?>()

    var location= MutableLiveData<String?>()

    var newPlan= MutableLiveData<Plan?>()

    var isTodoList= MutableLiveData<Boolean?>()

    var checkList = MutableLiveData<MutableList<Check>>()


    //FAKE DATA
    val check= Check(
        title="Meeting Presentation",
        isDone = false,
        done_time=null,
        owner=null,
        doner=null,
        id=1)

    val check2= Check(
        title="Meeting Discussion",
        isDone = false,
        done_time=null,
        owner=null,
        doner=null,
        id=2)


    val fakeCheckList=mutableListOf<Check>(check,check2)

    init {
        isTodoList.value=false
        checkText.value=null
        checkList.value=fakeCheckList
    }

    fun toToListModeChanged(){
        isTodoList.value = isTodoList.value==false
    }

    fun checkListTextCreated(){
        Log.i("Rita","checkListTextCreated()")
        val newCheck=Check(checkText.value,false,0,"","",3)
        fakeCheckList.add(newCheck)
        checkList.value = fakeCheckList

    }

    fun checkListTextRemoved(position:Int){
        val listGet =  checkList.value
        listGet?.removeAt(position)
        Log.i("Rita","Edit List removed: $listGet")
        checkList.value = listGet
    }

    fun clearText(){
        checkText.value = ""
    }

    fun createNewPlan(){
        val plan = Plan(
            id=1,
            title=title.value,
            description=description.value,
            location=location.value,null,null,null,
            categoryStatus.value?.name, emptyList(),
            isTodoList.value,false,null, emptyList())

        Log.i("Rita","new plan: $plan")

        newPlan.value=plan
    }

    fun doneNavigated(){
        newPlan.value=null
    }


}