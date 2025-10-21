package com.phonebook.sync.contactdirectory.models

data class ContactEntry(
    val name: String,
    val number: String,
    val callCount: Int = 0
)
