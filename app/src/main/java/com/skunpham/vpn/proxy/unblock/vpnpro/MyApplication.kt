package com.skunpham.vpn.proxy.unblock.vpnpro

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.skunpham.vpn.proxy.unblock.vpnpro.model.Server
import com.skunpham.vpn.proxy.unblock.vpnpro.utils.NetworkUtil
import com.skunpham.vpn.proxy.unblock.vpnpro.utils.SharePreferencesManager

class MyApplication : Application() {


    companion object {
        lateinit var app: MyApplication
        fun getInstance(): MyApplication {
            return app
        }

        var selectedServer = MutableLiveData<Server?>()
        var isResetTimeCount = false
        var isDisconnectServerFromTimeCountService = false
        val listServerFree = MutableLiveData<List<Server>>()
    }

    override fun onCreate() {
        super.onCreate()
        app = this
        NetworkUtil.initNetwork(this)
        SharePreferencesManager.initializeInstance(this)
    }

}