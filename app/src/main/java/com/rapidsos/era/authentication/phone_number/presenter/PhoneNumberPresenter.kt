package com.rapidsos.era.authentication.phone_number.presenter

import com.hannesdorfmann.mosby3.mvp.MvpPresenter
import com.rapidsos.era.authentication.phone_number.view.PhoneNumberView

/**
 * @author Josias Sena
 */
interface PhoneNumberPresenter : MvpPresenter<PhoneNumberView> {

    /**
     * Save the phone number to the shared preferences.
     */
    fun saveDevicePhoneNumber(phoneNumber: String)

    /**
     * Request a new pin number. The in will be sent to the  phone number provided
     * @param phoneNumber the phone number to send the pin too
     */
    fun requestPin(phoneNumber: String)

    /**
     * Validate the pin provided
     * @param pin the pin to validate
     */
    fun validatePin(pin: Int)

}
