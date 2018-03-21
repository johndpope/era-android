package com.rapidsos.era.authentication.phone_number.view

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.hannesdorfmann.mosby3.mvp.MvpActivity
import com.rapidsos.era.R
import com.rapidsos.era.application.App
import com.rapidsos.era.authentication.phone_number.presenter.PhoneNumberPresenterImpl
import com.rapidsos.era.helpers.log_out.LogOutDialog
import com.rapidsos.era.main.view.MainActivity
import com.rapidsos.utils.constants.permissionsList
import com.rapidsos.utils.extensions.*
import com.rapidsos.utils.permissions.PermissionsDialog
import com.rapidsos.utils.preferences.EraPreferences
import com.rapidsos.utils.utils.Utils
import kotlinx.android.synthetic.main.activity_phone_number.*
import org.jetbrains.anko.AnkoLogger
import javax.inject.Inject

class PhoneNumberActivity : MvpActivity<PhoneNumberView, PhoneNumberPresenterImpl>(),
        PhoneNumberView, AnkoLogger {

    private val logOutDialog by lazy { LogOutDialog(this) }

    private val termsOfServiceDialog by lazy {
        AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(R.string.tos_title)
                .setMessage(R.string.terms_of_service)
                .setPositiveButton(android.R.string.ok, { dialog, _ ->
                    dialog.dismiss()
                }).create()
    }

    private val privacyPolicyDialog by lazy {
        AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(R.string.privacy_policy)
                .setMessage(R.string.privacy_policy_text)
                .setPositiveButton(android.R.string.ok) { dialog, _ ->
                    dialog.dismiss()
                }.create()
    }

    @Inject
    lateinit var utils: Utils

    @Inject
    lateinit var preferences: EraPreferences

    private var isPinViewsVisible = false

    private lateinit var permissionsDialog: PermissionsDialog

    companion object {
        private const val PERMISSIONS_REQUEST_CODE = 159

        fun start(context: Context?) {
            val starter = Intent(context, PhoneNumberActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context?.startActivity(starter)
        }
    }

    init {
        App.component.inject(this)
    }

    override fun createPresenter() = PhoneNumberPresenterImpl()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_number)

        permissionsDialog = PermissionsDialog(this)

        val currentPhoneNumber = preferences.getCurrentDevicePhoneNumber()

        if (currentPhoneNumber.isEmpty()) {
            btnProceed.setOnClickListener {
                val phoneNumber = etInput.getString()

                etInput.hideKeyboard()

                presenter.requestPin(phoneNumber)

                showPinViews()
            }
        } else {
            showPinViews()
        }

        tvResendPin.setOnClickListener { view ->
            view.hideKeyboard()

            presenter.requestPin(currentPhoneNumber)
        }

        initTermsOfUseAndPrivacyPolicyTV()
    }

    private fun initTermsOfUseAndPrivacyPolicyTV() {
        val tOsPpString = getString(R.string.terms_of_use_and_privacy_policy)
        val spannableString = SpannableString(tOsPpString)
        val flags = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE

        val tosStartIndex = tOsPpString.indexOf("Terms of Use")
        val tosEndIndex = tosStartIndex + "Terms of Use".length

        spannableString.setSpan(object : ClickableSpan() {
            override fun onClick(textView: View) {
                termsOfServiceDialog.show()
            }
        }, tosStartIndex, tosEndIndex, flags)

        val ppStartIndex = tOsPpString.indexOf("Privacy Policy")
        val ppEndIndex = ppStartIndex + "Privacy Policy".length

        spannableString.setSpan(object : ClickableSpan() {
            override fun onClick(textView: View) {
                privacyPolicyDialog.show()
            }
        }, ppStartIndex, ppEndIndex, flags)

        tvTermsAndPrivacy.text = spannableString
        tvTermsAndPrivacy.highlightColor = resources.getColor(android.R.color.transparent)
        tvTermsAndPrivacy.movementMethod = LinkMovementMethod.getInstance()
    }

    override fun onPause() {
        super.onPause()
        permissionsDialog.dismiss()
        logOutDialog.dismiss()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_log_out, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item?.let {
            when (it.itemId) {
                R.id.options_logout -> logOutDialog.show()
                android.R.id.home -> hidePinViews()
            }
        }

        return true
    }

    /**
     * Displays all of the views related to a PIN
     */
    override fun showPinViews() = runOnUiThread {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        tvResendPin.show()

        tvInfo.text = getString(R.string.enter_verification_code)

        tilInput.hint = getString(R.string.pin_code)
        etInput.clearText()

        btnProceed.setOnClickListener {
            utils.hideKeyboard(btnProceed)

            val pin = etInput.text.toString()

            if (pin.isEmpty().or(pin.length > 5)) {
                tilInput.error = getString(R.string.enter_valid_pin)
            } else {
                presenter.validatePin(pin.toInt())
            }
        }

        isPinViewsVisible = true
    }

    private fun hidePinViews() = runOnUiThread {
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        tvResendPin.setInvisible()

        tvInfo.text = getString(R.string.phone_screen_text)

        tilInput.hint = getString(R.string.phone_number)
        etInput.clearText()

        btnProceed.setOnClickListener {
            val phoneNumber = etInput.getString()

            utils.hideKeyboard(btnProceed)

            presenter.requestPin(phoneNumber)

            showPinViews()
        }

        isPinViewsVisible = true
    }

    /**
     * Take the user to the main app screen
     */
    override fun goToMainScreen() {
        if (utils.isAllPermissionsGranted()) {
            MainActivity.start(this)
        } else {
            ActivityCompat.requestPermissions(this, permissionsList, PERMISSIONS_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        var isAllPermissionsGranted = true

        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            permissions.forEachIndexed { index, permission ->
                if (grantResults[index] == PackageManager.PERMISSION_DENIED) {
                    isAllPermissionsGranted = false

                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                        permissionsDialog.show()
                    }

                    return
                }
            }

            if (isAllPermissionsGranted) {
                goToMainScreen()
            }
        }
    }

    /**
     * Display an error to the user
     */
    override fun showError(error: String) = etInput.snack(error)

    /**
     * Display a loading view to the user
     */
    override fun showLoading() {
        btnProceed.startAnimation()
    }

    /**
     * Hide loading views if any
     */
    override fun hideLoading() = runOnUiThread { btnProceed.revertAnimation() }

    override fun onDestroy() {
        super.onDestroy()
        btnProceed.dispose()
    }

    override fun onBackPressed() {
        if (isPinViewsVisible) {
            hidePinViews()
        } else {
            super.onBackPressed()
        }
    }
}
