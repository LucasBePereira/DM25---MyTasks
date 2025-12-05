package com.lucas.mytasks.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.lucas.mytasks.R
import com.lucas.mytasks.databinding.ListItemBinding
import com.lucas.mytasks.entity.Task
import com.lucas.mytasks.listener.ClickListener
import com.lucas.mytasks.entity.TaskStatus

class ListAdapter(
    private val context: Context,
    private val emptyMessage: TextView,
    private val listener: ClickListener
) : RecyclerView.Adapter<ItemViewHolder>() {

    private val items = mutableListOf<Task>()

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ItemViewHolder {
        val binding = ListItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return ItemViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val task = items[position]

        // 1. Continua chamando seu método que configura os dados (textos, cliques, etc.)
        holder.setData(task)

        // 2. Adiciona a lógica para mudar a cor do card
        val cardView = holder.itemView // Assumindo que a raiz do seu `list_item.xml` é um CardView

        // Pega a cor correspondente ao status da tarefa
        val colorResId = when (task.getStatus()) {
            TaskStatus.OVERDUE -> R.color.red
            TaskStatus.DUE_TODAY -> R.color.yellow
            TaskStatus.COMPLETED -> R.color.green
            TaskStatus.IN_PROGRESS -> R.color.blue
        }

        // Aplica a cor de fundo
        cardView.setBackgroundColor(ContextCompat.getColor(context, colorResId))
    }

    override fun getItemCount() = items.size

    fun getItem(position: Int) = items[position]

    fun setData(data: List<Task>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()

        checkEmptyList()
    }

    fun removeItem(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)

        checkEmptyList()
    }

    private fun checkEmptyList() {
        if (items.isEmpty()) {
            emptyMessage.visibility = View.VISIBLE
            emptyMessage.text = ContextCompat.getString(
                context, R.string.empty_list
            )
        } else {
            emptyMessage.visibility = View.INVISIBLE
        }
    }
}