package com.rita.calendarprooo.addcategory

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.rita.calendarprooo.R
import com.rita.calendarprooo.databinding.DialogAddCategoryBinding
import com.rita.calendarprooo.databinding.DialogInviteBinding
import com.rita.calendarprooo.edit.EditFragmentArgs
import com.rita.calendarprooo.invite.InviteViewModel

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

        viewModel.planGet.value= AddCategoryDialogArgs.fromBundle(requireArguments()).plan


        //Test
        /*viewModel.planGet.observe(viewLifecycleOwner, Observer {
            Log.i("Rita","planGet.observe $it")
            val isIdEmpty = viewModel.planGet.value?.id ==""
            //Log.i("Rita","planGet.id ${it?.id}")
            Log.i("Rita","planGet.is id empty $isIdEmpty")
        })*/

        viewModel.startToCreate.observe(viewLifecycleOwner, Observer {
            Log.i("Rita","startToCreate.observe $it")
            if(it == true){
                if(viewModel.planGet.value?.id ==""){
                    viewModel.getCategoryFromUser()
                }else{
                    viewModel.getCategoryFromThePlan()
                }
            }
        })

        viewModel.startToPrepare.observe(viewLifecycleOwner, Observer {
            Log.i("Rita","startToCreate.observe $it")
            if(it == true){
                viewModel.prepareForCategory()
            }
        })

        viewModel.startToUpdate.observe(viewLifecycleOwner, Observer {
            Log.i("Rita","startToUpdate.observe $it")
            if(it == true){
                if(viewModel.planGet.value?.id !== "") {
                    viewModel.updateThePlan()
                }
                viewModel.updateUser()
            }
        })

        viewModel.startToNavigate.observe(viewLifecycleOwner, Observer {
            Log.i("Rita","startToNavigate.observe $it")
            if(it == true){
                dismiss()
                //viewModel.doneNavigated()
            }
        })



        binding.inviteBtnCancel.setOnClickListener {
            dismiss()
        }


        return binding.root
    }
}