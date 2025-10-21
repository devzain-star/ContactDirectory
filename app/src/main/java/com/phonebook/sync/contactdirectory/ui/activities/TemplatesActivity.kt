package com.phonebook.sync.contactdirectory.ui.activities

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.phonebook.sync.contactdirectory.R
import com.phonebook.sync.contactdirectory.adapter.TemplateAdapter
import com.phonebook.sync.contactdirectory.data.local.AppDatabase
import com.phonebook.sync.contactdirectory.data.local.entities.ResponseOption
import com.phonebook.sync.contactdirectory.data.local.entities.Template
import com.phonebook.sync.contactdirectory.databinding.ActivityTemplatesBinding
import kotlinx.coroutines.launch

class TemplatesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTemplatesBinding
    private lateinit var adapter: TemplateAdapter

    private val db by lazy { AppDatabase.getInstance(this) }
    private val optionDao by lazy { db.responseOptionDao() }
    private val templateDao by lazy { db.templateDao() }

    private var options: List<ResponseOption> = emptyList()
    private var selectedOption: ResponseOption? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTemplatesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupOptionDropdown()
        setupAddButton()
    }

    private fun setupRecyclerView() {
        adapter = TemplateAdapter(emptyList()) { template ->
            lifecycleScope.launch {
                templateDao.delete(template)
                loadTemplates()
                Toast.makeText(this@TemplatesActivity, "Template deleted", Toast.LENGTH_SHORT).show()
            }
        }
        binding.recyclerViewTemplates.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewTemplates.adapter = adapter
    }

    private fun setupOptionDropdown() {
        lifecycleScope.launch {
            options = optionDao.getAll()

            if (options.isEmpty()) {
                Toast.makeText(this@TemplatesActivity, "No response options found. Add some first.", Toast.LENGTH_LONG).show()
                return@launch
            }

            val titles = listOf("All Options") + options.map { it.title }

            val adapterDropdown = ArrayAdapter(
                this@TemplatesActivity,
                android.R.layout.simple_dropdown_item_1line,
                titles
            )

            binding.autoCompleteOptions.setAdapter(adapterDropdown)
            binding.autoCompleteOptions.setText("All Options", false)

            binding.autoCompleteOptions.setOnItemClickListener { _, _, position, _ ->
                selectedOption = if (position == 0) null else options[position - 1]
                loadTemplates()
            }

            loadTemplates()
        }
    }

    private fun setupAddButton() {
        binding.btnAddTemplate.setOnClickListener {
            val message = binding.etTemplate.text.toString().trim()
            val option = selectedOption

            if (option == null) {
                Toast.makeText(this, "Please select an option first.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (message.isEmpty()) {
                binding.etTemplate.error = "Template cannot be empty"
                return@setOnClickListener
            }

            lifecycleScope.launch {
                templateDao.insert(Template(optionId = option.id, optionTitle = option.title, message = message))
                binding.etTemplate.text?.clear()
                loadTemplates()
                Toast.makeText(this@TemplatesActivity, "Template added", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadTemplates() {
        lifecycleScope.launch {
            val templates = if (selectedOption == null) {
                templateDao.getAll()
            } else {
                templateDao.getByOption(selectedOption!!.id)
            }
            adapter.updateList(templates)
        }
    }
}