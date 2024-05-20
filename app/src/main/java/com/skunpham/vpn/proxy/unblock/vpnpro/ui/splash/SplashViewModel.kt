package com.skunpham.vpn.proxy.unblock.vpnpro.ui.splash

import android.annotation.SuppressLint
import com.skunpham.vpn.proxy.unblock.vpnpro.MyApplication
import com.skunpham.vpn.proxy.unblock.vpnpro.core.BaseViewModel
import com.skunpham.vpn.proxy.unblock.vpnpro.net.ApiClient
import com.skunpham.vpn.proxy.unblock.vpnpro.net.VpnApi
import com.skunpham.vpn.proxy.unblock.vpnpro.utils.AppDataUtils
import com.skunpham.vpn.proxy.unblock.vpnpro.utils.CsvParser
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody

class SplashViewModel : BaseViewModel() {

    @SuppressLint("CheckResult")
    fun getListServerData() {
        CompositeDisposable().add(
            ApiClient.fetchData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        val serverData = CsvParser.parse(it)
                        MyApplication.listServerFree.postValue(serverData)
                        AppDataUtils.setListServerFree(serverData)
                    },
                    {
                        it.printStackTrace()
                    }
                )
        )

    }

    fun getListServer(api: VpnApi) {
        api.getDataServerFree()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<ResponseBody> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onSuccess(t: ResponseBody) {
                    val serverData = CsvParser.parse(t)
                    MyApplication.listServerFree.postValue(serverData)
                    AppDataUtils.setListServerFree(serverData)
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }
            })
    }
}