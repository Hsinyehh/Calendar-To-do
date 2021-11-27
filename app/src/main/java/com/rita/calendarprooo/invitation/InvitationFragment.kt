package com.rita.calendarprooo.invitation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.rita.calendarprooo.R
import com.rita.calendarprooo.databinding.FragmentInvitationBinding
import com.rita.calendarprooo.ext.getVmFactory


class InvitationFragment : Fragment() {

    private val viewModel by viewModels<InvitationViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding: FragmentInvitationBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_invitation, container, false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner


        // invitation adapter
        val adapter = InvitationAdapter(viewModel)
        binding.invitationList.adapter = adapter


        viewModel.invitationListReset.observe(viewLifecycleOwner, {
            Log.i("Rita", "invitationListReset observe: $it")

            it?.let {
                viewModel.invitationList.observe(viewLifecycleOwner, { list ->
                    Log.i("Rita", "invitationList observe: $list")
                    adapter.submitList(list)
                    adapter.notifyDataSetChanged()

                    viewModel.convertToSize(list)
                })
                viewModel.invitationListReset.value = null
            }
        })


        // invitation for Category adapter
        val categoryAdapter = InvitationCategoryAdapter(viewModel)
        binding.invitationCategoryList.adapter = categoryAdapter


        viewModel.user.observe(viewLifecycleOwner,  {
            Log.i("Rita", "user observe: $it")
            it?.let {
                viewModel.getInvitations()
                viewModel.invitationForCategoryList.value = it.invitationList
            }
        })


        viewModel.invitationForCategoryList.observe(viewLifecycleOwner, {
                Log.i("Rita", "invitationForCategoryList observe: $it")
                it?.let {
                    categoryAdapter.submitList(it)
                    categoryAdapter.notifyDataSetChanged()
                }
        })


        // Accept Category invitation
        viewModel.invitationListUpdated.observe(viewLifecycleOwner,  {
            Log.i("Rita", "invitationListUpdated observe: $it")
            it?.let {
                viewModel.updateUserForInvitationList(it)
            }
        })


        viewModel.startToUpdate.observe(viewLifecycleOwner,  {
            Log.i("Rita", "startToUpdate observe: $it")
            it?.let {
                viewModel.getPlans()
            }
        })


        viewModel.plans.observe(viewLifecycleOwner,  {
            Log.i("Rita", "plans observe: $it")
            it?.let{
                viewModel.addCollaboratorForPlan()
            }
        })


        viewModel.plansUpdate.observe(viewLifecycleOwner, {
            Log.i("Rita", "plansUpdate observe: $it")
            it?.let {
                viewModel.updateCollaboratorForPlans(it)
            }
        })


        viewModel.renewCategories.observe(viewLifecycleOwner, {
            Log.i("Rita", "updateCategories observe: $it")
            if (it == true) {
                viewModel.renewCategories()
            }
        })


        viewModel.updateCategoriesForUser.observe(viewLifecycleOwner,  {
            Log.i("Rita", "updateCategoriesForUser observe: $it")
            if (it == true) {
                viewModel.updateCategoriesForUser()
                viewModel.doneWritten()
            }
        })


        return binding.root


    }
}