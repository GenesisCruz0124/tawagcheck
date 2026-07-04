package com.tawagcheck.app.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.tawagcheck.app.R
import com.tawagcheck.app.data.model.CallVerdict
import com.tawagcheck.app.data.model.RiskTier

class VerdictNotificationManager(private val context: Context) {

    fun ensureChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Call verdicts",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications about flagged or blocked calls"
        }
        context.getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
    }

    fun showVerdict(callHistoryId: Long, verdict: CallVerdict) {
        if (verdict.tier == RiskTier.SAFE) return
        if (!hasNotificationPermission()) return

        val number = verdict.normalizedNumber ?: verdict.rawNumber.ifBlank { "Unknown number" }
        val label = verdict.contactName?.let { "$it ($number)" } ?: number
        val title = when (verdict.tier) {
            RiskTier.LIKELY_SCAM -> "Likely scam call: $label"
            RiskTier.SUSPICIOUS -> "Suspicious call: $label"
            RiskTier.SAFE -> return
        }
        val body = verdict.reasons.joinToString(", ").ifBlank { "No specific reason recorded" }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .addAction(buildAction("Block", NotificationActionReceiver.ACTION_BLOCK, callHistoryId, label))
            .addAction(buildAction("Report as scam", NotificationActionReceiver.ACTION_REPORT, callHistoryId, label))
            .addAction(buildAction("Ignore", NotificationActionReceiver.ACTION_IGNORE, callHistoryId, label))
            .build()

        NotificationManagerCompat.from(context).notify(callHistoryId.toInt(), notification)
    }

    private fun buildAction(title: String, action: String, callHistoryId: Long, number: String): NotificationCompat.Action {
        val intent = Intent(context, NotificationActionReceiver::class.java).apply {
            this.action = action
            putExtra(NotificationActionReceiver.EXTRA_NUMBER, number)
            putExtra(NotificationActionReceiver.EXTRA_CALL_HISTORY_ID, callHistoryId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            (callHistoryId.toInt() * 10) + action.hashCode() % 10,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Action.Builder(R.drawable.ic_launcher_foreground, title, pendingIntent).build()
    }

    private fun hasNotificationPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
    }

    companion object {
        const val CHANNEL_ID = "call_verdicts"
    }
}
