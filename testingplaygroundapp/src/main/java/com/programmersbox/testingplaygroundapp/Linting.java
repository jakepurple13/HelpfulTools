package com.programmersbox.testingplaygroundapp;

import android.util.Log;

import com.programmersbox.loggingutils.Loged;

class Linting {
    void test() {
        Log.v("Hello", "World");
        Log.i("Hello", "World");
        Log.d("Hello", "World");
        Log.wtf("Hello", "World");
        Log.w("Hello", "World");
        Log.e("Hello", "World");

        Loged.INSTANCE.w("World", "Hello", true, true);
        Loged.INSTANCE.a("World", "Hello", true, true);
        Loged.INSTANCE.i("World", "Hello", true, true);
        Loged.INSTANCE.v("World", "Hello", true, true);
        Loged.INSTANCE.e("World", "Hello", true, true);
        Loged.INSTANCE.d("World", "Hello", true, true);
        Loged.INSTANCE.wtf("World", "Hello", true, true);
        Loged.INSTANCE.r("World", "Hello", true, true, 2, 3, 4, 5, 6, 7);
    }
}
