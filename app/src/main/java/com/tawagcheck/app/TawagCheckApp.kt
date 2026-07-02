package com.tawagcheck.app

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class TawagCheckApp : Application() {

    val appContainer: AppContainer by lazy { AppContainer(this) }

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        appContainer.verdictNotificationManager.ensureChannel()
        applicationScope.launch {
            appContainer.scamListSeeder.seedIfEmpty()
        }
    }
}
