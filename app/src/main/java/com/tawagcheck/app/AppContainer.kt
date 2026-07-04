package com.tawagcheck.app

import android.content.Context
import androidx.room.Room
import com.tawagcheck.app.data.local.contacts.ContactsLookup
import com.tawagcheck.app.data.local.datastore.SettingsDataStore
import com.tawagcheck.app.data.local.db.AppDatabase
import com.tawagcheck.app.data.local.seed.ScamListSeeder
import com.tawagcheck.app.data.remote.ScamListUpdateService
import com.tawagcheck.app.data.repository.CallHistoryRepository
import com.tawagcheck.app.data.repository.ScamRepository
import com.tawagcheck.app.domain.heuristics.HeuristicsEngine
import com.tawagcheck.app.domain.normalization.PhoneNumberNormalizer
import com.tawagcheck.app.notification.VerdictNotificationManager

/** Simple hand-rolled service locator — the app is small enough not to need a DI framework. */
class AppContainer(context: Context) {

    private val appContext = context.applicationContext

    val database: AppDatabase by lazy {
        Room.databaseBuilder(appContext, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
            // Phase 1, no real user data at stake yet - destructive fallback instead of hand-writing
            // migrations for every schema tweak. Revisit before this app has data worth preserving.
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    val settingsDataStore: SettingsDataStore by lazy { SettingsDataStore(appContext) }

    val scamRepository: ScamRepository by lazy { ScamRepository(database.scamNumberDao()) }

    val callHistoryRepository: CallHistoryRepository by lazy { CallHistoryRepository(database.callHistoryDao()) }

    val contactsLookup: ContactsLookup by lazy { ContactsLookup(appContext) }

    val phoneNumberNormalizer: PhoneNumberNormalizer by lazy { PhoneNumberNormalizer() }

    val heuristicsEngine: HeuristicsEngine by lazy {
        HeuristicsEngine(
            scamRepository = scamRepository,
            callHistoryRepository = callHistoryRepository,
            contactsLookup = contactsLookup,
            settingsDataStore = settingsDataStore,
            normalizer = phoneNumberNormalizer
        )
    }

    val scamListSeeder: ScamListSeeder by lazy { ScamListSeeder(appContext, database.scamNumberDao()) }

    val scamListUpdateService: ScamListUpdateService by lazy { ScamListUpdateService() }

    val verdictNotificationManager: VerdictNotificationManager by lazy { VerdictNotificationManager(appContext) }
}
