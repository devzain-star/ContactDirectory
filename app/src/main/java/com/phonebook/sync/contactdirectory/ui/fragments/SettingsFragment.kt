package com.phonebook.sync.contactdirectory.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.phonebook.sync.contactdirectory.R
import com.phonebook.sync.contactdirectory.databinding.FragmentSettingsBinding
import com.phonebook.sync.contactdirectory.ui.activities.ResponseOptionsActivity
import com.phonebook.sync.contactdirectory.ui.activities.TemplatesActivity

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cardSyncContacts.setOnClickListener {
            showConfirmDialog()
        }

        binding.cardResponseOptions.setOnClickListener {
            startActivity(Intent(activity, ResponseOptionsActivity::class.java))

        }

        binding.cardTemplates.setOnClickListener {
            startActivity(Intent(activity, TemplatesActivity::class.java))
         }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showConfirmDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Sync Contacts")
            .setMessage("Are you sure you want to sync your contacts? This may take a few minutes.")
            .setIcon(R.drawable.ic_sync)
            .setPositiveButton("Yes") { dialog, _ ->
                syncContacts()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun syncContacts() {
        // Your sync logic here
        Toast.makeText(requireContext(), "Failed to sync contacts!", Toast.LENGTH_SHORT).show()
    }
}