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
import com.rita.calendarprooo.NavigationDirections
import com.rita.calendarprooo.R
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

        viewModel.readPlanOnChanged()

        //schedule adapter
        val adapter = ScheduleAdapter(viewModel)
        binding.homeScheduleList.adapter = adapter
        viewModel.scheduleList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            Log.i("Rita","viewModel.scheduleList.observe: $it")
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        })


        //to-do adapter
        val todoAdapter = TodoAdapter(viewModel)
        binding.homeTodoList.adapter = todoAdapter
        viewModel.todoList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            Log.i("Rita","viewModel.todoList.observe: $it")
            todoAdapter.submitList(it)
            todoAdapter.notifyDataSetChanged()
        })

        //done adapter
        val doneAdapter = DoneAdapter(viewModel)
        binding.homeDoneList.adapter = doneAdapter
        viewModel.doneList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            Log.i("Rita","viewModel.doneList.observe: $it")
            doneAdapter.submitList(it)
            doneAdapter.notifyDataSetChanged()
        })


        viewModel.listFromToday.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let{
                Log.i("Rita","viewModel.listFromToday.observe: $it")
                viewModel.getTotalList()
            }
        })

        viewModel.listBeforeToday.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let{
                Log.i("Rita","viewModel.listBeforeToday.observe: $it")
                viewModel.getTotalList()
            }
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


        val address=""
        //Edit page navigation
        viewModel.navigateToEdit.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let{
                view?.findNavController()?.navigate(
                    NavigationDirections.navigateToEditFragment(
                        address))
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
                val dateSelected = ""+day!!.day + "-" + (day.month + 1) + "-" + day.year

                viewModel.convertToTimeStamp(dateSelected)

                Log.i("Rita", "Selected Day: $dateSelected")
            }

            override fun onItemClick(view: View) {}
            override fun onDataUpdate() {}
            override fun onMonthChange() {}
            override fun onWeekChange(i: Int) {}
        })


        viewModel.selectedEndTime.observe(viewLifecycleOwner, androidx.lifecycle.Observer{
            Log.i("Rita","HomeViewModel.selectedEndTime- $it")
            it?.let{
                viewModel.readPlan()
            }
        })


        return binding.root
    }


}