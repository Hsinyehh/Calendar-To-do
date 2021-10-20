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
import com.rita.calendarprooo.R
import com.rita.calendarprooo.data.Category
import com.rita.calendarprooo.data.Check
import com.rita.calendarprooo.databinding.FragmentEditBinding
import com.rita.calendarprooo.home.CheckAdapter


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


        //FAKE DATA
        val categoryList = mutableListOf<Category>(Category("Job",false),
            Category("Travel",false),Category("Meeting",false))

        val adapter = CategoryAdapter(viewModel)
        binding.categoryList.adapter=adapter
        adapter.submitList(categoryList)



        //checkAdapter
        val checkAdapter= CheckAdapter()
        binding.checkList.adapter=checkAdapter
        viewModel.checkList.observe(viewLifecycleOwner, Observer {
            Log.i("Rita","viewModel.checkList.observe")
            checkAdapter.submitList(it)
            checkAdapter.notifyDataSetChanged()
            viewModel.clearText()
        })



        return  binding.root
    }
}