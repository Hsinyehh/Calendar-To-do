package com.rita.calendarprooo.edit

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rita.calendarprooo.data.Category
import com.rita.calendarprooo.data.Check


class EditViewModel : ViewModel() {

    var categoryStatus= MutableLiveData<Category>()

    var checkText= MutableLiveData<String>()


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

    var checkList = MutableLiveData<MutableList<Check>>()

    val fakeCheckList=mutableListOf<Check>(check,check2)

    init {
        checkText.value=null
        checkList.value=fakeCheckList
    }

    fun checkListTextCreated(){
        Log.i("Rita","checkListTextCreated()")
        val newCheck=Check(checkText.value,false,0,"","",3)
        fakeCheckList.add(newCheck)
        checkList.value = fakeCheckList

    }

    fun clearText(){
        checkText.value = ""
    }


}