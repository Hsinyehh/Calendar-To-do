package com.rita.calendarprooo.home

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rita.calendarprooo.data.Check
import com.rita.calendarprooo.data.Plan
import com.rita.calendarprooo.databinding.ItemScheduleBinding
import com.rita.calendarprooo.databinding.ItemTodoBinding
import com.rita.calendarprooo.edit.EditViewModel

class TodoAdapter (val viewModel: HomeViewModel) : ListAdapter<Plan,
        TodoAdapter.ViewHolder>(TodoDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        Log.i("Rita","TodoAdapter dataSetChanged")

        //checkAdapter
        val adapter=CheckAdapter(viewModel)
        holder.binding.scheduleCheckList.adapter=adapter
        adapter.submitList(item.checkList)
        adapter.notifyDataSetChanged()

        holder.binding.scheduleOverview.setOnClickListener {
            if(holder.binding.scheduleDetail.visibility== View.GONE){
                holder.binding.scheduleDetail.visibility= View.VISIBLE
            }
            else if(holder.binding.scheduleDetail.visibility== View.VISIBLE){
                holder.binding.scheduleDetail.visibility= View.GONE
            }
            notifyDataSetChanged()
        }

        holder.binding.root.setOnClickListener {
            if(holder.binding.scheduleDetail.visibility== View.VISIBLE){
                holder.binding.scheduleDetail.visibility= View.GONE
            }
            notifyDataSetChanged()
        }

        holder.binding.scheduleBtnUncheck.setOnClickListener {
            if(item.isToDoListDone){
                item.isToDoListDone=false
            }
            else if(!item.isToDoListDone){
                item.isToDoListDone=true
            }
            viewModel.getPlanAndChangeStatus(item)
            notifyDataSetChanged()
        }
        holder.bind(item)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ItemTodoBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Plan) {
            binding.plan = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemTodoBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }

}


class TodoDiffCallback : DiffUtil.ItemCallback<Plan>() {
    override fun areItemsTheSame(oldItem: Plan, newItem: Plan): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Plan, newItem: Plan): Boolean {
        return oldItem == newItem
    }
}
