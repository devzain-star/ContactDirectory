package com.phonebook.sync.contactdirectory.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phonebook.sync.contactdirectory.databinding.ItemCallLogBinding
import com.phonebook.sync.contactdirectory.utils.CallEntry

class CallLogAdapter(private var items: List<CallEntry>) :
    RecyclerView.Adapter<CallLogAdapter.CallViewHolder>() {

    inner class CallViewHolder(val binding: ItemCallLogBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallViewHolder {
        val binding = ItemCallLogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CallViewHolder(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: CallViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            tvName.text = item.name
            tvNumber.text = item.number
            tvType.text = item.type
            tvDate.text = item.date
            tvDuration.text = item.duration
        }
    }

    fun updateList(newList: List<CallEntry>) {
        items = newList
        notifyDataSetChanged()
    }
}
