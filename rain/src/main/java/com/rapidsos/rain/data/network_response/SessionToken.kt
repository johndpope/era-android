package com.rapidsos.rain.data.network_response

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * @author Josias Sena
 */
@Entity(tableName = "session_token")
data class SessionToken(@PrimaryKey @SerializedName("access_token") @Expose var accessToken: String = "",
                        @SerializedName("token_type") @Expose var tokenType: String = "",
                        @SerializedName("issued_at") @Expose var issuedAt: String = "",
                        @SerializedName("expires_in") @Expose var expiresIn: String = "",
                        @SerializedName("refresh_token") @Expose var refreshToken: String = "",
                        @SerializedName("refresh_token_issued_at") @Expose var refreshTokenIssuedAt: String = "",
                        @SerializedName("refresh_token_expires_in") @Expose var refreshTokenExpiresIn: String = ""
)