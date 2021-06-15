package com.atta.cicshuttle

import android.content.Context
import android.content.SharedPreferences
import com.atta.cicshuttle.model.User

class SessionManager {



    companion object{
        private const val PREF_NAME = "med_pref"
        //mode 0 private
        private const val MODE = 0
        private const val KEY_FIRST_NAME = "first_name"
        private const val KEY_LAST_NAME = "last_name"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_EMAIL = "email"
        private const val KEY_PHONE = "phone"
        private const val KEY_ENABLED = "enabled"
        private const val KEY_ROUTE_NAME = "route_name"
        private const val KEY_ROUTE_ID = "route_id"
        private const val KEY_DRIVER_NAME = "driver_name"
        private const val KEY_DRIVER_ID = "driver_id"
        private const val KEY_COLLEGE_ID = "college_id"
        private val KEY_LANG = "lang"
        private val KEY_IS_LANG_SELECTED = "isLanguageSelected"

        private var singleton: SessionManager? = null
        private lateinit var pref: SharedPreferences
        private lateinit var editor: SharedPreferences.Editor

        fun with(context: Context) : SessionManager {
            if (null == singleton)
                singleton = Builder(context).build()
            return singleton as SessionManager
        }
    }
    constructor()

    constructor(context: Context) {
        pref =  context.getSharedPreferences(PREF_NAME, MODE)
        editor = pref.edit()
    }

    fun login(user: User){
        editor.putString(KEY_EMAIL, user.email)
        editor.putString(KEY_FIRST_NAME, user.firstName)
        editor.putString(KEY_LAST_NAME, user.lastName)
        editor.putString(KEY_PHONE, user.phone)
        editor.putString(KEY_USER_ID, user.id)
        editor.putBoolean(KEY_ENABLED, user.enabled)
        editor.putString(KEY_COLLEGE_ID, user.collegeId)

        editor.apply()

    }

    fun saveRouteName(routeName: String){
        editor.putString(KEY_ROUTE_NAME, routeName)

        editor.apply()

    }

    fun enable(enable: Boolean){
        editor.putBoolean(KEY_ENABLED, enable)

        editor.apply()

    }

    fun isEnabled(): Boolean {
        return pref.getBoolean(KEY_ENABLED, false)
    }

    fun getCollegeId(): String{

        return pref.getString(KEY_COLLEGE_ID, "").toString()
    }

    fun getEmail(): String{

        return pref.getString(KEY_EMAIL, "").toString()
    }

    fun getPhone(): String{

        return pref.getString(KEY_PHONE, "").toString()
    }
    fun getFirstName(): String{

        return pref.getString(KEY_FIRST_NAME, "").toString()
    }
    fun getLastName(): String{

        return pref.getString(KEY_LAST_NAME, "").toString()
    }


    fun getRouteName(): String{

        return pref.getString(KEY_ROUTE_NAME, "").toString()
    }

    fun getUserId(): String{

        return pref.getString(KEY_USER_ID, "").toString()
    }

    fun getUserName(): String{

        return pref.getString(KEY_FIRST_NAME, "").toString() + " " +
                pref.getString(KEY_LAST_NAME, "").toString()
    }

    fun getUserEmail(): String{

        return pref.getString(KEY_EMAIL, "").toString()
    }


    fun saveRouteData(id: String, routeName: String, driverName: String, driverId: String) {
        editor.putString(KEY_ROUTE_ID, id)
        editor.putString(KEY_ROUTE_NAME, routeName)
        editor.putString(KEY_DRIVER_ID, driverId)
        editor.putString(KEY_DRIVER_NAME, driverName)

        editor.apply()
    }


    fun getRouteId(): String{

        return pref.getString(KEY_ROUTE_ID, "").toString()
    }

    fun getDriverName(): String{

        return pref.getString(KEY_DRIVER_NAME, "").toString()
    }
    fun getDriverId(): String{

        return pref.getString(KEY_DRIVER_ID, "").toString()
    }

    fun logout() {
        editor.clear().apply()

    }
    fun setLanguage(lang: String) {


        // Storing national ID in pref
        editor.putBoolean(KEY_IS_LANG_SELECTED, true)
        editor.putString(KEY_LANG, lang)

        editor.apply()
    }


    fun getLanguage(): String{

        return pref.getString(KEY_LANG, "ar").toString()
    }

    private class Builder(val context: Context) {

        fun build() : SessionManager {

            return SessionManager(context)
        }
    }

}