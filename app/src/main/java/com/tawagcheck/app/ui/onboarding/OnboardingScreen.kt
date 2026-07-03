package com.tawagcheck.app.ui.onboarding

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tawagcheck.app.service.RoleRequestHelper
import com.tawagcheck.app.ui.strings.LocalStrings

@Composable
fun OnboardingScreen(
    roleRequestHelper: RoleRequestHelper,
    onDone: () -> Unit
) {
    val strings = LocalStrings.current

    val roleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        if (roleRequestHelper.isRoleHeld()) {
            onDone()
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.Shield,
                contentDescription = null,
                modifier = Modifier.size(96.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.size(24.dp))
            Text(text = strings.onboardingTitle, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.size(16.dp))
            Text(text = strings.onboardingBody, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.size(32.dp))
            Button(onClick = {
                if (roleRequestHelper.isRoleHeld()) {
                    onDone()
                } else {
                    roleRequestHelper.createRequestRoleIntent()?.let { roleLauncher.launch(it) }
                }
            }) {
                Text(text = strings.onboardingEnableButton)
            }
        }
    }
}
