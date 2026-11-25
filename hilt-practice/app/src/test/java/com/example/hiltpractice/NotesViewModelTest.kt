package com.example.hiltpractice

import com.example.hiltpractice.data.FakeNoteRepository
import com.example.hiltpractice.ui.NotesViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class NotesViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    @Test
    fun add_blankTitle_isIgnored() = runTest {
        val repo = FakeNoteRepository().apply { setNotes(emptyList()) }
        val vm = NotesViewModel(repo)

        vm.add("   ", "body")

        assertTrue(repo.notes().first().isEmpty())
    }

    @Test
    fun add_insertsTrimmedNote() = runTest {
        val repo = FakeNoteRepository().apply { setNotes(emptyList()) }
        val vm = NotesViewModel(repo)

        vm.add("  Milk ", " 2 liters ")

        val note = repo.notes().first().single()
        assertEquals("Milk", note.title)
        assertEquals("2 liters", note.body)
    }
}
