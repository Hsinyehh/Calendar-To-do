package com.rita.calendarprooo.invitation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rita.calendarprooo.data.Invitation
import com.rita.calendarprooo.databinding.ItemInvitationCategoryBinding


class InvitationCategoryAdapter(val viewModel: InvitationViewModel) : ListAdapter<Invitation,
        InvitationCategoryAdapter.ViewHolder>(InvitationCategoryDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.binding.invitationBtnAccept.setOnClickListener {
            viewModel.updateInvitation(item, true)
        }

        holder.binding.invitationBtnDecline.setOnClickListener {
            viewModel.updateInvitation(item, false)
        }

        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ItemInvitationCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Invitation) {
            binding.invitation = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemInvitationCategoryBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }

}


class InvitationCategoryDiffCallback : DiffUtil.ItemCallback<Invitation>() {
    override fun areItemsTheSame(oldItem: Invitation, newItem: Invitation): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Invitation, newItem: Invitation): Boolean {
        return oldItem == newItem
    }
}
