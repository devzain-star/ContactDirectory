package com.phonebook.sync.contactdirectory.ui.fragments.calls

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.phonebook.sync.contactdirectory.R
import com.phonebook.sync.contactdirectory.databinding.FragmentMissedCallsBinding

class MissedCallsFragment : Fragment() {

    private var _binding: FragmentMissedCallsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMissedCallsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}