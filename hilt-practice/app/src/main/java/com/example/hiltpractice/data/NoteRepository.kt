package com.example.hiltpractice.data

import com.example.hiltpractice.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface NoteRepository {
    fun notes(): Flow<List<NoteEntity>>
    suspend fun note(id: Long): NoteEntity?
    suspend fun add(title: String, body: String)
}

class RoomNoteRepository @Inject constructor(
    private val dao: NoteDao,
    @IoDispatcher private val io: CoroutineDispatcher,
) : NoteRepository {

    override fun notes(): Flow<List<NoteEntity>> = dao.observeAll()

    override suspend fun note(id: Long): NoteEntity? = withContext(io) { dao.getById(id) }

    override suspend fun add(title: String, body: String) {
        withContext(io) { dao.insert(NoteEntity(title = title, body = body)) }
    }
}

class FakeNoteRepository @Inject constructor() : NoteRepository {

    private val data = MutableStateFlow(
        listOf(
            NoteEntity(1, "Sample note", "Fake data for debug mode"),
            NoteEntity(2, "Another note", "Provided by FakeNoteRepository"),
        ),
    )
    private var nextId = 3L

    fun setNotes(notes: List<NoteEntity>) {
        data.value = notes
        nextId = (notes.maxOfOrNull { it.id } ?: 0L) + 1
    }

    override fun notes(): Flow<List<NoteEntity>> = data

    override suspend fun note(id: Long): NoteEntity? = data.value.firstOrNull { it.id == id }

    override suspend fun add(title: String, body: String) {
        data.value = listOf(NoteEntity(nextId++, title, body)) + data.value
    }
}
