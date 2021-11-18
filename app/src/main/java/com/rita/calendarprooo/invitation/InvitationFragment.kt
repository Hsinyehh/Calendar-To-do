package com.rita.calendarprooo.invitation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.rita.calendarprooo.R
import com.rita.calendarprooo.data.Plan
import com.rita.calendarprooo.databinding.FragmentInvitationBinding
import com.rita.calendarprooo.ext.getVmFactory
import com.rita.calendarprooo.home.TodoAdapter
import com.rita.calendarprooo.sort.HomeSortViewModel


class InvitationFragment : Fragment() {
    private val viewModel by viewModels<InvitationViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //layout binding
        val binding: FragmentInvitationBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_invitation, container, false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner


        //invitation adapter
        val adapter = InvitationAdapter(viewModel)
        binding.invitationList.adapter = adapter

        viewModel.invitationList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            Log.i("Rita", "invitationList.observe: $it")
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        })

        //invitation for Category adapter
        val categoryAdapter = InvitationCategoryAdapter(viewModel)
        binding.invitationCategoryList.adapter = categoryAdapter

        viewModel.user.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            Log.i("Rita", "user.observe: $it")
            it?.let {
                viewModel.readInvitation()
                viewModel.invitationForCategoryList.value = it.invitationList
            }
        })

        viewModel.invitationForCategoryList.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer {
                Log.i("Rita", "invitationForCategoryList.observe: $it")
                it?.let {
                    categoryAdapter.submitList(it)
                    categoryAdapter.notifyDataSetChanged()
                }
            })

        // Accept Category invitation

        viewModel.invitationListUpdated.observe(viewLifecycleOwner, Observer {
            Log.i("Rita", "invitationListUpdated observe- $it")
            it?.let{
                viewModel.updateInvitationList(it)
            }
        })

        viewModel.startToUpdate.observe(viewLifecycleOwner, Observer {
            Log.i("Rita", "updateSuccess observe- $it")
            it?.let{
                viewModel.getPlans()
            }
        })


        viewModel.addCollaboratorForPlan.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            Log.i("Rita", "addCollaboratorForPlan observe- $it")
            if (it == true) {
                viewModel.addCollaboratorForPlan()
            }
        })

        viewModel.updatePlan.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            Log.i("Rita", "updatePlan observe- $it")
            if (it == true) {
                viewModel.plans.value?.let {
                    for (plan in it) {
                        viewModel.updatePlan(plan)
                    }
                }
                viewModel.updateCategories.value = true
            }
        })

        viewModel.updateCategories.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            Log.i("Rita", "updateCategories observe- $it")
            if (it == true) {
                viewModel.updateCategories()
            }
        })

        viewModel.updateCategories.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            Log.i("Rita", "updateCategories observe- $it")
            if (it == true) {
                viewModel.updateCategories()
            }
        })

        viewModel.updateCategoriesForUser.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            Log.i("Rita", "updateCategoriesForUser.observe- $it")
            if (it == true) {
                viewModel.updateCategoriesForUser()
                viewModel.doneWritten()
            }
        })



        return binding.root
    }
}