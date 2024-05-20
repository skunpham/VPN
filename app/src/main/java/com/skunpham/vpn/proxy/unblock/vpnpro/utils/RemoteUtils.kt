package com.skunpham.vpn.proxy.unblock.vpnpro.utils

object RemoteUtils {

    const val VPN_VIP_CONFIG = "vpn_vip_config"

    const val REMOTE_REWARD_60_MIN = "ads_reward_60min"
    const val REMOTE_DISPLAY_LABEL_VIP_FREE = "ads_display_label_vip_free_3"

    fun setListServerVip(data: String) {
        AppDataUtils.setListServerVIP(data)
    }

    fun getConfigServerVip(): String {
        return SharePreferencesManager.instance.getValue(VPN_VIP_CONFIG)!!
    }

    fun setConfigServerVip(data: String) {
        SharePreferencesManager.instance.setValue(VPN_VIP_CONFIG, data)
    }

    var isShowAdsReward60min: Boolean
        get() = SharePreferencesManager.instance.getValueBool(REMOTE_REWARD_60_MIN, true)
        set(value) = SharePreferencesManager.instance.setValueBool(REMOTE_REWARD_60_MIN, value)

    var isShowLabelVipOrFree: Boolean
        get() = SharePreferencesManager.instance.getValueBool(REMOTE_DISPLAY_LABEL_VIP_FREE, true)
        set(value) = SharePreferencesManager.instance.setValueBool(REMOTE_DISPLAY_LABEL_VIP_FREE, value)
}