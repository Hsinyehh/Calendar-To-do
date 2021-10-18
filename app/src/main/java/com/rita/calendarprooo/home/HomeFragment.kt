package com.rita.calendarprooo.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.rita.calendarprooo.R
import com.rita.calendarprooo.data.Check
import com.rita.calendarprooo.data.Plan
import com.rita.calendarprooo.databinding.FragmentHomeBinding
import com.shrikanthravi.collapsiblecalendarview.widget.CollapsibleCalendar

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(this).get(HomeViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //layout binding
        val binding: FragmentHomeBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home, container, false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        //fake data
        val check= Check(
            title="Meeting Presentation",
            isDone = false,
            done_time=null,
            owner=null,
            doner=null,
            id=1)

        val check_List= mutableListOf<Check>(check,check)

        val plan=Plan(
            id=1,
            title="Meeting",
            description="for product development",
            location="Taipei",null,null,null,null,check_List,
            false,false,null, emptyList())

        val plan_list= mutableListOf<Plan>(plan,plan)


        //schedule adapter
        val adapter = ScheduleAdapter()
        binding.homeScheduleList.adapter = adapter
        adapter.submitList(plan_list)

        //to-do adapter
        val todoAdapter = TodoAdapter()
        binding.homeTodoList.adapter = todoAdapter
        todoAdapter.submitList(plan_list)

        //done adapter
        val doneAdapter = DoneAdapter()
        binding.homeDoneList.adapter = doneAdapter
        doneAdapter.submitList(plan_list)


        //calendar
        val collapsibleCalendar: CollapsibleCalendar = binding.calendarView
        collapsibleCalendar.setCalendarListener(object : CollapsibleCalendar.CalendarListener {
            override fun onDayChanged() {}
            override fun onClickListener() {}
            override fun onDaySelect() {
                val day = collapsibleCalendar.selectedDay
                Log.i(
                    javaClass.name, "Selected Day: "
                            + day!!.year + "/" + (day.month + 1) + "/" + day.day
                )
            }

            override fun onItemClick(view: View) {}
            override fun onDataUpdate() {}
            override fun onMonthChange() {}
            override fun onWeekChange(i: Int) {}
        })


        return binding.root
    }
}