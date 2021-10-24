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
import com.rita.calendarprooo.edit.EditViewModel
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



        //schedule adapter
        val adapter = ScheduleAdapter(viewModel)
        binding.homeScheduleList.adapter = adapter
        viewModel.planList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            adapter.submitList(it)
        })


        //to-do adapter
        val todoAdapter = TodoAdapter(viewModel)
        binding.homeTodoList.adapter = todoAdapter
        viewModel.todoList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            todoAdapter.submitList(it)
        })

        //done adapter
        val doneAdapter = DoneAdapter(viewModel)
        binding.homeDoneList.adapter = doneAdapter
        viewModel.todoList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            doneAdapter.submitList(it)
        })



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
                var startPosition = viewHolder.adapterPosition
                var endPosition = target.adapterPosition

                //swap position
                viewModel.swapCheckListItem(startPosition,endPosition)
                recyclerView.adapter?.notifyItemMoved(startPosition,endPosition)
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


        //Edit page navigation
        viewModel.navigateToEdit.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let{
                view?.findNavController()?.navigate(R.id.navigate_to_edit_fragment)
                viewModel.doneNavigated()
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