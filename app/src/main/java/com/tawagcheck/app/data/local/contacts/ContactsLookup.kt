package com.tawagcheck.app.data.local.contacts

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.ContactsContract
import androidx.core.content.ContextCompat
import androidx.core.net.toUri

/** Wraps the optional READ_CONTACTS lookup used by the "not in contacts" heuristic. */
class ContactsLookup(private val context: Context) {

    fun hasPermission(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) ==
            PackageManager.PERMISSION_GRANTED

    /** Returns null (unknown) when permission isn't granted, instead of a false "not a contact". */
    fun isKnownContact(e164Number: String): Boolean? {
        if (!hasPermission()) return null

        val uri = "content://com.android.contacts/phone_lookup".toUri()
            .buildUpon()
            .appendPath(e164Number)
            .build()

        return context.contentResolver.query(
            uri,
            arrayOf(ContactsContract.PhoneLookup._ID),
            null,
            null,
            null
        )?.use { cursor -> cursor.count > 0 } ?: false
    }

    /** Returns the saved contact's display name for this number, or null if unknown/no permission. */
    fun lookupContactName(e164Number: String): String? {
        if (!hasPermission()) return null

        val uri = "content://com.android.contacts/phone_lookup".toUri()
            .buildUpon()
            .appendPath(e164Number)
            .build()

        return context.contentResolver.query(
            uri,
            arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME),
            null,
            null,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)
                if (nameIndex >= 0) cursor.getString(nameIndex) else null
            } else {
                null
            }
        }
    }
}
