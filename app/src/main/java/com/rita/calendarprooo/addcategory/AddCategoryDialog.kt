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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.rita.calendarprooo.R
import com.rita.calendarprooo.databinding.DialogAddCategoryBinding

class AddCategoryDialog : DialogFragment() {
    private val viewModel: AddCategoryViewModel by lazy {
        ViewModelProvider(this).get(AddCategoryViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //layout binding
        val binding: DialogAddCategoryBinding = DataBindingUtil.inflate(
            inflater, R.layout.dialog_add_category, container, false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.planGet.value = AddCategoryDialogArgs.fromBundle(requireArguments()).plan


        viewModel.planGet.observe(viewLifecycleOwner, Observer {
            Log.i("Rita", "planGet.observe $it")
            it?.let {
                viewModel.getPlanFromUserFirst()
            }
        })


        viewModel.startToCreate.observe(viewLifecycleOwner, Observer {
            Log.i("Rita", "startToCreate.observe $it")
            if (it == true) {
                if (viewModel.planGet.value?.id == "") {
                    viewModel.startToPrepare.value = true
                } else {
                    viewModel.getCategoryFromThePlan()
                }
            }
        })

        viewModel.startToPrepare.observe(viewLifecycleOwner, Observer {
            Log.i("Rita", "startToPrepare.observe $it")
            if (it == true) {
                viewModel.prepareForCategory()
            }
        })

        viewModel.startToUpdate.observe(viewLifecycleOwner, Observer {
            Log.i("Rita", "startToUpdate.observe $it")
            if (it == true) {
                if (viewModel.planGet.value?.id !== "") {
                    viewModel.updateThePlan()
                }
                viewModel.updateUser()
            }
        })

        viewModel.startToNavigate.observe(viewLifecycleOwner, Observer {
            Log.i("Rita", "startToNavigate.observe $it")
            if (it == true) {
                dismiss()
                //viewModel.doneNavigated()
            } else if (it == false) {
                Toast.makeText(context, "The input can't be blank!", Toast.LENGTH_LONG).show()
            }
        })

        viewModel.categoryListFromUser.observe(viewLifecycleOwner, Observer {
            Log.i("Rita", "categoryListFromUser.observe $it")
            it?.let {
                val adapter = ArrayAdapter<String>(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line, it
                )
                binding.inviteEditEmail.setAdapter(adapter)
            }

        })

        viewModel.categoryList.observe(viewLifecycleOwner, Observer {
            Log.i("Rita", "categoryList.observe $it")
            viewModel.convertToUnselectedList(it)
        })





        binding.inviteBtnCancel.setOnClickListener {
            dismiss()
        }


        return binding.root
    }
}