package com.example.hiltpractice

import com.example.hiltpractice.data.User
import com.example.hiltpractice.data.UserEntity
import com.example.hiltpractice.data.UserRepositoryImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class UserRepositoryTest {

    private val dispatcher = StandardTestDispatcher()

    @Test
    fun refresh_storesUsersInDao() = runTest(dispatcher) {
        val dao = FakeUserDao()
        val repo = UserRepositoryImpl(
            FakeGitHubService { listOf(User("octocat", 1, null)) }, dao, dispatcher,
        )

        val result = repo.refresh()

        assertTrue(result.isSuccess)
        assertEquals(listOf(UserEntity(1, "octocat", null)), dao.data.value)
    }

    @Test
    fun refresh_networkFailure_returnsIOException_andKeepsCache() = runTest(dispatcher) {
        val dao = FakeUserDao().apply { data.value = listOf(UserEntity(7, "cached", null)) }
        val repo = UserRepositoryImpl(
            FakeGitHubService { throw IOException("no network") }, dao, dispatcher,
        )

        val result = repo.refresh()

        assertTrue(result.exceptionOrNull() is IOException)
        assertEquals(listOf(UserEntity(7, "cached", null)), dao.data.value)
    }

    @Test
    fun refresh_httpError_returnsHttpException() = runTest(dispatcher) {
        val repo = UserRepositoryImpl(
            FakeGitHubService {
                throw HttpException(Response.error<Any>(404, "".toResponseBody(null)))
            },
            FakeUserDao(),
            dispatcher,
        )

        val error = repo.refresh().exceptionOrNull()

        assertTrue(error is HttpException)
        assertEquals(404, (error as HttpException).code())
    }
}
