package com.programmersbox.helpfultools;

import android.os.CountDownTimer;
import android.util.Log;

import com.programmersbox.funutils.cards.Card;
import com.programmersbox.funutils.cards.Deck;
import com.programmersbox.helpfulutils.DeviceInfo;
import com.programmersbox.helpfulutils.EasyCountDownTimer;
import com.programmersbox.loggingutils.Loged;

class Linting {
    void test() {
        Log.w("Hello", "World");
        Log.wtf("Hello", "World");
        Log.i("Hello", "World");
        Log.v("Hello", "World");
        Log.e("Hello", "World");
        Log.d("Hello", "World");

        Loged.w("World", "Hello");
        Loged.a("World", "Hello", true);
        Loged.i("World", "Hello", true, true);
        Loged.v("World", "Hello", true, true);
        Loged.e("World");
        Loged.d();
        Loged.wtf("World", "Hello", true, true);
        Loged.r("World", "Hello", true, true, 2, 3, 4, 5, 6, 7);
    }

    void test2() {
        Log.d("Gson", new DeviceInfo.Info().toString());
        Loged.d(new DeviceInfo.Info(), "Gson", true, true);
    }

    void test3() {
        CountDownTimer timer = EasyCountDownTimer.invoke(1000L, () -> {
            System.out.println("Finished");
            return null;
        });

        Deck<Card> deck = Deck.defaultDeck();
    }

}
