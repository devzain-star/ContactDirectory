package com.phonebook.sync.contactdirectory.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.phonebook.sync.contactdirectory.data.local.entities.ResponseOption
import com.phonebook.sync.contactdirectory.databinding.ItemTextOptionBinding

class ResponseOptionListAdapter(
    private val onClick: (ResponseOption) -> Unit
) : ListAdapter<ResponseOption, ResponseOptionListAdapter.VH>(DiffCallback()) {

    inner class VH(val binding: ItemTextOptionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemTextOptionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val option = getItem(position)
        holder.binding.tvTitle.text = option.title
        holder.binding.root.setOnClickListener { onClick(option) }
    }

    class DiffCallback : DiffUtil.ItemCallback<ResponseOption>() {
        override fun areItemsTheSame(oldItem: ResponseOption, newItem: ResponseOption) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ResponseOption, newItem: ResponseOption) = oldItem == newItem
    }
}
