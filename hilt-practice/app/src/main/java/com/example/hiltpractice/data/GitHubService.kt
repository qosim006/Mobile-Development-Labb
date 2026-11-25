package com.example.hiltpractice.data

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHubService {
    @GET("users")
    suspend fun users(@Query("per_page") perPage: Int? = null): List<User>

    @GET("users/{login}")
    suspend fun user(@Path("login") login: String): User
}
