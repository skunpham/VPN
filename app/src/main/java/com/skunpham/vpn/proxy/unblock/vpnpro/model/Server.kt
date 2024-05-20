package com.skunpham.vpn.proxy.unblock.vpnpro.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.skunpham.vpn.proxy.unblock.vpnpro.utils.RemoteUtils
import kotlinx.android.parcel.Parcelize
import java.util.Locale
import java.util.Random

@Parcelize
@Keep
class Server() : Parcelable {
    var hostName: String? = null
    var ipAddress: String? = null
    var score: Int? = null
    var ping: String? = null
    var speed: Long? = null
    var countryLong: String? = null
    var countryShort: String? = null
    var city: String? = null
    var displayName: String? = null
    var vpnSessions: Long? = null
    var uptime: Long? = null
    var totalUsers: Long? = null
    var totalTraffic: String? = null
    var logType: String? = null
    var operator: String? = null
    var message: String? = null
    var oVpnConfigData: String? = null
    var port: Int? = null
    var protocol: String? = null
    var isStarred = false
    var isVip = false

    constructor(
        hostName: String,
        ipAddress: String,
        score: Int,
        ping: String,
        speed: Long,
        countryLong: String,
        countryShort: String,
        city: String?,
        displayName: String?,
        vpnSessions: Long,
        uptime: Long,
        totalUsers: Long,
        totalTraffic: String,
        logType: String,
        operator: String,
        message: String,
        oVpnConfigData: String,
        port: Int,
        protocol: String,
        isStarred: Boolean,
        isVip: Boolean,
    ) : this() {
        this.hostName = hostName
        this.ipAddress = ipAddress
        this.score = score
        this.ping = ping
        this.speed = speed
        this.countryLong = countryLong
        this.countryShort = countryShort
        this.city = city
        this.displayName = displayName
        this.vpnSessions = vpnSessions
        this.uptime = uptime
        this.totalUsers = totalUsers
        this.totalTraffic = totalTraffic
        this.logType = logType
        this.operator = operator
        this.message = message
        this.oVpnConfigData = oVpnConfigData
        this.port = port
        this.protocol = protocol
        this.isStarred = isStarred
        this.isVip = isVip
    }

    constructor(serverVip: ServerVip) : this() {
        val locale = Locale("en", serverVip.country)
        Locale.setDefault(locale)
        this.ping = (Random().nextInt(10) + 1).toString()
        this.speed = Long.MAX_VALUE
        this.countryLong = locale.displayCountry
        this.displayName = serverVip.displayName
        this.countryShort = serverVip.country
        this.city = serverVip.city
        this.oVpnConfigData = RemoteUtils.getConfigServerVip().replace("/##/", "\n")
            .replace("&&&&&", serverVip.servers)
        this.isVip = true
    }

}
