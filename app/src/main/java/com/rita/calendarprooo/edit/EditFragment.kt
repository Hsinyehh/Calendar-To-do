package com.rita.calendarprooo.edit

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.rita.calendarprooo.R
import com.rita.calendarprooo.data.Category
import com.rita.calendarprooo.data.Check
import com.rita.calendarprooo.databinding.FragmentEditBinding
import com.rita.calendarprooo.home.CheckAdapter
import java.text.SimpleDateFormat


class EditFragment : Fragment() {

    private val viewModel: EditViewModel by lazy {
        ViewModelProvider(this).get(EditViewModel::class.java)
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
        viewModel.location.value= EditFragmentArgs.fromBundle(requireArguments()).address

        //Time&Date Picker binding
        val startTimePicker=binding.startTimepicker
        val startDatePicker=binding.startDatepicker
        val endTimePicker=binding.endTimepicker
        val endDatePicker=binding.endDatepicker


        //FAKE DATA
        val categoryList = mutableListOf<Category>(Category("Job",false),
            Category("Travel",false),Category("Meeting",false))

        val adapter = CategoryAdapter(viewModel)
        binding.categoryList.adapter=adapter
        adapter.submitList(categoryList)


        //checkAdapter
        val checkAdapter= CheckAdapter(viewModel)
        binding.checkList.adapter=checkAdapter
        viewModel.checkList.observe(viewLifecycleOwner, Observer {
            Log.i("Rita","viewModel.checkList.observe")
            checkAdapter.submitList(it)
            checkAdapter.notifyDataSetChanged()
            viewModel.clearText()
        })

        //save button
        binding.buttonSave.setOnClickListener { view: View ->
            //StartTime
            val startDateSelected = ""+ startDatePicker.getDayOfMonth()+
                    "-"+ (startDatePicker.getMonth() + 1)+"-"+startDatePicker.getYear()+" "+
                    startTimePicker.hour + ":"+startTimePicker.minute
            viewModel.convertToStartTimeStamp(startDateSelected)

            //EndTime
            val endDateSelected = ""+ endDatePicker.getDayOfMonth()+
                    "-"+ (endDatePicker.getMonth() + 1)+"-"+endDatePicker.getYear()+" "+
                    endTimePicker.hour + ":"+endTimePicker.minute
            viewModel.convertToEndTimeStamp(endDateSelected)
        }

        viewModel.end_time.observe(viewLifecycleOwner, Observer {
            it?.let{
                Log.i("Rita","viewModel.end_time.observe")
                viewModel.createNewPlan()
            }
        })


        viewModel.newPlan.observe(viewLifecycleOwner, Observer {
            it?.let{
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


        return  binding.root
    }
}