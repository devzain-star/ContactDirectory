package com.phonebook.sync.contactdirectory.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.phonebook.sync.contactdirectory.R
import com.phonebook.sync.contactdirectory.data.local.entities.ResponseOption
import com.phonebook.sync.contactdirectory.databinding.ItemSimpleOptionBinding

class ResponseOptionAdapter(
    private var items: List<ResponseOption>,
    private val onDeleteConfirmed: (ResponseOption) -> Unit
) : RecyclerView.Adapter<ResponseOptionAdapter.OptionViewHolder>() {

    inner class OptionViewHolder(val binding: ItemSimpleOptionBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionViewHolder {
        val binding = ItemSimpleOptionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OptionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OptionViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            tvTitle.text = item.title
            btnDelete.setOnClickListener {
                showDeleteDialog(holder.itemView.context, item)
            }
        }
    }

    private fun showDeleteDialog(context: Context, option: ResponseOption) {
        MaterialAlertDialogBuilder(context)
            .setTitle("Delete Option")
            .setMessage("Are you sure you want to delete \"${option.title}\"?")
            .setIcon(R.drawable.ic_warning)
            .setPositiveButton("Delete") { _, _ ->
                onDeleteConfirmed(option)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun getItemCount() = items.size

    fun updateList(newList: List<ResponseOption>) {
        items = newList
        notifyDataSetChanged()
    }
}
