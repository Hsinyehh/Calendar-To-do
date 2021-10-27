package com.rita.calendarprooo.home

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rita.calendarprooo.data.Check
import com.rita.calendarprooo.databinding.ItemCheckBinding
import com.rita.calendarprooo.databinding.ItemScheduleBinding
import com.rita.calendarprooo.edit.EditViewModel

class CheckAdapter (val viewModel: HomeViewModel) : ListAdapter<Check,
        CheckAdapter.ViewHolder>(CheckDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        Log.i("Rita","CheckAdapter dataSetChanged")

        holder.binding.checklistImage.setOnClickListener {
            if(item.isDone){
                item.isDone=false
            }
            else if(!item.isDone){
                item.isDone=true
            }
            viewModel.getPlanAndChangeStatus(item, position)
            notifyDataSetChanged()
        }
        holder.binding.checklistBtnRemoved.setOnClickListener {
            Log.i("Rita","home remove btn clicked")
            viewModel.getPlanAndRemoveItem(item,position)
            //notifyItemRemoved (position)
            //notifyItemRangeChanged(position, viewModel.checkListSize.value!!)
            notifyDataSetChanged()
        }

        holder.bind(item)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ItemCheckBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Check) {
            binding.check = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemCheckBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }

}


class CheckDiffCallback : DiffUtil.ItemCallback<Check>() {
    override fun areItemsTheSame(oldItem: Check, newItem: Check): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Check, newItem: Check): Boolean {
        return oldItem == newItem
    }
}
