package com.skunpham.vpn.proxy.unblock.vpnpro.utils.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.skunpham.vpn.proxy.unblock.vpnpro.R

class TabBar(context: Context, attrs: AttributeSet): LinearLayout(context, attrs) {

    init {
        inflate(context, R.layout.view_tab_bar, this)
        attrs.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.item_tab_bar, 0, 0)
            val text = typedArray.getString(R.styleable.item_tab_bar_bar_text)
            val selectDefault = typedArray.getBoolean(R.styleable.item_tab_bar_bar_select, false)

            findViewById<TextView>(R.id.txtName).text = text
            if(selectDefault){
                active()
            }else{
                deActive()
            }

            typedArray.recycle()
        }
    }

    fun active(){
        findViewById<TextView>(R.id.txtName).setTextColor(resources.getColor(R.color.app_tone))
        findViewById<View>(R.id.vSelect).alpha = 1f
    }

    fun deActive(){
        findViewById<TextView>(R.id.txtName).setTextColor(resources.getColor(R.color.gray))
        findViewById<View>(R.id.vSelect).alpha = 0f
    }
}