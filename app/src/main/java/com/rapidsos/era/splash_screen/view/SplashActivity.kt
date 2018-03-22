package com.rapidsos.era.splash_screen.view

import android.os.Bundle
import com.hannesdorfmann.mosby3.mvp.MvpActivity
import com.rapidsos.era.R
import com.rapidsos.era.application.App
import com.rapidsos.era.authentication.auth_activity.view.AuthenticationActivity
import com.rapidsos.era.authentication.phone_number.view.PhoneNumberActivity
import com.rapidsos.era.main.view.MainActivity
import com.rapidsos.era.splash_screen.presenter.SplashScreenPresenterImpl
import com.rapidsos.utils.utils.Utils
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import javax.inject.Inject

class SplashActivity : MvpActivity<SplashView, SplashScreenPresenterImpl>(), SplashView {

    @Inject
    lateinit var utils: Utils

    @Inject
    lateinit var presenterImpl: SplashScreenPresenterImpl

    init {
        App.component.inject(this)
    }

    override fun createPresenter() = presenterImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        utils.hideStatusBarWithDelay(window, 300)

        setContentView(R.layout.activity_splash)

        presenter.goToNextScreen()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unSubscribe()
    }

    /**
     * Take the user to the main app screen
     */
    override fun goToMainScreen() = startActivity(intentFor<MainActivity>()
            .clearTask().newTask())

    /**
     * Take the user to the login screen
     */
    override fun goToLoginScreen() = startActivity(intentFor<AuthenticationActivity>()
            .clearTask().newTask())

    /**
     * Take the user to the phone number screen
     */
    override fun goToPhoneNumberScreen() = startActivity(intentFor<PhoneNumberActivity>()
            .clearTask().newTask())
}
