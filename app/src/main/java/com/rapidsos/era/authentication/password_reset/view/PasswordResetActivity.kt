package com.rapidsos.era.authentication.password_reset.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.hannesdorfmann.mosby3.mvp.MvpActivity
import com.rapidsos.androidutils.isValidEmail
import com.rapidsos.emergencydatasdk.auth.password.PasswordHelper
import com.rapidsos.era.R
import com.rapidsos.era.application.App
import com.rapidsos.era.authentication.password_reset.presenter.PasswordResetPresenterImpl
import com.rapidsos.utils.extensions.hideKeyboard
import com.rapidsos.utils.extensions.snack
import com.rapidsos.utils.utils.Utils
import kotlinx.android.synthetic.main.activity_password_reset.*
import org.jetbrains.anko.longToast
import javax.inject.Inject

class PasswordResetActivity : MvpActivity<PasswordResetView, PasswordResetPresenterImpl>(),
        PasswordResetView {

    @Inject
    lateinit var utils: Utils

    companion object {
        @JvmStatic
        fun start(context: Context?) {
            context?.startActivity(Intent(context, PasswordResetActivity::class.java))
        }
    }

    init {
        App.component.inject(this)
    }

    override fun createPresenter() = PasswordResetPresenterImpl(PasswordHelper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_reset)

        btnResetPwd.setOnClickListener { view ->
            view.hideKeyboard()
            resetPassword()
        }
    }

    private fun resetPassword() {
        val email = etResetEmail.text.toString()

        when {
            email.isEmpty() -> tilPwdResetEmail.error = getString(R.string.cannot_be_empty)
            email.isValidEmail().not() -> {
                tilPwdResetEmail.error = getString(R.string.enter_valid_email)
            }
            else -> presenter.resetPasswordForEmail(email)
        }
    }

    override fun onStart() {
        super.onStart()
        utils.hideStatusBar(window)
    }

    override fun onPause() {
        super.onPause()
        btnResetPwd.stopAnimation()
    }

    override fun onDestroy() {
        super.onDestroy()
        btnResetPwd.dispose()
        presenter.dispose()
    }

    override fun showLoading() = btnResetPwd.startAnimation()

    override fun hideLoading() = btnResetPwd.revertAnimation()

    override fun showMessage(message: String) = btnResetPwd.snack(message)

    override fun onResetEmailSent() {
        longToast(getString(R.string.pwd_reset_email_sent))
        onBackPressed()
    }
}