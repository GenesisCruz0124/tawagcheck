package com.tawagcheck.app.service

import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.getSystemService

/** Wraps the ROLE_CALL_SCREENING check/request so the rest of the app doesn't touch RoleManager directly. */
class RoleRequestHelper(private val context: Context) {

    private val roleManager: RoleManager? = context.getSystemService()

    fun isRoleAvailable(): Boolean =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && roleManager?.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING) == true

    fun isRoleHeld(): Boolean =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && roleManager?.isRoleHeld(RoleManager.ROLE_CALL_SCREENING) == true

    fun createRequestRoleIntent(): Intent? =
        roleManager?.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
}
