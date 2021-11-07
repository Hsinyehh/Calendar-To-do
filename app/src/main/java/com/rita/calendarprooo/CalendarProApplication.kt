package com.rita.calendarprooo

import android.app.Application
import android.content.Context
import com.rita.calendarprooo.Util.ServiceLocator
import com.rita.calendarprooo.data.source.CalendarRepository

class CalendarProApplication : Application() {
    // Depends on the flavor,
    val repository: CalendarRepository
        get() = ServiceLocator.provideRepository(this)

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        instance = this
    }

    companion object {
        lateinit var instance : CalendarProApplication
        var appContext: Context? = null

        /*fun getContext(): Context? {
            return instance.applicationContext
        }*/
    }
}