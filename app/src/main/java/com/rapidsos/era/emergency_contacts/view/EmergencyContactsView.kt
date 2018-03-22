package com.rapidsos.era.emergency_contacts.view

import com.hannesdorfmann.mosby3.mvp.MvpView
import com.rapidsos.emergencydatasdk.data.profile.values.EmergencyContactValue

/**
 * @author Josias Sena
 */
interface EmergencyContactsView : MvpView {

    /**
     * Called when emergency contacts have been fetched successfully
     */
    fun onGotEmergencyContacts(emergencyContacts: List<EmergencyContactValue>)

    /**
     * Display a loading view to the user
     */
    fun showLoading()

    /**
     * Hide a loading view if any
     */
    fun hideLoading()

    /**
     * Display an error to the user
     */
    fun showError(error: String)

    /**
     * Display a dialog for the user to confirm the deletion of a contact
     *
     * @param contact the contact that is about to get deleted
     */
    fun showDeleteContactDialog(contact: EmergencyContactValue)

    /**
     * Display a view to edit the following contact
     *
     * @param index the index the item is in
     * @param contact the contact to be edited
     */
    fun editContact(index: Int, contact: EmergencyContactValue)
}