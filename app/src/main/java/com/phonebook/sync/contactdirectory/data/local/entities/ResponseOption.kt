package com.phonebook.sync.contactdirectory.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "response_options")
data class ResponseOption(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String
)