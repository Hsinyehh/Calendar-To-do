package com.rita.calendarprooo.alarm

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.rita.calendarprooo.AlarmReceiver
import com.rita.calendarprooo.R
import com.rita.calendarprooo.data.Plan
import com.rita.calendarprooo.databinding.DialogAlarmBinding
import com.rita.calendarprooo.ext.stringToTimestamp
import com.rita.calendarprooo.ext.timestampToString
import com.rita.calendarprooo.invite.InviteDialogArgs

class AlarmDialog : DialogFragment() {
    private val viewModel: AlarmViewModel by lazy {
        ViewModelProvider(this).get(AlarmViewModel::class.java)
    }
    val CHANNEL_ID = "notify"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //layout binding
        val binding: DialogAlarmBinding = DataBindingUtil.inflate(
            inflater, R.layout.dialog_alarm, container, false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        //safe args
        viewModel.plan.value = InviteDialogArgs.fromBundle(requireArguments()).plan

        //TimePicker
        binding.alarmTimepicker.setIs24HourView(true)

        val alarmTimePicker = binding.alarmTimepicker
        val alarmDatePicker = binding.alarmDatepicker


        // notification
        createNotificationChannel()

        //set button
        binding.btnSet.setOnClickListener { view: View ->

            val dateSelected = "" + alarmDatePicker.dayOfMonth +
                    "-" + (alarmDatePicker.month + 1) + "-" + alarmDatePicker.year + " " +
                    alarmTimePicker.hour + ":" + alarmTimePicker.minute
            viewModel.alarm_time.value = stringToTimestamp(dateSelected)


        }

        // broadcast
        // send broadcast
        viewModel.alarm_time.observe(viewLifecycleOwner, Observer {
            it?.let {
                viewModel.plan.value?.let { it1 -> setAlarm(it, it1) }
                dismiss()
            }
        })


        // receive broadcast
        val br = AlarmReceiver()

        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION).apply {
            addAction("com.rita.calendarprooo.broadcast")
        }
        getActivity()?.registerReceiver(br, filter)


        binding.alarmBtnCancel.setOnClickListener {
            dismiss()
        }


        return binding.root
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun setAlarm(time: Long, plan: Plan) {
        val timeShowed = plan.start_time?.let { timestampToString(it) } + "  -  " +
                plan.end_time?.let { timestampToString(it) }

        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        intent.setAction("com.rita.calendarprooo.broadcast")
        intent.putExtra("title", "${plan.title}")
        intent.putExtra("time", "$timeShowed")

        val pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, 0)
        val alarmManager = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent)

        Toast.makeText(context, "Alarm Setup success!", Toast.LENGTH_SHORT).show()
    }


}