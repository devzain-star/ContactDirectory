package com.phonebook.sync.contactdirectory.callend

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.phonebook.sync.contactdirectory.data.local.AppDatabase
import com.phonebook.sync.contactdirectory.data.local.entities.ResponseOption
import com.phonebook.sync.contactdirectory.data.local.entities.Template
import kotlinx.coroutines.Dispatchers
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class BottomSheetViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val responseDao = db.responseOptionDao()
    private val templateDao = db.templateDao()

    private val _responseOptions = MutableLiveData<List<ResponseOption>>()
    val responseOptions: LiveData<List<ResponseOption>> = _responseOptions

    private val _templates = MutableLiveData<List<Template>>()
    val templates: LiveData<List<Template>> = _templates

    fun loadResponses() {
        viewModelScope.launch(Dispatchers.IO) {
            val data = responseDao.getAll()
            _responseOptions.postValue(data)
        }
    }

    fun loadTemplates(optionId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val data = templateDao.getByOption(optionId)
            _templates.postValue(data)
        }
    }
}
