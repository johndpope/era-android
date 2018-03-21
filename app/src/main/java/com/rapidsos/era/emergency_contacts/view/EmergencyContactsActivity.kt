package com.rapidsos.era.emergency_contacts.view

import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.hannesdorfmann.mosby3.mvp.MvpActivity
import com.rapidsos.androidutils.isValidEmail
import com.rapidsos.androidutils.isValidName
import com.rapidsos.androidutils.removeSpaces
import com.rapidsos.emergencydatasdk.data.profile.values.EmergencyContactValue
import com.rapidsos.era.R
import com.rapidsos.era.application.App
import com.rapidsos.era.emergency_contacts.presenter.EmergencyContactsPresenterImpl
import com.rapidsos.era.emergency_contacts.view.rec_view.EmergencyContactPosition
import com.rapidsos.era.emergency_contacts.view.rec_view.EmergencyContactsAdapter
import com.rapidsos.utils.extensions.clearText
import com.rapidsos.utils.extensions.getString
import com.rapidsos.utils.extensions.hideKeyboard
import com.rapidsos.utils.extensions.snack
import com.rapidsos.utils.utils.Utils
import kotlinx.android.synthetic.main.activity_emergency_contacts.*
import kotlinx.android.synthetic.main.content_emergency_contacts.*
import kotlinx.android.synthetic.main.dialog_emergency_contact.view.*
import org.jetbrains.anko.AnkoLogger
import javax.inject.Inject

class EmergencyContactsActivity : MvpActivity<EmergencyContactsView,
        EmergencyContactsPresenterImpl>(), EmergencyContactsView, AnkoLogger {

    private val emgContactsAdapter = EmergencyContactsAdapter()

    @Inject
    lateinit var utils: Utils

    private lateinit var contactDialog: AlertDialog
    private lateinit var deleteContactDialog: AlertDialog
    private lateinit var contactDialogView: View

    override fun createPresenter() = EmergencyContactsPresenterImpl()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emergency_contacts)
        setSupportActionBar(toolbar)
        App.component.inject(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initRecView()

        buildContactDialog()
        buildDeleteContactDialog()

        fabAddEmergencyContact.setOnClickListener { view ->
            view.hideKeyboard()

            if (emgContactsAdapter.itemCount == 5) {
                view.snack(getString(R.string.max_contacts_reached))
            } else {
                buildAndShowTheContactDialog()
            }
        }

        refreshLayoutEmg.setOnRefreshListener {
            presenter.getEmergencyContacts()
        }

        emgContactsAdapter.onClickListener
                .subscribe({ contactPosition: EmergencyContactPosition ->
                    editContact(contactPosition.position, contactPosition.contact)
                })

        emgContactsAdapter.onLongClickListener
                .subscribe({ contact: EmergencyContactValue ->
                    showDeleteContactDialog(contact)
                })
    }

    private fun buildAndShowTheContactDialog() {
        clearErrorsFromContactDialogFields()

        contactDialogView.etEmgFullName.clearText()
        contactDialogView.etEmgPhoneNumber.clearText()
        contactDialogView.etEmgEmail.clearText()
        contactDialog.show()

        contactDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            saveNewContact()
        }
    }

    private fun clearErrorsFromContactDialogFields() {
        contactDialogView.tilEmgFullName.error = null
        contactDialogView.tilEmgPhoneNumber.error = null
        contactDialogView.tilEmgEmail.error = null
    }

    private fun saveNewContact() {
        val fullName = contactDialogView.etEmgFullName.getString()
        var phoneNumber = contactDialogView.etEmgPhoneNumber.getString()
        val email = contactDialogView.etEmgEmail.getString()

        if (fullName.isEmpty()) {
            contactDialogView.tilEmgFullName?.error = getString(R.string.required)
        } else if (!fullName.removeSpaces().isValidName()) {
            contactDialogView.tilEmgFullName?.error = getString(R.string.enter_valid_name)
        } else if (phoneNumber.isEmpty()) {
            contactDialogView.tilEmgPhoneNumber?.error = getString(R.string.required)
        } else if (email.isEmpty()) {
            contactDialogView.tilEmgEmail?.error = getString(R.string.required)
        } else if (!email.isValidEmail()) {
            contactDialogView.tilEmgEmail?.error = getString(R.string.enter_valid_email)
        } else if (emgContactsAdapter.contacts.any { contact -> contact.phone.contains(phoneNumber, true) }) {
            contactDialogView.tilEmgPhoneNumber?.error = getString(R.string.cannot_add_similar_emg_contact)
        } else {
            if (phoneNumber.length < 10 || phoneNumber.length > 12) {
                contactDialogView.tilEmgPhoneNumber.error = getString(R.string.enter_valid_phone)
            } else {
                if (!phoneNumber.startsWith("+")) {
                    val desiredLength = phoneNumber.length + 1
                    phoneNumber = phoneNumber.padStart(desiredLength, '+')
                }

                if (phoneNumber[1] != '1') {
                    phoneNumber = phoneNumber.replace("+", "+1")
                }

                val emergencyContactValue = EmergencyContactValue().apply {
                    this.fullName = fullName
                    this.phone = phoneNumber
                    this.email = email
                }

                presenter.addEmgContact(emergencyContactValue)

                contactDialog.dismiss()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        presenter.getEmergencyContacts()
    }

    private fun initRecView() {
        rvEmgContacts.adapter = emgContactsAdapter
        rvEmgContacts.layoutManager = LinearLayoutManager(this)
        rvEmgContacts.setItemViewCacheSize(30)
    }

    private fun buildContactDialog() {
        contactDialogView = layoutInflater.inflate(R.layout.dialog_emergency_contact, null)

        contactDialog = AlertDialog.Builder(this)
                .setView(contactDialogView)
                .setCancelable(false)
                .setPositiveButton(R.string.save, { _, _ -> })
                .setNegativeButton(R.string.cancel, null)
                .create()
    }

    private fun buildDeleteContactDialog() {
        deleteContactDialog = AlertDialog.Builder(this)
                .setCancelable(false)
                .setNegativeButton(R.string.no, { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                })
                .create()
    }

    override fun onGotEmergencyContacts(emergencyContacts: List<EmergencyContactValue>) =
            emgContactsAdapter.setContacts(emergencyContacts)

    override fun onPause() {
        super.onPause()
        contactDialog.dismiss()
        deleteContactDialog.dismiss()
    }

    override fun showError(error: String) = rvEmgContacts.snack(error)

    override fun showLoading() = runOnUiThread {
        refreshLayoutEmg.isRefreshing = true
    }

    override fun hideLoading() = runOnUiThread {
        refreshLayoutEmg.isRefreshing = false
    }

    override fun showDeleteContactDialog(contact: EmergencyContactValue) {
        deleteContactDialog.setMessage("Are you sure you want to delete ${contact.fullName}?")
        deleteContactDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.yes), { _, _ ->
            presenter.deleteContact(contact)
        })

        deleteContactDialog.show()
    }

    override fun editContact(index: Int, contact: EmergencyContactValue) {
        clearErrorsFromContactDialogFields()

        contactDialogView.etEmgFullName.setText(contact.fullName)
        contactDialogView.etEmgPhoneNumber.setText(contact.phone)
        contactDialogView.etEmgEmail.setText(contact.email)

        contactDialog.show()
        contactDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            updateContact(index, contact)
        }
    }

    private fun updateContact(index: Int, contact: EmergencyContactValue) {
        val fullName = contactDialogView.etEmgFullName.getString()
        var phoneNumber = contactDialogView.etEmgPhoneNumber.getString()
        val email = contactDialogView.etEmgEmail.getString()

        if (fullName.isEmpty()) {
            contactDialogView.tilEmgFullName?.error = getString(R.string.required)
        } else if (!fullName.removeSpaces().isValidName()) {
            contactDialogView.tilEmgFullName?.error = getString(R.string.enter_valid_name)
        } else if (phoneNumber.isEmpty()) {
            contactDialogView.tilEmgPhoneNumber?.error = getString(R.string.required)
        } else if (email.isEmpty()) {
            contactDialogView.tilEmgEmail?.error = getString(R.string.required)
        } else if (!email.isValidEmail()) {
            contactDialogView.tilEmgEmail?.error = getString(R.string.enter_valid_email)
        } else {
            if (phoneNumber.length < 10 || phoneNumber.length > 12) {
                contactDialogView.tilEmgPhoneNumber.error = getString(R.string.enter_valid_phone)
            } else {
                if (!phoneNumber.startsWith("+")) {
                    val desiredLength = phoneNumber.length + 1
                    phoneNumber = phoneNumber.padStart(desiredLength, '+')
                }

                if (phoneNumber[1] != '1') {
                    phoneNumber = phoneNumber.replace("+", "+1")
                }

                contact.fullName = fullName
                contact.phone = phoneNumber
                contact.email = email

                presenter.updateEmgContact(index, contact)

                contactDialog.dismiss()
            }
        }
    }
}
