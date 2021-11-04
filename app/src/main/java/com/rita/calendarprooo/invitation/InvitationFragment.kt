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


        viewModel.readInvitation()


        //invitation adapter
        val adapter = InvitationAdapter(viewModel)
        binding.invitationList.adapter = adapter

        viewModel.invitationList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            Log.i("Rita","invitationList.observe: $it")
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        })

        return binding.root
    }
}