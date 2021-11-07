package com.rita.calendarprooo.Util

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.rita.calendarprooo.data.source.CalendarDataSource
import com.rita.calendarprooo.data.source.CalendarRepository
import com.rita.calendarprooo.data.source.DefaultCalendarRepository
import com.rita.calendarprooo.data.source.local.CalendarLocalDataSource
import com.rita.calendarprooo.data.source.remote.CalendarRemoteDataSource

object ServiceLocator {
    @Volatile
    var repository: CalendarRepository? = null
        @VisibleForTesting set

    fun provideRepository(context: Context): CalendarRepository {
        synchronized(this) {
            return repository
                ?: repository
                ?: createPublisherRepository(context)
        }
    }

    private fun createPublisherRepository(context: Context): CalendarRepository {
        return DefaultCalendarRepository(
            CalendarRemoteDataSource,
            createLocalDataSource(context)
        )
    }

    private fun createLocalDataSource(context: Context): CalendarDataSource {
        return CalendarLocalDataSource(context)
    }
}