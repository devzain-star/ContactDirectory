package com.phonebook.sync.contactdirectory.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.phonebook.sync.contactdirectory.data.local.entities.Template

@Dao
interface TemplateDao {

    @Query("SELECT * FROM templates WHERE optionId = :optionId")
    suspend fun getByOption(optionId: Int): List<Template>

    @Query("SELECT * FROM templates ORDER BY id DESC")
    suspend fun getAll(): List<Template>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(template: Template)

    @Delete
    suspend fun delete(template: Template)
}