package com.example.hiltpractice.di

import com.example.hiltpractice.BuildConfig
import com.example.hiltpractice.data.GitHubService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @BaseUrl
    fun baseUrl(): String = "https://api.github.com/"

    @Provides
    @Singleton
    @ApiClient
    fun apiClient(): OkHttpClient {
        println("created OkHttpClient (api, 10 s)")
        return OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor())
            .build()
    }

    @Provides
    @Singleton
    @DownloadClient
    fun downloadClient(): OkHttpClient {
        println("created OkHttpClient (download, 60 s)")
        return OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun retrofit(@ApiClient client: OkHttpClient, @BaseUrl baseUrl: String): Retrofit {
        println("created Retrofit")
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun gitHubService(retrofit: Retrofit): GitHubService {
        println("created GitHubService")
        return retrofit.create(GitHubService::class.java)
    }

    private fun loggingInterceptor() = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BASIC
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }
}
