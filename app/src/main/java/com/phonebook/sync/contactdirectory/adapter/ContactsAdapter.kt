package com.phonebook.sync.contactdirectory.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.phonebook.sync.contactdirectory.databinding.ItemContactBinding
import com.phonebook.sync.contactdirectory.models.ContactEntry
import androidx.core.graphics.toColorInt

class ContactsAdapter(
    private var items: List<ContactEntry>,
    private val listener: OnContactClickListener
) : RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>() {

    interface OnContactClickListener {
        fun onCallClick(entry: ContactEntry)
        fun onMessageClick(entry: ContactEntry)
        fun onWhatsAppClick(entry: ContactEntry)
        fun onCopyClick(entry: ContactEntry)
    }

    inner class ContactViewHolder(val binding: ItemContactBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ItemContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val entry = items[position]
        val context = holder.binding.root.context
        with(holder.binding) {
            val formattedName = entry.name.split(" ")
                .joinToString(" ") { it.replaceFirstChar { ch -> ch.uppercaseChar() } }
            tvName.text = formattedName
            tvNumber.text = entry.number
            tvCallCount.text = if (entry.callCount > 0) "Calls: ${entry.callCount}" else ""

            val initials = formattedName.split(" ").take(2).joinToString("") { it.take(1).uppercase() }
            tvAvatar.text = initials

            val colors = listOf("#81C784", "#64B5F6", "#BA68C8", "#4DB6AC", "#FFD54F", "#E57373", "#A1887F")
            val randomColor = colors[position % colors.size].toColorInt()
            val bg = tvAvatar.background.mutate() as GradientDrawable
            bg.setColor(randomColor)
            tvAvatar.background = bg

            btnCall.setOnClickListener { listener.onCallClick(entry) }
            btnMessage.setOnClickListener { listener.onMessageClick(entry) }
            btnWhatsapp.setOnClickListener { listener.onWhatsAppClick(entry) }
            btnCopy.setOnClickListener { listener.onCopyClick(entry) }
        }
    }

    override fun getItemCount() = items.size

    fun updateList(newList: List<ContactEntry>) {
        items = newList
        notifyDataSetChanged()
    }
}