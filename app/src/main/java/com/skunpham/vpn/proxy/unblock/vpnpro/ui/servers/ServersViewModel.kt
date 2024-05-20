package com.skunpham.vpn.proxy.unblock.vpnpro.ui.servers

import androidx.lifecycle.MutableLiveData
import com.skunpham.vpn.proxy.unblock.vpnpro.core.BaseViewModel
import com.skunpham.vpn.proxy.unblock.vpnpro.model.GroupServer
import com.skunpham.vpn.proxy.unblock.vpnpro.model.Server
import com.skunpham.vpn.proxy.unblock.vpnpro.utils.AppDataUtils
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.SingleOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.text.Normalizer
import java.util.*

class ServersViewModel : BaseViewModel() {
    companion object {
        const val TAB_FREE = 2
        const val TAB_VIP = 3
    }

    var dataGroupServerResultSearch = mutableListOf<GroupServer>()
    var dataGroupServerFreeResponseLiveData = MutableLiveData<MutableList<GroupServer>>()
    var queryLiveData = MutableLiveData("")
    var isLoading = MutableLiveData<Boolean>()

    fun getListServer(isAll : Boolean) {
        isLoading.postValue(true)
        if (AppDataUtils.getListServerFree().size > 0){
            val serverData = AppDataUtils.getListServerFree()
            if (isAll){
                serverData.addAll(0, getListServerFreeFromServerVip())
            }else{
                randomServer(serverData)
            }
            groupServer(serverData, isAll)
        }
    }

    private fun getListServerFreeFromServerVip() : ArrayList<Server>{
        val serverData = ArrayList<Server>()
        for (item in AppDataUtils.getListServerVIP()){
            serverData.add(Server(item))
        }
        return serverData
    }

    private fun groupServer(serverData: ArrayList<Server>, isAll: Boolean) {
        val single = Single.create(SingleOnSubscribe<MutableList<GroupServer>> { emitter ->
            try {
                val listGroupServer : MutableList<GroupServer> = mutableListOf()
                val groupServer = serverData.groupBy { it.countryLong }
                groupServer.forEach {
                    listGroupServer.add(GroupServer(it.key!!, it.value as MutableList<Server>, isAll))
                }
                if(listGroupServer.count() > 0){
                    emitter.onSuccess(listGroupServer)
                }
            } catch (e: Exception) {
                emitter.onError(e)
            }
        })
        single.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<MutableList<GroupServer>> {
                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }
                override fun onSuccess(t: MutableList<GroupServer>) {
                    isLoading.postValue(false)
                    dataGroupServerResultSearch.clear()
                    dataGroupServerResultSearch.addAll(t)
                    if (queryLiveData.value != null && queryLiveData.value?.isNotEmpty() == true){
                        queryServer(queryLiveData.value!!)
                        return
                    }
                    dataGroupServerFreeResponseLiveData.postValue(t)
                }

                override fun onError(e: Throwable) {

                }
            })
    }

    fun queryServer(query: String){
        isLoading.postValue(true)
        queryLiveData.postValue(query)
        val resultQueryServer = ArrayList<GroupServer>()
        val serverData = dataGroupServerResultSearch
        if (query.isEmpty()){
            isLoading.postValue(false)
            dataGroupServerFreeResponseLiveData.postValue(dataGroupServerResultSearch)
            return
        }
        for (item in serverData){
            val countryLong = item.title.lowercase(Locale.getDefault()).trim()
            val newCountryLong = Normalizer.normalize(countryLong, Normalizer.Form.NFD)
            val newKey = Normalizer.normalize(query, Normalizer.Form.NFD)
            if (newCountryLong.contains(newKey)){
                resultQueryServer.add(item)
            }
        }
        isLoading.postValue(false)
        dataGroupServerFreeResponseLiveData.postValue(resultQueryServer)
    }

    private fun randomServer(serverData: ArrayList<Server>){
        val serverVipUSs = ArrayList<Server>()
        val serverVipDEs = ArrayList<Server>()
        for (item in getListServerFreeFromServerVip()){
            if (item.displayName == "United States"){
                item.isVip = false
                serverVipUSs.add(item)
            }
            if (item.displayName == "Germany"){
                item.isVip = false
                serverVipDEs.add(item)
            }
        }
        if (serverVipUSs.size <= 0 && serverVipDEs.size <= 0)
            return
        val randomUS = Random().nextInt(serverVipUSs.size)
        var randomUS1 = Random().nextInt(serverVipUSs.size)
        while (randomUS == randomUS1) {
            randomUS1 = Random().nextInt(serverVipUSs.size)
        }
        val randomDE = Random().nextInt(serverVipDEs.size)
        serverData.add(0, serverVipUSs[randomUS])
        serverData.add(1, serverVipUSs[randomUS1])
        serverData.add(serverVipDEs[randomDE])
    }
}