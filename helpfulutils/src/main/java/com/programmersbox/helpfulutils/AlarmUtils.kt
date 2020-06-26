package com.programmersbox.helpfulutils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.preference.PreferenceManager
import androidx.annotation.RequiresApi
import org.json.JSONArray
import java.util.*

object AlarmUtils {
    private const val sTagAlarms = ":alarms"

    @RequiresApi(Build.VERSION_CODES.M)
    fun addAlarm(context: Context, intent: Intent?, notificationId: Int, calendar: Calendar) {
        val pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        context.alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        saveAlarmId(context, notificationId)
    }

    fun addRepeatingAlarm(context: Context, intent: Intent?, notificationId: Int, calendar: Calendar, interval: Long) {
        val pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        context.alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, interval, pendingIntent)
        saveAlarmId(context, notificationId)
    }

    fun cancelAlarm(context: Context, intent: Intent?, notificationId: Int) {
        val pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        context.alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
        removeAlarmId(context, notificationId)
    }

    fun cancelAllAlarms(context: Context, intent: Intent?) = getAlarmIds(context).forEach { cancelAlarm(context, intent, it) }

    fun hasAlarm(context: Context?, intent: Intent?, notificationId: Int): Boolean =
        PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_NO_CREATE) != null

    private fun saveAlarmId(context: Context, id: Int) {
        val idsAlarms = getAlarmIds(context)
        if (idsAlarms.contains(id)) return
        idsAlarms.add(id)
        saveIdsInPreferences(context, idsAlarms)
    }

    private fun removeAlarmId(context: Context, id: Int) {
        val idsAlarms: MutableList<Int> = getAlarmIds(context)
        for (i in idsAlarms.indices) {
            if (idsAlarms[i] == id) idsAlarms.removeAt(i)
        }
        saveIdsInPreferences(context, idsAlarms)
    }

    private fun getAlarmIds(context: Context): MutableList<Int> {
        val ids: MutableList<Int> = ArrayList()
        try {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val jsonArray2 = JSONArray(prefs.getString(context.packageName + sTagAlarms, "[]"))
            for (i in 0 until jsonArray2.length()) {
                ids.add(jsonArray2.getInt(i))
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return ids
    }

    private fun saveIdsInPreferences(context: Context, lstIds: List<Int>) {
        val jsonArray = JSONArray()
        for (idAlarm in lstIds) jsonArray.put(idAlarm)
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = prefs.edit()
        editor.putString(context.packageName + sTagAlarms, jsonArray.toString())
        editor.apply()
    }
}