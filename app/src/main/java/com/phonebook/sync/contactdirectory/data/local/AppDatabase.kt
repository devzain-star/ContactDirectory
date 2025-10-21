package com.phonebook.sync.contactdirectory.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.phonebook.sync.contactdirectory.data.local.dao.ResponseOptionDao
import com.phonebook.sync.contactdirectory.data.local.dao.TemplateDao
import com.phonebook.sync.contactdirectory.data.local.entities.ResponseOption
import com.phonebook.sync.contactdirectory.data.local.entities.Template

@Database(entities = [ResponseOption::class, Template::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun responseOptionDao(): ResponseOptionDao
    abstract fun templateDao(): TemplateDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "contact_directory_db"
                ).build().also { INSTANCE = it }
            }
    }
}