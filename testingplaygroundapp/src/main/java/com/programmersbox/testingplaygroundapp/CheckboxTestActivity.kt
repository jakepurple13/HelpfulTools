package com.programmersbox.testingplaygroundapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.programmersbox.funutils.views.CheckBoxGroup
import kotlinx.android.synthetic.main.activity_checkbox_test.*

class CheckboxTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkbox_test)

        checkboxGroup.setOnCheckedChangeListener { c, i, b ->

        }

        checkboxGroup.setOnCheckedChangeListener { group, checkedId, isChecked -> }

        checkboxGroup.checkedCheckboxButtonIds
        checkboxGroup.clearCheck()
        checkboxGroup.check(-1)
        checkboxGroup.setOnCheckedChangeListener(CheckBoxGroup.OnCheckedChangeListener { group: CheckBoxGroup, checkedId: Int, isChecked: Boolean -> })
        checkboxGroup.headerCheckBox

    }
}