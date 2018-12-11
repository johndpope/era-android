package com.rapidsos.era.profile.view

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hannesdorfmann.mosby3.mvp.MvpFragment
import com.rapidsos.emergencydatasdk.data.profile.Profile
import com.rapidsos.era.R
import com.rapidsos.era.application.App
import com.rapidsos.era.emergency_contacts.view.EmergencyContactsActivity
import com.rapidsos.era.emergency_contacts.view.rec_view.EmergencyContactsAdapter
import com.rapidsos.era.profile.edit_profile.view.EditProfileActivity
import com.rapidsos.era.profile.presenter.ProfilePresenterImpl
import com.rapidsos.utils.extensions.hide
import com.rapidsos.utils.extensions.show
import com.rapidsos.utils.utils.LanguageUtils
import kotlinx.android.synthetic.main.caller_location.*
import kotlinx.android.synthetic.main.contact_info.*
import kotlinx.android.synthetic.main.demographics.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.medical_information.*
import kotlinx.android.synthetic.main.modify_emergency_contacts_card.*
import org.jetbrains.anko.AnkoLogger
import java.util.*
import javax.inject.Inject

class ProfileFragment : MvpFragment<ProfileView, ProfilePresenterImpl>(), ProfileView, AnkoLogger {

    private val emgContactsAdapter = EmergencyContactsAdapter()
    private val languageUtils = LanguageUtils()

    @Inject
    lateinit var presenterImpl: ProfilePresenterImpl

    override fun createPresenter() = presenterImpl

    init {
        App.component.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnEditContacts.setOnClickListener {
            startActivity(Intent(context, EmergencyContactsActivity::class.java))
        }

        btnEditProfile.setOnClickListener {
            startActivity(Intent(context, EditProfileActivity::class.java))
        }

        llEmgContactsHeader.setOnClickListener {

            TransitionManager.beginDelayedTransition(llContactsCardBody)

            if (llContactsCardBody.isShown) {
                llContactsCardBody.hide()
                ivCollapseExpand.setImageResource(R.drawable.ic_expand)
            } else {
                llContactsCardBody.show()
                ivCollapseExpand.setImageResource(R.drawable.ic_collapse)
                rvHomeEmgContacts.requestFocus()
                llEmgContactsHeader.clearFocus()
            }
        }

        initRecView()
    }

    private fun initRecView() {
        rvHomeEmgContacts.adapter = emgContactsAdapter
        rvHomeEmgContacts.layoutManager = LinearLayoutManager(context)
    }

    override fun onStart() {
        super.onStart()
        presenter.getCurrentProfile()
    }

    override fun onGotProfileData(profile: Profile) = with(profile) {

        profile.emergencyContact?.let {
            val emergencyContacts = it.value

            if (emergencyContacts.isEmpty()) {
                btnEditContacts.text = getString(R.string.add_contacts)
            } else {
                btnEditContacts.text = getString(R.string.edit_contacts)
            }

            emgContactsAdapter.setContacts(emergencyContacts)
        }

        displayDemographicsInfo()

        displayContactInfo()

        displayCallerLocation()

        displayMedicalInfo()
    }

    private fun Profile.displayMedicalInfo() {
        allergy?.let {
            tvAllergies.text = it.value.firstOrNull()
        }

        disability?.let {
            tvDisabilities.text = it.value.firstOrNull()
        }

        bloodType?.let {
            tvBloodType.text = it.value.firstOrNull()
        }

        medicalCondition?.let {
            tvMedicalCondition.text = it.value.firstOrNull()
        }

        medication?.let {
            tvMedications.text = it.value.firstOrNull()
        }

        medicalNote?.let {
            tvMedicalNotes.text = it.value.firstOrNull()
        }
    }

    private fun Profile.displayCallerLocation() {
        address?.let {
            it.value?.let {
                val addr = it.first()
                tvLabel.text = addr.label
                tvAddress.text = addr.streetAddress
                tvCity.text = addr.locality
                tvZipCode.text = addr.postalCode
                tvState.text = addr.region
                tvCountryCode.text = addr.countryCode
            }
        }
    }

    private fun Profile.displayContactInfo() {
        phoneNumber?.let {
            val value = it.value
            if (value.isNotEmpty()) {
                tvPhoneNumberType.text = value.first().label
                tvPhoneNumber.text = value.first().number
            }
        }

        email?.let {
            val value = it.value

            if (value.isNotEmpty()) {
                tvEmail.text = value.first().emailAddress
            }
        }
    }

    private fun Profile.displayDemographicsInfo() {
        fullName?.let { tvFullName.text = it.value.firstOrNull() }

        gender?.let { tvGender.text = it.value.firstOrNull() }

        weight?.let { tvWeight.text = "${it.value.first().or(0)} lb" }

        birthday?.let {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = it.value.first().or(0)

            val month = calendar.get(Calendar.MONTH)
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
            val year = calendar.get(Calendar.YEAR)

            setBirthday(calendar.timeInMillis, "${month + 1}/$dayOfMonth/$year")
        }

        ethnicity?.let { tvEthnicity.text = it.value.firstOrNull() }

        height?.let {
            val heightInInches = it.value.first()
            val height = heightInInches.div(12)
            val inches = heightInInches.rem(12)

            tvHeight.text = "$height\'$inches\""
        }

        occupation?.let { tvOccupation.text = it.value.firstOrNull() }

        language?.let {
            it.value.firstOrNull()?.let {
                tvLanguages.text = languageUtils.getLanguageForLanguageCode(it.languageCode)
            }
        }
    }

    private fun setBirthday(timeInMillis: Long, birthDay: String?) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis

        if (!birthDay.isNullOrEmpty()) {
            tvBirthday.text = birthDay
        }
    }

}
