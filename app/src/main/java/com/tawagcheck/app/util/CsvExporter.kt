package com.tawagcheck.app.util

import android.content.Context
import androidx.core.content.FileProvider
import com.tawagcheck.app.data.local.db.entity.CallHistoryEntity
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Builds a local CSV of call history for the user to share via the system chooser. Nothing is uploaded. */
object CsvExporter {

    fun export(context: Context, calls: List<CallHistoryEntity>): android.net.Uri {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val csv = buildString {
            appendLine("number,timestamp,hidden,score,risk_tier,action,reasons")
            calls.forEach { call ->
                val number = (call.normalizedNumber ?: call.rawNumber).replace(",", " ")
                val reasons = call.reasons.replace(",", ";").replace("|", "; ")
                appendLine(
                    listOf(
                        number,
                        dateFormat.format(Date(call.timestamp)),
                        call.isHidden.toString(),
                        call.score.toString(),
                        call.tier.name,
                        call.action.name,
                        reasons
                    ).joinToString(",")
                )
            }
        }

        val exportsDir = File(context.cacheDir, "exports").apply { mkdirs() }
        val file = File(exportsDir, "tawagcheck_call_history.csv")
        file.writeText(csv)

        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }
}
