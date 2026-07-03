package com.tawagcheck.app.ui.strings

import com.tawagcheck.app.data.model.CallAction
import com.tawagcheck.app.data.model.RiskTier
import com.tawagcheck.app.data.model.ScamCategory
import com.tawagcheck.app.data.model.ScamMatchType

object TaglishStrings : Strings {
    override val onboardingTitle = "Iwas scam calls, protektahan ka namin"
    override val onboardingBody =
        "Kailangan ng TawagCheck na maging call screening app mo para ma-check ang mga tumatawag " +
            "laban sa local scam database bago pa man mag-ring ang phone mo. Nasa device mo lang " +
            "lahat — walang lumalabas na caller data maliban kung ikaw mismo mag-a-update ng scam list."
    override val onboardingEnableButton = "I-enable ang call screening"
    override val onboardingContinueButton = "Sige, tuloy"

    override val dashboardTitle = "Dashboard"
    override val protectionOn = "Naka-on ang proteksyon"
    override val protectionOff = "Naka-off ang proteksyon"
    override val callsScreenedToday = "Na-screen na tawag ngayong araw"
    override val scamsBlocked = "Na-block na scam"
    override val chartTitle = "Na-flag na tawag, huling 7 araw"
    override val navDashboard = "Dashboard"
    override val navHistory = "History"
    override val navScamList = "Scam Numbers"
    override val navSettings = "Settings"

    override val historyTitle = "Call history"
    override val filterAll = "Lahat"
    override val historyEmpty = "Wala pang na-screen na tawag."

    override val scamListTitle = "Scam Numbers"
    override val scamListEmpty = "Wala pang number sa local scam database."
    override val scamListAddTitle = "Magdagdag ng scam number"
    override val scamListNumberLabel = "Phone number"
    override val scamListNumberError = "Maglagay ng valid na PH number, hal. 09171234567"
    override val scamListTypeLabel = "Match type"
    override val scamListCategoryLabel = "Kategorya"
    override val scamListAddButton = "Idagdag"
    override val scamListCancelButton = "Kanselahin"

    override val settingsTitle = "Settings"
    override val settingsActionsSection = "Aksyon per risk tier"
    override val settingsHiddenNumberPolicy = "Policy para sa hidden / private number"
    override val settingsContactsPermission = "Gamitin ang contacts para sa detection"
    override val settingsContactsPermissionDesc =
        "Optional: para ma-flag ang paulit-ulit na tawag mula sa unknown number. Okay lang kung i-deny mo."
    override val settingsUpdateSection = "Scam database"
    override val settingsUpdateUrlLabel = "Update URL"
    override val settingsUpdateButton = "I-update ang database"
    override val settingsUpdating = "Ina-update..."
    override val settingsLastUpdated = "Huling na-update"
    override val settingsNeverUpdated = "Wala pa"
    override val settingsLanguage = "Wika"
    override val settingsExportCsv = "I-export ang call history"
    override val settingsExportCsvDesc = "I-share ang call history mo bilang CSV file."

    override fun riskTierLabel(tier: RiskTier): String = when (tier) {
        RiskTier.SAFE -> "Safe"
        RiskTier.SUSPICIOUS -> "Kaduda-duda"
        RiskTier.LIKELY_SCAM -> "Malamang scam"
    }

    override fun callActionLabel(action: CallAction): String = when (action) {
        CallAction.ALLOW -> "Payagan"
        CallAction.WARN -> "Balaan"
        CallAction.SILENCE -> "Patahimikin"
        CallAction.REJECT -> "Tanggihan"
    }

    override fun scamMatchTypeLabel(type: ScamMatchType): String = when (type) {
        ScamMatchType.FULL -> "Buong number"
        ScamMatchType.PREFIX -> "Prefix"
    }

    override fun scamCategoryLabel(category: ScamCategory): String = when (category) {
        ScamCategory.SMISHING -> "Smishing"
        ScamCategory.FAKE_BANK -> "Fake bank"
        ScamCategory.SPOOFED -> "Spoofed"
        ScamCategory.USER_REPORTED -> "User reported"
    }
}
