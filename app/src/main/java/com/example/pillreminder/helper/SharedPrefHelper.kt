package com.example.pillreminder.helper

import android.content.Context
import android.content.SharedPreferences

class SharedPrefHelper(context: Context) {
    private var instance: SharedPrefHelper?=null
    private var context: Context?=null
    private val prefName: String = context.getString(com.example.pillreminder.R.string.app_name)
    private val prefPassCode: String = context.getString(com.example.pillreminder.R.string.app_name)
    private val keyUserId: String = "user_id"
    private val keyPassword: String = "password"
    private val keyPassCode: String = "pass_code"

    init {
        this.context=context
    }

    @Synchronized
    fun getInstance(): SharedPrefHelper {
        if (instance == null) {
            instance=SharedPrefHelper(context!!)
        }
        return instance as SharedPrefHelper
    }

    fun loginUser(user_id: String,password:String): Boolean {
        val sp: SharedPreferences=context!!.getSharedPreferences(prefName,Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sp.edit()
        editor.putString(keyUserId, user_id)
        editor.putString(keyPassword, password)
        editor.apply()
        return true
    }

    fun isLoggedIn(): Boolean {
        val sp: SharedPreferences=context!!.getSharedPreferences(prefName,Context.MODE_PRIVATE)
        return sp.getString(keyUserId, null) != null
    }

    fun getUserId(): String? {
        val sp: SharedPreferences=context!!.getSharedPreferences(prefName,Context.MODE_PRIVATE)
        return sp.getString(keyUserId, null)
    }

    fun getPassword(): String? {
        val sp: SharedPreferences=context!!.getSharedPreferences(prefName,Context.MODE_PRIVATE)
        return sp.getString(keyPassword, null)
    }

    fun setPassCode(passCode: String): Boolean {
        val sp: SharedPreferences=context!!.getSharedPreferences(prefPassCode,Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sp.edit()
        editor.putString(keyPassCode, passCode)
        editor.apply()
        return true
    }

    fun getPassCode(): String? {
        val sp: SharedPreferences=context!!.getSharedPreferences(prefPassCode,Context.MODE_PRIVATE)
        return sp.getString(keyPassCode, null)
    }

    fun logout(): Boolean {
        val sp: SharedPreferences=context!!.getSharedPreferences(prefName,Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sp.edit()
        editor.clear();
        editor.apply();
        return true;
    }

    fun clearPassCode(): Boolean {
        val sp: SharedPreferences=context!!.getSharedPreferences(prefPassCode,Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sp.edit()
        editor.clear();
        editor.apply();
        return true;
    }

}
