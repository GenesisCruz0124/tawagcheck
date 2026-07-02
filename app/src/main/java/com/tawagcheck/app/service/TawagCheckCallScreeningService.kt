package com.tawagcheck.app.service

import android.telecom.Call
import android.telecom.CallScreeningService
import android.telecom.CallScreeningService.CallResponse
import android.telecom.TelecomManager
import com.tawagcheck.app.AppContainer
import com.tawagcheck.app.TawagCheckApp
import com.tawagcheck.app.data.model.CallAction
import com.tawagcheck.app.data.model.CallVerdict
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TawagCheckCallScreeningService : CallScreeningService() {

    private val appContainer: AppContainer by lazy { (application as TawagCheckApp).appContainer }
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onScreenCall(callDetails: Call.Details) {
        val rawNumber = callDetails.handle?.schemeSpecificPart.orEmpty()
        // NOTE (Android platform limitation): calls with a RESTRICTED/UNKNOWN/UNAVAILABLE/PAYPHONE
        // handle presentation are never delivered to onScreenCall at all - the telecom framework
        // filters them out before any CallScreeningService is invoked. This only catches the rarer
        // case of a blank/empty handle that still reaches us.
        val isHidden = rawNumber.isBlank() || callDetails.handlePresentation != TelecomManager.PRESENTATION_ALLOWED

        serviceScope.launch {
            val protectionEnabled = appContainer.settingsDataStore.protectionEnabled.first()
            if (!protectionEnabled) {
                respondToCall(callDetails, CallResponse.Builder().build())
                return@launch
            }

            val verdict = appContainer.heuristicsEngine.evaluate(rawNumber, isHidden)
            respondToCall(callDetails, verdict.toCallResponse())

            val callHistoryId = appContainer.callHistoryRepository.record(verdict)
            appContainer.verdictNotificationManager.showVerdict(callHistoryId, verdict)
        }
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun CallVerdict.toCallResponse(): CallResponse = CallResponse.Builder().apply {
        when (action) {
            CallAction.ALLOW, CallAction.WARN -> Unit
            CallAction.SILENCE -> setSilenceCall(true)
            CallAction.REJECT -> {
                setDisallowCall(true)
                setRejectCall(true)
            }
        }
    }.build()
}
