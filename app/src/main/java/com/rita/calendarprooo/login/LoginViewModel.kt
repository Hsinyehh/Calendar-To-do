package com.rita.calendarprooo.login

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

    val isUserCreated = MutableLiveData<Boolean>()

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


    private fun createCollaborator(email: String): MutableList<Category> {
        val collaborator = mutableListOf<String>(email)
        return mutableListOf(
            Category("Job", false, collaborator),
            Category("Travel", false, collaborator),
            Category("Family", false, collaborator)
        )
    }

    fun createUser(token: String, email: String, name: String, photo: Uri) {
        val user = User(
            id = token,
            categoryList = createCollaborator(email),
            email = email, name = name, photo = photo.toString()
        )
        Log.i("Rita", "new user: $user")
        newUser.value = user
        UserManager.user.value = user
    }

    fun addUser(user: User) {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            when (val result = repository.createUser(user)) {
                is Result.Success -> {
                    getUserData(user.id)
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
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

    fun updateUser(user: User) {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            when (val result = repository.updateUser(user)) {
                is Result.Success -> {
                    getUserData(user.id)
                    Log.i("Rita","${UserManager.user.value}")
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
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

    fun checkUserCreated(user: User) {
        Log.i("Rita","checkUserCreated")

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = repository.checkUserCreated(user)
            Log.i("Rita","checkUserCreated result: $result")

            isUserCreated.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    Log.i("Rita","checkUserCreated result.data: ${result.data}")
                    result.data
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                    null
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                    null
                }
                else -> {
                    _error.value = CalendarProApplication.instance.getString(R.string.error)
                    _status.value = LoadApiStatus.ERROR
                    null
                }
            }
            isUserCreated.value = isUserCreated.value
            Log.i("Rita","checkUserCreated - ${isUserCreated.value}")
            _refreshStatus.value = false
        }
    }


    fun getUserData(id: String) {
        UserManager.user = repository.getUser(id)
    }

    fun startToNavigateToHome() {
        navigateToHome.value = true
    }

    fun doneNavigated() {
        navigateToHome.value = null
    }

    init {
        newUser.value = null
        navigateToHome.value = null
    }

}
