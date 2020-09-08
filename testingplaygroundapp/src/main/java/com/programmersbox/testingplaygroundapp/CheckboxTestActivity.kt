package com.programmersbox.testingplaygroundapp

import android.os.Bundle
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
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
        checkboxGroup.headerCheckBox?.text = "Enable"

        checkboxButton.setOnClickListener {
            checkboxGroup.setIsCheckGroupHeaderEnabled(!checkboxGroup.isCheckGroupHeaderEnabled)
            checkboxGroup2.isCheckGroupHeaderEnabled = !checkboxGroup2.isCheckGroupHeaderEnabled
            checkboxGroup3.setIsCheckGroupHeaderEnabled(!checkboxGroup3.isCheckGroupHeaderEnabled)
        }

        checkboxGroup3.setOnCustomGroupCheckListener { group, isChecked ->
            group.children.drop(1).filterIsInstance<CheckBox>().forEach { it.isChecked = isChecked }
        }

        checkboxGroup.setOnCheckedChangeListener { group, checkedId, isChecked ->
            when (checkedId) {
                R.id.first -> println("First!")
                R.id.second -> println("Second!")
                R.id.third -> println("Third!")
                group.headerCheckBox?.id -> {
                    /*group.children.drop(1).forEach {
                        it.isEnabled = isChecked
                    }*/
                }
            }
        }


    }
}