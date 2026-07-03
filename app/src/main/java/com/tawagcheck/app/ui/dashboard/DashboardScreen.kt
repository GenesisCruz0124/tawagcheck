package com.tawagcheck.app.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.tawagcheck.app.data.local.db.dao.DailyFlaggedCount
import com.tawagcheck.app.ui.common.StatCard
import com.tawagcheck.app.ui.strings.LocalStrings
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun DashboardScreen(viewModel: DashboardViewModel, modifier: Modifier = Modifier) {
    val strings = LocalStrings.current
    val protectionEnabled by viewModel.protectionEnabled.collectAsStateWithLifecycle()
    val screenedToday by viewModel.screenedToday.collectAsStateWithLifecycle()
    val scamsBlocked by viewModel.scamsBlocked.collectAsStateWithLifecycle()
    val last7Days by viewModel.last7DaysFlagged.collectAsStateWithLifecycle()

    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(last7Days) {
        val counts = last7DaysAsFilledList(last7Days)
        modelProducer.runTransaction {
            columnSeries { series(y = counts) }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = strings.dashboardTitle, style = MaterialTheme.typography.headlineMedium)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (protectionEnabled) strings.protectionOn else strings.protectionOff,
                    style = MaterialTheme.typography.titleMedium
                )
                Switch(checked = protectionEnabled, onCheckedChange = viewModel::setProtectionEnabled)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = strings.callsScreenedToday,
                    value = screenedToday.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = strings.scamsBlocked,
                    value = scamsBlocked.toString(),
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = strings.chartTitle,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
            )

            CartesianChartHost(
                chart = rememberCartesianChart(
                    rememberColumnCartesianLayer(),
                    startAxis = VerticalAxis.rememberStart(),
                    bottomAxis = HorizontalAxis.rememberBottom()
                ),
                modelProducer = modelProducer,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )
    }
}

/** Fills in zero-count days so the chart always shows a full 7-day window, oldest first. */
private fun last7DaysAsFilledList(daily: List<DailyFlaggedCount>): List<Int> {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val byDay = daily.associate { it.day to it.count }
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, -6)

    return (0 until 7).map {
        val key = formatter.format(calendar.time)
        val count = byDay[key] ?: 0
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        count
    }
}
