package com.example.hiltpractice

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

// Q30: the replaced network module serves answers from MockWebServer
@HiltAndroidTest
class UsersMockWebServerTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val compose = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var server: MockWebServer

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun users_areLoadedFromMockServer() {
        server.enqueue(
            MockResponse().setResponseCode(200)
                .setBody("""[{"login":"mock-octocat","id":1,"avatar_url":null}]"""),
        )

        compose.onNodeWithText("GitHub users").performClick()

        compose.waitUntil(5_000) {
            compose.onAllNodesWithText("mock-octocat").fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun httpError_isShownOnScreen() {
        server.enqueue(MockResponse().setResponseCode(404))

        compose.onNodeWithText("GitHub users").performClick()

        compose.waitUntil(5_000) {
            compose.onAllNodesWithText("Server error: 404").fetchSemanticsNodes().isNotEmpty()
        }
    }
}
