package com.rita.calendarprooo.ext

import androidx.fragment.app.Fragment
import com.rita.calendarprooo.CalendarProApplication
import com.rita.calendarprooo.data.Plan
import com.rita.calendarprooo.factory.RepoViewModelFactory
import com.rita.calendarprooo.factory.ViewModelFactory

fun Fragment.getVmFactory(plan: Plan?): ViewModelFactory {
    val repository = (requireContext().applicationContext as CalendarProApplication).repository
    return ViewModelFactory(plan, repository)
}

fun Fragment.getVmFactory(): RepoViewModelFactory {
    val repository = (requireContext().applicationContext as CalendarProApplication).repository
    return RepoViewModelFactory(repository)
}