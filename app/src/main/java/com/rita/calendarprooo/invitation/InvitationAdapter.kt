package com.rita.calendarprooo.invitation

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rita.calendarprooo.data.Plan
import com.rita.calendarprooo.databinding.ItemInvitationBinding


class InvitationAdapter(val viewModel: InvitationViewModel) : ListAdapter<Plan,
        InvitationAdapter.ViewHolder>(InvitationDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.binding.invitationBtnAccept.setOnClickListener {
            viewModel.acceptOrDeclineInvitation(item, true)
        }

        holder.binding.invitationBtnDecline.setOnClickListener {
            viewModel.acceptOrDeclineInvitation(item, false)
        }

        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ItemInvitationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Plan) {
            binding.plan = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemInvitationBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }

}


class InvitationDiffCallback : DiffUtil.ItemCallback<Plan>() {
    override fun areItemsTheSame(oldItem: Plan, newItem: Plan): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Plan, newItem: Plan): Boolean {
        return oldItem == newItem
    }
}
