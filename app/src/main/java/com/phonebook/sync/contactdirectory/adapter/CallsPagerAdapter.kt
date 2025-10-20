package com.phonebook.sync.contactdirectory.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.phonebook.sync.contactdirectory.ui.fragments.calls.*

class CallsPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val fragments = listOf(
        AllCallsFragment(),
        IncomingCallsFragment(),
        OutgoingCallsFragment(),
        MissedCallsFragment(),
        RecordedCallsFragment()
    )

    override fun getItemCount(): Int = fragments.size
    override fun createFragment(position: Int): Fragment = fragments[position]
}