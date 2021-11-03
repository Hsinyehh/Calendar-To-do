package com.rita.calendarprooo.home

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rita.calendarprooo.data.Check
import com.rita.calendarprooo.data.Plan
import com.rita.calendarprooo.databinding.ItemDoneBinding
import com.rita.calendarprooo.databinding.ItemScheduleBinding
import com.rita.calendarprooo.edit.EditViewModel

class DoneAdapter (val viewModel: HomeViewModel) : ListAdapter<Plan,
        DoneAdapter.ViewHolder>(DoneDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)


        //checkAdapter
        val adapter=CheckAdapter(viewModel)
        holder.binding.scheduleCheckList.adapter=adapter
        adapter.submitList(item.checkList)
        adapter.notifyDataSetChanged()

        holder.binding.scheduleOverview.setOnClickListener {
            viewModel.changeDoneView(position)
        }

        /*holder.binding.root.setOnClickListener {
            viewModel.changeDoneView(position)
        }*/

        holder.binding.scheduleBtnCheck.setOnClickListener {
            if(item.isToDoListDone){
                item.isToDoListDone=false
            }
            else if(!item.isToDoListDone){
                item.isToDoListDone=true
            }
            viewModel.getPlanAndChangeStatus(item)

            //get viewList again
            viewModel.startToGetViewListForTodo()
            notifyDataSetChanged()
        }

        holder.binding.scheduleImageEdit.setOnClickListener {
            viewModel.startNavigateToEditByPlan(item)
        }

        holder.bind(item, position,viewModel)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ItemDoneBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Plan, position: Int ,viewModel: HomeViewModel) {
            binding.plan = item
            binding.position = position
            binding.viewModel = viewModel
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemDoneBinding.inflate(layoutInflater, parent, false)
                binding.lifecycleOwner = parent.context as LifecycleOwner
                return ViewHolder(binding)
            }
        }
    }

}


class DoneDiffCallback : DiffUtil.ItemCallback<Plan>() {
    override fun areItemsTheSame(oldItem: Plan, newItem: Plan): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Plan, newItem: Plan): Boolean {
        return oldItem == newItem
    }
}
