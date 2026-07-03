package com.tawagcheck.app.ui.strings

import com.tawagcheck.app.data.model.CallAction
import com.tawagcheck.app.data.model.RiskTier
import com.tawagcheck.app.data.model.ScamCategory
import com.tawagcheck.app.data.model.ScamMatchType

object EnStrings : Strings {
    override val onboardingTitle = "Protect yourself from scam calls"
    override val onboardingBody =
        "TawagCheck needs to become your call screening app to check incoming calls against a " +
            "local scam database before your phone even rings. Everything stays on your device " +
            "— no caller data ever leaves your phone except when you manually update the scam list."
    override val onboardingEnableButton = "Enable call screening"
    override val onboardingContinueButton = "Continue"

    override val dashboardTitle = "Dashboard"
    override val protectionOn = "Protection is on"
    override val protectionOff = "Protection is off"
    override val callsScreenedToday = "Calls screened today"
    override val scamsBlocked = "Scams blocked"
    override val chartTitle = "Flagged calls, last 7 days"
    override val navDashboard = "Dashboard"
    override val navHistory = "History"
    override val navScamList = "Scam Numbers"
    override val navSettings = "Settings"

    override val historyTitle = "Call history"
    override val filterAll = "All"
    override val historyEmpty = "No screened calls yet."

    override val scamListTitle = "Scam Numbers"
    override val scamListEmpty = "No numbers in the local scam database yet."
    override val scamListAddTitle = "Add a scam number"
    override val scamListNumberLabel = "Phone number"
    override val scamListNumberError = "Enter a valid PH number, e.g. 09171234567"
    override val scamListTypeLabel = "Match type"
    override val scamListCategoryLabel = "Category"
    override val scamListAddButton = "Add"
    override val scamListCancelButton = "Cancel"

    override val settingsTitle = "Settings"
    override val settingsActionsSection = "Actions per risk tier"
    override val settingsHiddenNumberPolicy = "Hidden / private number policy"
    override val settingsContactsPermission = "Use contacts for detection"
    override val settingsContactsPermissionDesc =
        "Optional: lets TawagCheck flag repeated calls from unknown numbers. Degrades gracefully if denied."
    override val settingsUpdateSection = "Scam database"
    override val settingsUpdateUrlLabel = "Update URL"
    override val settingsUpdateButton = "Update database"
    override val settingsUpdating = "Updating..."
    override val settingsLastUpdated = "Last updated"
    override val settingsNeverUpdated = "Never"
    override val settingsLanguage = "Language"
    override val settingsExportCsv = "Export call history"
    override val settingsExportCsvDesc = "Share your call history as a CSV file."

    override fun riskTierLabel(tier: RiskTier): String = when (tier) {
        RiskTier.SAFE -> "Safe"
        RiskTier.SUSPICIOUS -> "Suspicious"
        RiskTier.LIKELY_SCAM -> "Likely scam"
    }

    override fun callActionLabel(action: CallAction): String = when (action) {
        CallAction.ALLOW -> "Allow"
        CallAction.WARN -> "Warn"
        CallAction.SILENCE -> "Silence"
        CallAction.REJECT -> "Reject"
    }

    override fun scamMatchTypeLabel(type: ScamMatchType): String = when (type) {
        ScamMatchType.FULL -> "Full number"
        ScamMatchType.PREFIX -> "Prefix"
    }

    override fun scamCategoryLabel(category: ScamCategory): String = when (category) {
        ScamCategory.SMISHING -> "Smishing"
        ScamCategory.FAKE_BANK -> "Fake bank"
        ScamCategory.SPOOFED -> "Spoofed"
        ScamCategory.USER_REPORTED -> "User reported"
    }
}
