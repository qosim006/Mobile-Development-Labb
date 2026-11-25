package com.example.hiltpractice

import com.example.hiltpractice.data.NoteRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

// Q28: the first @HiltAndroidTest
@HiltAndroidTest
class HiltSmokeTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: NoteRepository

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun repositoryIsInjected() {
        assertNotNull(repository)
    }
}
