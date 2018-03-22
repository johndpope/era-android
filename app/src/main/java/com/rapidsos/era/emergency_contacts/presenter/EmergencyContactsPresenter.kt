package com.rapidsos.era.emergency_contacts.presenter

import com.hannesdorfmann.mosby3.mvp.MvpPresenter
import com.rapidsos.emergencydatasdk.data.profile.values.EmergencyContactValue
import com.rapidsos.era.emergency_contacts.view.EmergencyContactsView

/**
 * @author Josias Sena
 */
interface EmergencyContactsPresenter : MvpPresenter<EmergencyContactsView> {

    /**
     * Fetch emergency contacts
     */
    fun getEmergencyContacts()

    /**
     * Create a new emergency contact
     *
     * @param contact the contact to insert
     */
    fun addEmgContact(contact: EmergencyContactValue)

    /**
     * Delete an emergency contact
     *
     * @param contact the emergency contact to delete
     */
    fun deleteContact(contact: EmergencyContactValue)

    /**
     * Update an emergency contact
     *
     * @param contact the contact to update
     */
    fun updateEmgContact(index: Int, contact: EmergencyContactValue)
}