package com.rita.calendarprooo.sort

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.rita.calendarprooo.NavigationDirections
import com.rita.calendarprooo.R
import com.rita.calendarprooo.data.Plan
import com.rita.calendarprooo.databinding.FragmentHomeSortBinding
import com.rita.calendarprooo.ext.getVmFactory
import com.shrikanthravi.collapsiblecalendarview.widget.CollapsibleCalendar

class HomeSortFragment : Fragment() {

    private val viewModel by viewModels<HomeSortViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //layout binding
        val binding: FragmentHomeSortBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home_sort, container, false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner



        //read Plans when date selected changed
        viewModel.selectedEndTime.observe(viewLifecycleOwner, androidx.lifecycle.Observer{
            Log.i("Rita","selectedEndTime observe- $it")
            it?.let{
                viewModel.readPlanFromToday()
            }
        })

        viewModel.readListFromToday.observe(viewLifecycleOwner, androidx.lifecycle.Observer{
            Log.i("Rita","readListFromToday observe - $it")
            it?.let{
                viewModel.readPlanBeforeToday()
            }
        })

        viewModel.readListBeforeToday.observe(viewLifecycleOwner, androidx.lifecycle.Observer{
            Log.i("Rita","readListBeforeToday observe - $it")
            it?.let{
                viewModel.readPlanInTotal()
            }
        })

        viewModel.startToGetViewList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if(it==true){
                viewModel.getViewList()
                viewModel.doneGetViewList()
            }
        })

        //test
        viewModel.scheduleViewList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            Log.i("Rita","scheduleViewList.observe: $it")
        })

        viewModel.currentUser.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            Log.i("Rita","currentUser.observe: $it")
            viewModel.readPlanFromToday()
            viewModel.readPlanOnChanged()
        })


        //update plans on home pages when data changed
        viewModel.listFromToday.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let{
                Log.i("Rita","listFromToday.observe: $it")
                viewModel.getTotalList()
            }
        })

        viewModel.listBeforeToday.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let{
                Log.i("Rita","listBeforeToday.observe: $it")
                viewModel.getTotalListBefore()
            }
        })



        //schedule adapter
        val adapter = ScheduleAdapter(viewModel)
        binding.homeScheduleList.adapter = adapter
        viewModel.scheduleList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            Log.i("Rita","scheduleList.observe: $it")
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        })



        //to-do adapter
        val todoAdapter = TodoAdapter(viewModel)
        binding.homeTodoList.adapter = todoAdapter
        viewModel.todoList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            Log.i("Rita","todoList.observe: $it")

            //get size again for to-do/done mode changed
            Log.i("Rita","todoList.observe: ${viewModel.startToGetViewListForTodoMode.value}")
            if(viewModel.startToGetViewListForTodoMode.value==true){
                viewModel.getViewListForTodoMode()
                viewModel.startToGetViewListForTodoMode.value=null
            }

            todoAdapter.submitList(it)
            todoAdapter.notifyDataSetChanged()
            viewModel.doneGetViewList()
        })

        //done adapter
        val doneAdapter = DoneAdapter(viewModel)
        binding.homeDoneList.adapter = doneAdapter
        viewModel.doneList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            Log.i("Rita","doneList.observe: $it")

            //get size again for to-do/done mode changed
            Log.i("Rita","doneList.observe: ${viewModel.startToGetViewListForDoneMode.value}")
            if(viewModel.startToGetViewListForDoneMode.value==true){
                viewModel.getViewListForTodoMode()
                viewModel.startToGetViewListForDoneMode.value=null
            }

            doneAdapter.submitList(it)
            doneAdapter.notifyDataSetChanged()
            viewModel.doneGetViewList()
        })



        val address = ""
        val plan: Plan? = Plan()
        //Edit page navigation
        viewModel.navigateToEdit.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let{
                view?.findNavController()?.navigate(
                    NavigationDirections.navigateToEditFragment(
                        address, plan))
                viewModel.doneNavigated()
            }
        })

        viewModel.navigateToEditByPlan.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let{
                Log.i("Rita","navigateToEditByPlan.observe: $it")
                view?.findNavController()?.navigate(
                    NavigationDirections.navigateToEditFragment(
                        address, viewModel.navigateToEditByPlan.value))
                viewModel.doneNavigated()
            }
        })

        viewModel.navigateToInvite.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let{
                Log.i("Rita","navigateToInvite.observe: $it")
                view?.findNavController()?.navigate(
                    NavigationDirections.navigateToInviteFragment(
                        viewModel.navigateToInvite.value))
                viewModel.doneNavigated()
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



        //calendar
        val collapsibleCalendar: CollapsibleCalendar = binding.calendarView
        collapsibleCalendar.setCalendarListener(object : CollapsibleCalendar.CalendarListener {
            override fun onDayChanged() {}
            override fun onClickListener() {}
            override fun onDaySelect() {
                val day = collapsibleCalendar.selectedDay
                val dateSelected = ""+day!!.day + "-" + (day.month + 1) + "-" + day.year

                viewModel.selectedTimeSet(dateSelected)

                Log.i("Rita", "Selected Day: $dateSelected")
            }

            override fun onItemClick(view: View) {}
            override fun onDataUpdate() {}
            override fun onMonthChange() {}
            override fun onWeekChange(i: Int) {}
        })


        return binding.root
    }
}