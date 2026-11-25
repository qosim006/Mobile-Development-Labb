package com.example.hiltpractice

import androidx.lifecycle.SavedStateHandle
import com.example.hiltpractice.data.FakeNoteRepository
import com.example.hiltpractice.data.NoteEntity
import com.example.hiltpractice.ui.NoteDetailViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class NoteDetailViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    @Test
    fun loadsNoteFromSavedStateHandle() {
        val repo = FakeNoteRepository().apply { setNotes(listOf(NoteEntity(7, "Seven", "body"))) }

        val vm = NoteDetailViewModel(SavedStateHandle(mapOf("noteId" to 7L)), repo)

        assertEquals("Seven", vm.note.value?.title)
    }

    @Test
    fun unknownId_givesNullNote() {
        val repo = FakeNoteRepository().apply { setNotes(emptyList()) }

        val vm = NoteDetailViewModel(SavedStateHandle(mapOf("noteId" to 99L)), repo)

        assertNull(vm.note.value)
    }
}
