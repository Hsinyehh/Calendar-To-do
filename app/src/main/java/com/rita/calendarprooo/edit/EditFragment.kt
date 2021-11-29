package com.rita.calendarprooo.edit

import android.app.Activity
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.rita.calendarprooo.NavigationDirections
import com.rita.calendarprooo.R
import com.rita.calendarprooo.Util.Logger
import com.rita.calendarprooo.databinding.FragmentEditBinding
import com.rita.calendarprooo.ext.getVmFactory


class EditFragment : Fragment() {

    private val viewModel by viewModels<EditViewModel> {
        getVmFactory(EditFragmentArgs.fromBundle(requireArguments()).plan)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // layout binding
        val binding: FragmentEditBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_edit, container, false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner


        // get safe argument from previous fragment
        viewModel.planGet.value = EditFragmentArgs.fromBundle(requireArguments()).plan
        viewModel.location.value = EditFragmentArgs.fromBundle(requireArguments()).address


        // Time&Date Picker binding
        val startTimePicker = binding.startTimepicker
        val startDatePicker = binding.startDatepicker
        val endTimePicker = binding.endTimepicker
        val endDatePicker = binding.endDatepicker


        viewModel.updatedUser.observe(viewLifecycleOwner, {
            Logger.i("updatedUser observe: $it")
            it?.let {
                // if plan is created, then get category from User
                viewModel.getCategoryFromUser()
            }
        })


        val adapter = CategoryAdapter(viewModel)
        binding.categoryList.adapter = adapter
        viewModel.categoryList.observe(viewLifecycleOwner, {
            Logger.i("categoryList observe: $it")
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        })


        // checkAdapter
        val checkAdapter = CheckAdapter(viewModel)
        binding.checkList.adapter = checkAdapter
        viewModel.checkList.observe(viewLifecycleOwner, {
            Logger.i("checkList observe: $it")
            checkAdapter.submitList(it)
            checkAdapter.notifyDataSetChanged()
            viewModel.clearText()
        })


        // Default value setup for timepicker
        viewModel.planGet.observe(viewLifecycleOwner, {
            Logger.i("planGet observe: ${viewModel.planGet.value}")

            // if the plan is edited
            it?.start_time_detail?.let { list ->

                viewModel.initPlanExtra(it)

                startTimePicker.currentMinute = list[4]
                startTimePicker.currentHour = list[3]
                startDatePicker.init(list[0], list[1] - 1, list[2], null)
            }

            it?.end_time_detail?.let { list ->
                endTimePicker.currentMinute = list[4]
                endTimePicker.currentHour = list[3]
                endDatePicker.init(list[0], list[1] - 1, list[2], null)
            }

            it?.let {
                // if plan is edited, then get category from Plan
                viewModel.getCategoryFromPlan(it)
            }

            Logger.i("planGet id: ${viewModel.id.value}")

        })


        // save button
        binding.buttonSave.setOnClickListener {
            viewModel.loadingStatus.value = true

            // StartTime
            viewModel.start_time_detail.value = listOf<Int>(
                startDatePicker.year,
                startDatePicker.month + 1, startDatePicker.dayOfMonth, startTimePicker.hour,
                startTimePicker.minute
            )

            val startDateSelected = "" + startDatePicker.dayOfMonth +
                    "-" + (startDatePicker.month + 1) + "-" + startDatePicker.year + " " +
                    startTimePicker.hour + ":" + startTimePicker.minute

            // EndTime
            viewModel.end_time_detail.value = listOf<Int>(
                endDatePicker.year,
                endDatePicker.month + 1, endDatePicker.dayOfMonth, endTimePicker.hour,
                endTimePicker.minute
            )

            val endDateSelected = "" + endDatePicker.dayOfMonth +
                    "-" + (endDatePicker.month + 1) + "-" + endDatePicker.year + " " +
                    endTimePicker.hour + ":" + endTimePicker.minute

            viewModel.convertToTimestamp(startDateSelected, endDateSelected)

        }


        viewModel.doneConverted.observe(viewLifecycleOwner, {
            Logger.i("doneConverted observe: $it")
            if (it == true) {
                if (viewModel.editStatus.value == true) {
                    viewModel.preparePlan()
                } else {
                    viewModel.prepareNewPlan()
                }
                viewModel.doneConverted()
            }
        })


        viewModel.newPlan.observe(viewLifecycleOwner, {
            Logger.i("newPlan observe: $it")
            it?.let {
                if (viewModel.editStatus.value == true) {
                    viewModel.updatePlan(it)
                } else {
                    viewModel.createPlan(it)
                }
            }
        })


        // update is done, then navigate to home page
        viewModel.loadingStatus.observe(viewLifecycleOwner, {
            Logger.i("loadingStatus observe: $it")
            it?.let {
                if (!it) {
                    view?.findNavController()?.navigate(R.id.navigate_to_home_fragment)
                    viewModel.doneNavigated()
                }
            }
        })


        // cancel button
        binding.buttonCancel.setOnClickListener { view: View ->
            view.findNavController().popBackStack()
        }


        // TimePicker setup as 24 hrs format
        binding.startTimepicker.setIs24HourView(true)
        binding.endTimepicker.setIs24HourView(true)


        // add category Button
        binding.btnCategoryPlus.setOnClickListener {
            view?.findNavController()?.navigate(
                NavigationDirections.navigateToAddCategoryDialog(viewModel.planGet.value)
            )
        }


        // create checkList Item
        binding.checklistEditText.setOnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_DONE
                || keyEvent.action == KeyEvent.ACTION_DOWN || keyEvent.action == KeyEvent.KEYCODE_ENTER
            ) {
                Logger.i("checklist item setOnEditorActionListener")
                viewModel.checkListTextCreated()
                binding.checklistEditText.hideKeyboard()
            }
            false
        }


        return binding.root


    }


    private fun View.hideKeyboard() {
        context?.let {
            val inputMethodManager =
                it.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
        }
    }
}