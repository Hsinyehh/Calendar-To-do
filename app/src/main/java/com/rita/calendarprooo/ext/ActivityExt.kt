package com.rita.calendarprooo.ext

import android.app.Activity
import android.view.Gravity
import android.widget.Toast
import com.rita.calendarprooo.CalendarProApplication
import com.rita.calendarprooo.factory.RepoViewModelFactory

fun Activity.getVmFactory(): RepoViewModelFactory {
    val repository = (applicationContext as CalendarProApplication).repository
    return RepoViewModelFactory(repository)
}

fun Activity?.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).apply {
        setGravity(Gravity.CENTER, 0, 0)
        show()
    }
}
