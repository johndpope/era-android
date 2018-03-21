package com.rapidsos.era.authentication.login.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hannesdorfmann.mosby3.mvp.MvpFragment
import com.rapidsos.era.R
import com.rapidsos.era.application.App
import com.rapidsos.era.authentication.login.presenter.LoginPresenterImpl
import com.rapidsos.era.authentication.password_reset.view.PasswordResetActivity
import com.rapidsos.era.authentication.phone_number.view.PhoneNumberActivity
import com.rapidsos.era.authentication.register.view.RegisterFragment
import com.rapidsos.utils.extensions.hide
import com.rapidsos.utils.extensions.show
import com.rapidsos.utils.extensions.snack
import com.rapidsos.utils.utils.Utils
import kotlinx.android.synthetic.main.fragment_login.*
import javax.inject.Inject

class LoginFragment : MvpFragment<LoginView, LoginPresenterImpl>(), LoginView {

    @Inject
    lateinit var utils: Utils

    init {
        App.component.inject(this)
    }

    override fun createPresenter() = LoginPresenterImpl()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_login, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnLogin.setOnClickListener { logIn() }

        tvForgotLoginDetails.setOnClickListener { PasswordResetActivity.start(context) }
        tvRegisterNewAccount.setOnClickListener { goToRegisterFragment() }
    }

    /**
     * Log the user in.
     *
     * - Make sure the email is not empty and it is in the right format
     * - Make sure the password is not empty
     * - If so, tell the presenter to log in using the email and password
     */
    private fun logIn() {
        val userName = etLoginUsername.text.toString()

        when {
            userName.isEmpty() -> {
                tilLoginUsername.requestFocus()
                tilLoginUsername.error = getString(R.string.required)
            }
            (userName.length <= 7).or(userName.length > 150) -> {
                tilLoginUsername.requestFocus()
                tilLoginUsername.error = getString(R.string.length_error_8_150)
            }
            etLoginPassword.text.toString().isEmpty() -> {
                tilLoginPassword.requestFocus()
                tilLoginPassword.error = getString(R.string.required)
            }
            else -> {
                utils.hideKeyboard(btnLogin)
                presenter.logIn(userName, etLoginPassword.text.toString())
            }
        }
    }

    override fun onPause() {
        super.onPause()
        hideLoading()
    }

    /**
     * Called when the user has successfully been logged in.
     */
    override fun onLoggedInSuccessfully() = PhoneNumberActivity.start(context)

    /**
     * Display a loading view to the user
     */
    override fun showLoading() {
        activity?.runOnUiThread {
            loginMainView.hide()
            loadingView.show()
        }
    }

    /**
     * Hide loading views if any
     */
    override fun hideLoading() {
        activity?.runOnUiThread {
            loadingView.hide()
            loginMainView.show()
        }
    }

    override fun showError(error: String) = loginMainView.snack(error)

    /**
     * Loads the [RegisterFragment] into the [R.id.authContainer]
     */
    private fun goToRegisterFragment() {
        activity?.supportFragmentManager?.beginTransaction()
                ?.addToBackStack(null)
                ?.replace(R.id.authContainer, RegisterFragment())
                ?.commit()
    }
}