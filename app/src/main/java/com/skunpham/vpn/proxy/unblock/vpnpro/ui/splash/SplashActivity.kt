package com.skunpham.vpn.proxy.unblock.vpnpro.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.skunpham.vpn.proxy.unblock.vpnpro.R
import com.skunpham.vpn.proxy.unblock.vpnpro.core.BaseActivity
import com.skunpham.vpn.proxy.unblock.vpnpro.ui.main.MainActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {

    private val viewModel by lazy {
        SplashViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        viewModel.getListServerData()

        Handler(Looper.getMainLooper()).postDelayed({
            startMain()
        }, 2000L)
    }

    private fun startMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}