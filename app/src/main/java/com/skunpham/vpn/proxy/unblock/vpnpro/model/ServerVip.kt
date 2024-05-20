package com.skunpham.vpn.proxy.unblock.vpnpro.model

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep data class ServerVip(
    @SerializedName("displayName")
    @Expose
    var displayName: String,

    @SerializedName("country")
    @Expose
    var country: String,

    @SerializedName("servers")
    @Expose
    val servers: String,

    @SerializedName("city")
    @Expose
    val city: String
)
