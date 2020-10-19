package com.programmersbox.testingplaygroundapp
/*

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Icon
import android.os.Build
import android.provider.Settings
import android.service.controls.Control
import android.service.controls.ControlsProviderService
import android.service.controls.DeviceTypes
import android.service.controls.actions.*
import android.service.controls.templates.RangeTemplate
import android.service.controls.templates.TemperatureControlTemplate
import androidx.annotation.RequiresApi
import io.reactivex.Flowable
import io.reactivex.processors.ReplayProcessor
import org.reactivestreams.FlowAdapters
import java.util.concurrent.Flow
import java.util.function.Consumer

@RequiresApi(Build.VERSION_CODES.R)
class TestingControlService : ControlsProviderService() {

    private lateinit var updatePublisher: ReplayProcessor<Control>
    private val settingsPendingIntent by lazy {
        val context: Context = baseContext
        val i = Intent(Settings.ACTION_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        PendingIntent.getActivity(
            context,
            CONTROL_REQUEST_CODE,
            i,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    override fun createPublisherForAllAvailable(): Flow.Publisher<Control> {
        //do all rest apis/getting data here and map it to a Control
        return FlowAdapters.toFlowPublisher(
            Flowable.fromIterable(
                mutableListOf(
                    settingsControl(),
                    slideControl(),
                    tempControl()
                )
            )
        )
    }

    private fun settingsControl() = Control.StatefulBuilder(CONTROL_BUTTON_ID, settingsPendingIntent)
        .setTitle(getString(R.string.settings_button_title))
        .setSubtitle(getString(R.string.settings_button_summary))
        .setStructure(getString(R.string.structure_home))
        .setCustomIcon(Icon.createWithResource(this, R.drawable.ace1))
        .setDeviceType(DeviceTypes.TYPE_SWITCH)
        .setStatus(Control.STATUS_OK)
        .build()

    private fun slideControl() = Control.StatefulBuilder(SLIDE_CONTROL_ID, settingsPendingIntent)
        .setTitle("Slide Me!")
        .setSubtitle("Please?")
        .setStructure(getString(R.string.structure_home))
        .setCustomColor(ColorStateList.valueOf(Color.parseColor("#448aff")))
        .setCustomIcon(Icon.createWithResource(this, R.drawable.ace1))
        .setDeviceType(DeviceTypes.TYPE_LIGHT)
        .setControlTemplate(
            RangeTemplate(
                SLIDE_CONTROL_ID,
                0f,
                100f,
                50f,
                1f,
                "• %.0f%%"
            )
        )
        .setStatus(Control.STATUS_OK)
        .build()

    private fun tempControl() = Control.StatefulBuilder(TEMP_CONTROL_ID, settingsPendingIntent)
        .setTitle("Change Me!")
        .setSubtitle("Please?")
        .setStructure(getString(R.string.structure_home))
        .setCustomColor(ColorStateList.valueOf(Color.parseColor("#448aff")))
        .setDeviceType(DeviceTypes.TYPE_HEATER)
        .setControlTemplate(
            TemperatureControlTemplate(
                TEMP_CONTROL_ID,
                RangeTemplate(
                    TEMP_CONTROL_ID,
                    0f,
                    100f,
                    50f,
                    1f,
                    null
                ),
                TemperatureControlTemplate.MODE_HEAT_COOL,
                TemperatureControlTemplate.MODE_HEAT_COOL,
                TemperatureControlTemplate.FLAG_MODE_HEAT_COOL
            )
        )
        .setStatus(Control.STATUS_OK)
        .build()

    override fun performControlAction(
        controlId: String,
        action: ControlAction,
        consumer: Consumer<Int>
    ) {
        when (action) {

            is BooleanAction -> {
                if (controlId == CONTROL_BUTTON_ID) {
                    consumer.accept(ControlAction.RESPONSE_OK)
                    val control = Control.StatefulBuilder(CONTROL_BUTTON_ID, settingsPendingIntent)
                        .setTitle(getString(R.string.settings_button_title))
                        .setSubtitle(getString(R.string.settings_button_summary))
                        .setStructure(getString(R.string.structure_home))
                        .setCustomIcon(Icon.createWithResource(this, R.drawable.ace1))
                        .setDeviceType(DeviceTypes.TYPE_SWITCH)
                        .setStatus(Control.STATUS_OK)
                        .build()
                    updatePublisher.onNext(control)
                }
            }

            is ModeAction -> {

            }

            is FloatAction -> {
                if (controlId == SLIDE_CONTROL_ID) {
                    consumer.accept(ControlAction.RESPONSE_OK)
                    val control = Control.StatefulBuilder(SLIDE_CONTROL_ID, settingsPendingIntent)
                        .setTitle("Slide Me!")
                        .setSubtitle("Please?")
                        .setStructure(getString(R.string.structure_home))
                        .setCustomColor(ColorStateList.valueOf(Color.parseColor("#448aff")))
                        .setCustomIcon(Icon.createWithResource(this, R.drawable.ace1))
                        .setDeviceType(DeviceTypes.TYPE_LIGHT)
                        .setStatus(Control.STATUS_OK)
                        .setControlTemplate(
                            RangeTemplate(
                                SLIDE_CONTROL_ID,
                                0f,
                                100f,
                                action.newValue,
                                1f,
                                null
                            )
                        )
                        .build()
                    updatePublisher.onNext(control)
                }

                if(controlId == TEMP_CONTROL_ID) {
                    consumer.accept(ControlAction.RESPONSE_OK)
                    Control.StatefulBuilder(TEMP_CONTROL_ID, settingsPendingIntent)
                        .setTitle("Change Me!")
                        .setSubtitle("Please?")
                        .setStructure(getString(R.string.structure_home))
                        .setCustomColor(ColorStateList.valueOf(Color.parseColor("#448aff")))
                        .setDeviceType(DeviceTypes.TYPE_HEATER)
                        .setControlTemplate(
                            TemperatureControlTemplate(
                                TEMP_CONTROL_ID,
                                RangeTemplate(
                                    TEMP_CONTROL_ID,
                                    0f,
                                    100f,
                                    action.newValue,
                                    1f,
                                    null
                                ),
                                TemperatureControlTemplate.MODE_HEAT_COOL,
                                TemperatureControlTemplate.MODE_HEAT_COOL,
                                TemperatureControlTemplate.FLAG_MODE_HEAT_COOL
                            )
                        )
                        .setStatus(Control.STATUS_OK)
                        .build()
                }
            }

            is CommandAction -> {

            }
        }
    }

    override fun createPublisherFor(controlIds: MutableList<String>): Flow.Publisher<Control> {
        updatePublisher = ReplayProcessor.create()
        val controls = mutableListOf<Control>()
        if (controlIds.contains(CONTROL_BUTTON_ID)) {
            val control = Control.StatefulBuilder(CONTROL_BUTTON_ID, settingsPendingIntent)
                .setTitle(getString(R.string.settings_button_title))
                .setSubtitle(getString(R.string.settings_button_summary))
                .setStructure(getString(R.string.structure_home))
                .setCustomIcon(Icon.createWithResource(this, R.drawable.ace1))
                .setDeviceType(DeviceTypes.TYPE_SWITCH)
                .setStatus(Control.STATUS_OK)
                .build()
            updatePublisher.onNext(control)
            controls.add(control)
        }

        if (controlIds.contains(SLIDE_CONTROL_ID)) {
            val control = Control.StatefulBuilder(SLIDE_CONTROL_ID, settingsPendingIntent)
                .setTitle("Slide Me!")
                .setSubtitle("Please?")
                .setStructure(getString(R.string.structure_home))
                .setCustomColor(ColorStateList.valueOf(Color.parseColor("#448aff")))
                .setCustomIcon(Icon.createWithResource(this, R.drawable.ace1))
                .setDeviceType(DeviceTypes.TYPE_LIGHT)
                .setStatus(Control.STATUS_OK)
                .setControlTemplate(
                    RangeTemplate(
                        SLIDE_CONTROL_ID,
                        0f,
                        100f,
                        50f,
                        1f,
                        "• %.0f%%"
                    )
                )
                .build()
            updatePublisher.onNext(control)
            controls.add(control)
        }

        if (controlIds.contains(TEMP_CONTROL_ID)) {
            val control = Control.StatefulBuilder(TEMP_CONTROL_ID, settingsPendingIntent)
                .setTitle("Change Me!")
                .setSubtitle("Please?")
                .setStructure(getString(R.string.structure_home))
                .setCustomColor(ColorStateList.valueOf(Color.parseColor("#448aff")))
                .setDeviceType(DeviceTypes.TYPE_HEATER)
                .setControlTemplate(
                    TemperatureControlTemplate(
                        TEMP_CONTROL_ID,
                        RangeTemplate(
                            TEMP_CONTROL_ID,
                            0f,
                            100f,
                            50f,
                            1f,
                            "• %.0f%%"
                        ),
                        TemperatureControlTemplate.MODE_HEAT_COOL,
                        TemperatureControlTemplate.MODE_HEAT_COOL,
                        TemperatureControlTemplate.FLAG_MODE_HEAT_COOL
                    )
                )
                .setStatus(Control.STATUS_OK)
                .build()
            updatePublisher.onNext(control)
            controls.add(control)
        }
        return FlowAdapters.toFlowPublisher(Flowable.fromIterable(controls))
    }

    companion object {
        private const val CONTROL_REQUEST_CODE = 100
        private const val CONTROL_BUTTON_ID = "button_id"
        private const val SLIDE_CONTROL_ID = "slide_id"
        private const val TEMP_CONTROL_ID = "temp_id"
    }

}
*/
