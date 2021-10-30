package com.rita.calendarprooo.ext

import androidx.fragment.app.Fragment
import com.rita.calendarprooo.data.Plan
import com.rita.calendarprooo.factory.ViewModelFactory

fun Fragment.getVmFactory(plan: Plan?): ViewModelFactory {
    return ViewModelFactory(plan)
}