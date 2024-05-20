package com.skunpham.vpn.proxy.unblock.vpnpro.ui.main

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.skunpham.vpn.proxy.unblock.vpnpro.R
import com.skunpham.vpn.proxy.unblock.vpnpro.core.BaseActivity
import com.skunpham.vpn.proxy.unblock.vpnpro.databinding.ActivityMainBinding
import com.skunpham.vpn.proxy.unblock.vpnpro.databinding.DialogNoInternetBinding
import com.skunpham.vpn.proxy.unblock.vpnpro.utils.CheckInternetConnection
import kotlin.system.exitProcess

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    var isBackToCloseApp = true
    private var dialog: Dialog? = null

    companion object {
        var isClickSelectServer = false
        var isConnectFromButton = false
    }

    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dialog = Dialog(this)
        dialog!!.window?.requestFeature(Window.FEATURE_NO_TITLE)
        navHostFragment = supportFragmentManager
            .findFragmentById(R.id.container_fragment) as NavHostFragment
        navController = navHostFragment.navController
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.mobile_navigation)

        navController.graph = graph
        if (!CheckInternetConnection.isOnline(this)) {
            showDialogNoInternet()
        }
    }

    fun showDialogNoInternet() {
        val binding = DialogNoInternetBinding.inflate(layoutInflater)
        dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.setContentView(binding.root)
        dialog!!.setCancelable(false)
        binding.tvAgree.setOnClickListener { exitProcess(0) }
        dialog!!.show()
    }


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (navHostFragment.childFragmentManager.backStackEntryCount < 1) {
            if (isBackToCloseApp) {
                finish()
            } else {
                super.onBackPressed()
            }
        } else {
            navController.navigateUp()
        }

    }

    override fun onDestroy() {
        if (dialog != null) {
            if (dialog!!.isShowing) {
                dialog!!.dismiss()
            }
        }
        super.onDestroy()
    }
}