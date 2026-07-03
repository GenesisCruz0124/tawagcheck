package com.tawagcheck.app.ui.strings

import com.tawagcheck.app.data.model.CallAction
import com.tawagcheck.app.data.model.RiskTier

interface Strings {
    // Onboarding
    val onboardingTitle: String
    val onboardingBody: String
    val onboardingEnableButton: String
    val onboardingContinueButton: String

    // Dashboard
    val dashboardTitle: String
    val protectionOn: String
    val protectionOff: String
    val callsScreenedToday: String
    val scamsBlocked: String
    val chartTitle: String
    val navDashboard: String
    val navHistory: String
    val navSettings: String

    // History
    val historyTitle: String
    val filterAll: String
    val historyEmpty: String

    // Settings
    val settingsTitle: String
    val settingsActionsSection: String
    val settingsHiddenNumberPolicy: String
    val settingsContactsPermission: String
    val settingsContactsPermissionDesc: String
    val settingsUpdateSection: String
    val settingsUpdateUrlLabel: String
    val settingsUpdateButton: String
    val settingsUpdating: String
    val settingsLastUpdated: String
    val settingsNeverUpdated: String
    val settingsLanguage: String
    val settingsExportCsv: String
    val settingsExportCsvDesc: String

    // Shared labels
    fun riskTierLabel(tier: RiskTier): String
    fun callActionLabel(action: CallAction): String
}
