package com.phonebook.sync.contactdirectory.ui.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.phonebook.sync.contactdirectory.R
import com.phonebook.sync.contactdirectory.databinding.ActivityMainBinding
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
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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


}