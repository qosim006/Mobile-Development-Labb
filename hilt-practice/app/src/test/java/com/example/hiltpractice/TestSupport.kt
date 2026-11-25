package com.example.hiltpractice

import com.example.hiltpractice.data.GitHubService
import com.example.hiltpractice.data.User
import com.example.hiltpractice.data.UserDao
import com.example.hiltpractice.data.UserEntity
import com.example.hiltpractice.data.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestWatcher() {
    override fun starting(description: Description) = Dispatchers.setMain(dispatcher)
    override fun finished(description: Description) = Dispatchers.resetMain()
}

class FakeUserDao : UserDao {
    val data = MutableStateFlow<List<UserEntity>>(emptyList())
    override fun observeAll(): Flow<List<UserEntity>> = data
    override suspend fun upsertAll(users: List<UserEntity>) {
        data.value = users
    }
}

class FakeGitHubService(private val result: () -> List<User>) : GitHubService {
    override suspend fun users(perPage: Int?): List<User> = result()
    override suspend fun user(login: String): User = result().first { it.login == login }
}

class FakeUserRepository : UserRepository {
    val stored = MutableStateFlow<List<UserEntity>>(emptyList())
    var refreshResult: Result<Unit> = Result.success(Unit)
    override val users: Flow<List<UserEntity>> = stored
    override suspend fun refresh(): Result<Unit> = refreshResult
}
