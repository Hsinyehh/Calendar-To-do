package com.rita.calendarprooo.home

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
import com.rita.calendarprooo.databinding.FragmentHomeBinding
import com.rita.calendarprooo.ext.getVmFactory
import com.shrikanthravi.collapsiblecalendarview.widget.CollapsibleCalendar

class HomeFragment : Fragment() {

    private val viewModel by viewModels<HomeViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding: FragmentHomeBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home, container, false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner


        // get user at first to get plans
        viewModel.currentUser.observe(viewLifecycleOwner,  {
            Log.i("Rita", "home currentUser observe: $it")
            it?.let{
                // need to get plans once to create viewList, which control whether the detail hide or show
                viewModel.getPlansToday()

                // if plans update in firebase, it will fetch data again
                viewModel.getLivePlans()
            }
        })


        // read Plans when date selected changed
        viewModel.selectedEndTime.observe(viewLifecycleOwner, {
            Log.i("Rita", "home selectedEndTime observe: $it")
            it?.let {
                if (viewModel.currentUser.value != null) {
                    // need to get plans once to create viewList, which control whether the detail hide or show
                    viewModel.getPlansToday()

                    // if plans update in firebase, it will fetch data again
                    viewModel.getLivePlans()
                }
            }
        })


        // get plans once
        viewModel.plansToday.observe(viewLifecycleOwner, {
            Log.i("Rita", "home plansToday observe: $it")
            viewModel.getPlansBeforeToday()
        })


        viewModel.plansBeforeToday.observe(viewLifecycleOwner, {
            Log.i("Rita", "home plansBeforeToday observe: $it")
            viewModel.getTotalPlans()
        })


        // get plans onChanged
        viewModel.livePlansReset.observe(viewLifecycleOwner, {
            Log.i("Rita", "home livePlansReset observe: $it")

            // need to set Observer here so we can get the same reference for livedata
            it?.let{
                if(it){
                    viewModel.livePlansToday.observe(viewLifecycleOwner, { plans ->
                        Log.i("Rita", "home livePlansToday observe: $plans")
                        plans?.let {
                            viewModel.getTotalLivePlans()
                            binding.viewModel = viewModel
                        }
                    })

                    viewModel.livePlansBeforeToday.observe(viewLifecycleOwner, { plans->
                        Log.i("Rita", "home livePlansBeforeToday observe: $plans")
                        plans?.let {
                            viewModel.getTotalLivePlans()
                            binding.viewModel = viewModel
                        }
                    })
                }
            }
        })


        viewModel.startToGetViewList.observe(viewLifecycleOwner, {
            Log.i("Rita", "home startToGetViewList observe: $it")
            it?.let {
                    viewModel.getViewList()
                    binding.viewModel = viewModel
            }
        })


        fun createScheduleRecyclerview() {
            // bind adapter again before adapter submit list so that the items' position is correct
            val adapter = ScheduleAdapter(viewModel)
            binding.homeScheduleList.adapter = adapter
            adapter.submitList(viewModel.scheduleList.value)
            adapter.notifyDataSetChanged()
            viewModel.loadingStatus.value = false
        }


        fun createTodoRecyclerview() {
            val todoAdapter = TodoAdapter(viewModel)
            binding.homeTodoList.adapter = todoAdapter
            todoAdapter.submitList(viewModel.todoList.value)
            todoAdapter.notifyDataSetChanged()
            viewModel.loadingStatus.value = false
        }


        fun createDoneRecyclerview() {
            val doneAdapter = DoneAdapter(viewModel)
            binding.homeDoneList.adapter = doneAdapter
            doneAdapter.submitList(viewModel.doneList.value)
            doneAdapter.notifyDataSetChanged()
            viewModel.loadingStatus.value = false
        }


        // schedule adapter
        viewModel.scheduleList.observe(viewLifecycleOwner, {
            Log.i("Rita", "home scheduleList observe: $it")
            if (viewModel.getViewListAlready.value == true) {
                createScheduleRecyclerview()
            }
        })


        // to-do adapter
        viewModel.todoList.observe(viewLifecycleOwner, {
            Log.i("Rita", "home todoList observe: $it")

            // get size again for to-do/done mode changed
            if (viewModel.startToGetViewListForTodoMode.value == true) {
                //Because the size of todoList and doneList may change
                viewModel.getViewListForTodoMode()
                viewModel.startToGetViewListForTodoMode.value = null
            }

            if (viewModel.getViewListAlready.value == true) {
                createTodoRecyclerview()
            }
        })


        // done adapter
        viewModel.doneList.observe(viewLifecycleOwner, {
            Log.i("Rita", "home doneList observe: $it")

            // get size again for to-do/done mode changed
            if (viewModel.startToGetViewListForDoneMode.value == true) {
                //Because the size of todoList and doneList may change
                viewModel.getViewListForTodoMode()
                viewModel.startToGetViewListForDoneMode.value = null
            }

            if (viewModel.getViewListAlready.value == true) {
                Log.i("Rita", "homeVM.getViewListAlready.value == true, submit")
                createDoneRecyclerview()
            }
        })


        // After the viewList is got, the recyclerView will show
        viewModel.getViewListAlready.observe(viewLifecycleOwner, {
            Log.i("Rita", "home getViewListAlready observe: $it")
            it?.let {
                if (it) {
                    createScheduleRecyclerview()
                    createTodoRecyclerview()
                    createDoneRecyclerview()
                }
            }
        })


        // update Plan
        viewModel.planUpdate.observe(viewLifecycleOwner, {
            Log.i("Rita", "home planUpdate observe: $it")

            it?.let{
                if(viewModel.isCheckDoneChanged.value == true){
                    val plan = viewModel.renewCheckDoneStatus(it)

                    // update check - change check done status step3 - firebase update
                    viewModel.updatePlanByCheck(plan)
                }
                else if(viewModel.isCheckRemoved.value == true){
                    val plan = viewModel.renewCheckRemoval(it)

                    // update check - remove check step3 - firebase update
                    viewModel.updatePlanByCheck(plan)
                }
            }
            viewModel.doneUpdated()
        })


        val address = ""
        val plan = Plan()
        viewModel.navigateToEdit.observe(viewLifecycleOwner, {
            it?.let {
                view?.findNavController()?.navigate(
                    NavigationDirections.navigateToEditFragment(address, plan)
                )
                viewModel.doneNavigated()
            }
        })


        viewModel.navigateToEditByPlan.observe(viewLifecycleOwner, {
            Log.i("Rita", "home navigateToEditByPlan observe: $it")
            it?.let {
                view?.findNavController()?.navigate(
                    NavigationDirections.navigateToEditFragment(address, it)
                )
                viewModel.doneNavigated()
            }
        })


        viewModel.navigateToInvite.observe(viewLifecycleOwner, {
            Log.i("Rita", "home navigateToInvite observe: $it")
            it?.let {
                view?.findNavController()?.navigate(
                    NavigationDirections.navigateToInviteFragment(it)
                )
                viewModel.doneNavigated()
            }
        })


        viewModel.navigateToAlarm.observe(viewLifecycleOwner, {
            Log.i("Rita", "home navigateToAlarm observe: $it")
            it?.let {
                view?.findNavController()?.navigate(
                    NavigationDirections.navigateToAlarmDialog(it)
                )
                viewModel.doneNavigated()
            }
        })


        // To-do adapter drag item
        val simpleCallback = setupTouchHelper()
        val toToListRecyclerView = binding.homeTodoList
        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(toToListRecyclerView)


        // Calendar
        val collapsibleCalendar: CollapsibleCalendar = binding.calendarView
        collapsibleCalendar.setCalendarListener(object : CollapsibleCalendar.CalendarListener {
            override fun onDayChanged() {}
            override fun onClickListener() {}
            override fun onDaySelect() {
                val day = collapsibleCalendar.selectedDay
                val dateSelected = "" + day!!.day + "-" + (day.month + 1) + "-" + day.year

                viewModel.selectedTimeSet(dateSelected)

                Log.i("Rita", "home Selected Day: $dateSelected")
            }

            override fun onItemClick(v: View) {}
            override fun onDataUpdate() {}
            override fun onMonthChange() {}
            override fun onWeekChange(position: Int) {}
        })


        return binding.root


    }

    private fun setupTouchHelper() : ItemTouchHelper.SimpleCallback{
        return object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or (ItemTouchHelper.DOWN),
            0
        ) {

            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                val swipeFlags = 0
                return makeMovementFlags(dragFlags, swipeFlags)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val startPosition = viewHolder.adapterPosition
                val endPosition = target.adapterPosition

                //swap position
                viewModel.swapCheckListItem(startPosition, endPosition)
                recyclerView.adapter?.notifyItemMoved(startPosition, endPosition)
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
    }

}