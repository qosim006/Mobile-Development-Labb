package com.example.hiltpractice.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun AppNav() {
    val nav = rememberNavController()

    NavHost(navController = nav, startDestination = "notes") {

        composable("notes") {
            NotesScreen(
                onOpen = { id -> nav.navigate("note/$id") },
                onUsers = { nav.navigate("users") },
                onEdit = { nav.navigate("edit") },
            )
        }

        composable("users") { UsersScreen() }

        composable(
            route = "note/{noteId}",
            arguments = listOf(navArgument("noteId") { type = NavType.LongType }),
        ) { NoteDetailScreen() }

        // Q25: two screens share one DraftViewModel scoped to the "edit" graph
        navigation(startDestination = "edit_title", route = "edit") {
            composable("edit_title") { entry ->
                val parent = remember(entry) { nav.getBackStackEntry("edit") }
                val draft: DraftViewModel = hiltViewModel(parent)
                EditTitleScreen(draft = draft, onNext = { nav.navigate("edit_body") })
            }
            composable("edit_body") { entry ->
                val parent = remember(entry) { nav.getBackStackEntry("edit") }
                val draft: DraftViewModel = hiltViewModel(parent)
                EditBodyScreen(draft = draft)
            }
        }
    }
}
