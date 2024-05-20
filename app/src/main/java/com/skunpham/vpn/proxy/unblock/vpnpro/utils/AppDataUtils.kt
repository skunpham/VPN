package com.skunpham.vpn.proxy.unblock.vpnpro.utils

import android.content.Context
import com.skunpham.vpn.proxy.unblock.vpnpro.model.Server
import com.skunpham.vpn.proxy.unblock.vpnpro.model.ServerVip
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object AppDataUtils {
    private const val LIST_SERVER_FREE = "list_server_free"
    const val LIST_SERVER_VIP = "list_server_vip"
    private const val SERVER_CHOOSER = "server_chooser"
    var isHideAdsResume = false

    fun setSelectedServer(server: Server) {
        SharePreferencesManager.instance.setValue(SERVER_CHOOSER, Gson().toJson(server))
    }

    fun getServerSelected(): Server? {
        val data = SharePreferencesManager.instance.getValue(SERVER_CHOOSER)
        if (data!!.isEmpty())
            return null
        val type = object : TypeToken<Server>() {}.type
        return Gson().fromJson(data, type)
    }

    fun setListServerFree(listServerFree: ArrayList<Server>) {
        SharePreferencesManager.instance.setValue(LIST_SERVER_FREE, Gson().toJson(listServerFree))
    }

    fun getListServerFree(): ArrayList<Server> {
        val data = SharePreferencesManager.instance.getValue(LIST_SERVER_FREE)
        if (data!!.isEmpty())
            return ArrayList()
        val type = object : TypeToken<ArrayList<Server>>() {}.type
        return Gson().fromJson(data, type)
    }

    fun setListServerVIP(data: String) {
        SharePreferencesManager.instance.setValue(LIST_SERVER_VIP, data)
    }

    fun getListServerVIP(): ArrayList<ServerVip> {
        val data = SharePreferencesManager.instance.getValue(LIST_SERVER_VIP)
        if (data!!.isEmpty())
            return ArrayList()
        val type = object : TypeToken<ArrayList<ServerVip>>() {}.type
        return Gson().fromJson(data, type)
    }

    fun setTimeLeftInMillis(value: Long) {
        SharePreferencesManager.instance.setValue("millisLeft", value)
    }

    fun getTimeLeftInMillis(): Long {
        return SharePreferencesManager.instance.getValueLong("millisLeft")
    }

    fun setIsClickBtnTimeBonus(value: Boolean) {
        SharePreferencesManager.instance.setValueBool("IsClickBtnTimeBonus", value)
    }

    fun getIsClickBtnTimeBonus(): Boolean {
        return SharePreferencesManager.instance.getValueLimitTimeBonusBool("IsClickBtnTimeBonus")
    }

    fun setEndTime(value: Long) {
        SharePreferencesManager.instance.setValue("endTime", value)
    }

    fun getEndTime(): Long {
        return SharePreferencesManager.instance.getValueLong("endTime")
    }

    @JvmStatic
    fun hideAdsResume(context: Context) {

    }

    @JvmStatic
    fun enableAdsResume(context: Context) {

    }
}