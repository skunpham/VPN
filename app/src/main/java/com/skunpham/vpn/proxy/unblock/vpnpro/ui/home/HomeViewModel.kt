package com.skunpham.vpn.proxy.unblock.vpnpro.ui.home

import androidx.databinding.ObservableField
import com.skunpham.vpn.proxy.unblock.vpnpro.core.BaseViewModel
import com.skunpham.vpn.proxy.unblock.vpnpro.model.Server

class HomeViewModel : BaseViewModel() {
    companion object {
        const val STATUS_DISCONNECTING = "disconnecting"
        const val STATUS_DISCONNECTED = "disconnect"
        const val STATUS_CONNECT = "connect"
        const val STATUS_CONNECTING = "connecting"
        const val STATUS_CONNECTED = "connected"
        const val STATUS_TRY_DIFFERENT_SERVER = "tryDifferentServer"
        const val STATUS_LOADING = "loading"
        const val STATUS_INVALID_DEVICE = "invalidDevice"
        const val STATUS_AUTHENTICATION_CHECK = "authenticationCheck"
        const val STATUS_CONNECTING_TIMEOUT = "connecting_timeout"
        const val STATUS_NO_NETWORK = "nonetwork"

        const val AUTH = "auth"
        const val GET_CONFIG = "get_config"
        const val TCP_CONNECT = "tcp_connect"
    }

    var selectedServer = ObservableField<Server?>()

    var connectVpnStatus = ObservableField(STATUS_CONNECT)

}