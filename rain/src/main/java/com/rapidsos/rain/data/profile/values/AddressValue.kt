package com.rapidsos.rain.data.profile.values

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AddressValue(@SerializedName("label") @Expose var label: String = "",
                        @SerializedName("street_address") @Expose var streetAddress: String = "",
                        @SerializedName("locality") @Expose var locality: String = "",
                        @SerializedName("region") @Expose var region: String = "",
                        @SerializedName("postal_code") @Expose var postalCode: String = "",
                        @SerializedName("country_code") @Expose var countryCode: String = "",
                        @SerializedName("latitude") @Expose var latitude: Int = 0,
                        @SerializedName("longitude") @Expose var longitude: Int = 0,
                        @SerializedName("note") @Expose var note: String = "")