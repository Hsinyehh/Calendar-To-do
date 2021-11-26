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
    ): View {

        val binding: DialogInviteCategoryBinding = DataBindingUtil.inflate(
            inflater, R.layout.dialog_invite_category, container, false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // safe args
        viewModel.category.value = InviteCategoryDialogArgs.fromBundle(requireArguments()).category
        viewModel.user.value = InviteCategoryDialogArgs.fromBundle(requireArguments()).user
        viewModel.categoryPosition.value =
            InviteCategoryDialogArgs.fromBundle(requireArguments()).position


        binding.inviteBtnCancel.setOnClickListener {
            dismiss()
        }


        viewModel.isInputBlank.observe(viewLifecycleOwner, {
            Log.i("Rita", "isInputBlank observe: $it")
            it?.let {
                Toast.makeText(activity, "The input can't be blank!", Toast.LENGTH_LONG).show()
            }
            viewModel.isUserNotExist.value = null
        })


        viewModel.userTobeInvited.observe(viewLifecycleOwner, { user ->
            Log.i("Rita", "userTobeInvited observe: $user")
            user?.let {
                if (user.email != "") {
                    viewModel.createInvitation()
                } else {
                    viewModel.isUserNotExist.value = true
                }
            }
        })


        viewModel.isUserNotExist.observe(viewLifecycleOwner, {
            Log.i("Rita", "isUserNotExist observe: $it")
            it?.let {
                Toast.makeText(activity, "The user doesn't use the app yet.", Toast.LENGTH_LONG)
                    .show()
                viewModel.isUserNotExist.value = null
            }
        })


        viewModel.invitationList.observe(viewLifecycleOwner, {
            Log.i("Rita", "invitationList observe: $it")
            it?.let {
                viewModel.updateInvitation()
            }
        })


        viewModel.isInvited.observe(viewLifecycleOwner, {
            Log.i("Rita", "isInvited observe: $it")
            if (it == true) {
                Toast.makeText(activity, "The person is invited already.", Toast.LENGTH_LONG).show()
                viewModel.isInvited.value = null
            }
        })


        viewModel.updateSuccess.observe(viewLifecycleOwner, {
            Log.i("Rita", "updateSuccess observe: $it")
            if (it == true) {
                Toast.makeText(context, "Invite Success", Toast.LENGTH_LONG).show()
                dismiss()
                viewModel.doneUpdate()
            }
        })

        return binding.root

    }
}