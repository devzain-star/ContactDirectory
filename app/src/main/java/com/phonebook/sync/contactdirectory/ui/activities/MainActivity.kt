package com.phonebook.sync.contactdirectory.ui.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.phonebook.sync.contactdirectory.R
import com.phonebook.sync.contactdirectory.callend.CallEndBottomSheet
import com.phonebook.sync.contactdirectory.databinding.ActivityMainBinding
import com.phonebook.sync.contactdirectory.services.CallStateWatcherService
import com.phonebook.sync.contactdirectory.ui.fragments.CallsFragment
import com.phonebook.sync.contactdirectory.ui.fragments.ContactsFragment
import com.phonebook.sync.contactdirectory.ui.fragments.SettingsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var activeFragment: Fragment? = null

    private val callsFragment = CallsFragment()
    private val contactsFragment = ContactsFragment()
    private val settingsFragment = SettingsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkAndRequestPermissions()

        // Show bottom sheet if triggered from service
        if (intent.getBooleanExtra("show_bottom_sheet", false)) {
            CallEndBottomSheet().show(supportFragmentManager, "CallEndBottomSheet")
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, settingsFragment, "3")
                .hide(settingsFragment)
                .add(R.id.fragment_container, contactsFragment, "2")
                .hide(contactsFragment)
                .add(R.id.fragment_container, callsFragment, "1")
                .commit()
            activeFragment = callsFragment
        }

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_calls -> switchFragment(callsFragment)
                R.id.nav_contacts -> switchFragment(contactsFragment)
                R.id.nav_settings -> switchFragment(settingsFragment)
            }
            true
        }
    }

    private fun switchFragment(target: Fragment) {
        if (activeFragment == target) return
        supportFragmentManager.beginTransaction()
            .hide(activeFragment!!)
            .show(target)
            .commit()
        activeFragment = target
    }

    private fun checkAndRequestPermissions() {
        val needed = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
            != PackageManager.PERMISSION_GRANTED
        ) needed.add(Manifest.permission.READ_PHONE_STATE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) needed.add(Manifest.permission.POST_NOTIFICATIONS)

        if (needed.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, needed.toTypedArray(), 101)
        } else {
            startCallWatcherService()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 101) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                startCallWatcherService()
            } else {
                Toast.makeText(this, "Phone permissions are required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startCallWatcherService() {
        val serviceIntent = Intent(this, CallStateWatcherService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent?.getBooleanExtra("show_bottom_sheet", false) == true) {
            CallEndBottomSheet().show(supportFragmentManager, "CallEndBottomSheet")
        }
    }
}