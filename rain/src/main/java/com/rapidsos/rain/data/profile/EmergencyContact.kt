package com.rapidsos.rain.data.profile

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.rapidsos.rain.data.profile.values.EmergencyContactValue
import java.util.*

@Entity(tableName = "emergency_contact")
data class EmergencyContact(@PrimaryKey(autoGenerate = true) var id: Int = 0,
                            @SerializedName("value") @Expose var value: ArrayList<EmergencyContactValue> = arrayListOf(),
                            @SerializedName("type") @Expose var type: String? = null,
                            @SerializedName("display_name") @Expose var displayName: String? = null)
