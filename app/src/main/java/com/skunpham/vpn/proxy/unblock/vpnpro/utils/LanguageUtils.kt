package com.skunpham.vpn.proxy.unblock.vpnpro.utils

import android.content.Context
import android.content.res.Configuration
import java.util.*

object LanguageUtils {
    const val LANGUAGE_EN = "en"
    const val LANGUAGE_ES = "es"
    const val LANGUAGE_FR = "fr"
    const val LANGUAGE_HI = "hi"
    const val LANGUAGE_PT = "pt"


    val LANGUAGE = "LANGUAGE"

    private var myLocale: Locale? = null

    // Lưu ngôn ngữ đã cài đặt
    private fun saveLocale(lang: String?, context: Context?) {
        SharePreferencesManager.instance.setValue(LANGUAGE,lang)
    }

    // Load lại ngôn ngữ đã lưu và thay đổi chúng
    fun loadLocale(context: Context) {
        val language: String = SharePreferencesManager.instance.getValue(LANGUAGE,"en")!!
        if (language == "") {
            val config = Configuration()
            val locale = Locale.getDefault()
            Locale.setDefault(locale)
            config.locale = locale
            context.resources
                .updateConfiguration(config, context.resources.displayMetrics)
        } else {
            changeLang(language, context)
        }
    }

    // method phục vụ cho việc thay đổi ngôn ngữ.
    fun changeLang(lang: String, context: Context) {
        if (lang.equals("", ignoreCase = true)) return
        myLocale = Locale(lang)
        saveLocale(lang, context)
        myLocale?.let { Locale.setDefault(it) }
        val config = Configuration()
        config.locale = myLocale
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
}