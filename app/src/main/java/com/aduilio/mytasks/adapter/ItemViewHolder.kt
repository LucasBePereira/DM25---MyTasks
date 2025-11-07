package com.aduilio.mytasks.adapter

import androidx.recyclerview.widget.RecyclerView
import com.aduilio.mytasks.R
import com.aduilio.mytasks.databinding.ListItemBinding
import com.aduilio.mytasks.entity.Task
import com.aduilio.mytasks.listener.ClickListener

class ItemViewHolder(
    private val binding: ListItemBinding,
    private val listener: ClickListener
) : RecyclerView.ViewHolder(binding.root) {

    fun setData(task: Task) {
        binding.tvTitle.text = task.title

        binding.root.setOnCreateContextMenuListener { menu, _, _ ->
            menu.add(R.string.mark_completed).setOnMenuItemClickListener {
                task.id?.let { id -> listener.onComplete(task.id) }
                true
            }
        }
    }
}