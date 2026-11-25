package com.example.hiltpractice

import android.content.Context
import androidx.room.Room
import com.example.hiltpractice.data.AppDatabase
import com.example.hiltpractice.data.GitHubService
import com.example.hiltpractice.data.NoteDao
import com.example.hiltpractice.data.UserDao
import com.example.hiltpractice.di.ApiClient
import com.example.hiltpractice.di.BaseUrl
import com.example.hiltpractice.di.DatabaseModule
import com.example.hiltpractice.di.DownloadClient
import com.example.hiltpractice.di.NetworkModule
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

// Q29: every instrumented test gets a fresh in-memory database
@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [DatabaseModule::class])
object TestDatabaseModule {

    @Provides
    @Singleton
    fun database(@ApplicationContext context: Context): AppDatabase =
        Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

    @Provides
    fun noteDao(db: AppDatabase): NoteDao = db.noteDao()

    @Provides
    fun userDao(db: AppDatabase): UserDao = db.userDao()
}

// Q30: the real GitHub API is replaced by MockWebServer
@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [NetworkModule::class])
object MockNetworkModule {

    @Provides
    @Singleton
    fun mockWebServer(): MockWebServer = MockWebServer().apply { start() }

    @Provides
    @Singleton
    @BaseUrl
    fun baseUrl(server: MockWebServer): String = server.url("/").toString()

    @Provides
    @Singleton
    @ApiClient
    fun apiClient(): OkHttpClient = OkHttpClient()

    @Provides
    @Singleton
    @DownloadClient
    fun downloadClient(): OkHttpClient = OkHttpClient()

    @Provides
    @Singleton
    fun retrofit(@ApiClient client: OkHttpClient, @BaseUrl baseUrl: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun gitHubService(retrofit: Retrofit): GitHubService = retrofit.create(GitHubService::class.java)
}
