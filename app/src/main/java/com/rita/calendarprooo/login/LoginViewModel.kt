package com.rita.calendarprooo.login

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.rita.calendarprooo.CalendarProApplication
import com.rita.calendarprooo.R
import com.rita.calendarprooo.data.Category
import com.rita.calendarprooo.data.User
import com.rita.calendarprooo.data.Result
import com.rita.calendarprooo.data.source.CalendarRepository
import com.rita.calendarprooo.network.LoadApiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LoginViewModel(repository: CalendarRepository) : ViewModel() {

    val repository = repository

    val newUser = MutableLiveData<User>()

    val navigateToHome = MutableLiveData<Boolean>()

    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String>()

    val error: LiveData<String>
        get() = _error

    // status for the loading icon of swl
    private val _refreshStatus = MutableLiveData<Boolean>()

    val refreshStatus: LiveData<Boolean>
        get() = _refreshStatus

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    val categoryList = mutableListOf<Category>(
        Category("Job", false),
        Category("Travel", false), Category("Family", false)
    )

    fun createUser(token: String, email: String, name: String, photo: Uri) {
        val user = User(
            id = token,
            categoryList = categoryList,
            email = email, name = name, photo = photo.toString()
        )
        Log.i("Rita", "new plan: $user")
        newUser.value = user
    }

    fun addUser(user: User) {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            when (val result = repository.createUser(user)) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    getUserData(user.id)
                    //startToNavigateToHome()
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                }
                else -> {
                    _error.value =
                        CalendarProApplication.instance.getString(R.string.Error)
                    _status.value = LoadApiStatus.ERROR
                }
            }
        }
    }

    fun getUserData(id: String) {
        UserManager.user = repository.getUser(id)
    }

    fun  startToNavigateToHome(){
        navigateToHome.value = true
    }

    fun  doneNavigated(){
        navigateToHome.value = null
    }

    init {
        newUser.value = null
        navigateToHome.value = null
    }

}
