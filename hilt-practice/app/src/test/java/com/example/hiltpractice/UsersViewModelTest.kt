package com.example.hiltpractice

import com.example.hiltpractice.ui.UsersViewModel
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class UsersViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    @Test
    fun success_hasNoError() {
        val vm = UsersViewModel(FakeUserRepository())
        assertNull(vm.error.value)
    }

    @Test
    fun ioFailure_showsNoNetwork() {
        val repo = FakeUserRepository().apply { refreshResult = Result.failure(IOException()) }
        val vm = UsersViewModel(repo)
        assertEquals("No network", vm.error.value)
    }

    @Test
    fun httpFailure_showsServerErrorWithCode() {
        val repo = FakeUserRepository().apply {
            refreshResult = Result.failure(
                HttpException(Response.error<Any>(404, "".toResponseBody(null))),
            )
        }
        val vm = UsersViewModel(repo)
        assertEquals("Server error: 404", vm.error.value)
    }
}
