package com.programmersbox.helpfulutils

import android.accounts.AccountManager
import android.app.*
import android.app.admin.DevicePolicyManager
import android.bluetooth.BluetoothManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.hardware.SensorManager
import android.hardware.display.DisplayManager
import android.hardware.input.InputManager
import android.hardware.usb.UsbManager
import android.location.LocationManager
import android.media.AudioManager
import android.media.MediaRouter
import android.net.ConnectivityManager
import android.net.nsd.NsdManager
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pManager
import android.nfc.NfcManager
import android.os.*
import android.os.storage.StorageManager
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.accessibility.AccessibilityManager
import android.view.inputmethod.InputMethodManager
import android.view.textservice.TextServicesManager
import androidx.annotation.RequiresApi

//----------------------------------------------------------------------------------------------------------------------------------------------------
enum class AudioStreamTypes(internal var type: Int) {
    STREAM_VOICE_CALL(AudioManager.STREAM_VOICE_CALL),
    STREAM_SYSTEM(AudioManager.STREAM_SYSTEM),
    STREAM_RING(AudioManager.STREAM_RING),
    STREAM_MUSIC(AudioManager.STREAM_MUSIC),
    STREAM_ALARM(AudioManager.STREAM_ALARM),
    STREAM_NOTIFICATION(AudioManager.STREAM_NOTIFICATION),
    STREAM_DTMF(AudioManager.STREAM_DTMF),

    @RequiresApi(Build.VERSION_CODES.O)
    STREAM_ACCESSIBILITY(AudioManager.STREAM_ACCESSIBILITY);

    fun setVolume(context: Context, value: Int) = context.setStreamVolume(value, this)
    fun getVolume(context: Context) = context.getStreamVolume(this)
}

/**
 * Set the volume
 * @param value - the volume level
 * @param type - The category of the type of stream. Default is [AudioStreamTypes.STREAM_MUSIC]
 */
fun Context.setStreamVolume(value: Int, type: AudioStreamTypes = AudioStreamTypes.STREAM_MUSIC, flags: Int = 0) =
    audioManager.setStreamVolume(type.type, value, flags)

/**
 * Get the volume
 * @param type - The category of the type of stream. Default is [AudioStreamTypes.STREAM_MUSIC]
 */
fun Context.getStreamVolume(type: AudioStreamTypes = AudioStreamTypes.STREAM_MUSIC) = audioManager.getStreamVolume(type.type)

/**
 * get AudioManager
 * @see AudioManager
 */
val Context.audioManager get() = getSystemService(Context.AUDIO_SERVICE) as AudioManager
//----------------------------------------------------------------------------------------------------------------------------------------------------
/**
 * get ActivityManager
 * @see ActivityManager
 */
val Context.activityManager get() = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
//----------------------------------------------------------------------------------------------------------------------------------------------------
/**
 * get AlarmManager
 * @see AlarmManager
 */
val Context.alarmManager get() = getSystemService(Context.ALARM_SERVICE) as AlarmManager
//----------------------------------------------------------------------------------------------------------------------------------------------------
/**
 * Easy copy method for the clipboard manager
 * @see ClipboardManager.setPrimaryClip
 */
fun ClipboardManager.copy(label: CharSequence, text: CharSequence) = setPrimaryClip(ClipData.newPlainText(label, text))

/**
 * Easy paste/getText method for the clipboard manager
 * @see ClipData.Item.getText
 */
fun ClipboardManager.paste() = primaryClip?.getItemAt(0)?.text

/**
 * get ClipboardManager
 * @see ClipboardManager
 */
val Context.clipboardManager get() = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//----------------------------------------------------------------------------------------------------------------------------------------------------
/**
 * get ConnectivityManager
 * @see ConnectivityManager
 */
val Context.connectivityManager get() = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//----------------------------------------------------------------------------------------------------------------------------------------------------
/**
 * get KeyguardManager
 * @see KeyguardManager
 */
val Context.keyguardManager get() = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
//----------------------------------------------------------------------------------------------------------------------------------------------------
/**
 * get Layout Inflater Service
 */
val Context.layoutInflater: LayoutInflater get() = LayoutInflater.from(this)
//----------------------------------------------------------------------------------------------------------------------------------------------------
/**
 * get LocationManager
 * @see LocationManager
 */
val Context.locationManager get() = getSystemService(Context.LOCATION_SERVICE) as LocationManager
//----------------------------------------------------------------------------------------------------------------------------------------------------
/**
 * get NotificationManager
 * @see NotificationManager
 */
val Context.notificationManager get() = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//----------------------------------------------------------------------------------------------------------------------------------------------------
/**
 * get PowerManager
 * @see PowerManager
 */
val Context.powerManager get() = getSystemService(Context.POWER_SERVICE) as PowerManager
//----------------------------------------------------------------------------------------------------------------------------------------------------
/**
 * get SearchManager
 * @see SearchManager
 */
val Context.searchManager get() = getSystemService(Context.SEARCH_SERVICE) as SearchManager
//----------------------------------------------------------------------------------------------------------------------------------------------------
/**
 * get SensorManager
 * @see SensorManager
 */
val Context.sensorManager get() = getSystemService(Context.SENSOR_SERVICE) as SensorManager
//----------------------------------------------------------------------------------------------------------------------------------------------------
/**
 * get TelephonyManager
 * @see TelephonyManager
 */
val Context.telephonyManager get() = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//----------------------------------------------------------------------------------------------------------------------------------------------------
/**
 * get Vibrator Service
 * @see Vibrator
 */
val Context.vibrator get() = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
//----------------------------------------------------------------------------------------------------------------------------------------------------
/**
 * get WifiManager
 * @see WifiManager
 */
val Context.wifiManager get() = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
//----------------------------------------------------------------------------------------------------------------------------------------------------
/**
 * get WindowManager
 * @see WindowManager
 */
val Context.windowManager get() = getSystemService(Context.WINDOW_SERVICE) as WindowManager
//----------------------------------------------------------------------------------------------------------------------------------------------------
/**
 * get InputMethodManager
 * @see InputMethodManager
 */
val Context.inputMethodManager get() = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//----------------------------------------------------------------------------------------------------------------------------------------------------
/**
 * get AccessibilityManager
 * @see AccessibilityManager
 */
val Context.accessibilityManager get() = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
//----------------------------------------------------------------------------------------------------------------------------------------------------
/**
 * get AccountManager
 * @see AccountManager
 */
val Context.accountManager get() = getSystemService(Context.ACCOUNT_SERVICE) as AccountManager
//----------------------------------------------------------------------------------------------------------------------------------------------------
/**
 * get DevicePolicyManager
 * @see DevicePolicyManager
 */
val Context.devicePolicyManager get() = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
//----------------------------------------------------------------------------------------------------------------------------------------------------
/**
 * get DropBoxManager
 * @see DropBoxManager
 */
val Context.dropBoxManager get() = getSystemService(Context.DROPBOX_SERVICE) as DropBoxManager
//----------------------------------------------------------------------------------------------------------------------------------------------------
/**
 * get UiModeManager
 * @see UiModeManager
 */
val Context.uiModeManager get() = getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
//----------------------------------------------------------------------------------------------------------------------------------------------------
/**
 * get DownloadManager
 * @see DownloadManager
 */
val Context.downloadManager get() = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
//----------------------------------------------------------------------------------------------------------------------------------------------------
/**
 * get StorageManager
 * @see StorageManager
 */
val Context.storageManager get() = getSystemService(Context.STORAGE_SERVICE) as StorageManager
//----------------------------------------------------------------------------------------------------------------------------------------------------
/**
 * get NfcManager
 * @see NfcManager
 */
val Context.nfcManager get() = getSystemService(Context.NFC_SERVICE) as NfcManager
//----------------------------------------------------------------------------------------------------------------------------------------------------
/**
 * get UsbManager
 * @see UsbManager
 */
val Context.usbManager get() = getSystemService(Context.USB_SERVICE) as UsbManager
//----------------------------------------------------------------------------------------------------------------------------------------------------
/**
 * get TextServicesManager
 * @see TextServicesManager
 */
val Context.textServicesManager get() = getSystemService(Context.TEXT_SERVICES_MANAGER_SERVICE) as TextServicesManager
//----------------------------------------------------------------------------------------------------------------------------------------------------
/**
 * get WifiP2pManager
 * @see WifiP2pManager
 */
val Context.wifiP2pManager get() = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
//----------------------------------------------------------------------------------------------------------------------------------------------------
/**
 * get InputManager
 * @see InputManager
 */
val Context.inputManager get() = getSystemService(Context.INPUT_SERVICE) as InputManager
//----------------------------------------------------------------------------------------------------------------------------------------------------
/**
 * get Media Router
 */
val Context.mediaRouter get() = getSystemService(Context.MEDIA_ROUTER_SERVICE) as MediaRouter
//----------------------------------------------------------------------------------------------------------------------------------------------------
/**
 * get NsdManager
 * @see NsdManager
 */
val Context.nsdManager get() = getSystemService(Context.NSD_SERVICE) as NsdManager
//----------------------------------------------------------------------------------------------------------------------------------------------------
/**
 * get DisplayManager
 * @see DisplayManager
 */
val Context.displayManager get() = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
//----------------------------------------------------------------------------------------------------------------------------------------------------
/**
 * get UserManager
 * @see UserManager
 */
val Context.userManager get() = getSystemService(Context.USER_SERVICE) as UserManager
//----------------------------------------------------------------------------------------------------------------------------------------------------
/**
 * get BluetoothManager
 * @see BluetoothManager
 */
val Context.bluetoothManager get() = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
//----------------------------------------------------------------------------------------------------------------------------------------------------