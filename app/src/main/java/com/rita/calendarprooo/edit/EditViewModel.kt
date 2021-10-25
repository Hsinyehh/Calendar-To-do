package com.rita.calendarprooo.edit

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.rita.calendarprooo.data.Category
import com.rita.calendarprooo.data.Check
import com.rita.calendarprooo.data.Plan
import java.text.SimpleDateFormat


class EditViewModel : ViewModel() {

    var categoryStatus= MutableLiveData<Category?>()

    var checkText= MutableLiveData<String?>()

    var title= MutableLiveData<String?>()

    var description= MutableLiveData<String?>()

    var location= MutableLiveData<String?>()

    var newPlan= MutableLiveData<Plan?>()

    var isTodoList= MutableLiveData<Boolean?>()

    var checkList = MutableLiveData<MutableList<Check>>()

    var start_time = MutableLiveData<Long>()

    var end_time = MutableLiveData<Long>()

    private val db = Firebase.firestore
    val newPlanRef = db.collection("plan").document()


    val fakeCheckList=mutableListOf<Check>()


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
            id=newPlanRef.id,
            title=title.value,
            description=description.value,
            location=location.value,
            start_time=start_time.value,
            end_time=end_time.value,15L,
            category=categoryStatus.value?.name, emptyList(),
            isToDoList=isTodoList.value,
            isToDoListDone=false,
            owner="lisa@gmail.com", emptyList(),1)

        Log.i("Rita","new plan: $plan")

        newPlan.value=plan

    }

    fun writeNewPlan(){
        // Add a new document with a generated ID
        newPlanRef
            .set(newPlan.value!!)
            .addOnSuccessListener { documentReference ->
                Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: $newPlanRef.id")
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
            }
    }

    fun convertToStartTimeStamp(dateSelected:String){
        try {
            val dateSelectedFormat = SimpleDateFormat("dd-MM-yyyy HH:mm").parse(dateSelected)
            Log.i("Rita", "${dateSelectedFormat.time} ")
            start_time.value=dateSelectedFormat.time
        }
        catch(e:java.text.ParseException){
            Log.i("Rita","$e")
        }
    }

    fun convertToEndTimeStamp(dateSelected:String){
        try {
            val dateSelectedFormat = SimpleDateFormat("dd-MM-yyyy HH:mm").parse(dateSelected)
            Log.i("Rita", "${dateSelectedFormat.time} ")
            end_time.value=dateSelectedFormat.time
        }
        catch(e:java.text.ParseException){
            Log.i("Rita","$e")
        }
    }


    fun doneNavigated(){
        newPlan.value=null
    }

    init {
        isTodoList.value=false
        checkText.value=null
        checkList.value=fakeCheckList
        categoryStatus.value= Category("",false)
        title.value=""
        description.value=""
        location.value=""
        start_time.value=null
        end_time.value=null
        newPlan.value=null
    }

}