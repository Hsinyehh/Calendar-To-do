package com.rita.calendarprooo.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rita.calendarprooo.MainViewModel
import com.rita.calendarprooo.data.source.CalendarRepository
import com.rita.calendarprooo.home.HomeViewModel
import com.rita.calendarprooo.invitation.InvitationViewModel
import com.rita.calendarprooo.invite.InviteCategoryViewModel
import com.rita.calendarprooo.login.LoginViewModel
import com.rita.calendarprooo.result.ResultViewModel
import com.rita.calendarprooo.sort.HomeSortViewModel

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

                isAssignableFrom(HomeSortViewModel::class.java) ->
                    HomeSortViewModel(repository)

                isAssignableFrom(InviteCategoryViewModel::class.java) ->
                    InviteCategoryViewModel(repository)

                isAssignableFrom(InvitationViewModel::class.java) ->
                    InvitationViewModel(repository)

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}