package com.rita.calendarprooo.home

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rita.calendarprooo.data.Plan
import com.rita.calendarprooo.databinding.ItemTodoBinding
import com.rita.calendarprooo.login.UserManager
import java.util.*

class TodoAdapter(val viewModel: HomeViewModel) : ListAdapter<Plan,
        TodoAdapter.ViewHolder>(TodoDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.i("Rita", "todo onBindViewHolder position=$position")
        val item = getItem(position)


        // checkAdapter
        val adapter = CheckAdapter(viewModel)
        holder.binding.scheduleCheckList.adapter = adapter
        adapter.submitList(item.checkList)
        adapter.notifyDataSetChanged()

        holder.binding.scheduleOverview.setOnClickListener {
            Log.i("Rita", "todoOverview click position=$position")
            viewModel.changeTodoView(position)
        }

        holder.binding.scheduleBtnUncheck.setOnClickListener {
            if (!item.isToDoListDone) {
                item.isToDoListDone = true
                item.done_time = Calendar.getInstance().timeInMillis
                item.doner = UserManager.user.value?.name
            } else if (item.isToDoListDone) {
                item.isToDoListDone = false
                item.done_time = null
                item.doner = null
            }
            viewModel.updatePlanDoneStatus(item)

            // get viewList again
            viewModel.startToGetViewListForTodo()
            notifyDataSetChanged()
        }

        holder.binding.scheduleImageEdit.setOnClickListener {
            viewModel.startNavigateToEditByPlan(item)
        }

        holder.binding.scheduleImageInvite.setOnClickListener {
            viewModel.startNavigateToInvite(item)
        }

        holder.binding.scheduleImageAlarm.setOnClickListener {
            viewModel.startNavigateToAlarm(item)
        }

        holder.bind(item, position, viewModel)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ItemTodoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Plan, position: Int, viewModel: HomeViewModel) {
            binding.plan = item
            binding.position = position
            binding.viewModel = viewModel
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemTodoBinding.inflate(layoutInflater, parent, false)
                binding.lifecycleOwner = parent.context as LifecycleOwner
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
