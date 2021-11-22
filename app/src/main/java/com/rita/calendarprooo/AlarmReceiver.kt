package com.rita.calendarprooo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class AlarmReceiver : BroadcastReceiver() {

    val CHANNEL_ID = "notify"

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.

        if (intent.getAction().equals("com.rita.calendarprooo.broadcast")) {
            val title = intent.getStringExtra("title")
            val time = intent.getStringExtra("time")
            Log.i("Rita", "start broadcast")

            val notification =
                NotificationCompat.Builder(CalendarProApplication.appContext!!, CHANNEL_ID)
                    .setSmallIcon(R.drawable.date4)
                    .setContentTitle(title)
                    .setContentText(time)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .build()


            with(NotificationManagerCompat.from(CalendarProApplication.appContext!!)) {
                // notificationId is a unique int for each notification that you must define
                notify(1, notification)
            }

        }

    }


}
