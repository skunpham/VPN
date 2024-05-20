package com.skunpham.vpn.proxy.unblock.vpnpro.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

object CheckInternetConnection {

    fun netCheck(context: Context): Boolean {
        var result = false
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm.run {
                cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                    when {
                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || hasTransport(
                            NetworkCapabilities.TRANSPORT_CELLULAR
                        ) ||
                                hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> {
                            result = true
                        }
                        else -> false
                    }
                }
            }
        } else {
            cm.run {
                cm.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> {
                            true
                        }
                        ConnectivityManager.TYPE_MOBILE -> {
                            true
                        }
                        ConnectivityManager.TYPE_VPN -> {
                            true
                        }
                        else -> false
                    }
                }
            }
        }
        return result
    }

    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = connectivityManager.activeNetworkInfo
        return netInfo != null && netInfo.isConnected
    }
}