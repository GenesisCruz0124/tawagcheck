package com.tawagcheck.app.data.model

/** What the screening service does with a call once it's been scored. */
enum class CallAction {
    ALLOW,
    WARN,
    SILENCE,
    REJECT
}
