package com.skunpham.vpn.proxy.unblock.vpnpro.core

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

open class BaseHolder<out T : ViewDataBinding>(val binding: T) : RecyclerView.ViewHolder(binding.root) {
    open fun onBind(item: Any, position: Int, category: String?) {

    }
}