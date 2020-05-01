package com.programmersbox.helpfulutils

import android.Manifest
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.random.Random

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    val timer = EasyCountDownTimer(1000) {
        println("Done")
    }

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.programmersbox.helpfulutils.test", appContext.packageName)
        appContext.setStreamVolume(50, AudioStreamTypes.STREAM_MUSIC)
        println(appContext.getStreamVolume(AudioStreamTypes.STREAM_MUSIC))
        BiometricBuilder.biometricBuilder(FragmentActivity(R.layout.device_credential_handler_activity)) {

            authSuccess {
                "Success"
            }

            authError { _, _ ->
                "Error"
            }

            authFailed {
                "Failed"
            }

            error {
                "Error"
            }

            promptInfo {
                title = "Testing"
                subtitle = "Tester"
                description = "Test"
                negativeButton = null
                confirmationRequired = true
                deviceCredentialAllowed = true
            }
        }
    }

    @Test
    fun otherUtil() {
        println(Random.nextColor())
        println(DeviceInfo.Info())
        println(DeviceInfo.RuntimeInfo())
        println(0x0000FF.toARGB().toColor())
        println(0x0000FF.toRGB().toColor())
        println(0x0000FF.toCMYK().toColor())
        println(0x0000FF.toARGB().toRGB())
        println(0x0000FF.toRGB().toARGB())
        println(0x0000FF.toCMYK().toRGB())
        println(0x0000FF.toARGB().toCMYK())
        println(0x0000FF.toRGB().toCMYK())
        println(0x0000FF.toCMYK().toARGB())
    }

    @Test
    fun sharedPref() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        appContext.defaultSharedPref.edit().putBoolean("boolean", true).commit()
        appContext.defaultSharedPrefName = "Something Else"
        appContext.defaultSharedPref.edit().put("Hello" to "World").commit()
    }

    @Test
    fun viewUtil() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val view = View(appContext)
        view.postDelayed(1000) { }
        val color = appContext.colorFromTheme(R.attr.actionMenuTextColor)
        println(color)
        val linearLayout = LinearLayout(appContext)
        linearLayout.addView(view)
        linearLayout.animateChildren {
            view.visibility = View.GONE
        }
        val recycle = RecyclerView(appContext)
        recycle.quickAdapter<String>()
        recycle.quickAdapter(R.layout.support_simple_spinner_dropdown_item, "Hello", "World") {
            //this is to render the view
            println(it)
        }
        recycle.adapter = QuickAdapter<String>(appContext)
        QuickAdapter<String>(appContext).add(R.layout.support_simple_spinner_dropdown_item, "Hello") {
            println(it)
        }

        val quick = QuickAdapter<String>(appContext)
        quick.add(R.layout.support_simple_spinner_dropdown_item, "Hello", "World") {
            println(it)
        }
        quick.remove()

        val item = quick[0]
        println(item)
        quick[0] = "Goodbye"
        println(quick[0])
        println(quick.dataList)

        runOnUIThread {
            println("On the ui thread now!")
        }

    }

    @Test
    fun activity() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        appContext.startActivity<TestActivity>("Hello" to "World")
        TestActivity().requestPermissions(Manifest.permission.READ_EXTERNAL_STORAGE) {
            println(it.isGranted)
            println(it.grantedPermissions)
            println(it.deniedPermissions)
        }
    }

    class TestActivity : AppCompatActivity()

    @Test
    fun newThings() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        appContext.accessibilityManager
        appContext.packageManager
        appContext.audioManager
        appContext.accountManager
        appContext.activityManager
        appContext.alarmManager
        appContext.bluetoothManager
        appContext.clipboardManager
        appContext.devicePolicyManager
        appContext.downloadManager
        appContext.inputManager
        appContext.connectivityManager
        appContext.keyguardManager
        appContext.layoutInflater
        appContext.notificationManager
        appContext.locationManager
        appContext.powerManager
        appContext.searchManager
        appContext.sensorManager
        appContext.telephonyManager
        appContext.vibrator
        appContext.wifiManager
        appContext.windowManager
        appContext.inputMethodManager
        appContext.dropBoxManager
        appContext.uiModeManager
        appContext.storageManager
        appContext.nfcManager
        appContext.usbManager
        appContext.textServicesManager
        appContext.wifiP2pManager
        appContext.mediaRouter
        appContext.nsdManager
        appContext.displayManager
        appContext.userManager
    }

}