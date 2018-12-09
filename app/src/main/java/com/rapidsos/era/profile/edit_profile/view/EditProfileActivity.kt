package com.rapidsos.era.profile.edit_profile.view

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.DatePicker
import com.hannesdorfmann.mosby3.mvp.MvpActivity
import com.rapidsos.androidutils.isValidEmail
import com.rapidsos.emergencydatasdk.data.profile.*
import com.rapidsos.emergencydatasdk.data.profile.values.AddressValue
import com.rapidsos.emergencydatasdk.data.profile.values.EmailValue
import com.rapidsos.emergencydatasdk.data.profile.values.LanguageValue
import com.rapidsos.emergencydatasdk.data.profile.values.PhoneNumberValue
import com.rapidsos.era.R
import com.rapidsos.era.application.App
import com.rapidsos.era.profile.edit_profile.presenter.EditProfilePresenterImpl
import com.rapidsos.utils.extensions.getString
import com.rapidsos.utils.extensions.hideKeyboard
import com.rapidsos.utils.extensions.snack
import com.rapidsos.utils.utils.LanguageUtils
import com.rapidsos.utils.utils.Utils
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.edit_caller_location.*
import kotlinx.android.synthetic.main.edit_contact_info.*
import kotlinx.android.synthetic.main.edit_demographics.*
import kotlinx.android.synthetic.main.edit_medical_information.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.longToast
import java.util.*
import javax.inject.Inject

class EditProfileActivity : MvpActivity<EditProfileView, EditProfilePresenterImpl>(),
        EditProfileView, AnkoLogger, DatePickerDialog.OnDateSetListener {

    private val languageUtils = LanguageUtils()

    private val birthdayDialog: DatePickerDialog by lazy {
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val dayOfMonth = calendar[Calendar.DAY_OF_MONTH]

        DatePickerDialog(this, this, year, month, dayOfMonth)
    }

    private val bloodTypeSelected: String
        get() {
            val itemPosition = spinnerBloodType.selectedItemPosition

            if (itemPosition != 0) {
                return spinnerBloodType.selectedItem.toString()
            }

            return ""
        }

    private val languageSelected: String
        get() {
            val itemPosition = spinnerLanguage.selectedItemPosition

            if (itemPosition != 0) {
                return spinnerLanguage.selectedItem.toString()
            }

            return ""
        }

    private val genderSelected: String
        get() {
            val itemPosition = spinnerGenders.selectedItemPosition

            if (itemPosition != 0) {
                return spinnerGenders.selectedItem.toString()
            }

            return ""
        }

    private val countryCodeSelected: String
        get() {
            val itemPosition = spinnerCountryCodes.selectedItemPosition

            if (itemPosition != 0) {
                return spinnerCountryCodes.selectedItem.toString()
            }

            return ""
        }

    @Inject
    lateinit var utils: Utils

    @Inject
    lateinit var editProfilePresenter: EditProfilePresenterImpl

    private var birthdayInMillis: Long = 0

    private lateinit var profile: Profile

    override fun createPresenter() = editProfilePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        App.component.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        setSupportActionBar(toolbar)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initGenderSpinner()
        initLanguageSpinner()

        tvBirthday.setOnClickListener { birthdayDialog.show() }

        presenter.getProfileInformation()

        refreshLayoutEP.setOnRefreshListener {
            presenter.getProfileInformation()
        }
    }

    override fun onPause() {
        super.onPause()
        birthdayDialog.dismiss()
    }

    private fun initGenderSpinner() {
        val adapter = ArrayAdapter.createFromResource(this, R.array.genders,
                android.R.layout.simple_spinner_item)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGenders.adapter = adapter
        spinnerGenders.setSelection(0)
    }

    private fun initLanguageSpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item,
                languageUtils.getLanguagesSorted())

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLanguage.adapter = adapter
        spinnerLanguage.setSelection(0)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_save, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.options_save) {
            saveProfile()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun saveProfile() {
        if (etLabel.getString().isEmpty()
                .or(etAddress.getString().isEmpty())
                .or(etLocality.getString().isEmpty())
                .or(etPostalCode.getString().isEmpty())
                .or(etRegion.getString().isEmpty())
                .or(countryCodeSelected.isEmpty())
                .or(countryCodeSelected == "(Country Code)")) {

            etLabel.hideKeyboard()

            showError(getString(R.string.all_home_address_fields_required))

            val xScrollTo = tvCallerLocationHeader.x.toInt()
            val yScrollTo = tvCallerLocationHeader.y.toInt()
            svEditProfile.smoothScrollTo(xScrollTo, yScrollTo)

            etLabel.requestFocus()
        } else {

            profile.apply {

                if (birthdayInMillis > 0) {
                    birthday = Birthday().apply {
                        this.value = listOf(birthdayInMillis)
                    }
                } else {
                    birthday = null
                }

                if (genderSelected.isNotEmpty()) {
                    gender = Gender().apply {
                        this.value = listOf(genderSelected)
                    }
                } else {
                    gender = null
                }

                fullName = FullName().apply {
                    this.value = listOf(etFullName.getString())
                }

                val weightString = etWeight.getString()
                if (weightString.isNotEmpty()) {
                    weight = Weight().apply {
                        this.value = listOf(weightString.toInt())
                    }
                } else {
                    weight = null
                }

                if (languageSelected.isNotEmpty()) {
                    language = Language().apply {
                        this.value = arrayListOf(LanguageValue().apply {
                            this.languageCode = languageUtils.getLanguageCodeForLanguage(languageSelected)
                        })
                    }
                } else {
                    language = null
                }

                ethnicity = Ethnicity().apply {
                    this.value = listOf(etEthnicity.getString())
                }

                val heightFeet = etHeightFeet.getString()
                if (heightFeet.isNotEmpty()) {
                    val heightInches = etHeightInches.getString()
                    val heightInInches = presenter.getHeightInInches(heightFeet, heightInches)

                    height = Height().apply {
                        this.value = listOf(heightInInches)
                    }
                } else {
                    height = null
                }

                val occupationString = etOccupation.getString()
                if (occupationString.isNotEmpty()) {
                    occupation = Occupation().apply {
                        this.value = listOf(occupationString)
                    }
                } else {
                    occupation = null
                }

                address = Address().apply {
                    val addressValue = AddressValue().apply {
                        this.label = etLabel.getString()
                        this.streetAddress = etAddress.getString()
                        this.locality = etLocality.getString()
                        this.postalCode = etPostalCode.getString()
                        this.countryCode = countryCodeSelected
                        this.region = etRegion.getString()
                    }

                    this.value = listOf(addressValue)
                }

                savePhoneNumber()

                saveEmailAddress()

                allergy = Allergy().apply {
                    this.value = listOf(etAllergies.getString())
                }

                disability = Disability().apply {
                    this.value = listOf(etDisabilities.getString())
                }

                if (bloodTypeSelected.isNotEmpty()) {
                    bloodType = BloodType().apply {
                        this.value = listOf(bloodTypeSelected)
                    }
                } else {
                    bloodType = null
                }

                medicalNote = MedicalNote().apply {
                    this.value = listOf(etMedicalNotes.getString())
                }

                medicalCondition = MedicalCondition().apply {
                    this.value = listOf(etMedicalCondition.getString())
                }

                medication = Medication().apply {
                    this.value = listOf(etMedications.getString())
                }
            }
        }
    }

    private fun Profile.saveEmailAddress() {
        val emailString = etEmail.getString()
        if (emailString.isNotEmpty()) {

            if (emailString.isValidEmail()) {
                email = Email().apply {
                    val emailValue = EmailValue().apply {
                        this.emailAddress = emailString
                    }

                    this.value = listOf(emailValue)
                }
            } else {
                hideLoading()
                tilEmail.error = "Please enter a valid email address"
            }
        } else {
            email = null
        }
    }

    private fun Profile.savePhoneNumber() {
        var phoneNumberString = etPhone.getString()
        if (phoneNumberString.isNotEmpty()) {

            if (phoneNumberString.length < 10 || phoneNumberString.length > 12) {
                hideLoading()
                tilPhone.error = "Please enter a valid phone number"
            } else {

                if (!phoneNumberString.startsWith("+")) {
                    val desiredLength = phoneNumberString.length + 1
                    phoneNumberString = phoneNumberString.padStart(desiredLength, '+')
                }

                if (phoneNumberString[1] != '1') {
                    phoneNumberString = phoneNumberString.replace("+", "+1")
                }

                phoneNumber = PhoneNumber().apply {
                    val phoneNumberValue = PhoneNumberValue().apply {
                        this.label = etType.getString()
                        this.number = phoneNumberString
                    }

                    this.value = listOf(phoneNumberValue)
                }
            }
        } else {
            phoneNumber = null
        }
    }

    override fun displayProfile(profile: Profile) {
        this.profile = profile

        with(profile) {

            displayDemographicsInfo()

            displayContactInfo()

            displayCallerLocation()

            displayMedicalInfo()
        }
    }

    private fun Profile.displayMedicalInfo() {
        allergy?.let { etAllergies.setText(it.value.firstOrNull()) }
        disability?.let { etDisabilities.setText(it.value.firstOrNull()) }
        bloodType?.let {
            val bloodTypeList = resources.getStringArray(R.array.blood_types)
            val index = bloodTypeList.indexOf(it.value.first())
            spinnerBloodType.setSelection(index)
        }
        medicalCondition?.let { etMedicalCondition.setText(it.value.firstOrNull()) }
        medication?.let { etMedications.setText(it.value.firstOrNull()) }
        medicalNote?.let { etMedicalNotes.setText(it.value.firstOrNull()) }
    }

    private fun Profile.displayCallerLocation() {
        address?.let {
            it.value?.let {
                val addr = it.first()
                etLabel.setText(addr.label)
                etAddress.setText(addr.streetAddress)
                etLocality.setText(addr.locality)
                etPostalCode.setText(addr.postalCode)
                etRegion.setText(addr.region)

                val countryCode = addr.countryCode
                val countryCodeList = Arrays.asList(*resources.getStringArray(R.array.country_codes))
                val countryCodeIndex = countryCodeList.indexOf(countryCode)
                spinnerCountryCodes.setSelection(countryCodeIndex)
            }
        }
    }

    private fun Profile.displayContactInfo() {
        phoneNumber?.let {
            val value = it.value
            if (value.isNotEmpty()) {
                etType.setText(value.first().label)
                etPhone.setText(value.first().number)
            }
        }

        email?.let {
            val value = it.value

            if (value.isNotEmpty()) {
                etEmail.setText(value.first().emailAddress)
            }
        }
    }

    private fun Profile.displayDemographicsInfo() {
        fullName?.let {
            etFullName.setText(it.value.firstOrNull())
        }

        gender?.let {
            val gendersList = Arrays.asList(*resources.getStringArray(R.array.genders))
            val gender = it.value.firstOrNull()
            val genderIndex = gendersList.indexOf(gender)

            spinnerGenders.setSelection(genderIndex)
        }

        weight?.let { etWeight.setText(it.value.first().or(0).toString()) }

        language?.let {
            val languageCode = it.value[0].languageCode
            val languageCodeIndex = languageUtils.getLanguageCodeIndex(languageCode)
            spinnerLanguage.setSelection(languageCodeIndex)
        }

        birthday?.let {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = it.value.first().or(0)

            val month = calendar.get(Calendar.MONTH)
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
            val year = calendar.get(Calendar.YEAR)

            setBirthday(calendar.timeInMillis, "${month + 1}/$dayOfMonth/$year")
        }

        ethnicity?.let { etEthnicity.setText(it.value.firstOrNull()) }

        height?.let {
            val heightFeet = presenter.getFeetFromInches(it.value.first().or(0))
            val inchesRemaining = presenter.getRemainingInches(it.value.first().or(0))

            etHeightFeet.setText(heightFeet.toString())
            etHeightInches.setText(inchesRemaining.toString())
        }

        occupation?.let { etOccupation.setText(it.value.firstOrNull()) }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        // Create a calendar object for easy access to the birth date selected
        val calendar = Calendar.getInstance()
        calendar[Calendar.YEAR] = year
        calendar[Calendar.MONTH] = month
        calendar[Calendar.DAY_OF_MONTH] = dayOfMonth

        // parse the birth date string from the calendar object
        tvBirthday.text = "Birthday: ${month + 1}/$dayOfMonth/$year"

        // save the birth date in milliseconds for saving later on
        birthdayInMillis = calendar.timeInMillis
    }

    private fun setBirthday(timeInMillis: Long, birthDay: String?) {
        birthdayInMillis = timeInMillis

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = birthdayInMillis

        birthdayDialog.updateDate(calendar[Calendar.YEAR], calendar[Calendar.MONTH],
                calendar[Calendar.DAY_OF_MONTH])

        birthDay?.let {
            tvBirthday.text = "Birthday: $birthDay"
        }
    }

    override fun onSuccessUpdatingProfile() {
        longToast("Successfully updated profile information")
        onBackPressed()
    }

    override fun showError(error: String) = etFullName.snack(error)

    override fun showLoading() {
        refreshLayoutEP.hideKeyboard()
        refreshLayoutEP.isRefreshing = true
    }

    override fun hideLoading() {
        refreshLayoutEP.hideKeyboard()
        refreshLayoutEP.isRefreshing = false
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.dispose()
    }
}
