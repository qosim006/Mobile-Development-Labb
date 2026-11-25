package com.example.hiltpractice.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [NoteEntity::class, UserEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun userDao(): UserDao
}
