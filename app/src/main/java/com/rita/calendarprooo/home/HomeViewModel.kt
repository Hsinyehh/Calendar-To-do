package com.rita.calendarprooo.home

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.rita.calendarprooo.data.Check
import com.rita.calendarprooo.data.Plan
import java.util.*

class HomeViewModel : ViewModel() {

    private var _navigateToEdit = MutableLiveData<Boolean>()
    val navigateToEdit : LiveData<Boolean>
        get() = _navigateToEdit

    private var _planList = MutableLiveData<MutableList<Plan>>()
    val planList : LiveData<MutableList<Plan>>
        get() = _planList

    private var _todoList = MutableLiveData<MutableList<Plan>>()
    val todoList : LiveData<MutableList<Plan>>
        get() = _todoList

    private var _doneList = MutableLiveData<MutableList<Plan>>()
    val doneList : LiveData<MutableList<Plan>>
        get() = _doneList

    var checkList = MutableLiveData<MutableList<Check>>()

    //Test for fire base
    val db = Firebase.firestore


    //fake data
    val check= Check(
        title="Meeting Presentation",
        isDone = false,
        done_time=null,
        owner=null,
        doner=null,
        id=1)

    private val check_List= mutableListOf<Check>(check,check)

    val plan= Plan(
        id="1",
        title="Meeting",
        description="for product development",
        location="Taipei",null,null,null,null,check_List,
        false,false,null, emptyList(),1)

    val plan2= Plan(
        id="2",
        title="Jogging",
        description="for Health",
        location="Taipei",null,null,null,null,check_List,
        false,false,null, emptyList(),2)

    val plan3= Plan(
        id="3",
        title="Reading",
        description="for Leisure",
        location="Taipei",null,null,null,null,check_List,
        false,false,null, emptyList(),3)

    private val plan_list= mutableListOf<Plan>(plan,plan)
    private val todo_list= mutableListOf<Plan>(plan2,plan3)


    fun swapCheckListItem(start:Int , end:Int){
        val todoListGet=_todoList.value
        Collections.swap(todoListGet,start,end)
        _todoList.value=todoListGet
    }

    fun checkListTextRemoved(position:Int){
        val listGet =  checkList.value
        listGet?.removeAt(position)
        Log.i("Rita","Home List removed: $listGet")
        checkList.value = listGet
    }

    fun startNavigateToEdit(){
        _navigateToEdit.value=true
    }

    fun doneNavigated(){
        _navigateToEdit.value=null
    }




    fun readPlan(){
        val list= mutableListOf<Plan>()

        db.collection("plan")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    val plan= document.toObject(Plan::class.java)
                    list.add(plan)
                }
                Log.i("Rita","$list")
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }


    init {
        _navigateToEdit.value = null
        _planList.value=plan_list
        _todoList.value=todo_list
        _doneList.value=todo_list
        checkList.value=check_List

        readPlan()
    }


}