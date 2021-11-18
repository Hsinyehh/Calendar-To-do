package com.rita.calendarprooo.edit

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.rita.calendarprooo.NavigationDirections
import com.rita.calendarprooo.R
import com.rita.calendarprooo.data.Category
import com.rita.calendarprooo.data.Check
import com.rita.calendarprooo.databinding.FragmentEditBinding
import com.rita.calendarprooo.ext.getVmFactory
import com.rita.calendarprooo.home.CheckAdapter
import java.text.SimpleDateFormat


class EditFragment : Fragment() {

    private val viewModel by viewModels<EditViewModel> {
        getVmFactory(EditFragmentArgs.fromBundle(requireArguments()).plan)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //layout binding
        val binding: FragmentEditBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_edit, container, false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner


        //get safe argument from previous fragment
        viewModel.planGet.value = EditFragmentArgs.fromBundle(requireArguments()).plan
        viewModel.location.value = EditFragmentArgs.fromBundle(requireArguments()).address

        //Time&Date Picker binding
        val startTimePicker = binding.startTimepicker
        val startDatePicker = binding.startDatepicker
        val endTimePicker = binding.endTimepicker
        val endDatePicker = binding.endDatepicker


        val adapter = CategoryAdapter(viewModel)
        binding.categoryList.adapter = adapter
        viewModel.categoryList.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        })

        //checkAdapter
        val checkAdapter = CheckAdapter(viewModel)
        binding.checkList.adapter = checkAdapter
        viewModel.checkList.observe(viewLifecycleOwner, Observer {
            Log.i("Rita", "viewModel.checkList.observe")
            checkAdapter.submitList(it)
            checkAdapter.notifyDataSetChanged()
            viewModel.clearText()
        })

        //Default value setup for timepicker
        viewModel.planGet.observe(viewLifecycleOwner, Observer {
            Log.i("Rita", "plan.observe: ${viewModel.planGet.value}")
            it?.start_time_detail?.let {
                //recognize as edit rather than created a plan
                viewModel.editStatus.value = true
                viewModel.location.value = viewModel.planGet?.value?.location
                startTimePicker.currentHour = it[4]
                startTimePicker.currentMinute = it[4]
                startDatePicker.init(it[0], it[1] - 1, it[2], null)
            }
            it?.end_time_detail?.let {
                endTimePicker.currentHour = it[3]
                endTimePicker.currentMinute = it[4]
                endDatePicker.init(it[0], it[1] - 1, it[2], null)
            }

            if (it?.categoryList.isNullOrEmpty()) {
                viewModel.getCategoryFromUser()
            } else {
                viewModel.getCategoryFromPlan()
            }

            Log.i("Rita", "plan.location.observe: ${viewModel.location.value}")

        })

        // test
        viewModel.loadingStatus.observe(viewLifecycleOwner, Observer {
            Log.i("Rita", "loadingStatus.observe: ${it}")
        })



        //save button
        binding.buttonSave.setOnClickListener { view: View ->
            viewModel.loadingStatus.value = true
            //StartTime
            viewModel.start_time_detail.value = listOf<Int>(
                startDatePicker.year,
                startDatePicker.month + 1, startDatePicker.dayOfMonth, startTimePicker.hour,
                startTimePicker.minute
            )

            val startDateSelected = "" + startDatePicker.dayOfMonth +
                    "-" + (startDatePicker.month + 1) + "-" + startDatePicker.year + " " +
                    startTimePicker.hour + ":" + startTimePicker.minute
            viewModel.convertToStartTimeStamp(startDateSelected)

            //EndTime
            viewModel.end_time_detail.value = listOf<Int>(
                endDatePicker.year,
                endDatePicker.month + 1, endDatePicker.dayOfMonth, endTimePicker.hour,
                endTimePicker.minute
            )

            val endDateSelected = "" + endDatePicker.dayOfMonth +
                    "-" + (endDatePicker.month + 1) + "-" + endDatePicker.year + " " +
                    endTimePicker.hour + ":" + endTimePicker.minute
            viewModel.convertToEndTimeStamp(endDateSelected)
        }

        viewModel.createStatus.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                Log.i("Rita", "viewModel.createStatus.observe ${viewModel.editStatus.value}")
                if (viewModel.editStatus.value == true) {
                    viewModel.updatePlan()
                    view?.findNavController()?.navigate(R.id.navigate_to_home_fragment)
                    viewModel.doneNavigated()
                } else {
                    Log.i("Rita", "viewModel.create_status.observe")
                    viewModel.createNewPlan()
                    viewModel.doneConverted()
                }
            }
        })


        viewModel.newPlan.observe(viewLifecycleOwner, Observer {
            it?.let {
                viewModel.writeNewPlan()
                view?.findNavController()?.navigate(R.id.navigate_to_home_fragment)
                viewModel.doneNavigated()
            }
        })

        //cancel button
        binding.buttonCancel.setOnClickListener { view: View ->
            view.findNavController().popBackStack()
        }

        //TimePicker
        binding.startTimepicker.setIs24HourView(true)
        binding.endTimepicker.setIs24HourView(true)

        //add category Button
        binding.btnCategoryPlus.setOnClickListener {
            view?.findNavController()?.navigate(
                NavigationDirections.navigateToAddCategoryDialog(viewModel.planGet.value)
            )
        }

        //create checkList Item
        binding.checklistEditText.setOnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_DONE
                || keyEvent.action == KeyEvent.ACTION_DOWN || keyEvent.action == KeyEvent.KEYCODE_ENTER
            ) {
                Log.i("Rita", "checklist item setOnEditorActionListener")
                viewModel.checkListTextCreated()
                binding.checklistEditText.hideKeyboard()
            }
            false
        }


        return binding.root
    }


    fun View.hideKeyboard() {
        context?.let {
            val inputMethodManager =
                it.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
        }
    }
}