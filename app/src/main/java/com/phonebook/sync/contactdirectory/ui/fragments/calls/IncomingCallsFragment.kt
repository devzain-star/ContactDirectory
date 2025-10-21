package com.phonebook.sync.contactdirectory.ui.fragments.calls

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.CallLog
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.phonebook.sync.contactdirectory.R
import com.phonebook.sync.contactdirectory.adapter.CallLogAdapter
import com.phonebook.sync.contactdirectory.adapter.ResponseOptionListAdapter
import com.phonebook.sync.contactdirectory.data.local.AppDatabase
import com.phonebook.sync.contactdirectory.data.local.entities.ResponseOption
import com.phonebook.sync.contactdirectory.databinding.DialogAddNoteBinding
import com.phonebook.sync.contactdirectory.databinding.DialogSelectOptionBinding
import com.phonebook.sync.contactdirectory.databinding.FragmentIncomingCallsBinding
import com.phonebook.sync.contactdirectory.utils.CallEntry
import com.phonebook.sync.contactdirectory.utils.CallLogHelper
import com.phonebook.sync.contactdirectory.utils.PermissionManager
import kotlinx.coroutines.launch

class IncomingCallsFragment : Fragment() {

    private var _binding: FragmentIncomingCallsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CallLogAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIncomingCallsBinding.inflate(inflater, container, false)
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
                showResponseOptionsBottomSheet(entry)
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
            val logs = CallLogHelper.getCallLogs(requireContext(), CallLog.Calls.INCOMING_TYPE)
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

    private fun showResponseOptionsBottomSheet(entry: CallEntry) {
        val bottomSheet = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        val binding = DialogSelectOptionBinding.inflate(layoutInflater)

        val dao = AppDatabase.getInstance(requireContext()).responseOptionDao()
        val adapter = ResponseOptionListAdapter { selectedOption ->
            bottomSheet.dismiss()
            showTemplateDialog(entry, selectedOption)
        }

        binding.recyclerOptions.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerOptions.adapter = adapter

        lifecycleScope.launch {
            val data = dao.getAll()
            adapter.submitList(data)
        }

        binding.btnCancel.setOnClickListener { bottomSheet.dismiss() }

        bottomSheet.setContentView(binding.root)
        bottomSheet.show()
    }

    private fun showTemplateDialog(entry: CallEntry, option: ResponseOption) {
        val dialogBinding = DialogAddNoteBinding.inflate(layoutInflater)
        val dao = AppDatabase.getInstance(requireContext()).templateDao()

        lifecycleScope.launch {
            val templates = dao.getByOption(option.id)
            val templateTitles = templates.map { it.message }

            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                templateTitles
            )
            dialogBinding.spinnerTemplates.adapter = adapter

            dialogBinding.spinnerTemplates.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    dialogBinding.etNote.setText(templateTitles[position])
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }

        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.RoundedDialog)
            .setTitle("Add Note for ${option.title}")
            .setView(dialogBinding.root)
            .setPositiveButton("Save", null)
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {
            val button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener {
                val noteText = dialogBinding.etNote.text.toString().trim()
                if (noteText.isEmpty()) {
                    dialogBinding.etNote.error = "Please enter or select a note"
                    return@setOnClickListener
                }

                lifecycleScope.launch {
                    uploadNoteToServer(entry, option, noteText)
                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }

    private suspend fun uploadNoteToServer(entry: CallEntry, option: ResponseOption, note: String) {

        Toast.makeText(requireContext(), "Server error", Toast.LENGTH_SHORT).show()
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