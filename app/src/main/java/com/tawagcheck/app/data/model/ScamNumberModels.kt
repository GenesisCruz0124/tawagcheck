package com.tawagcheck.app.data.model

enum class ScamMatchType {
    FULL,
    PREFIX
}

enum class ScamCategory {
    SMISHING,
    FAKE_BANK,
    SPOOFED,
    USER_REPORTED
}

/** Where a scam_numbers row came from, so remote updates never clobber user reports. */
object ScamSource {
    const val SEED = "seed"
    const val REMOTE = "remote"
    const val USER_REPORTED = "user_reported"
}
