package com.example.hiltpractice.di

import android.content.Context
import androidx.room.Room
import com.example.hiltpractice.data.AppDatabase
import com.example.hiltpractice.data.NoteDao
import com.example.hiltpractice.data.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun database(@ApplicationContext context: Context): AppDatabase {
        println("created AppDatabase")
        return Room.databaseBuilder(context, AppDatabase::class.java, "app.db").build()
    }

    @Provides
    fun noteDao(db: AppDatabase): NoteDao = db.noteDao()

    @Provides
    fun userDao(db: AppDatabase): UserDao = db.userDao()
}
