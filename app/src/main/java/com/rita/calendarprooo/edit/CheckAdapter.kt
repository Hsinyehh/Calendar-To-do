package com.rita.calendarprooo.edit


import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rita.calendarprooo.data.Check
import com.rita.calendarprooo.databinding.ItemCheckBinding
import com.rita.calendarprooo.login.UserManager
import java.util.*

class CheckAdapter(val viewModel: EditViewModel) : ListAdapter<Check,
        CheckAdapter.ViewHolder>(CheckDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.checklistImage.setOnClickListener {
            if (item.isDone) {
                item.isDone = false
                item.done_time = null
                item.doner = null
            } else if (!item.isDone) {
                item.isDone = true
                item.done_time = Calendar.getInstance().timeInMillis
                item.doner = UserManager.user.value?.name
            }
            notifyDataSetChanged()
        }
        holder.binding.checklistBtnRemoved.setOnClickListener {
            Log.i("Rita", "edit remove btn clicked")
            viewModel.checkListTextRemoved(position)
            notifyDataSetChanged()
        }

        holder.bind(item)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ItemCheckBinding) :
        RecyclerView.ViewHolder(binding.root) {

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
