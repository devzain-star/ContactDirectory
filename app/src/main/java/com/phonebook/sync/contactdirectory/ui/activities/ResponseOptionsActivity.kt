package com.phonebook.sync.contactdirectory.ui.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.phonebook.sync.contactdirectory.R
import com.phonebook.sync.contactdirectory.adapter.ResponseOptionAdapter
import com.phonebook.sync.contactdirectory.data.local.AppDatabase
import com.phonebook.sync.contactdirectory.data.local.entities.ResponseOption
import com.phonebook.sync.contactdirectory.databinding.ActivityResponseOptionsBinding
import kotlinx.coroutines.launch

class ResponseOptionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResponseOptionsBinding
    private lateinit var adapter: ResponseOptionAdapter
    private val dao by lazy { AppDatabase.getInstance(this).responseOptionDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResponseOptionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupAddButton()
        loadOptions()
    }

    private fun setupRecyclerView() {
        adapter = ResponseOptionAdapter(emptyList()) { option ->
            lifecycleScope.launch {
                dao.delete(option)
                loadOptions()
                Toast.makeText(this@ResponseOptionsActivity, "Deleted \"${option.title}\"", Toast.LENGTH_SHORT).show()
            }
        }

        binding.recyclerViewOptions.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewOptions.adapter = adapter
    }

    private fun setupAddButton() {
        binding.btnAdd.setOnClickListener {
            val text = binding.etOption.text.toString().trim()
            if (text.isEmpty()) {
                binding.etOption.error = "Enter option name"
                return@setOnClickListener
            }

            lifecycleScope.launch {
                dao.insert(ResponseOption(title = text))
                binding.etOption.text?.clear()
                loadOptions()
                Toast.makeText(this@ResponseOptionsActivity, "Option added", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadOptions() {
        lifecycleScope.launch {
            val options = dao.getAll()
            adapter.updateList(options)
        }
    }
}
