package com.phonebook.sync.contactdirectory.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.phonebook.sync.contactdirectory.data.local.entities.ResponseOption

class SimpleTextAdapter(
    private val items: List<ResponseOption>,
    private val onClick: (ResponseOption) -> Unit
) : RecyclerView.Adapter<SimpleTextAdapter.VH>() {

    inner class VH(val tv: TextView) : RecyclerView.ViewHolder(tv)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val tv = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false) as TextView
        tv.setPadding(20, 24, 20, 24)
        return VH(tv)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.tv.text = item.title
        holder.tv.setOnClickListener { onClick(item) }
    }

    override fun getItemCount() = items.size
}
