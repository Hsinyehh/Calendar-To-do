package com.rita.calendarprooo.addcategory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.rita.calendarprooo.R
import com.rita.calendarprooo.Util.Logger
import com.rita.calendarprooo.databinding.DialogAddCategoryBinding
import com.rita.calendarprooo.ext.getVmFactory

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
            Logger.i("currentUser observe: $it")
            it?.let {
                viewModel.getCategoryFromUserFirst()
            }
        })


        viewModel.startToCreate.observe(viewLifecycleOwner, {
            Logger.i("startToCreate observe: $it")
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
            Logger.i("startToPrepare observe: $it")
            if (it == true) {
                viewModel.prepareForCategory()
            }
        })


        viewModel.startToUpdate.observe(viewLifecycleOwner, {
            Logger.i("startToUpdate observe: $it")
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
            Logger.i("startToNavigate observe: $it")
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
            Logger.i("categoryListForAutoInput observe: $it")
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