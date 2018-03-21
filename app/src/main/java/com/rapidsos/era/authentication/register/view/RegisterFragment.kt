package com.rapidsos.era.authentication.register.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hannesdorfmann.mosby3.mvp.MvpFragment
import com.rapidsos.androidutils.isValidEmail
import com.rapidsos.emergencydatasdk.data.user.User
import com.rapidsos.era.R
import com.rapidsos.era.application.App
import com.rapidsos.era.authentication.phone_number.view.PhoneNumberActivity
import com.rapidsos.era.authentication.register.presenter.RegisterPresenterImpl
import com.rapidsos.utils.extensions.hide
import com.rapidsos.utils.extensions.hideKeyboard
import com.rapidsos.utils.extensions.show
import com.rapidsos.utils.extensions.snack
import com.rapidsos.utils.utils.Utils
import kotlinx.android.synthetic.main.fragment_register.*
import javax.inject.Inject

class RegisterFragment : MvpFragment<RegisterView, RegisterPresenterImpl>(), RegisterView {

    @Inject
    lateinit var utils: Utils

    init {
        App.component.inject(this)
    }

    override fun createPresenter() = RegisterPresenterImpl()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_register, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnRegister.setOnClickListener { register() }
        tvLogin.setOnClickListener { fragmentManager?.popBackStackImmediate() }
        ivBackArrow.setOnClickListener { fragmentManager?.popBackStackImmediate() }
    }

    private fun register() {
        val userName = etRegisterUserName.text.toString()
        val email = etRegisterEmail.text.toString()
        val password = etRegisterPassword.text.toString()

        when {
            userName.isEmpty() -> {
                etRegisterUserName.requestFocus()
                tilRegisterUserName.error = getString(R.string.required)
            }
            (userName.length < 8).or(userName.length > 150) -> {
                etRegisterUserName.requestFocus()
                tilRegisterUserName.error = getString(R.string.length_error_8_150)
            }
            email.isEmpty() -> {
                etRegisterEmail.requestFocus()
                tilRegisterEmail.error = getString(R.string.required)
            }
            email.isValidEmail().not() -> {
                etRegisterEmail.requestFocus()
                tilRegisterEmail.error = getString(R.string.enter_valid_email)
            }
            password.isEmpty() -> {
                etRegisterPassword.requestFocus()
                tilRegisterPassword.error = getString(R.string.required)
            }
            (password.length < 8).or(password.length > 150) -> {
                etRegisterPassword.requestFocus()
                tilRegisterPassword.error = getString(R.string.length_error_8_150)
            }
            else -> {
                clearErrorsFromAllFields()

                etRegisterEmail.hideKeyboard()

                val user = User().apply {
                    this.username = userName
                    this.email = email
                    this.password = password
                }

                presenter.register(user)
            }
        }
    }

    private fun clearErrorsFromAllFields() {
        tilRegisterUserName.error = null
        tilRegisterEmail.error = null
        tilRegisterPassword.error = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.unSubscribe()
    }

    override fun onRegisteredSuccessfully() = PhoneNumberActivity.start(context)

    override fun showError(error: String) = etRegisterPassword.snack(error)

    /**
     * Display a loading view to the user
     */
    override fun showLoading() {
        activity?.runOnUiThread {
            registerMainView.hide()
            loadingView.show()
        }
    }

    /**
     * Hide loading views if any
     */
    override fun hideLoading() {
        activity?.runOnUiThread {
            loadingView.hide()
            registerMainView.show()
        }
    }
}