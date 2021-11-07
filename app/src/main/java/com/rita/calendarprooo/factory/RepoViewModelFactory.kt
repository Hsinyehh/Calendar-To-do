package com.rita.calendarprooo.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rita.calendarprooo.MainViewModel
import com.rita.calendarprooo.data.source.CalendarRepository
import com.rita.calendarprooo.home.HomeViewModel
import com.rita.calendarprooo.login.LoginViewModel

class RepoViewModelFactory constructor(
    private val repository: CalendarRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {

                isAssignableFrom(LoginViewModel::class.java) ->
                    LoginViewModel(repository)

                isAssignableFrom(MainViewModel::class.java) ->
                    MainViewModel(repository)

                isAssignableFrom(HomeViewModel::class.java) ->
                    HomeViewModel(repository)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}