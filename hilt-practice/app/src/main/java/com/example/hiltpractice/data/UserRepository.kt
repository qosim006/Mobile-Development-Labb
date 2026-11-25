package com.example.hiltpractice.data

import com.example.hiltpractice.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

interface UserRepository {
    val users: Flow<List<UserEntity>>
    suspend fun refresh(): Result<Unit>
}

class UserRepositoryImpl @Inject constructor(
    private val api: GitHubService,
    private val dao: UserDao,
    @IoDispatcher private val io: CoroutineDispatcher,
) : UserRepository {

    override val users: Flow<List<UserEntity>> = dao.observeAll()

    override suspend fun refresh(): Result<Unit> = withContext(io) {
        try {
            dao.upsertAll(api.users(PER_PAGE).map { it.toEntity() })
            Result.success(Unit)
        } catch (e: HttpException) {
            Result.failure(e)
        } catch (e: IOException) {
            Result.failure(e)
        }
    }

    private companion object {
        const val PER_PAGE = 5
    }
}
