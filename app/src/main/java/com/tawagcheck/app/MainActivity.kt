package com.tawagcheck.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tawagcheck.app.data.model.AppLanguage
import com.tawagcheck.app.service.RoleRequestHelper
import com.tawagcheck.app.ui.navigation.AppNavGraph
import com.tawagcheck.app.ui.strings.LocalStrings
import com.tawagcheck.app.ui.strings.stringsFor
import com.tawagcheck.app.ui.theme.TawagCheckTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appContainer = (application as TawagCheckApp).appContainer
        val roleRequestHelper = RoleRequestHelper(this)

        setContent {
            val language by appContainer.settingsDataStore.language
                .collectAsStateWithLifecycle(initialValue = AppLanguage.ENGLISH)

            TawagCheckTheme {
                CompositionLocalProvider(LocalStrings provides stringsFor(language)) {
                    AppNavGraph(appContainer = appContainer, roleRequestHelper = roleRequestHelper)
                }
            }
        }
    }
}
