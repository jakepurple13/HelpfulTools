package com.programmersbox.testingplaygroundapp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.programmersbox.funutils.views.TableAdapter
import com.programmersbox.funutils.views.TableAdapterCreator
import com.programmersbox.funutils.views.TableModel
import com.programmersbox.helpfulutils.nextColor
import kotlinx.android.synthetic.main.activity_table.*
import kotlin.random.Random

class TableActivity : AppCompatActivity() {

    private val adapter by lazy {
        TableAdapter<Any>(object : TableAdapterCreator<Any> {
            override fun setHeader(textView: TextView, rowPosition: Int, columnPosition: Int) {
                textView.setBackgroundColor(Random.nextColor(255))
            }

            override fun cellClick(textView: TextView, item: Any, rowPosition: Int, columnPosition: Int) {
                cellClick(item)
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_table)

        tableRV.adapter = adapter
            .apply {
                addItem(
                    TableModel.HeaderModel<Any>(
                        "Number 1" to "",
                        "Number 2" to "",
                        "Number 3" to ""
                    ).apply {
                        weightArray = floatArrayOf(2f, 4f, 4f)
                    }
                )

                addHeader(
                    "1" to "", "2" to "",
                    "3" to "", "4" to "", "5" to "", "6" to "",
                    "7" to "", "8" to "", "9" to "", "10" to "",
                )

                var count = 0

                addCells(
                    "${++count}" to "", "${++count}" to "",
                    "${++count}" to "", "${++count}" to "", "${++count}" to "", "${++count}" to "",
                    "${++count}" to "", "${++count}" to "", "${++count}" to "", "${++count}" to ""
                )

                addCells(
                    "${++count}" to "", "${++count}" to "",
                    "${++count}" to "", "${++count}" to "", "${++count}" to "", "${++count}" to "",
                    "${++count}" to "", "${++count}" to "", "${++count}" to "", "${++count}" to ""
                )

                addCells(
                    "${++count}" to "", "${++count}" to "",
                    "${++count}" to "", "${++count}" to "", "${++count}" to "", "${++count}" to "",
                    "${++count}" to "", "${++count}" to "", "${++count}" to "", "${++count}" to ""
                )

                addCells(
                    "${++count}" to "", "${++count}" to "",
                    "${++count}" to "", "${++count}" to "", "${++count}" to "", "${++count}" to "",
                    "${++count}" to "", "${++count}" to "", "${++count}" to "", "${++count}" to ""
                )

                addCells(
                    "${++count}" to "", "${++count}" to "",
                    null, null, null, null,
                    "${++count}" to "", "${++count}" to "", "${++count}" to "", "${++count}" to ""
                )

                addCells(
                    "${++count}" to "", "${++count}" to "",
                    null, null, null, null,
                    null, null, null, null
                )

                addCells(
                    "${++count}" to "", "${++count}" to "",
                    null, null, null, null,
                    null, null, null, null
                )

                addCells(
                    "${++count}" to "", "${++count}" to "",
                    null, null, null, null,
                    null, null, null, null
                )
            }

    }

    private fun cellClick(item: Any) {
        println(item)
        println(adapter.getColumn(0))
    }
}