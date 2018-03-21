package com.rapidsos.era.splash_screen.presenter

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import com.rapidsos.era.splash_screen.view.SplashView
import com.rapidsos.utils.preferences.EraPreferences
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * @author Josias Sena
 */
class SplashScreenPresenterImpl @Inject constructor(private val preferences: EraPreferences) :
        MvpBasePresenter<SplashView>(), SplashScreenPresenter, AnkoLogger {

    private val compositeDisposable = CompositeDisposable()

    /**
     * Take the user to the next screen. Whatever that screen may be.
     */
    override fun goToNextScreen() = Observable.timer(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Long> {

                override fun onError(e: Throwable) = e.let { error(e.message, e) }

                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onNext(t: Long) {
                    if (isViewAttached) {
                        if (preferences.isLoggedIn()) {
                            if (preferences.isPinValidated()) {
                                view?.goToMainScreen()
                            } else {
                                view?.goToPhoneNumberScreen()
                            }

                        } else {
                            view?.goToLoginScreen()
                        }
                    }
                }

                override fun onComplete() = Unit
            })

    /**
     * Un-subscribe the presenter and all of the subscriptions in it
     */
    override fun unSubscribe() = compositeDisposable.clear()
}