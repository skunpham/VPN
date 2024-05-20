package com.skunpham.vpn.proxy.unblock.vpnpro.core

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.skunpham.vpn.proxy.unblock.vpnpro.utils.LanguageUtils


abstract class BaseActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        LanguageUtils.loadLocale(this)
        super.onCreate(savedInstanceState)
    }

    fun getPermission(): Array<String> {
        return arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    fun checkPermission(per: Array<String>): Boolean {
        for (s in per) {
            if (checkSelfPermission(s) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }
}
