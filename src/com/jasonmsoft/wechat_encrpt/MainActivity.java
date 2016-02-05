package com.jasonmsoft.wechat_encrpt;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity  implements Handler.Callback {

    private EditText mEncrypto_key;
    private EditText mDecrypto_key;
    private String mEnKey = "";
    private String mDeKey = "";
    private TextView mMsgShow = null;
    private LinearLayout mMsgShowContainer = null;
    private final String mInstructions = "说明:\n设置加密秘钥和解密秘钥,加密秘钥是用于加密你发送的数据," +
            "解密秘钥是用于解密接收的数据.确保你的加密秘钥和对方的解密秘钥一致,否则对方无法解开你的信息,同样,对方也一样.\n" +
            "为了让程序能够正常工作:\na.请确保点击'开启加解密服务' 将wechat_encryption的服务设置为开启." +
            "\nb.'开启浮动窗口'将本应用程序的浮动窗口权限打开(权限管理-->显示浮动窗 设置为开启)." +
            "\nc.关闭本程序的方式：点击关闭按钮，将本程序的服务关闭即可!";

    private TextView mTipMsg = null;
    private String mTag = MainActivity.class.getSimpleName();
    private static final String SERVICE_CMD_MSG = "SERVICE_CMD_MSG";
    private Handler mHandle = new Handler(this);
    private static final int MSG_SHOW_TIP = 1;
    private DataReceiver mReceiver = new DataReceiver();

    @Override
    public boolean handleMessage(Message message) {

        switch (message.what)
        {
            case MSG_SHOW_TIP:
                String data = (String)message.obj;
                Toast.makeText(this, data, Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
        return false;
    }

    protected void sendCmd(int msg, String data)
    {
        Message message = mHandle.obtainMessage(msg);
        message.obj = data;
        mHandle.sendMessage(message);
    }


    private class DataReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(mTag, "接受要更新的广播数据="+intent.getStringExtra("cmd"));

            String cmd = intent.getStringExtra("cmd");
            String msg = intent.getStringExtra("data");
            switch (msg)
            {
                case SERVICE_CMD_MSG:
                    Log.d(mTag, "service msg:"+ msg);
                    sendCmd(MSG_SHOW_TIP, msg);
                    break;
                default:
                    break;
            }
        }
    }



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
        mMsgShow = (TextView)findViewById(R.id.msg_show_txt);
        mMsgShowContainer = (LinearLayout)findViewById(R.id.msg_show);
        mMsgShowContainer.setBackgroundColor(Color.GRAY);
        mMsgShow.setText(mInstructions);
        mTipMsg = (TextView)findViewById(R.id.tip_msg);
        IntentFilter intent = new IntentFilter();
        intent.addAction("MainActivity");
        registerReceiver(mReceiver, intent);

        if(!AppConfig.getFirstRun())
        {
            mEnKey = AppConfig.getString("encrypto_key");
            mDeKey = AppConfig.getString("decrypto_key");
            mEncrypto_key.setText(mEnKey);
            mDecrypto_key.setText(mDeKey);
        }


    }

    protected void onDestroy()
    {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    public void onBtnOk(View view)
    {
        Log.d("MainActivity", "on click!");
        mEnKey = mEncrypto_key.getText().toString();
        mDeKey = mDecrypto_key.getText().toString();
        if(mEnKey.length() != 8 || mDeKey.length() != 8)
        {
            Toast.makeText(this, "秘钥的长度必须为8个字符", Toast.LENGTH_LONG).show();
            mTipMsg.setText("设置秘钥失败!");
            return;
        }

        Intent intent = new Intent(this, wechat_encrpt.class);
        intent.putExtra("encrypto_key", mEnKey);
        intent.putExtra("decrypto_key", mDeKey);
        startService(intent);
        AppConfig.putString("encrypto_key", mEnKey);
        AppConfig.putString("decrypto_key", mDeKey);

        mTipMsg.setText("设置秘钥成功!");
        Toast.makeText(this, "设置秘钥成功", Toast.LENGTH_LONG).show();
    }

    public void onBtnClose(View view)
    {
        Log.d("MainActivity", "on close click!");



        Intent intent2 = new Intent();
        intent2.setAction("wechat_encrpt");
        intent2.putExtra("cmd", wechat_encrpt.CMD_STOP_SERVICE);
        sendBroadcast(intent2);
        finish();
    }

    //onBtnSettings
    public void onBtnSettings(View view)
    {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
    }

    //onBtnFloatWindowSettings
    public void onBtnFloatWindowSettings(View view)
    {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", "com.jasonmsoft.wechat_encrpt", null);
        intent.setData(uri);
        startActivity(intent);
    }




}
