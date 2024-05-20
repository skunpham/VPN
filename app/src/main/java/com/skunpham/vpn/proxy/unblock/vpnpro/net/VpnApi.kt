package com.skunpham.vpn.proxy.unblock.vpnpro.net

import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.GET

interface VpnApi {
    @GET("api/iphone/")
    fun getDataServerFree() : Single<ResponseBody>
}