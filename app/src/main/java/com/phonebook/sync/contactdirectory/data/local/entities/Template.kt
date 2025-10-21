package com.phonebook.sync.contactdirectory.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "templates")
data class Template(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val optionId: Int,
    val optionTitle: String,
    val message: String
)
