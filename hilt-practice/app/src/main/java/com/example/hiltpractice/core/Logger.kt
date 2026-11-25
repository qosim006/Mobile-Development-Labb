package com.example.hiltpractice.core

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

interface Logger {
    fun log(message: String)
}

class LogcatLogger @Inject constructor() : Logger {
    override fun log(message: String) {
        Log.d("HiltPractice", message)
    }
}

class FileLogger @Inject constructor(
    @ApplicationContext private val context: Context,
) : Logger {
    override fun log(message: String) {
        File(context.filesDir, "app.log").appendText("$message\n")
    }
}
