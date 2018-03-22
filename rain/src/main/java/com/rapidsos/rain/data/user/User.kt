package com.rapidsos.rain.data.user

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * @author Josias Sena
 */
@Entity
data class User(@PrimaryKey @SerializedName("id") var id: Long = 0,
                @SerializedName("username") var username: String = "",
                @SerializedName("email") var email: String = "",
                @SerializedName("password") var password: String = "",
                @SerializedName("created") var phone: String = "",
                @SerializedName("modified") var userStatus: Int = 0)