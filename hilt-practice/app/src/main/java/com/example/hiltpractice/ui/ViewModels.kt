package com.example.hiltpractice.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hiltpractice.data.NoteEntity
import com.example.hiltpractice.data.NoteRepository
import com.example.hiltpractice.data.UserEntity
import com.example.hiltpractice.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val repository: NoteRepository,
) : ViewModel() {

    val notes: StateFlow<List<NoteEntity>> = repository.notes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun add(title: String, body: String) {
        if (title.isBlank()) return
        viewModelScope.launch { repository.add(title.trim(), body.trim()) }
    }
}

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: NoteRepository,
) : ViewModel() {

    private val noteId: Long = checkNotNull(savedStateHandle["noteId"])

    private val _note = MutableStateFlow<NoteEntity?>(null)
    val note: StateFlow<NoteEntity?> = _note

    init {
        viewModelScope.launch { _note.value = repository.note(noteId) }
    }
}

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val repository: UserRepository,
) : ViewModel() {

    val users: StateFlow<List<UserEntity>> = repository.users
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            repository.refresh()
                .onSuccess { _error.value = null }
                .onFailure { _error.value = messageFor(it) }
        }
    }

    private fun messageFor(t: Throwable): String = when (t) {
        is HttpException -> "Server error: ${t.code()}"
        is IOException -> "No network"
        else -> "Unknown error"
    }
}

@HiltViewModel
class DraftViewModel @Inject constructor() : ViewModel() {

    val title = MutableStateFlow("")
    val body = MutableStateFlow("")

    override fun onCleared() {
        println("DraftViewModel cleared")
    }
}
