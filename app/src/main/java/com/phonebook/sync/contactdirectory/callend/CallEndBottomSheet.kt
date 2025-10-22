package com.phonebook.sync.contactdirectory.callend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.phonebook.sync.contactdirectory.R
import com.phonebook.sync.contactdirectory.databinding.BottomsheetCallEndBinding

class CallEndBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomsheetCallEndBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: BottomSheetViewModel
    private lateinit var adapterResponse: ArrayAdapter<String>
    private lateinit var adapterTemplate: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetCallEndBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[BottomSheetViewModel::class.java]

        setupUI()
        observeData()
        viewModel.loadResponses()

        return binding.root
    }

    private fun setupUI() {
        adapterResponse = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line)
        adapterTemplate = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line)

        binding.spinnerResponse.setAdapter(adapterResponse)
        binding.spinnerTemplate.setAdapter(adapterTemplate)

        binding.spinnerResponse.setOnItemClickListener { _, _, position, _ ->
            val selected = viewModel.responseOptions.value?.get(position)
            selected?.let {
                viewModel.loadTemplates(it.id)
            }
        }

        binding.spinnerTemplate.setOnItemClickListener { _, _, position, _ ->
            val selected = viewModel.templates.value?.get(position)
            binding.editNote.setText(selected?.message)
        }

        binding.btnSave.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Note saved!",
                Toast.LENGTH_SHORT
            ).show()
            dismiss()
        }
    }

    private fun observeData() {
        viewModel.responseOptions.observe(viewLifecycleOwner) { responses ->
            adapterResponse.clear()
            adapterResponse.addAll(responses.map { it.title })
        }

        viewModel.templates.observe(viewLifecycleOwner) { templates ->
            adapterTemplate.clear()
            adapterTemplate.addAll(templates.map { it.message })
        }
    }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

