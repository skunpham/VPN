package com.skunpham.vpn.proxy.unblock.vpnpro.utils.anim

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout

object SubServerViewAnim {
    /*
       mở rộng view : targetHeight = 100
       thu lại view: targetHeight = 0
   */

    fun expand(v: View, duration: Int, targetHeight: Int) {
        val prevHeight = v.height
        v.visibility = LinearLayout.VISIBLE
        val valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight)
        valueAnimator.addUpdateListener { animation ->
            v.layoutParams.height = animation.animatedValue as Int
            v.requestLayout()
        }
        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                if(targetHeight<0){
                    v.visibility = LinearLayout.GONE
                }
            }
        })
        valueAnimator.interpolator = DecelerateInterpolator()
        valueAnimator.duration = duration.toLong()
        valueAnimator.start()
    }
}