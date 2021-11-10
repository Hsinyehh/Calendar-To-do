package com.rita.calendarprooo.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rita.calendarprooo.MainViewModel
import com.rita.calendarprooo.data.source.CalendarRepository
import com.rita.calendarprooo.home.HomeViewModel
import com.rita.calendarprooo.login.LoginViewModel
import com.rita.calendarprooo.result.ResultViewModel

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

                isAssignableFrom(ResultViewModel::class.java) ->
                    ResultViewModel(repository)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}