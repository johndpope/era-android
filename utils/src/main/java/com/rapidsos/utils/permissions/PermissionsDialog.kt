package com.rapidsos.utils.permissions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.support.v7.app.AlertDialog
import com.rapidsos.utils.R

class PermissionsDialog(private val context: Context) {

    private var dialog: AlertDialog

    init {
        dialog = AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle(context.getString(R.string.perms_req))
                .setMessage(context.getString(R.string.perm_req_dialog_body))
                .setPositiveButton(android.R.string.ok, { _, _ ->
                    goToPermissionsSettingsScreen(context)
                }).create()
    }

    private fun goToPermissionsSettingsScreen(context: Context) =
            context.startActivity(Intent().apply {
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                data = Uri.fromParts("package", context.packageName, null)
            })

    fun show() {
        dialog.show()
    }

    fun dismiss() {
        dialog.hide()
    }
}
