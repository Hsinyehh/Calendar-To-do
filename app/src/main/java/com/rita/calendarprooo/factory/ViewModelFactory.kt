package com.rita.calendarprooo.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rita.calendarprooo.data.Plan
import com.rita.calendarprooo.edit.EditViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory constructor(
    private val plan: Plan?
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {

                isAssignableFrom(EditViewModel::class.java) ->
                    plan?.let { EditViewModel(it) }
                /*isAssignableFrom(HomeViewModel::class.java) ->
                    HomeViewModel(repository)*/

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}