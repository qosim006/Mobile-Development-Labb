package com.example.hiltpractice.ui

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.hiltpractice.experiments.ScopeActivityA

private fun Modifier.screen() = this
    .fillMaxSize()
    .statusBarsPadding()
    .padding(16.dp)

@Composable
fun NotesScreen(
    onOpen: (Long) -> Unit,
    onUsers: () -> Unit,
    onEdit: () -> Unit,
    vm: NotesViewModel = hiltViewModel(),
) {
    val notes by vm.notes.collectAsStateWithLifecycle()
    var title by rememberSaveable { mutableStateOf("") }
    var body by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current

    Column(Modifier.screen(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth().testTag("title"),
        )
        OutlinedTextField(
            value = body,
            onValueChange = { body = it },
            label = { Text("Body") },
            modifier = Modifier.fillMaxWidth().testTag("body"),
        )
        Button(onClick = {
            vm.add(title, body)
            title = ""
            body = ""
        }) { Text("Add") }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onUsers) { Text("GitHub users") }
            OutlinedButton(onClick = onEdit) { Text("Edit draft") }
        }
        OutlinedButton(onClick = {
            context.startActivity(Intent(context, ScopeActivityA::class.java))
        }) { Text("Scope experiment") }

        LazyColumn {
            items(notes, key = { it.id }) { note ->
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpen(note.id) }
                        .padding(vertical = 8.dp),
                )
            }
        }
    }
}

@Composable
fun NoteDetailScreen(vm: NoteDetailViewModel = hiltViewModel()) {
    val note by vm.note.collectAsStateWithLifecycle()
    Column(Modifier.screen(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        val n = note
        if (n == null) {
            Text("Loading...")
        } else {
            Text(n.title, style = MaterialTheme.typography.headlineSmall)
            Text(n.body)
        }
    }
}

@Composable
fun UsersScreen(vm: UsersViewModel = hiltViewModel()) {
    val users by vm.users.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()

    Column(Modifier.screen(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Button(onClick = vm::refresh) { Text("Refresh") }
        LazyColumn {
            items(users, key = { it.id }) { user ->
                Text(user.login, Modifier.padding(vertical = 8.dp))
            }
        }
    }
}

@Composable
fun EditTitleScreen(draft: DraftViewModel, onNext: () -> Unit) {
    val title by draft.title.collectAsStateWithLifecycle()
    Column(Modifier.screen(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = title,
            onValueChange = { draft.title.value = it },
            label = { Text("Draft title") },
        )
        Button(onClick = onNext) { Text("Next") }
    }
}

@Composable
fun EditBodyScreen(draft: DraftViewModel) {
    val title by draft.title.collectAsStateWithLifecycle()
    val body by draft.body.collectAsStateWithLifecycle()
    Column(Modifier.screen(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Title: $title")
        OutlinedTextField(
            value = body,
            onValueChange = { draft.body.value = it },
            label = { Text("Draft body") },
        )
    }
}
