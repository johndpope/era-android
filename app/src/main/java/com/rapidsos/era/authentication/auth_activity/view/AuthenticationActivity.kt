package com.rapidsos.era.authentication.auth_activity.view

import android.os.Bundle
import com.hannesdorfmann.mosby3.mvp.MvpActivity
import com.rapidsos.era.R
import com.rapidsos.era.application.App
import com.rapidsos.era.authentication.auth_activity.presenter.AuthPresenterImpl
import com.rapidsos.era.authentication.login.view.LoginFragment
import com.rapidsos.utils.utils.Utils
import javax.inject.Inject

class AuthenticationActivity : MvpActivity<AuthView, AuthPresenterImpl>() {

    @Inject
    lateinit var utils: Utils

    init {
        App.component.inject(this)
    }

    override fun createPresenter() = AuthPresenterImpl()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        utils.hideStatusBarWithDelay(window, 300)

        setContentView(R.layout.activity_authentication)

        goToLoginFragment()
    }

    /**
     * Loads the [LoginFragment] into the [R.id.authContainer]
     */
    private fun goToLoginFragment() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.authContainer, LoginFragment())
                .commit()
    }

}
