package com.phonebook.sync.contactdirectory.ui.fragments.calls

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.phonebook.sync.contactdirectory.R
import com.phonebook.sync.contactdirectory.databinding.FragmentIncomingCallsBinding

class IncomingCallsFragment : Fragment() {

    private var _binding: FragmentIncomingCallsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIncomingCallsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}