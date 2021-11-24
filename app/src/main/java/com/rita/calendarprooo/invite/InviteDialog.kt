package com.rita.calendarprooo.invite

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.rita.calendarprooo.R
import com.rita.calendarprooo.databinding.DialogInviteBinding


class InviteDialog : DialogFragment() {
    private val viewModel: InviteViewModel by lazy {
        ViewModelProvider(this).get(InviteViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // layout binding
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


        viewModel.invitation.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            Log.i("Rita", "invitation observe- $it")
            it?.let {
                viewModel.writeInvitation()
            }
        })


        viewModel.isInvited.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            Log.i("Rita", "isInvited observe- $it")
            if (it == true) {
                Toast.makeText(context, "The person is invited already.", Toast.LENGTH_LONG).show()
            }
        })

        viewModel.isCollaborator.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            Log.i("Rita", "isInvited observe- $it")
            if (it == true) {
                Toast.makeText(context, "The person is collaborator already.", Toast.LENGTH_LONG)
                    .show()
            }
        })

        viewModel.updateSuccess.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            Log.i("Rita", "isInvited observe- $it")
            if (it == true) {
                Toast.makeText(context, "Invite success", Toast.LENGTH_LONG).show()
                viewModel.doneWritten()
                dismiss()
            }
        })

        return binding.root
    }
}