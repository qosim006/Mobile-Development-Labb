package com.example.hiltpractice

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test

// Q29: add a note through the UI; run twice - the second run starts with an empty list
@HiltAndroidTest
class NotesUiTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val compose = createAndroidComposeRule<MainActivity>()

    @Test
    fun addNote_showsItInTheList() {
        compose.onNodeWithTag("title").performTextInput("Buy milk")
        compose.onNodeWithText("Add").performClick()

        compose.waitUntil(5_000) {
            compose.onAllNodesWithText("Buy milk").fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithText("Buy milk").assertIsDisplayed()
    }

    @Test
    fun listStartsEmpty() {
        compose.onAllNodesWithText("Buy milk").assertCountEquals(0)
    }
}
