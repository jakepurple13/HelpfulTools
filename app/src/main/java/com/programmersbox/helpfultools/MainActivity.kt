package com.programmersbox.helpfultools

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.programmersbox.loggingutils.Loged

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Loged.i("Hello World")
    }
}