package com.programmersbox.helpfulutils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import androidx.annotation.RequiresApi

/**
 * @see Intent.ACTION_TIME_TICK
 */
fun Context.timeTick(received: (context: Context, intent: Intent) -> Unit) = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) = received(context, intent)
}.also { registerReceiver(it, IntentFilter(Intent.ACTION_TIME_TICK)) }

//--------------------------
/**
 * This will give updates whenever the battery changes
 * @see Intent.ACTION_BATTERY_CHANGED
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun Context.battery(batteryInfo: (info: Battery) -> Unit) = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) = batteryInfo(batteryInformation(context, intent))
}.also { registerReceiver(it, batteryIntentFilter()) }

/**
 * This will give the current status
 * @see Intent.ACTION_BATTERY_CHANGED
 */
val Context.batteryInfo: Battery
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    get() = batteryInformation(this, registerReceiver(null, batteryIntentFilter()))

data class Battery(
    val percent: Float,
    val isCharging: Boolean,
    val chargeType: ChargeType,
    val health: BatteryHealth,
    val technology: String?,
    val temperature: Float,
    val voltage: Int,
    val capacity: Long
)

enum class ChargeType { USB, AC, WIRELESS, NONE }
enum class BatteryHealth { COLD, DEAD, GOOD, OVER_VOLTAGE, OVERHEAT, UNSPECIFIED_FAILURE, UNKNOWN }

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun batteryInformation(context: Context, intent: Intent?): Battery {
    //percentage
    val level: Int = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
    val scale: Int = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
    val batteryPct: Float = level * 100 / scale.toFloat()

    //charging status
    val status: Int = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
    val isCharging: Boolean = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL

    // How are we charging?
    val chargePlug: Int = intent?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) ?: -1
    val usbCharge: Boolean = chargePlug == BatteryManager.BATTERY_PLUGGED_USB
    val acCharge: Boolean = chargePlug == BatteryManager.BATTERY_PLUGGED_AC
    val wirelessCharge: Boolean = chargePlug == BatteryManager.BATTERY_PLUGGED_WIRELESS
    val type = when {
        usbCharge -> ChargeType.USB
        acCharge -> ChargeType.AC
        wirelessCharge -> ChargeType.WIRELESS
        else -> ChargeType.NONE
    }

    //Battery Health
    val present = intent?.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false)
    val health = if (present == true) {
        when (intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0)) {
            BatteryManager.BATTERY_HEALTH_COLD -> BatteryHealth.COLD
            BatteryManager.BATTERY_HEALTH_DEAD -> BatteryHealth.DEAD
            BatteryManager.BATTERY_HEALTH_GOOD -> BatteryHealth.GOOD
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> BatteryHealth.OVER_VOLTAGE
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> BatteryHealth.OVERHEAT
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> BatteryHealth.UNSPECIFIED_FAILURE
            BatteryManager.BATTERY_HEALTH_UNKNOWN -> BatteryHealth.UNKNOWN
            else -> BatteryHealth.UNKNOWN
        }

    } else {
        BatteryHealth.UNKNOWN
    }

    //technology
    val technology = intent?.extras?.getString(BatteryManager.EXTRA_TECHNOLOGY)

    //temperature
    val temperature = intent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 0

    val temp = if (temperature > 0) (temperature.toFloat() / 10f) else temperature.toFloat()

    //voltage
    val voltage = intent?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) ?: 0

    //capacity
    val mBatteryManager = context.batteryManager
    val chargeCounter = mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
    val capacity = mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    val cap = (chargeCounter.toFloat() / capacity.toFloat() * 100f).toLong()

    return Battery(batteryPct, isCharging, type, health, technology, temp, voltage, cap)
}

fun batteryIntentFilter() = IntentFilter().apply {
    addAction(Intent.ACTION_POWER_CONNECTED)
    addAction(Intent.ACTION_POWER_DISCONNECTED)
    addAction(Intent.ACTION_BATTERY_CHANGED)
}

//--------------------------
/**
 * @see Intent.ACTION_SCREEN_OFF
 */
fun Context.screenOff(received: (context: Context, intent: Intent) -> Unit) = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) = received(context, intent)
}.also { registerReceiver(it, IntentFilter(Intent.ACTION_SCREEN_OFF)) }

/**
 * @see Intent.ACTION_SCREEN_ON
 */
fun Context.screenOn(received: (context: Context, intent: Intent) -> Unit) = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) = received(context, intent)
}.also { registerReceiver(it, IntentFilter(Intent.ACTION_SCREEN_ON)) }

enum class ScreenState { ON, OFF, UNKNOWN }

/**
 * @see Intent.ACTION_SCREEN_ON
 * @see Intent.ACTION_SCREEN_OFF
 */
fun Context.screenState(received: (ScreenState) -> Unit) = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) = received(
        when (intent.action) {
            Intent.ACTION_SCREEN_OFF -> ScreenState.OFF
            Intent.ACTION_SCREEN_ON -> ScreenState.ON
            else -> ScreenState.UNKNOWN
        }
    )
}.also { registerReceiver(it, IntentFilter(Intent.ACTION_SCREEN_ON).apply { addAction(Intent.ACTION_SCREEN_OFF) }) }
//--------------------------
