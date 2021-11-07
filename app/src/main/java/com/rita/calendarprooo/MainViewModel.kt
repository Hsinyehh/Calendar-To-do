package com.rita.calendarprooo

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rita.calendarprooo.data.source.CalendarRepository
import com.rita.calendarprooo.login.UserManager

class MainViewModel(repository: CalendarRepository)
    : ViewModel() {

    val navigateToHome = MutableLiveData<Boolean>()

    val navigateToLogin = MutableLiveData<Boolean>()

    private val repository = repository

    fun getUserData(email: String) {
        Log.d("Rita", "userId: $email")
        UserManager.user = repository.getUser(email)
        Log.d("Rita", "mainActivity VM getUserData: ${UserManager.user.value}")
    }

    fun doneNavigated(){
        navigateToHome.value = null
        navigateToLogin.value = null
    }
}