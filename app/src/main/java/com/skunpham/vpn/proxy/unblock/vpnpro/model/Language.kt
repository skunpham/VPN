package com.skunpham.vpn.proxy.unblock.vpnpro.model

import android.graphics.drawable.Drawable

class Language {
     var code: String? = null
     var name: String? = null
    var icon: Drawable? = null
    var isChoose = false

    constructor(code: String?, name: String?, icon: Drawable?, isChoose: Boolean) {
        this.code = code
        this.name = name
        this.icon = icon
        this.isChoose = isChoose
    }
}