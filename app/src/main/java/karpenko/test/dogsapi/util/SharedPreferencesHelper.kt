package karpenko.test.dogsapi.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager


class SharedPreferencesHelper {

    companion object{

        private const val TIME = "prefTime"
        private var sharedPrefs: SharedPreferences? = null

        @Volatile private var instance: SharedPreferencesHelper? = null
        private val LOCK = Any()

        operator fun invoke(context: Context): SharedPreferencesHelper = instance?: synchronized(
            LOCK){
            instance?: buildHelper(context).also{
                instance = it
            }
        }

        private fun buildHelper(context: Context): SharedPreferencesHelper{
            sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
            return SharedPreferencesHelper()
        }

    }

    fun updateTime(time: Long){
        sharedPrefs?.edit(commit = true){
            putLong(TIME, time)
        }
    }

    fun getLastUpdateTime() = sharedPrefs?.getLong(TIME, 0)


}