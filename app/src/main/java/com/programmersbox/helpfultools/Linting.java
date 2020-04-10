package com.programmersbox.helpfultools;

import android.util.Log;

import com.programmersbox.helpfulutils.DeviceInfo;
import com.programmersbox.loggingutils.Loged;

class Linting {
    void test() {
        Log.w("Hello", "World");
        Log.wtf("Hello", "World");
        Log.i("Hello", "World");
        Log.v("Hello", "World");
        Log.e("Hello", "World");
        Log.d("Hello", "World");

        Loged.INSTANCE.w("World", "Hello", true, true);
        Loged.INSTANCE.a("World", "Hello", true, true);
        Loged.INSTANCE.i("World", "Hello", true, true);
        Loged.INSTANCE.v("World", "Hello", true, true);
        Loged.INSTANCE.e("World", "Hello", true, true);
        Loged.INSTANCE.d("World", "Hello", true, true);
        Loged.INSTANCE.wtf("World", "Hello", true, true);
        Loged.INSTANCE.r("World", "Hello", true, true, 2, 3, 4, 5, 6, 7);

        Log.d("Gson", new DeviceInfo.Info().toString());
        Loged.INSTANCE.d(new DeviceInfo.Info(), "Gson", true, true);
    }
}
