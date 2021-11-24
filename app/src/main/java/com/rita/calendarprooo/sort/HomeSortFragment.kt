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
    ): View {

        val binding: FragmentHomeSortBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home_sort, container, false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner


        // init plans
        viewModel.currentUser.observe(viewLifecycleOwner, {
            Log.i("Rita", "currentUser.observe: $it")
            viewModel.initCategory(it)
        })


        viewModel.categoryStatus.observe(viewLifecycleOwner, {
            Log.i("Rita", "categoryStatus.observe: $it")
            viewModel.getPlansToday()
            viewModel.getLivePlans()
        })


        // read Plans when date selected changed
        viewModel.selectedEndTime.observe(viewLifecycleOwner, {
            Log.i("Rita", "selectedEndTime observe- $it")
            it?.let {
                viewModel.getPlansToday()
                viewModel.getLivePlans()
            }
        })


        viewModel.plansToday.observe(viewLifecycleOwner, {
            Log.i("Rita", "plansToday observe - $it")
            it?.let {
                viewModel.getPlansBeforeToday()
            }
        })


        viewModel.plansBeforeToday.observe(viewLifecycleOwner, {
            Log.i("Rita", "plansBeforeToday observe - $it")
            it?.let {
                viewModel.getTotalPlans()
            }
        })


        viewModel.startToGetViewList.observe(viewLifecycleOwner, {
            if (it == true) {
                viewModel.getViewList()
                viewModel.doneGetViewList()
            }
        })


        // test
        viewModel.scheduleViewList.observe(viewLifecycleOwner, {
            Log.i("Rita", "scheduleViewList.observe: $it")
        })


        // update plans on home pages when data changed
        viewModel.livePlansToday.observe(viewLifecycleOwner, {
            it?.let {
                Log.i("Rita", "livePlansToday.observe: $it")
                viewModel.getTotalLivePlans()
            }
        })


        viewModel.livePlansBeforeToday.observe(viewLifecycleOwner, {
            it?.let {
                Log.i("Rita", "livePlansBeforeToday.observe: $it")
                viewModel.getTotalLivePlansBefore()
            }
        })


        fun createScheduleRecyclerview() {
            // bind adapter again before the adapter submit list so the position item is right
            val adapter = ScheduleAdapter(viewModel)
            binding.homeScheduleList.adapter = adapter
            adapter.submitList(viewModel.scheduleList.value)
            adapter.notifyDataSetChanged()
        }


        fun createTodoRecyclerview() {
            val todoAdapter = TodoAdapter(viewModel)
            binding.homeTodoList.adapter = todoAdapter
            todoAdapter.submitList(viewModel.todoList.value)
            todoAdapter.notifyDataSetChanged()
        }


        fun createDoneRecyclerview() {
            val doneAdapter = DoneAdapter(viewModel)
            binding.homeDoneList.adapter = doneAdapter
            doneAdapter.submitList(viewModel.doneList.value)
            doneAdapter.notifyDataSetChanged()
        }


        // schedule adapter
        val adapter = ScheduleAdapter(viewModel)
        binding.homeScheduleList.adapter = adapter
        viewModel.scheduleList.observe(viewLifecycleOwner, {
            Log.i("Rita", "scheduleList.observe: $it")
            if (viewModel.getViewListAlready.value == true) {
                createScheduleRecyclerview()
            }
        })


        // to-do adapter
        val todoAdapter = TodoAdapter(viewModel)

        binding.homeTodoList.adapter = todoAdapter

        viewModel.todoList.observe(viewLifecycleOwner, {
            Log.i("Rita", "todoList.observe: $it")

            // get size again for to-do/done mode changed
            Log.i("Rita", "todoList.observe: ${viewModel.startToGetViewListForTodoMode.value}")
            if (viewModel.startToGetViewListForTodoMode.value == true) {
                viewModel.getViewListForTodoMode()
                viewModel.startToGetViewListForTodoMode.value = null
            }

            if (viewModel.getViewListAlready.value == true) {
                createTodoRecyclerview()
            }
        })


        // done adapter
        val doneAdapter = DoneAdapter(viewModel)

        binding.homeDoneList.adapter = doneAdapter

        viewModel.doneList.observe(viewLifecycleOwner, {
            Log.i("Rita", "doneList.observe: $it")

            // get size again for to-do/done mode changed
            Log.i("Rita", "doneList.observe: ${viewModel.startToGetViewListForDoneMode.value}")
            if (viewModel.startToGetViewListForDoneMode.value == true) {
                viewModel.getViewListForTodoMode()
                viewModel.startToGetViewListForDoneMode.value = null
            }

            if (viewModel.getViewListAlready.value == true) {
                Log.e("Rita", "viewModel.getViewListAlready.value == true, submit")
                createDoneRecyclerview()
            }
        })


        // After the viewList is got, the recyclerView will show
        viewModel.getViewListAlready.observe(viewLifecycleOwner, {
            Log.e("Rita", "getViewListAlready observe - $it")
            it?.let {
                if (it) {
                    createScheduleRecyclerview()
                    createTodoRecyclerview()
                    createDoneRecyclerview()
                }
            }
        })


        // category Adapter
        val categoryAdapter = CategoryAdapter(viewModel)

        binding.categoryList.adapter = categoryAdapter


        viewModel.categoryList.observe(viewLifecycleOwner, {
            Log.i("Rita", "categoryList.observe: $it")
            it?.let {
                categoryAdapter.submitList(it)
                categoryAdapter.notifyDataSetChanged()
            }
        })


        val address = ""
        val plan = Plan()
        // Edit page navigation
        viewModel.navigateToEdit.observe(viewLifecycleOwner, {
            it?.let {
                view?.findNavController()?.navigate(
                    NavigationDirections.navigateToEditFragment(
                        address, plan
                    )
                )
                viewModel.doneNavigated()
            }
        })


        viewModel.navigateToEditByPlan.observe(viewLifecycleOwner, {
            it?.let {
                Log.i("Rita", "navigateToEditByPlan.observe: $it")
                view?.findNavController()?.navigate(
                    NavigationDirections.navigateToEditFragment(
                        address, viewModel.navigateToEditByPlan.value
                    )
                )
                viewModel.doneNavigated()
            }
        })


        viewModel.navigateToInvite.observe(viewLifecycleOwner, {
            it?.let {
                Log.i("Rita", "navigateToInvite.observe: $it")
                view?.findNavController()?.navigate(
                    NavigationDirections.navigateToInviteFragment(
                        viewModel.navigateToInvite.value
                    )
                )
                viewModel.doneNavigated()
            }
        })


        viewModel.navigateToInviteCategory.observe(viewLifecycleOwner, {
            Log.i("Rita", "navigateToInviteCategory.observe: $it")
            it?.let {
                view?.findNavController()?.navigate(
                    NavigationDirections.navigateToInviteCategoryDialog(
                        viewModel.categoryStatus.value, viewModel.currentUser.value,
                        viewModel.categoryPosition.value!!
                    )
                )
                viewModel.doneNavigated()
            }
        })


        viewModel.navigateToAlarm.observe(viewLifecycleOwner, {
            it?.let {
                Log.i("Rita", "navigateToAlarm.observe: $it")
                view?.findNavController()?.navigate(
                    NavigationDirections.navigateToAlarmDialog(it)
                )
                viewModel.doneNavigated()
            }
        })


        // to-do adapter drag item
        val simpleCallback = setupTouchHelper()

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
                val dateSelected = "" + day!!.day + "-" + (day.month + 1) + "-" + day.year

                viewModel.selectedTimeSet(dateSelected)

                Log.i("Rita", "Selected Day: $dateSelected")
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