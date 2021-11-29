package com.rita.calendarprooo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rita.calendarprooo.Util.Logger
import com.rita.calendarprooo.data.source.CalendarRepository
import com.rita.calendarprooo.login.UserManager

class MainViewModel(repository: CalendarRepository) : ViewModel() {

    val currentUser = UserManager.user

    val navigateToHome = MutableLiveData<Boolean>()

    val navigateToLogin = MutableLiveData<Boolean>()

    private val repository = repository

    fun getUserData(id: String) {
        Logger.d("userId: $id")
        UserManager.user = repository.getUser(id)
        Logger.d("mainActivity VM getUserData: ${UserManager.user.value}")
    }

    fun doneNavigated() {
        navigateToHome.value = null
        navigateToLogin.value = null
    }
}