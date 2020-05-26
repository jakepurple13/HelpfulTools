package com.programmersbox.testingplaygroundapp

import android.content.Context
import android.util.AttributeSet
import com.programmersbox.funutils.views.SlideValuePicker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged

class FlowSlidePicker @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    SlideValuePicker(context, attrs, defStyleAttr) {

    private val prog = MutableStateFlow(0.5f)

    fun toFlow() = prog.distinctUntilChanged { old, new -> old == new }

    override fun progressChanged(progress: Float) {
        super.progressChanged(progress)
        prog.value = progress
    }

}

open class SwitchSlidePicker @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    SlideValuePicker(context, attrs, defStyleAttr) {

    var checked = false

    private var listener: Listener? = null

    override fun progressChanged(progress: Float) {
        super.progressChanged(progress)
        checked = when (progress) {
            1.0f -> true
            0.0f -> false
            else -> checked
        }
        listener?.onChecked(checked)
    }

    interface Listener {
        fun onChecked(checked: Boolean)
    }

    fun setCheckedListener(checked: (Boolean) -> Unit) {
        listener = object : Listener {
            override fun onChecked(checked: Boolean) = checked(checked)
        }
    }

}