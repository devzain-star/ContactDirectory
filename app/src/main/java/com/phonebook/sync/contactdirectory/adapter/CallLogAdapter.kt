package com.phonebook.sync.contactdirectory.adapter

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phonebook.sync.contactdirectory.databinding.ItemCallLogBinding
import com.phonebook.sync.contactdirectory.utils.CallEntry
import java.text.SimpleDateFormat
import java.util.Locale

class CallLogAdapter(
    private var items: List<CallEntry>,
    private val listener: OnCallLogClickListener
) : RecyclerView.Adapter<CallLogAdapter.CallViewHolder>() {

    interface OnCallLogClickListener {
        fun onCallClick(entry: CallEntry)
        fun onWhatsAppClick(entry: CallEntry)
        fun onAddNoteClick(entry: CallEntry)
        fun onItemClick(entry: CallEntry)
    }

    inner class CallViewHolder(val binding: ItemCallLogBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallViewHolder {
        val binding = ItemCallLogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CallViewHolder(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: CallViewHolder, position: Int) {
        val item = items[position]
        val context = holder.binding.root.context
        with(holder.binding) {
            val formattedName = item.name?.split(" ")
                ?.joinToString(" ") { it.replaceFirstChar { ch -> ch.uppercaseChar() } }
                ?: "Unknown"
            tvName.text = formattedName

            val initials = formattedName.split(" ").take(2).joinToString("") { it.take(1).uppercase() }
            tvAvatar.text = initials

            val colors = listOf(
                "#F06292", "#64B5F6", "#81C784", "#FFD54F",
                "#BA68C8", "#4DB6AC", "#E57373", "#A1887F"
            )
            val randomColor = Color.parseColor(colors[position % colors.size])
            (tvAvatar.background as GradientDrawable).setColor(randomColor)

            when (item.type.lowercase(Locale.getDefault())) {
                "incoming" -> tvType.setTextColor(Color.parseColor("#4CAF50"))
                "outgoing" -> tvType.setTextColor(Color.parseColor("#2196F3"))
                "missed" -> tvType.setTextColor(Color.parseColor("#E53935"))
                else -> tvType.setTextColor(Color.GRAY)
            }

            tvNumber.text = item.number
            tvType.text = item.type

            try {
                val input = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.getDefault())
                val output = SimpleDateFormat("MMM dd, yyyy • h:mm a", Locale.getDefault())
                val parsed = input.parse(item.date)
                tvDate.text = output.format(parsed!!)
            } catch (e: Exception) {
                tvDate.text = item.date
            }

            val durSeconds = item.duration.replace(" sec", "").toIntOrNull() ?: 0
            val formattedDuration = when {
                durSeconds >= 60 -> String.format("(%.0f min)", durSeconds / 60.0)
                durSeconds > 0 -> "(${durSeconds}s)"
                else -> "(0s)"
            }
            tvDuration.text = formattedDuration

            btnCall.setOnClickListener { listener.onCallClick(item) }
            btnWhatsapp.setOnClickListener { listener.onWhatsAppClick(item) }
            tvAddNote.setOnClickListener { listener.onAddNoteClick(item) }
            root.setOnClickListener { listener.onItemClick(item) }
        }
    }

    fun updateList(newList: List<CallEntry>) {
        items = newList
        notifyDataSetChanged()
    }
}