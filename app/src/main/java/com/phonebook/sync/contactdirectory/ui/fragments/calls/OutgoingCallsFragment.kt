package com.phonebook.sync.contactdirectory.ui.fragments.calls

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.CallLog
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.phonebook.sync.contactdirectory.R
import com.phonebook.sync.contactdirectory.adapter.CallLogAdapter
import com.phonebook.sync.contactdirectory.databinding.FragmentOutgoingCallsBinding
import com.phonebook.sync.contactdirectory.utils.CallEntry
import com.phonebook.sync.contactdirectory.utils.CallLogHelper
import com.phonebook.sync.contactdirectory.utils.PermissionManager

class OutgoingCallsFragment : Fragment() {

    private var _binding: FragmentOutgoingCallsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CallLogAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOutgoingCallsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadCallLogs()
    }

    private fun setupRecyclerView() {
        adapter = CallLogAdapter(emptyList(), object : CallLogAdapter.OnCallLogClickListener {

            override fun onCallClick(entry: CallEntry) {
                try {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:${entry.number}")
                    }
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Unable to open dialer", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onWhatsAppClick(entry: CallEntry) {
                try {
                    val packageManager = requireContext().packageManager
                    val uri = "https://wa.me/${
                        entry.number?.replace("+", "")?.replace(" ", "")
                    }".toUri()
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    intent.setPackage("com.whatsapp")
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivity(intent)
                    } else {
                        Toast.makeText(requireContext(), "WhatsApp not installed", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Unable to open WhatsApp", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onAddNoteClick(entry: CallEntry) {
                // TODO: Open note input bottom sheet and make API call
                Toast.makeText(requireContext(), "TODO: Add note for ${entry.name}", Toast.LENGTH_SHORT).show()
            }

            override fun onItemClick(entry: CallEntry) {
                // Optionally: show details sheet later
            }
        })

        binding.recyclerViewCalls.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewCalls.adapter = adapter
    }

    private fun loadCallLogs() {
        if (PermissionManager.hasAllPermissions(requireActivity())) {
            val logs = CallLogHelper.getCallLogs(requireContext(), CallLog.Calls.OUTGOING_TYPE)
            if (logs.isNotEmpty()) {
                binding.tvEmpty.visibility = View.GONE
                adapter.updateList(logs)
            } else {
                binding.tvEmpty.visibility = View.VISIBLE
            }
        } else {
            PermissionManager.requestAllPermissions(requireActivity())
        }
    }

    override fun onResume() {
        super.onResume()
        loadCallLogs()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}