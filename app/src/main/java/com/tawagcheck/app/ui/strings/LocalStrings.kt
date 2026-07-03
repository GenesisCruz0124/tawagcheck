package com.tawagcheck.app.ui.strings

import androidx.compose.runtime.staticCompositionLocalOf
import com.tawagcheck.app.data.model.AppLanguage

val LocalStrings = staticCompositionLocalOf<Strings> { EnStrings }

fun stringsFor(language: AppLanguage): Strings = when (language) {
    AppLanguage.ENGLISH -> EnStrings
    AppLanguage.TAGLISH -> TaglishStrings
}
