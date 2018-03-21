package com.rapidsos.era.authentication.password_reset.presenter

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import com.rapidsos.emergencydatasdk.auth.password.PasswordHelper
import com.rapidsos.era.authentication.password_reset.view.PasswordResetView
import io.reactivex.MaybeObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import okhttp3.ResponseBody
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.error
import org.jetbrains.anko.uiThread
import retrofit2.Response

/**
 * @author Josias Sena
 */
class PasswordResetPresenterImpl constructor(private val passwordHelper: PasswordHelper) :
        MvpBasePresenter<PasswordResetView>(), PasswordResetPresenter, AnkoLogger {

    private val compositeDisposable = CompositeDisposable()

    override fun resetPasswordForEmail(email: String) {
        if (isViewAttached) {
            view?.showLoading()
        }

        passwordHelper.resetPassword(email)
                .subscribe(object : MaybeObserver<Response<ResponseBody>> {

                    override fun onSubscribe(disposable: Disposable) {
                        compositeDisposable.add(disposable)
                    }

                    override fun onError(throwable: Throwable) {
                        error("Error resetting password", throwable)

                        if (isViewAttached) {
                            view?.doAsync {
                                uiThread {
                                    it.hideLoading()
                                    it.showMessage(throwable.message.toString())
                                }
                            }
                        }
                    }

                    override fun onSuccess(response: Response<ResponseBody>) {
                        if (isViewAttached) {
                            view?.hideLoading()
                            view?.onResetEmailSent()
                        }
                    }

                    override fun onComplete() {
                    }
                })
    }

    override fun dispose() {
        compositeDisposable.clear()
    }

}