package com.phonebook.sync.contactdirectory.ui.fragments

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.CallLog
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.phonebook.sync.contactdirectory.R
import com.phonebook.sync.contactdirectory.adapter.ContactsAdapter
import com.phonebook.sync.contactdirectory.databinding.FragmentContactsBinding
import com.phonebook.sync.contactdirectory.models.ContactEntry
import com.phonebook.sync.contactdirectory.utils.PermissionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ContactsFragment : Fragment() {

    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ContactsAdapter

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allGranted = permissions.values.all { it }
            if (allGranted) loadContactsAsync()
            else Toast.makeText(requireContext(), "Contacts permission required", Toast.LENGTH_SHORT).show()
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        checkAndLoadContacts()
    }

    private fun setupRecyclerView() {
        adapter = ContactsAdapter(emptyList(), object : ContactsAdapter.OnContactClickListener {
            override fun onCallClick(entry: ContactEntry) {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${entry.number}"))
                startActivity(intent)
            }

            override fun onMessageClick(entry: ContactEntry) {
                val smsIntent = Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", entry.number, null))
                startActivity(smsIntent)
            }

            override fun onWhatsAppClick(entry: ContactEntry) {
                try {
                    val uri = Uri.parse("https://wa.me/${entry.number.replace("+", "").replace(" ", "")}")
                    val intent = Intent(Intent.ACTION_VIEW, uri).apply { setPackage("com.whatsapp") }
                    if (intent.resolveActivity(requireContext().packageManager) != null) {
                        startActivity(intent)
                    } else {
                        Toast.makeText(requireContext(), "WhatsApp not installed", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Unable to open WhatsApp", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCopyClick(entry: ContactEntry) {
                val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("Phone", entry.number))
                Toast.makeText(requireContext(), "Number copied", Toast.LENGTH_SHORT).show()
            }
        })

        binding.recyclerViewContacts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewContacts.adapter = adapter
    }

    private fun checkAndLoadContacts() {
        val neededPermissions = arrayOf(
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_PHONE_STATE
        )

        val allGranted = neededPermissions.all {
            ActivityCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
        }

        if (allGranted) {
            loadContactsAsync()
        } else {
            requestPermissionsLauncher.launch(neededPermissions)
        }
    }

    private fun loadContactsAsync() {
        binding.progressBar.visibility = View.VISIBLE
        binding.recyclerViewContacts.visibility = View.GONE

        lifecycleScope.launch(Dispatchers.IO) {
            val contacts = fetchContactsSafely()

            withContext(Dispatchers.Main) {
                binding.progressBar.visibility = View.GONE
                binding.recyclerViewContacts.visibility = View.VISIBLE
                adapter.updateList(contacts)
                if (contacts.isEmpty()) {
                    Toast.makeText(requireContext(), "No contacts found", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun fetchContactsSafely(): List<ContactEntry> {
        val result = mutableListOf<ContactEntry>()
        try {
            val resolver = requireContext().contentResolver
            val projection = arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            )

            val cursor = resolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
            )

            cursor?.use {
                val nameIdx = it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numberIdx = it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)
                while (it.moveToNext()) {
                    val name = it.getString(nameIdx)?.trim() ?: "Unknown"
                    val number = it.getString(numberIdx)?.trim()?.replace("\\s+".toRegex(), "") ?: continue

                    val count = try {
                        getCallCountForNumber(number)
                    } catch (_: SecurityException) {
                        0
                    }

                    result.add(ContactEntry(name, number, count))
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result
    }

    private fun getCallCountForNumber(number: String): Int {
        return try {
            val resolver = requireContext().contentResolver
            val cursor = resolver.query(
                CallLog.Calls.CONTENT_URI,
                null,
                "${CallLog.Calls.NUMBER} LIKE ?",
                arrayOf("%${number.takeLast(7)}%"),
                null
            )
            val count = cursor?.count ?: 0
            cursor?.close()
            count
        } catch (e: Exception) {
            0
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
