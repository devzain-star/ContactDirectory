package com.phonebook.sync.contactdirectory.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.phonebook.sync.contactdirectory.R
import com.phonebook.sync.contactdirectory.data.local.entities.Template
import com.phonebook.sync.contactdirectory.databinding.ItemSimpleOptionBinding
import com.phonebook.sync.contactdirectory.databinding.ItemTemplateBinding

class TemplateAdapter(
    private var items: List<Template>,
    private val onDeleteConfirmed: (Template) -> Unit
) : RecyclerView.Adapter<TemplateAdapter.TemplateViewHolder>() {

    inner class TemplateViewHolder(val binding: ItemTemplateBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemplateViewHolder {
        val binding = ItemTemplateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TemplateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TemplateViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            tvOptionTitle.text = item.optionTitle
            tvMessagePreview.text = item.message

            btnDelete.setOnClickListener {
                showDeleteDialog(holder.itemView.context, item)
            }
        }
    }

    private fun showDeleteDialog(context: Context, template: Template) {
        MaterialAlertDialogBuilder(context)
            .setTitle("Delete Template")
            .setMessage("Are you sure you want to delete this template?")
            .setIcon(R.drawable.ic_warning)
            .setPositiveButton("Delete") { _, _ ->
                onDeleteConfirmed(template)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun getItemCount() = items.size

    fun updateList(newList: List<Template>) {
        items = newList
        notifyDataSetChanged()
    }
}
