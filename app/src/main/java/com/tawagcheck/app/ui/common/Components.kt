package com.tawagcheck.app.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tawagcheck.app.data.model.RiskTier
import com.tawagcheck.app.ui.strings.LocalStrings
import com.tawagcheck.app.ui.theme.AmberWarning
import com.tawagcheck.app.ui.theme.GreenPrimary
import com.tawagcheck.app.ui.theme.RedDanger

@Composable
fun RiskBadge(tier: RiskTier, modifier: Modifier = Modifier) {
    val strings = LocalStrings.current
    val color = when (tier) {
        RiskTier.SAFE -> GreenPrimary
        RiskTier.SUSPICIOUS -> AmberWarning
        RiskTier.LIKELY_SCAM -> RedDanger
    }
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = color.copy(alpha = 0.15f),
        contentColor = color
    ) {
        Text(
            text = strings.riskTierLabel(tier),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = value, style = MaterialTheme.typography.headlineMedium)
            Text(text = title, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
