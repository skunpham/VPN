package com.skunpham.vpn.proxy.unblock.vpnpro.model

import com.skunpham.vpn.proxy.unblock.vpnpro.ui.servers.adapter.SubServerAdapter


class GroupServer(var title: String, var listServer: MutableList<Server>, var isAll: Boolean) {
    var isExpand = false
    var subServerAdapter: SubServerAdapter? = null
}
