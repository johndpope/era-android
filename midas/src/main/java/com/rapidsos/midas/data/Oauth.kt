package com.rapidsos.midas.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * @author Josias Sena
 */
data class Oauth(@SerializedName("refresh_token_expires_in") @Expose var refreshTokenExpiresIn: String = "",
                 @SerializedName("api_product_list") @Expose var apiProductList: String = "",
                 @SerializedName("organization_name") @Expose var organizationName: String = "",
                 @SerializedName("developer_email") @Expose var developerEmail: String = "",
                 @SerializedName("token_type") @Expose var tokenType: String = "",
                 @SerializedName("issued_at") @Expose var issuedAt: String = "",
                 @SerializedName("client_id") @Expose var clientId: String = "",
                 @SerializedName("access_token") @Expose var accessToken: String = "",
                 @SerializedName("application_name") @Expose var applicationName: String = "",
                 @SerializedName("scope") @Expose var scope: String = "",
                 @SerializedName("expires_in") @Expose var expiresIn: String = "",
                 @SerializedName("refresh_count") @Expose var refreshCount: String = "",
                 @SerializedName("status") @Expose var status: String = ""
)