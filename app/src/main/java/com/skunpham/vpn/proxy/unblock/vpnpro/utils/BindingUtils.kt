package com.skunpham.vpn.proxy.unblock.vpnpro.utils

import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.skunpham.vpn.proxy.unblock.vpnpro.R
import com.skunpham.vpn.proxy.unblock.vpnpro.model.Server
import com.skunpham.vpn.proxy.unblock.vpnpro.ui.home.HomeViewModel.Companion.STATUS_CONNECTED
import com.skunpham.vpn.proxy.unblock.vpnpro.ui.home.HomeViewModel.Companion.STATUS_CONNECTING
import com.skunpham.vpn.proxy.unblock.vpnpro.ui.home.HomeViewModel.Companion.STATUS_DISCONNECTING
import dots.animation.textview.TextAndAnimationView
import java.util.Locale

object BindingUtils {

    @BindingAdapter("tintTimerByStatus")
    @JvmStatic
    fun setTintTimerByStatus(imageView: ImageView, connectStatus: String) {
        val tintColor =
            if (connectStatus == STATUS_CONNECTED || connectStatus == STATUS_CONNECTING) {
                R.color.color_home_timer_connect
            } else {
                R.color.color_home_timer_disconnect
            }
        imageView.setColorFilter(
            ContextCompat.getColor(imageView.context, tintColor),
            android.graphics.PorterDuff.Mode.SRC_IN
        )
    }

    @BindingAdapter("textSelectServer")
    @JvmStatic
    fun setTextSelectServer(textView: TextView, connectStatus: String) {
        val content = when (connectStatus) {
            STATUS_CONNECTED -> {
                textView.context.getString(R.string.txt_home_connected)
            }
            STATUS_CONNECTING -> {
                textView.context.getString(R.string.txt_connecting)
            }
            else -> {
                textView.context.getString(R.string.txt_home_select_vpn)
            }
        }
        textView.text = content
    }

    @BindingAdapter("hideShowTextAndAnimationView")
    @JvmStatic
    fun setHideShowTextAndAnimationView(textAndAnimationView: TextAndAnimationView, connectStatus: String) {
        when (connectStatus) {
            STATUS_CONNECTING -> {
                textAndAnimationView.visibility = View.VISIBLE
            }
            else -> {
                textAndAnimationView.visibility = View.GONE
            }
        }
    }

    @BindingAdapter("statusLocationServer")
    @JvmStatic
    fun setStatusLocationServer(textView: TextView, server: Server?) {
        val content = if (server == null) {
            textView.context.getString(R.string.txt_home_disconnected)
        } else {
            textView.context.getString(R.string.txt_home_current_location)
        }
        textView.text = content
    }

    @BindingAdapter("nameLocationServer")
    @JvmStatic
    fun setNameLocationServer(textView: TextView, server: Server?) {
        textView.text =
            server?.countryLong ?: textView.context.getString(R.string.txt_home_connect_now)
    }

    @BindingAdapter("imageLocationServer")
    @JvmStatic
    fun setImageLocationServer(imageView: ImageView, server: Server?) {
        val context = imageView.context
        if (server == null) {
            Glide.with(context).load(
                context.resources.getIdentifier(
                    "ic_home_disconnect", "drawable", context.packageName
                )
            ).into(imageView)
        } else {
            Glide.with(context).load(
                Uri.parse("file:///android_asset/flags/" + server.countryShort!!.lowercase(Locale.getDefault()) + ".png")
            ).into(imageView)
        }
    }

    @BindingAdapter("animConnectStatus")
    @JvmStatic
    fun setAnimConnectStatus(lottieView: LottieAnimationView, connectStatus: String) {
        if (connectStatus == STATUS_CONNECTING || connectStatus == STATUS_DISCONNECTING || connectStatus == STATUS_CONNECTED) {
            lottieView.playAnimation()
        } else {
            lottieView.pauseAnimation()
        }
    }
}