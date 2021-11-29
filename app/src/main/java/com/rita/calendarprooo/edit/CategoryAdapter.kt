package com.rita.calendarprooo.edit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rita.calendarprooo.Util.Logger
import com.rita.calendarprooo.data.Category
import com.rita.calendarprooo.databinding.ItemCategoryBinding


class CategoryAdapter(val viewModel: EditViewModel) : ListAdapter<Category,
        CategoryAdapter.ViewHolder>(CategoryDiffCallback()) {

    var selectedItemPosition: Int = -1

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.categoryView.setOnClickListener {
            Logger.i("$selectedItemPosition")

            viewModel.changeCategory(position, selectedItemPosition)
            selectedItemPosition = position
            notifyDataSetChanged()
        }
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Category) {
            binding.category = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemCategoryBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }

}


class CategoryDiffCallback : DiffUtil.ItemCallback<Category>() {
    override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem == newItem
    }
}
