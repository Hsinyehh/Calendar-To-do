package com.rita.calendarprooo.invitation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.rita.calendarprooo.R
import com.rita.calendarprooo.data.Plan
import com.rita.calendarprooo.databinding.FragmentInvitationBinding
import com.rita.calendarprooo.home.TodoAdapter


class InvitationFragment : Fragment() {
    private val viewModel: InvitationViewModel by lazy {
        ViewModelProvider(this).get(InvitationViewModel::class.java)
    }

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


        viewModel.readInvitation()


        //invitation adapter
        val adapter = InvitationAdapter(viewModel)
        binding.invitationList.adapter = adapter

        viewModel.invitationList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            Log.i("Rita","invitationList.observe: $it")
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        })

        //invitation for Category adapter
        val categoryAdapter = InvitationCategoryAdapter(viewModel)
        binding.invitationCategoryList.adapter = categoryAdapter

        viewModel.user.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            Log.i("Rita","user.observe: $it")
            it?.let{
                viewModel.invitationForCategoryList.value = it.invitationList
            }
        })

        viewModel.invitationForCategoryList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            Log.i("Rita","invitationForCategoryList.observe: $it")
            it?.let{
                categoryAdapter.submitList(it)
                categoryAdapter.notifyDataSetChanged()
            }
        })

        // Accept invitation Category
        viewModel.invitationAccepted.observe(viewLifecycleOwner, androidx.lifecycle.Observer{
            it?.let{
                viewModel.getPlans(it)
            }
        })

        viewModel.addCollaboratorForPlan.observe(viewLifecycleOwner, androidx.lifecycle.Observer{
            Log.i("Rita","addCollaboratorForPlan observe- $it")
            if(it==true){
                viewModel.addCollaboratorForPlan()
            }
        })

        viewModel.updatePlan.observe(viewLifecycleOwner, androidx.lifecycle.Observer{
            Log.i("Rita","updatePlan observe- $it")
            if(it==true){
                viewModel.plans.value?.let{
                    for(plan in it){
                        viewModel.updatePlan(plan)
                    }
                }
                viewModel.getUsers.value = true
            }
        })

        viewModel.getUsers.observe(viewLifecycleOwner, androidx.lifecycle.Observer{
            Log.i("Rita","getUsers observe- $it")
            if(it==true){
                viewModel.getUsers()
            }
        })

        viewModel.addCollaboratorForUser.observe(viewLifecycleOwner, androidx.lifecycle.Observer{
            Log.i("Rita","addCollaboratorForUser observe- $it")
            if(it==true){
                viewModel.addCollaboratorForUser()
            }
        })

        viewModel.updateCategoryForUser.observe(viewLifecycleOwner, androidx.lifecycle.Observer{
            Log.i("Rita","updateCategoryForUser observe- $it")
            if(it==true){
                viewModel.userList.value?.let{
                    for(user in it){
                        viewModel.updateCategoryForUser(user)
                    }
                }
                viewModel.updateSuccess.value = true
            }
        })


        return binding.root
    }
}