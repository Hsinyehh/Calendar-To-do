package com.rita.calendarprooo.invitation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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


        //FAKE DATA
        val plan2= Plan(
            id="1",
            title="Meeting",
            description="for product development",
            location="Taipei",1636029000000,1636029000000,null,
            null,null,
            "Job",0,owner="lisa@fake.com")

        val plan3= Plan(
            id="1",
            title="Discussion",
            description="for product development",
            location="Taipei",1636029000000,1636029000000,null,
            null,null,
            "Job",0,owner="lisa@fake.com")


        val planList= mutableListOf<Plan>(plan2,plan3)


        //invitation adapter
        val adapter = InvitationAdapter(viewModel)
        binding.invitationList.adapter = adapter
        adapter.submitList(planList)


        /*viewModel.todoList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            Log.i("Rita","todoList.observe: $it")
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        })*/

        return binding.root
    }
}