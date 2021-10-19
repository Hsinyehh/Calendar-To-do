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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.rita.calendarprooo.R
import com.rita.calendarprooo.data.Check
import com.rita.calendarprooo.data.Plan
import com.rita.calendarprooo.databinding.FragmentHomeBinding
import com.shrikanthravi.collapsiblecalendarview.widget.CollapsibleCalendar
import java.util.*

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

        val plan2=Plan(
            id=1,
            title="Jogging",
            description="for Health",
            location="Taipei",null,null,null,null,check_List,
            false,false,null, emptyList())

        val plan3=Plan(
            id=1,
            title="Reading",
            description="for Leisure",
            location="Taipei",null,null,null,null,check_List,
            false,false,null, emptyList())

        val plan_list= mutableListOf<Plan>(plan,plan)
        val todo_list= mutableListOf<Plan>(plan2,plan3)


        //schedule adapter
        val adapter = ScheduleAdapter()
        binding.homeScheduleList.adapter = adapter
        adapter.submitList(plan_list)

        //to-do adapter
        val todoAdapter = TodoAdapter()
        binding.homeTodoList.adapter = todoAdapter
        todoAdapter.submitList(todo_list)

        //done adapter
        val doneAdapter = DoneAdapter()
        binding.homeDoneList.adapter = doneAdapter
        doneAdapter.submitList(todo_list)


        //to-do adapter drag item
        var simpleCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or (ItemTouchHelper.DOWN),
            0){

            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val  dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                val swipeFlags = 0
                return makeMovementFlags(dragFlags, swipeFlags)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                var startDestination = viewHolder.adapterPosition
                var endPosition = target.adapterPosition
                Collections.swap(todo_list,startDestination,endPosition)
                recyclerView.adapter?.notifyItemMoved(startDestination,endPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            }

            override fun isLongPressDragEnabled(): Boolean {
                // Allows for long click so items can be dragged, moved up or down in the list.
                return true
            }

            override fun isItemViewSwipeEnabled(): Boolean {
                // Not Allow items to be swiped left or right.
                return false
            }
        }
        val toToListRecyclerView = binding.homeTodoList
        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(toToListRecyclerView)


        //viewModel
        viewModel.navigateToEdit.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let{
                view?.findNavController()?.navigate(R.id.navigate_to_edit_fragment)
            }
        })

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