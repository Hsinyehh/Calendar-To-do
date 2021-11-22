package com.rita.calendarprooo.invite

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.rita.calendarprooo.R
import com.rita.calendarprooo.databinding.DialogInviteCategoryBinding
import com.rita.calendarprooo.ext.getVmFactory


class InviteCategoryDialog : DialogFragment() {
    private val viewModel by viewModels<InviteCategoryViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //layout binding
        val binding: DialogInviteCategoryBinding = DataBindingUtil.inflate(
            inflater, R.layout.dialog_invite_category, container, false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        //safe args
        viewModel.category.value = InviteCategoryDialogArgs.fromBundle(requireArguments()).category
        viewModel.user.value = InviteCategoryDialogArgs.fromBundle(requireArguments()).user
        viewModel.categoryPosition.value =
            InviteCategoryDialogArgs.fromBundle(requireArguments()).position


        binding.inviteBtnCancel.setOnClickListener {
            dismiss()
        }

        viewModel.userTobeInvited.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            Log.i("Rita", "userTobeInvited observe- $it")
            it?.let {
                viewModel.createInvitation()
            }
        })

        viewModel.invitationList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            Log.i("Rita", "invitationList observe- $it")
            it?.let {
                viewModel.updateInvitation(it)
            }
        })

        viewModel.isInvited.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            Log.i("Rita", "isInvited observe- $it")
            if (it == true) {
                Toast.makeText(context, "The person is invited already.", Toast.LENGTH_LONG).show()
            }
        })


        viewModel.updateSuccess.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            Log.i("Rita", "updateSuccess observe- $it")
            if (it == true) {
                Toast.makeText(context, "Invite success", Toast.LENGTH_LONG).show()
                dismiss()
                viewModel.doneWritten()
            }
        })

        return binding.root
    }
}