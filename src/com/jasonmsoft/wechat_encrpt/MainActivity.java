package com.jasonmsoft.wechat_encrpt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

    private EditText mEncrypto_key;
    private EditText mDecrypto_key;
    private String mEnKey = "";
    private String mDeKey = "";
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        AppConfig.initconfig(this);
        mEncrypto_key = (EditText)findViewById(R.id.encrypto_key);
        mDecrypto_key = (EditText)findViewById(R.id.decrypto_key);

        if(!AppConfig.getFirstRun())
        {
            mEnKey = AppConfig.getString("encrypto_key");
            mDeKey = AppConfig.getString("decrypto_key");
            mEncrypto_key.setText(mEnKey);
            mDecrypto_key.setText(mDeKey);
        }


    }

    public void onBtnOk(View view)
    {
        Log.d("MainActivity", "on click!");
        mEnKey = mEncrypto_key.getText().toString();
        mDeKey = mDecrypto_key.getText().toString();
        if(mEnKey.length() != 8 || mDeKey.length() != 8)
        {
            Toast.makeText(this, "秘钥的长度必须为8个字符", Toast.LENGTH_LONG);
            return;
        }

        Intent intent = new Intent(this, wechat_encrpt.class);
        intent.putExtra("encrypto_key", mEnKey);
        intent.putExtra("decrypto_key", mDeKey);
        startService(intent);
        AppConfig.putString("encrypto_key", mEnKey);
        AppConfig.putString("decrypto_key", mDeKey);
    }

    public void onBtnClose(View view)
    {
        Log.d("MainActivity", "on close click!");
        Intent intent = new Intent();
        intent.setAction("wechat_encrpt");
        intent.putExtra("cmd", wechat_encrpt.CMD_STOP_SERVICE);
        sendBroadcast(intent);
    }


}
