package com.example.hiltpractice.di

import com.example.hiltpractice.BuildConfig
import com.example.hiltpractice.data.FakeNoteRepository
import com.example.hiltpractice.data.NoteRepository
import com.example.hiltpractice.data.RoomNoteRepository
import com.example.hiltpractice.data.UserRepository
import com.example.hiltpractice.data.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository
}

// Q23: real repository by default, fake one when built with -PuseFake=true
@Module
@InstallIn(SingletonComponent::class)
object NoteRepositoryModule {

    @Provides
    @Singleton
    fun noteRepository(
        room: Provider<RoomNoteRepository>,
        fake: Provider<FakeNoteRepository>,
    ): NoteRepository = if (BuildConfig.USE_FAKE) fake.get() else room.get()
}
