package com.rapidsos.era.emergency_contacts.view.rec_view

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.rapidsos.emergencydatasdk.data.profile.values.EmergencyContactValue
import com.rapidsos.era.R
import io.reactivex.subjects.PublishSubject
import org.jetbrains.anko.AnkoLogger

class EmergencyContactPosition(val position: Int, val contact: EmergencyContactValue)

class EmergencyContactsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

class EmergencyContactsAdapter : RecyclerView.Adapter<EmergencyContactsViewHolder>(), AnkoLogger {

    val onClickListener: PublishSubject<EmergencyContactPosition>
            by lazy(LazyThreadSafetyMode.PUBLICATION) {
                PublishSubject.create<EmergencyContactPosition>()
            }

    val onLongClickListener: PublishSubject<EmergencyContactValue>
            by lazy(LazyThreadSafetyMode.PUBLICATION) {
                PublishSubject.create<EmergencyContactValue>()
            }

    val contacts = arrayListOf<EmergencyContactValue>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmergencyContactsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_emergency_contacts, parent, false)
        return EmergencyContactsViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmergencyContactsViewHolder, position: Int) {
        val itemView = holder.itemView
        val contact = contacts[position]

        itemView.setOnClickListener {
            onClickListener.onNext(EmergencyContactPosition(position, contact))
        }

        itemView.setOnLongClickListener {
            onLongClickListener.onNext(contact)
            true
        }

        itemView.findViewById<TextView>(R.id.tvEmgContactFullName)?.text = contact.fullName
        itemView.findViewById<TextView>(R.id.tvEmgContactPhone)?.text = contact.email
        itemView.findViewById<TextView>(R.id.tvEmgContactEmail)?.text = contact.phone
    }

    override fun getItemCount(): Int = contacts.size

    fun setContacts(emergencyContacts: List<EmergencyContactValue>) {
        contacts.clear()
        contacts.addAll(emergencyContacts)
        notifyDataSetChanged()
    }
}