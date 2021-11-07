package com.rita.calendarprooo

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rita.calendarprooo.data.source.CalendarRepository
import com.rita.calendarprooo.login.UserManager

class MainViewModel(repository: CalendarRepository)
    : ViewModel() {

    val currentUser = UserManager.user

    val navigateToHome = MutableLiveData<Boolean>()

    val navigateToLogin = MutableLiveData<Boolean>()

    private val repository = repository

    fun getUserData(id: String) {
        Log.d("Rita", "userId: $id")
        UserManager.user = repository.getUser(id)
        Log.d("Rita", "mainActivity VM getUserData: ${UserManager.user.value}")
    }

    fun doneNavigated(){
        navigateToHome.value = null
        navigateToLogin.value = null
    }
}