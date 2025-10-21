package com.phonebook.sync.contactdirectory.ui.fragments.calls

import android.os.Bundle
import android.provider.CallLog
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.phonebook.sync.contactdirectory.R
import com.phonebook.sync.contactdirectory.adapter.CallLogAdapter
import com.phonebook.sync.contactdirectory.databinding.FragmentOutgoingCallsBinding
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
        adapter = CallLogAdapter(emptyList())
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