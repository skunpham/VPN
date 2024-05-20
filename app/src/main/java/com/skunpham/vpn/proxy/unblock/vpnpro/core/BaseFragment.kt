package com.skunpham.vpn.proxy.unblock.vpnpro.core

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.skunpham.vpn.proxy.unblock.vpnpro.utils.LanguageUtils

abstract class BaseFragment : Fragment() {
    
    lateinit var myContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        LanguageUtils.loadLocale(requireContext())
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.myContext = context
    }

    open fun navigate(action: Int) {
        view?.let { _view ->
            Navigation.findNavController(_view).navigate(action)
        }
    }

    open fun navigate(directions: NavDirections) {
        view?.let { _view ->
            Navigation.findNavController(_view).navigate(directions)
        }
    }

    open fun popBackStack() {
        view?.let { _view ->
            Navigation.findNavController(_view).popBackStack()
        }
    }

    fun getPermission(): Array<String> {
        return arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    fun checkPermission(per: Array<String>): Boolean {
        for (s in per) {
            if (requireActivity().checkSelfPermission(s) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }
}
