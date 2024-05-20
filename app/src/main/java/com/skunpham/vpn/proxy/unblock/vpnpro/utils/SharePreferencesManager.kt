package com.skunpham.vpn.proxy.unblock.vpnpro.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.skunpham.vpn.proxy.unblock.vpnpro.core.Constants
import com.google.gson.Gson
import java.util.Arrays

class SharePreferencesManager(context: Context) {

    private val mPref = PreferenceManager.getDefaultSharedPreferences(context)

    fun setValue(key: String?, value: String?) {
        mPref.edit()
            .putString(key, value)
            .apply()
    }

    fun setValue(key: String?, value: Long) {
        mPref.edit()
            .putLong(key, value)
            .apply()
    }

    fun getValue(key: String?, default: String?): String? {
        return mPref.getString(key, default)
    }

    fun getValue(key: String?): String? {
        return mPref.getString(key, "")
    }

    fun getValueLong(key: String?): Long {
        return mPref.getLong(key, 0)
    }

    fun setIntValue(key: String?, value: Int) {
        mPref.edit()
            .putInt(key, value)
            .apply()
    }

    fun getIntValue(key: String?, i: Int): Int {
        return mPref.getInt(key, i)
    }

    fun setValueBool(key: String?, value: Boolean) {
        mPref.edit()
            .putBoolean(key, value)
            .apply()
    }

    fun getValueBool(key: String?): Boolean {
        return mPref.getBoolean(key, true)
    }

    fun getValueLimitTimeBonusBool(key: String?): Boolean {
        return mPref.getBoolean(key, false)
    }

    fun getValueBool(key: String?, def: Boolean): Boolean {
        return mPref.getBoolean(key, def)
    }

    fun remove(key: String?) {
        mPref.edit()
            .remove(key)
            .apply()
    }

    fun clear(): Boolean {
        return mPref.edit()
            .clear()
            .commit()
    }

    fun saveLocked(context: Context, lockedApp: List<String?>?) {
        val settings: SharedPreferences
        val editor: SharedPreferences.Editor
        settings = context.getSharedPreferences(
            Constants.MyPREFERENCES,
            Context.MODE_PRIVATE
        )
        editor = settings.edit()
        val gson = Gson()
        val jsonLockedApp = gson.toJson(lockedApp)
        editor.putString(Constants.DATA_APP_LOCK, jsonLockedApp)
        editor.commit()
    }

    fun addLocked(context: Context, app: String?) {
        var lockedApp: MutableList<String?>? = getLocked(context)
        if (lockedApp == null) lockedApp = ArrayList()
        lockedApp.add(app)
        saveLocked(context, lockedApp)
    }

    fun removeLocked(context: Context, app: String?) {
        val locked = getLocked(context)
        if (locked != null) {
            locked.remove(app)
            saveLocked(context, locked)
        }
    }

    fun getLocked(context: Context): ArrayList<String?>? {
        var locked: List<String?>?
        val settings: SharedPreferences = context.getSharedPreferences(
            Constants.MyPREFERENCES,
            Context.MODE_PRIVATE
        )
        if (settings.contains(Constants.DATA_APP_LOCK)) {
            val jsonLocked =
                settings.getString(Constants.DATA_APP_LOCK, null)
            val gson = Gson()
            val lockedItems = gson.fromJson(
                jsonLocked,
                Array<String>::class.java
            )
            locked = Arrays.asList(*lockedItems)
            locked = ArrayList(locked)
        } else return null
        return locked
    }


    companion object {
        private var sInstance: SharePreferencesManager? = null

        @Synchronized
        fun initializeInstance(context: Context?) {
            if (sInstance == null) {
                sInstance = context?.let { SharePreferencesManager(it) }
            }
        }

        @get:Synchronized
        val instance: SharePreferencesManager
            get() {
                if (sInstance == null) {
                    throw IllegalStateException(
                        SharePreferencesManager::class.java.simpleName +
                                " is not initialized, call initializeInstance(..) method first."
                    )
                }
                return sInstance as SharePreferencesManager
            }
    }

}