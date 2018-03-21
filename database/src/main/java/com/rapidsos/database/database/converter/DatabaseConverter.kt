package com.rapidsos.database.database.converter

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rapidsos.emergencydatasdk.data.profile.*

/**
 * @author Josias Sena
 */
class DatabaseConverter {

    @TypeConverter
    fun fromAddress(`object`: Address?): String = Gson().toJson(`object`)

    @TypeConverter
    fun toAddress(json: String): Address? =
            Gson().fromJson<Address>(json, object : TypeToken<Address>() {}.type)

    @TypeConverter
    fun fromAllergy(`object`: Allergy?): String = Gson().toJson(`object`)

    @TypeConverter
    fun toAllergy(json: String): Allergy? =
            Gson().fromJson<Allergy>(json, object : TypeToken<Allergy>() {}.type)

    @TypeConverter
    fun fromBirthday(`object`: Birthday?): String = Gson().toJson(`object`)

    @TypeConverter
    fun toBirthday(json: String): Birthday? =
            Gson().fromJson<Birthday>(json, object : TypeToken<Birthday>() {}.type)

    @TypeConverter
    fun fromBloodType(`object`: BloodType?): String = Gson().toJson(`object`)

    @TypeConverter
    fun toBloodType(json: String): BloodType? =
            Gson().fromJson<BloodType>(json, object : TypeToken<BloodType>() {}.type)

    @TypeConverter
    fun fromComment(`object`: Comment?): String = Gson().toJson(`object`)

    @TypeConverter
    fun toComment(json: String): Comment? =
            Gson().fromJson<Comment>(json, object : TypeToken<Comment>() {}.type)

    @TypeConverter
    fun fromDisability(`object`: Disability?): String = Gson().toJson(`object`)

    @TypeConverter
    fun toDisability(json: String): Disability? =
            Gson().fromJson<Disability>(json, object : TypeToken<Disability>() {}.type)

    @TypeConverter
    fun fromEmail(`object`: Email?): String = Gson().toJson(`object`)

    @TypeConverter
    fun toEmail(json: String): Email? =
            Gson().fromJson<Email>(json, object : TypeToken<Email>() {}.type)

    @TypeConverter
    fun fromEmergencyContact(`object`: EmergencyContact?): String = Gson().toJson(`object`)

    @TypeConverter
    fun toEmergencyContact(json: String): EmergencyContact? =
            Gson().fromJson<EmergencyContact>(json, object : TypeToken<EmergencyContact>() {}.type)

    @TypeConverter
    fun fromEthnicity(`object`: Ethnicity?): String = Gson().toJson(`object`)

    @TypeConverter
    fun toEthnicity(json: String): Ethnicity? =
            Gson().fromJson<Ethnicity>(json, object : TypeToken<Ethnicity>() {}.type)

    @TypeConverter
    fun fromFullName(`object`: FullName?): String = Gson().toJson(`object`)

    @TypeConverter
    fun toFullName(json: String): FullName? =
            Gson().fromJson<FullName>(json, object : TypeToken<FullName>() {}.type)

    @TypeConverter
    fun fromGender(`object`: Gender?): String = Gson().toJson(`object`)

    @TypeConverter
    fun toGender(json: String): Gender? =
            Gson().fromJson<Gender>(json, object : TypeToken<Gender>() {}.type)

    @TypeConverter
    fun fromHeight(`object`: Height?): String = Gson().toJson(`object`)

    @TypeConverter
    fun toHeight(json: String): Height? =
            Gson().fromJson<Height>(json, object : TypeToken<Height>() {}.type)

    @TypeConverter
    fun fromLanguage(`object`: Language?): String = Gson().toJson(`object`)

    @TypeConverter
    fun toLanguage(json: String): Language? =
            Gson().fromJson<Language>(json, object : TypeToken<Language>() {}.type)

    @TypeConverter
    fun fromMedicalCondition(`object`: MedicalCondition?): String = Gson().toJson(`object`)

    @TypeConverter
    fun toMedicalCondition(json: String): MedicalCondition? =
            Gson().fromJson<MedicalCondition>(json, object : TypeToken<MedicalCondition>() {}.type)

    @TypeConverter
    fun fromMedicalNote(`object`: MedicalNote?): String = Gson().toJson(`object`)

    @TypeConverter
    fun toMedicalNote(json: String): MedicalNote? =
            Gson().fromJson<MedicalNote>(json, object : TypeToken<MedicalNote>() {}.type)

    @TypeConverter
    fun fromMedication(`object`: Medication?): String = Gson().toJson(`object`)

    @TypeConverter
    fun toMedication(json: String): Medication? =
            Gson().fromJson<Medication>(json, object : TypeToken<Medication>() {}.type)

    @TypeConverter
    fun fromOccupation(`object`: Occupation?): String = Gson().toJson(`object`)

    @TypeConverter
    fun toOccupation(json: String): Occupation? =
            Gson().fromJson<Occupation>(json, object : TypeToken<Occupation>() {}.type)

    @TypeConverter
    fun fromPhoneNumber(`object`: PhoneNumber?): String = Gson().toJson(`object`)

    @TypeConverter
    fun toPhoneNumber(json: String): PhoneNumber? =
            Gson().fromJson<PhoneNumber>(json, object : TypeToken<PhoneNumber>() {}.type)

    @TypeConverter
    fun fromPhoto(`object`: Photo?): String = Gson().toJson(`object`)

    @TypeConverter
    fun toPhoto(json: String): Photo? =
            Gson().fromJson<Photo>(json, object : TypeToken<Photo>() {}.type)

    @TypeConverter
    fun fromWebsite(`object`: Website?): String = Gson().toJson(`object`)

    @TypeConverter
    fun toWebsite(json: String): Website? =
            Gson().fromJson<Website>(json, object : TypeToken<Website>() {}.type)

    @TypeConverter
    fun fromWeight(`object`: Weight?): String = Gson().toJson(`object`)

    @TypeConverter
    fun toWeight(json: String): Weight? =
            Gson().fromJson<Weight>(json, object : TypeToken<Weight>() {}.type)

}