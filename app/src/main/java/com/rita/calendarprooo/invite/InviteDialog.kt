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
import com.rita.calendarprooo.databinding.DialogInviteBinding
import com.rita.calendarprooo.ext.getVmFactory


class InviteDialog : DialogFragment() {

    private val viewModel by viewModels<InviteViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding: DialogInviteBinding = DataBindingUtil.inflate(
            inflater, R.layout.dialog_invite, container, false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // safe args
        viewModel.plan.value = InviteDialogArgs.fromBundle(requireArguments()).plan


        binding.inviteBtnCancel.setOnClickListener {
            dismiss()
        }


        viewModel.invitation.observe(viewLifecycleOwner, {
            Log.i("Rita", "invitation observe: $it")
            it?.let {
                viewModel.updateInvitation()
            }
        })


        viewModel.isInvited.observe(viewLifecycleOwner, {
            Log.i("Rita", "isInvited.observe: $it")
            if (it == true) {
                Toast.makeText(context, "The person is invited already.", Toast.LENGTH_LONG).show()
            }
        })


        viewModel.isCollaborator.observe(viewLifecycleOwner, {
            Log.i("Rita", "isInvited observe: $it")
            if (it == true) {
                Toast.makeText(context, "The person is collaborator already.", Toast.LENGTH_LONG)
                    .show()
            }
        })


        viewModel.updateSuccess.observe(viewLifecycleOwner, {
            Log.i("Rita", "isInvited.observe: $it")
            if (it == true) {
                Toast.makeText(context, "Invite Success", Toast.LENGTH_LONG).show()
                viewModel.doneUpdate()
                dismiss()
            }
        })


        return binding.root


    }
}