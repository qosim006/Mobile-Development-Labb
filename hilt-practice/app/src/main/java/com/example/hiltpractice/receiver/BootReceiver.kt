package com.example.hiltpractice.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.hiltpractice.core.Logger
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var logger: Logger

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent) // Q24: Hilt injects here; remove this line to see what breaks
        logger.log("BootReceiver got ${intent.action}")
    }
}
