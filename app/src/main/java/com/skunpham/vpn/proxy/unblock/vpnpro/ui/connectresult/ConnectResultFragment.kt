package com.skunpham.vpn.proxy.unblock.vpnpro.ui.connectresult

import android.annotation.SuppressLint
import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.skunpham.vpn.proxy.unblock.vpnpro.R
import com.skunpham.vpn.proxy.unblock.vpnpro.core.BaseFragment
import com.skunpham.vpn.proxy.unblock.vpnpro.databinding.FragmentConnectResultBinding
import com.skunpham.vpn.proxy.unblock.vpnpro.ui.main.MainActivity
import java.util.Locale

class ConnectResultFragment : BaseFragment() {

    private lateinit var binding: FragmentConnectResultBinding

    private val args: ConnectResultFragmentArgs by navArgs()
    private var mBottomSheetDialog: Dialog? = null

    private fun init() {
        (activity as MainActivity).isBackToCloseApp = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
        binding.llInfoServer.visibility = View.GONE
        binding.loadingView.visibility = View.VISIBLE
        binding.btnFinish.isEnabled = false
        binding.btnFinish.setBackgroundColor(myContext.resources.getColor(R.color.color_button_disable))
        Handler(Looper.getMainLooper()).postDelayed({
            binding.llInfoServer.visibility = View.VISIBLE
            binding.loadingView.visibility = View.GONE
            binding.btnFinish.isEnabled = true
            binding.btnFinish.setBackgroundColor(myContext.resources.getColor(R.color.colorPrimary))
        }, 2000)

    }

    @SuppressLint("CheckResult")
    private fun initEvent() {
        val server = args.server
        val status = args.status
        binding.tvNation.text = server.countryLong
        Glide.with(this).load(
            Uri.parse(
                "file:///android_asset/flags/" + server.countryShort!!.lowercase(
                    Locale.getDefault()
                ) + ".png"
            )
        ).into(binding.ivNation)
        if (status) {
            binding.tvStatus.text = getString(R.string.connected)
            binding.lotti.setAnimation(R.raw.lotti_connected)
        } else {
            binding.tvStatus.text = getString(R.string.disconnected)
            binding.lotti.setAnimation(R.raw.lotti_disconnected)
        }

        binding.btnFinish.setOnClickListener {
            popBackStack()
            (activity as MainActivity).isBackToCloseApp = true
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }


    private fun onBack() {
        findNavController().popBackStack()
        (activity as MainActivity).isBackToCloseApp = true
    }

}