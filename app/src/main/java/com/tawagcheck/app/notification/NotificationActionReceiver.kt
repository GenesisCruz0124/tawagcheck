package com.tawagcheck.app.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.tawagcheck.app.TawagCheckApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val number = intent.getStringExtra(EXTRA_NUMBER) ?: return
        val callHistoryId = intent.getLongExtra(EXTRA_CALL_HISTORY_ID, -1L)
        val appContainer = (context.applicationContext as TawagCheckApp).appContainer

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                when (intent.action) {
                    ACTION_BLOCK, ACTION_REPORT -> appContainer.scamRepository.reportUserScam(number)
                    ACTION_IGNORE -> Unit
                }
            } finally {
                if (callHistoryId >= 0) {
                    NotificationManagerCompat.from(context).cancel(callHistoryId.toInt())
                }
                pendingResult.finish()
            }
        }
    }

    companion object {
        const val ACTION_BLOCK = "com.tawagcheck.app.action.BLOCK"
        const val ACTION_REPORT = "com.tawagcheck.app.action.REPORT"
        const val ACTION_IGNORE = "com.tawagcheck.app.action.IGNORE"
        const val EXTRA_NUMBER = "extra_number"
        const val EXTRA_CALL_HISTORY_ID = "extra_call_history_id"
    }
}
