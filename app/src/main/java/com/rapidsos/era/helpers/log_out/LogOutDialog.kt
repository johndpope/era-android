package com.rapidsos.era.helpers.log_out

import android.content.Context
import android.support.v7.app.AlertDialog
import com.rapidsos.era.R
import com.rapidsos.era.application.App
import javax.inject.Inject

/**
 * A dialog that lets the user confirm if they want to log out or not.
 *
 * @see LogOutController
 *
 * @author Josias Sena
 */
class LogOutDialog(private val context: Context) {

    private val logOutDialog: AlertDialog by lazy {
        AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle(R.string.logout)
                .setMessage(context.getString(R.string.logout_confirmation))
                .setPositiveButton(R.string.yes, { _, _ ->
                    logOutController.logOut()
                })
                .setNegativeButton(R.string.no, { _, _ -> }).
                create()
    }

    @Inject
    lateinit var logOutController: LogOutController

    init {
        App.component.inject(this)
    }

    /**
     * Display the [logOutDialog] to the user to confirm if they want to really log out or not
     */
    fun show() = logOutDialog.show()

    /**
     * Hide the [logOutDialog]
     */
    fun dismiss() = logOutDialog.dismiss()
}