package com.rita.calendarprooo.addcategory

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.rita.calendarprooo.R
import com.rita.calendarprooo.databinding.DialogAddCategoryBinding
import com.rita.calendarprooo.ext.getVmFactory
import com.rita.calendarprooo.home.HomeViewModel

class AddCategoryDialog : DialogFragment() {

    private val viewModel by viewModels<AddCategoryViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding: DialogAddCategoryBinding = DataBindingUtil.inflate(
            inflater, R.layout.dialog_add_category, container, false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.planGet.value = AddCategoryDialogArgs.fromBundle(requireArguments()).plan


        viewModel.currentUser.observe(viewLifecycleOwner, {
            Log.i("Rita", "currentUser observe: $it")
            it?.let {
                viewModel.getCategoryFromUserFirst()
            }
        })


        viewModel.startToCreate.observe(viewLifecycleOwner, {
            Log.i("Rita", "startToCreate observe: $it")
            if (it == true) {
                // if the plan is created
                if (viewModel.isPlanCreated.value!!) {

                    viewModel.startToPrepare.value = true
                }
                // if the plan is edited
                else {
                    viewModel.getCategoryFromPlan()
                }
            }
        })


        viewModel.startToPrepare.observe(viewLifecycleOwner, {
            Log.i("Rita", "startToPrepare observe: $it")
            if (it == true) {
                viewModel.prepareForCategory()
            }
        })


        viewModel.startToUpdate.observe(viewLifecycleOwner, {
            Log.i("Rita", "startToUpdate observe: $it")
            if (it == true) {
                if (!viewModel.isPlanCreated.value!!) {
                    // if the plan is edited, update the plan's categoryList
                    viewModel.updatePlan()
                }
                // if the plan is edited or created, update the user's categoryList
                viewModel.updateUser()
            }
        })


        viewModel.startToNavigate.observe(viewLifecycleOwner, {
            Log.i("Rita", "startToNavigate observe: $it")
            it?.let {
                if (it) {
                    dismiss()
                } else {
                    Toast.makeText(context, "The input can't be blank!", Toast.LENGTH_LONG).show()
                }
                viewModel.doneNavigated()
            }
        })


        // AutoComplete Input
        viewModel.categoryListForAutoInput.observe(viewLifecycleOwner, {
            Log.i("Rita", "categoryListForAutoInput observe: $it")
            it?.let {
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line, it
                )
                binding.inviteEditEmail.setAdapter(adapter)
            }

        })


        binding.inviteBtnCancel.setOnClickListener {
            dismiss()
        }


        return binding.root


    }
}