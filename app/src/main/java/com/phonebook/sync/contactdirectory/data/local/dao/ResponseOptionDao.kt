package com.phonebook.sync.contactdirectory.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.phonebook.sync.contactdirectory.data.local.entities.ResponseOption

@Dao
interface ResponseOptionDao {
    @Query("SELECT * FROM response_options ORDER BY id DESC")
    suspend fun getAll(): List<ResponseOption>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(option: ResponseOption)

    @Delete
    suspend fun delete(option: ResponseOption)
}