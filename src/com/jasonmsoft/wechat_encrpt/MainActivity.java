package com.jasonmsoft.wechat_encrpt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    public void onBtnOk(View view)
    {
        Log.d("SSS", "on click!!!!!!!!!!!!!!!!");
        Intent intent = new Intent(this, wechat_encrpt.class);
        startService(intent);
    }
}
