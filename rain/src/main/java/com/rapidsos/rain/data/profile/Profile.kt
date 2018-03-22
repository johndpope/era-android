package com.rapidsos.rain.data.profile

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity
data class Profile(@PrimaryKey
                   @SerializedName("id")
                   @Expose
                   var id: Int = 0,

                   @SerializedName("address")
                   @Expose
                   var address: Address? = null,

                   @SerializedName("allergy")
                   @Expose
                   var allergy: Allergy? = null,

                   @SerializedName("birthday")
                   @Expose
                   var birthday: Birthday? = null,

                   @ColumnInfo(name = "blood_type")
                   @SerializedName("blood_type")
                   @Expose
                   var bloodType: BloodType? = null,

                   @SerializedName("comment")
                   @Expose
                   var comment: Comment? = null,

                   @SerializedName("disability")
                   @Expose
                   var disability: Disability? = null,

                   @SerializedName("email")
                   @Expose
                   var email: Email? = null,

                   @ColumnInfo(name = "emergency_contact")
                   @SerializedName("emergency_contact")
                   @Expose
                   var emergencyContact: EmergencyContact? = null,

                   @SerializedName("ethnicity")
                   @Expose
                   var ethnicity: Ethnicity? = null,

                   @ColumnInfo(name = "full_name")
                   @SerializedName("full_name")
                   @Expose
                   var fullName: FullName? = null,

                   @SerializedName("gender")
                   @Expose
                   var gender: Gender? = null,

                   @SerializedName("height")
                   @Expose
                   var height: Height? = null,

                   @SerializedName("language")
                   @Expose
                   var language: Language? = null,

                   @ColumnInfo(name = "medical_condition")
                   @SerializedName("medical_condition")
                   @Expose
                   var medicalCondition: MedicalCondition? = null,

                   @ColumnInfo(name = "medical_note")
                   @SerializedName("medical_note")
                   @Expose
                   var medicalNote: MedicalNote? = null,

                   @SerializedName("medication")
                   @Expose
                   var medication: Medication? = null,

                   @SerializedName("occupation")
                   @Expose
                   var occupation: Occupation? = null,

                   @ColumnInfo(name = "phone_number")
                   @SerializedName("phone_number")
                   @Expose
                   var phoneNumber: PhoneNumber? = null,

                   @SerializedName("photo")
                   @Expose
                   var photo: Photo? = null,

                   @ColumnInfo(name = "updated_time")
                   @SerializedName("updated_time")
                   @Expose
                   var updatedTime: Long = 0,

                   @SerializedName("website")
                   @Expose
                   var website: Website? = null,

                   @SerializedName("weight")
                   @Expose
                   var weight: Weight? = null) {

    constructor() : this(0, null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, 0, null, null)

}
