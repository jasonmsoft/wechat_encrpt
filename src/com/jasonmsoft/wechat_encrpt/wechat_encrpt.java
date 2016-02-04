package com.jasonmsoft.wechat_encrpt;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.content.*;
import android.graphics.PixelFormat;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.*;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by cdmaji1 on 2016/2/2.
 */
public class wechat_encrpt extends AccessibilityService implements Handler.Callback {
    private String mTag = wechat_encrpt.class.getSimpleName();
    private String mInputText = "";
    private int    mEffectMsgCount = 0;
    private ArrayList<AffectMsgObj> mEffectMsgList =  new ArrayList<AffectMsgObj>();
    private LinearLayout mFloatLayout;
    private LinearLayout mFloatLayout2;
    private LinearLayout mFloatLayout3;
    private WindowManager.LayoutParams params;
    private WindowManager.LayoutParams params2;
    private WindowManager.LayoutParams params3;
    private WindowManager mWindowMgr;
    private ImageButton mFloatView;
    private ImageButton mFloatView2;
    private TextView mFloatView3;
    private static final int MSG_SHOW_FLOAT_VIEW = 0x01;
    private static final int MSG_HIDE_FLOAT_VIEW = 0x02;
    private static final int MSG_START_ENCRPT = 0x03;
    private static final int MSG_FINISH_ENCRPT = 0x04;
    private static final int MSG_FINISH_DECRPT = 0x05;
    private static final int MSG_TIME_TO_ENCRYPT = 0x06;
    private static final int MSG_TIME_TO_DECRYPT = 0x07;
    private static final int MSG_HIDDEN_DECRYPTO_MSG = 0x08;
    private Thread mWorkThread = null;
    private boolean mIsStopThread = false;
    private ArrayList<Runnable> mJobList = new ArrayList<Runnable>();
    private Lock mJobListLock = new ReentrantLock();
    private Handler mHandle = new Handler(this);
    private String mEncryptoKey = "";
    private String mDecryptoKey = "";
    private DataReceiver mReceiver = null;
    private long mStartTouchTime = 0;
    private long mStopTouchTime = 0;
    private long mStartTouchTime2 = 0;
    private long mStopTouchTime2 = 0;
    private String mEncrypt_result = "";
    private String mDecrypt_result = "";
    private AccessibilityNodeInfo mLastSendMsgNode = null; //输入框所在的节点
    private ClipboardManager mClipMgr = null;
    private Timer mTimerSchedule = new Timer(true);


    public static final int CMD_STOP_SERVICE = 0x11;


    private class DataReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(mTag, "接受要更新的广播数据="+intent.getIntExtra("cmd", -1));

            int cmd = intent.getIntExtra("cmd", -1);
            switch (cmd)
            {
                case CMD_STOP_SERVICE:
                    Log.d(mTag, "stop service myself");
                    stopSelf();
                    break;
                default:
                    break;
            }
        }
    }



    protected String getEventTypeName(int eventType)
    {
        String eventText = "";
        switch(eventType)
        {
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                eventText = "TYPE_VIEW_CLICKED";
                break;
            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                eventText = "TYPE_VIEW_FOCUSED";
                break;
            case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
                eventText = "TYPE_VIEW_LONG_CLICKED";
                break;
            case AccessibilityEvent.TYPE_VIEW_SELECTED:
                eventText = "TYPE_VIEW_SELECTED";
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                eventText = "TYPE_VIEW_TEXT_CHANGED";
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                eventText = "TYPE_WINDOW_STATE_CHANGED";
                break;
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                eventText = "TYPE_NOTIFICATION_STATE_CHANGED";
                break;
            case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_END:
                eventText = "TYPE_TOUCH_EXPLORATION_GESTURE_END";
                break;
            case AccessibilityEvent.TYPE_ANNOUNCEMENT:
                eventText = "TYPE_ANNOUNCEMENT";
                break;
            case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_START:
                eventText = "TYPE_TOUCH_EXPLORATION_GESTURE_START";
                break;
            case AccessibilityEvent.TYPE_VIEW_HOVER_ENTER:
                eventText = "TYPE_VIEW_HOVER_ENTER";
                break;
            case AccessibilityEvent.TYPE_VIEW_HOVER_EXIT:
                eventText = "TYPE_VIEW_HOVER_EXIT";
                break;
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                eventText = "TYPE_VIEW_SCROLLED";
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:
                eventText = "TYPE_VIEW_TEXT_SELECTION_CHANGED";
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                eventText = "TYPE_WINDOW_CONTENT_CHANGED";
                break;
        }
        return eventText;
    }


    protected String getEventInputType(int inputType)
    {
        String inputtype = "";
        switch(inputType)
        {
            case InputType.TYPE_CLASS_DATETIME:
                inputtype = "TYPE_CLASS_DATETIME";
                break;
            case InputType.TYPE_CLASS_TEXT:
                inputtype = "TYPE_CLASS_TEXT";
                break;
            case InputType.TYPE_TEXT_FLAG_MULTI_LINE:
                inputtype = "TYPE_TEXT_FLAG_MULTI_LINE";
                break;
            case InputType.TYPE_CLASS_PHONE:
                inputtype = "TYPE_CLASS_PHONE";
                break;
            case InputType.TYPE_TEXT_VARIATION_NORMAL:
                inputtype = "TYPE_TEXT_VARIATION_NORMAL";
                break;
            case InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_CLASS_TEXT:
                inputtype = "InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_CLASS_TEXT";
                break;
            default:
                inputtype = "unknown";
        }
        return inputtype;
    }


    //显示加密浮动窗口
    public void showFloatView()
    {
        params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.format = PixelFormat.RGBA_8888;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.x = 0;
        params.y = 200;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        LayoutInflater inflater =  (LayoutInflater)LayoutInflater.from(this);
        //获取浮动窗口视图所在布局
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.float_button, null);
        //添加mFloatLayout
        mWindowMgr.addView(mFloatLayout, params);
        //浮动窗口按钮
        mFloatView = (ImageButton) mFloatLayout.findViewById(R.id.float_button);
        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        //设置监听浮动窗口的触摸移动
        mFloatView.setOnTouchListener(new View.OnTouchListener()
        {
            boolean isClick;
            @SuppressLint("ClickableViewAccessibility") @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.d(mTag, "encrypto touch down");
                        mFloatView.setBackgroundResource(R.drawable.float_view_activate);
                        isClick = false;
                        mStartTouchTime = System.currentTimeMillis();
                        addTimer(new TimerTask() {
                            @Override
                            public void run() {
                                Log.d(mTag, "encrypto start");
                                if(mStopTouchTime < mStartTouchTime)
                                    mStopTouchTime = System.currentTimeMillis();
                                if(mStopTouchTime - mStartTouchTime >= 500)
                                {
                                    Log.d(mTag, "encrypto start action");
                                    Message msg = mHandle.obtainMessage(MSG_TIME_TO_ENCRYPT);
                                    mHandle.sendMessage(msg);
                                }
                                else
                                {
                                    Log.d(mTag, "touch interval :"+ (mStopTouchTime - mStartTouchTime));
                                }
                            }
                        }, 500);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mFloatView.setBackgroundResource(R.drawable.float_view_activate);
                        isClick = true;
                        // getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
                        params.x = (int) event.getRawX()
                                - mFloatView.getMeasuredWidth() / 2;
                        // 减25为状态栏的高度
                        params.y = (int) event.getRawY()
                                - mFloatView.getMeasuredHeight() / 2 - 75;
                        // 刷新
                        mWindowMgr.updateViewLayout(mFloatLayout, params);
                        return true;
                    case MotionEvent.ACTION_UP:
                        //按住超过500ms则认为需要进行加密
                        Log.d(mTag, "encrypto touch up");
                        mStopTouchTime = System.currentTimeMillis();
                        mFloatView.setBackgroundResource(R.drawable.float_view_no_activate);
                        return isClick;// 此处返回false则属于移动事件，返回true则释放事件，可以出发点击否。

                    default:
                        break;
                }
                return false;
            }
        });


        mFloatView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(mTag, "click float float view");

            }
        });
    }


    //显示解密浮动窗口
    protected void showFloatView2()
    {
        params2 = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        params2.gravity = Gravity.LEFT | Gravity.TOP;
        params2.type = WindowManager.LayoutParams.TYPE_PHONE;
        params2.format = PixelFormat.RGBA_8888;
        params2.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params2.x = screen_utils.getScreenPixWidth(this) - 60;
        params2.y = 200;
        params2.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params2.height = WindowManager.LayoutParams.WRAP_CONTENT;
        LayoutInflater inflater =  (LayoutInflater)LayoutInflater.from(this);
        //获取浮动窗口视图所在布局
        mFloatLayout2 = (LinearLayout) inflater.inflate(R.layout.float_button2, null);

        //添加mFloatLayout
        mWindowMgr.addView(mFloatLayout2, params2);
        //浮动窗口按钮
        mFloatView2 = (ImageButton) mFloatLayout2.findViewById(R.id.float_button2);
        mFloatLayout2.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        //设置监听浮动窗口的触摸移动
        mFloatView2.setOnTouchListener(new View.OnTouchListener()
        {
            boolean isClick;
            @SuppressLint("ClickableViewAccessibility") @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.d(mTag, "decrypto touch down");
                        mFloatView2.setBackgroundResource(R.drawable.float_view2_activate);
                        isClick = false;
                        mStartTouchTime2 = System.currentTimeMillis();
                        addTimer(new TimerTask() {
                            @Override
                            public void run() {
                                Log.d(mTag, "decrypto start");
                                if(mStopTouchTime2 < mStartTouchTime2)
                                    mStopTouchTime2 = System.currentTimeMillis();
                                if(mStopTouchTime2 - mStartTouchTime2 >= 500)
                                {
                                    Log.d(mTag, "decrypto start action");
                                    Message msg = mHandle.obtainMessage(MSG_TIME_TO_DECRYPT);
                                    mHandle.sendMessage(msg);
                                }
                                else
                                {
                                    Log.d(mTag, "touch interval :"+ (mStopTouchTime2 - mStartTouchTime2));
                                }
                            }
                        }, 500);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mFloatView2.setBackgroundResource(R.drawable.float_view2_decrypto);
                        isClick = true;
                        // getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
                        params2.x = (int) event.getRawX()
                                - mFloatView2.getMeasuredWidth() / 2;
                        // 减25为状态栏的高度
                        params2.y = (int) event.getRawY()
                                - mFloatView2.getMeasuredHeight() / 2 - 75;
                        // 刷新
                        mWindowMgr.updateViewLayout(mFloatLayout2, params2);
                        return true;
                    case MotionEvent.ACTION_UP:
                        //按住超过500ms则认为需要进行加密
                        Log.d(mTag, "decrypto touch up");
                        mStopTouchTime2 = System.currentTimeMillis();
                        return isClick;// 此处返回false则属于移动事件，返回true则释放事件，可以出发点击否。

                    default:
                        break;
                }
                return false;
            }
        });


        mFloatView2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d(mTag, "click float float view");

            }
        });
    }


    protected void loadMsgWindow()
    {
        params3 = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        params3.gravity = Gravity.LEFT | Gravity.TOP;
        params3.type = WindowManager.LayoutParams.TYPE_PHONE;
        params3.format = PixelFormat.RGBA_8888;
        params3.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params3.x = screen_utils.getScreenPixWidth(this)/2 - 200;
        params3.y = screen_utils.getScreenPixHeight(this)/2;
        params3.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params3.height = WindowManager.LayoutParams.WRAP_CONTENT;
        LayoutInflater inflater =  (LayoutInflater)LayoutInflater.from(this);
        //获取浮动窗口视图所在布局
        mFloatLayout3 = (LinearLayout) inflater.inflate(R.layout.decrypto_msg_window, null);

        //添加mFloatLayout
        mWindowMgr.addView(mFloatLayout3, params3);
        //浮动窗口按钮
        mFloatView3 = (TextView) mFloatLayout3.findViewById(R.id.decrypto_msg_txt);
        mFloatLayout2.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        mFloatLayout3.setVisibility(View.INVISIBLE);
    }



    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(mTag, "create encrpto service!!!!!!!!!!!!!!!!!!!!!");
        mReceiver = new DataReceiver();
        IntentFilter intentfilter=new IntentFilter();
        intentfilter.addAction("wechat_encrpt");
        registerReceiver(mReceiver, intentfilter);

        mClipMgr = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        mWindowMgr = (WindowManager) getSystemService(this.WINDOW_SERVICE);

        mWorkThread = new Thread(new Runnable() {
            @Override
            public void run() {
                    while(!mIsStopThread)
                    {
                        Runnable job = null;
                        if((job = getJob()) != null)
                        {
                            Log.d(mTag, "get a job to execute");
                            job.run();
                        }
                        else
                        {
                            try
                            {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                Log.d(mTag, "quit from job thread");
            }
        });
        mWorkThread.start();
    }



    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        int eventType = accessibilityEvent.getEventType();
        String eventName = getEventTypeName(eventType);
        String text = "";
        AccessibilityNodeInfo node = accessibilityEvent.getSource();
        if(node == null)
            return;
        if(node.getText() != null)
            text = node.getText().toString();

        String inputtype = "";
        int iInputType = node.getInputType();
        inputtype = getEventInputType(iInputType);

        eventName = eventName + ":" + eventType +"  node class:"+ node.getClassName().toString();
        Log.i(mTag, eventName);
        if((node.getClassName().toString().equals("android.widget.EditText") && ((iInputType & InputType.TYPE_CLASS_TEXT) != 0)))
        {
            mInputText = text;
            mLastSendMsgNode = node;
            boolean isEdit = node.isEditable();
            Log.d(mTag, "Input text is : "+ mInputText + " node:"+ mLastSendMsgNode.getClassName().toString() + " editable:"+ isEdit);
        }


        if(eventType == AccessibilityEvent.TYPE_VIEW_CLICKED)
        {
            if(text != null && text.equals("发送"))
            {
                Bundle args = new Bundle();

                int windowid = node.getWindowId();
                AccessibilityNodeInfo parent = node.getParent();
                String className = node.getClassName().toString();
                String rn = node.getViewIdResourceName();
                if(rn != null)
                {
                    //Log.d(mTag, "resource name:" + rn);
                }

            }
        }

        mEffectMsgList.clear();
        isReceivceContentChangedEvent(accessibilityEvent);

        for(int i =0; i < mEffectMsgList.size(); i++)
        {
            Log.d(mTag, "@@parse msg: "+ mEffectMsgList.get(i).mMsgline +
                    " desc:"+ mEffectMsgList.get(i).mAvatarDesc +
                    " time:" + mEffectMsgList.get(i).mTimeStamp + " role:"+ mEffectMsgList.get(i).mRole);
        }

    }

    protected String getDigitalStr(String hour)
    {
        if(hour.substring(0, 1).equals("0") ||
                hour.substring(0, 1).equals("1") ||
                hour.substring(0, 1).equals("2") ||
                hour.substring(0, 1).equals("3") ||
                hour.substring(0, 1).equals("4") ||
                hour.substring(0, 1).equals("5") ||
                hour.substring(0, 1).equals("6") ||
                hour.substring(0, 1).equals("7") ||
                hour.substring(0, 1).equals("8") ||
                hour.substring(0, 1).equals("9") )
        {
             return hour;
        }
        else
        {
            return hour.substring(1, 2);
        }

    }


    protected  boolean isTimeStamp(String content)
    {

        if(content.contains("早上") || content.contains("中午") || content.contains("下午") || content.contains("晚上"))
        {
            int pos = content.indexOf(":");
            if(pos != -1)
            {
                String hour = getDigitalStr(content.substring(pos - 2, pos));
                String min = content.substring(pos+1, pos + 3);
                try
                {
                    int iHour = Integer.parseInt(hour);
                    int iMin = Integer.parseInt(min);
                    if(iHour <= 23 && iHour >= 0 && iMin >= 0 && iMin <= 59)
                    {
                        return true;
                    }
                }
                catch (NumberFormatException e)
                {
                    return false;
                }
            }
        }
        return false;
    }


    //判断是否是接收区域的内容发生了变化
    protected  boolean isReceivceContentChangedEvent(AccessibilityEvent event)
    {
        boolean isReceivedContentChangeEvent = false;
        AccessibilityNodeInfo event_node = event.getSource(); //event root node android.widget.ListView
        if(event_node == null)
        {
            Log.d(mTag, "event node is null");
            return false;
        }
        String node_name = "";
        int eventType = event.getEventType();

        if(eventType ==  AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED)
        {
            if(event_node != null && event_node.getClassName() != null)
            {
                node_name = event.getSource().getClassName().toString();
                if(node_name.equals("android.widget.ListView"))
                {
                    int event_node_child_count = event_node.getChildCount(); //受影响的滚动消息条数
                    mEffectMsgCount = 0;
                    Log.d(mTag, "show lines:"+ event_node_child_count);
                    for(int i = 0; i < event_node_child_count; i++) //list view child
                    {
                        AccessibilityNodeInfo event_child_node = event_node.getChild(i);
                        if(event_child_node == null)
                            continue;
                        String event_child_class_name = event_child_node.getClassName().toString();
                        if(event_child_node != null && event_child_class_name.equals("android.widget.RelativeLayout"))
                        {// a line
                            mEffectMsgCount++;
                            int child_child_count = event_child_node.getChildCount();
                            int avatar_pos = 0;
                            AffectMsgObj msg = new AffectMsgObj();
                            for(int j = 0; j < child_child_count; j++)
                            {

                                AccessibilityNodeInfo child_child_node = event_child_node.getChild(j);
                                String child_child_class_name = child_child_node.getClassName().toString();
                                Log.d(mTag, "child_child_class_name :" + child_child_class_name);
                                if(child_child_node != null && child_child_class_name.equals("android.widget.TextView")) //msg timestamp
                                {
                                    //timestamp or msg content
                                    String content = child_child_node.getText() == null ?"null":child_child_node.getText().toString();
                                    if(isTimeStamp(content))
                                    {
                                        msg.mTimeStamp = content;
                                    }
                                    else
                                    {
                                        msg.mMsgline = content;
                                    }
                                    avatar_pos++;
                                }
                                else if(child_child_node != null && child_child_class_name.equals("android.widget.ImageView")) //avatar
                                {
                                    //头像
                                    String desc = child_child_node.getContentDescription().toString();
                                    msg.mAvatarDesc = desc;
                                    int msg_content_node_count = child_child_node.getChildCount();
                                    avatar_pos++;
                                    if(avatar_pos == child_child_count)
                                    {
                                        msg.mRole = AffectMsgObj.ROLE_MYSELF;
                                    }
                                    else
                                    {
                                        msg.mRole = AffectMsgObj.ROLE_PEER;
                                    }
                                }
                            }

                            mEffectMsgList.add(msg);
                        }
                    }

                }
            }

        }
        return isReceivedContentChangeEvent;
    }

    @Override
    public void onInterrupt() {
        Log.d(mTag, "onInterrupt");
        removeFloatView();
    }



    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(mTag, "start command  flags:"+flags + " startid: "+startId );

        mEncryptoKey = intent.getStringExtra("encrypto_key");
        mDecryptoKey = intent.getStringExtra("decrypto_key");

        return START_NOT_STICKY;
    }

    protected void addTimer(TimerTask task, long delay)
    {
        mTimerSchedule.schedule(task, delay);
    }

    public void onDestroy() {

        Log.d(mTag, "onDestroy");
        unregisterReceiver(mReceiver);
        Message msg = mHandle.obtainMessage(MSG_HIDE_FLOAT_VIEW);
        mHandle.sendMessage(msg);
        mIsStopThread = true;
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mTimerSchedule.cancel();
    }

    protected void removeFloatView()
    {
        if(mFloatLayout != null)
        {
            //移除悬浮窗口
            mWindowMgr.removeView(mFloatLayout);
            mWindowMgr.removeView(mFloatView2);
        }
    }

    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(mTag, "onServiceConnected");

        Message msg = mHandle.obtainMessage(MSG_SHOW_FLOAT_VIEW);
        mHandle.sendMessage(msg);
    }



    protected boolean onKeyEvent(KeyEvent event)
    {
        Log.d(mTag, "onKeyEvent");
        return true;
    }


    @Override
    public boolean handleMessage(Message message) {
        Log.d(mTag, "handle message : "+ message.what);
        if(message.what == MSG_SHOW_FLOAT_VIEW)
        {
            showFloatView();
            showFloatView2();
            loadMsgWindow();
        }
        else if(message.what == MSG_HIDE_FLOAT_VIEW)
        {
            removeFloatView();
        }
        else if(message.what == MSG_FINISH_ENCRPT)
        {
            //加密完毕
            if(mLastSendMsgNode != null)
            {
                Bundle args = new Bundle();
                args.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_START_INT, 0);
                args.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_END_INT, mInputText.length());
                if(mLastSendMsgNode.performAction(AccessibilityNodeInfo.ACTION_SET_SELECTION, args))
                {
                    if(mEncrypt_result.isEmpty())
                    {
                        showMsg("加密失败!", 2000);
                    }
                    else
                    {
                        mLastSendMsgNode.performAction(AccessibilityNodeInfo.ACTION_CUT);
                        CharSequence oldData = mClipMgr.getPrimaryClip().getItemAt(0).getText();
                        CharSequence cd = mEncrypt_result;
                        ClipData data = ClipData.newPlainText("result", cd);
                        mClipMgr.setPrimaryClip(data);
                        mLastSendMsgNode.performAction(AccessibilityNodeInfo.ACTION_PASTE);
                        mClipMgr.setPrimaryClip(ClipData.newPlainText("data", oldData));
                        mFloatView.setBackgroundResource(R.drawable.float_view_click);
                        Log.d(mTag, "perform encrypto success!!!!!!");
                    }
                }
            }
            else
            {
                Log.d(mTag, "no detect text");
                showMsg("未检测到输入的文本", 2000);
            }
        }
        else if(message.what == MSG_FINISH_DECRPT)
        {
            Log.d(mTag, "finish decrypto :"+ mDecrypt_result);
            if(!mDecrypt_result.isEmpty())
            {
                showMsg(mDecrypt_result, 6000);
            }
            else
            {
                showMsg("解密失败,请检查解密秘钥", 3000);
            }
            mFloatLayout3.setBackgroundResource(R.drawable.float_view2_decrypto);

        }
        else if(message.what == MSG_TIME_TO_DECRYPT)
        {
            Log.d(mTag, "time to decrypto");
            addJob(new Runnable() {
                @Override
                public void run() {
                    try {
                        CharSequence data = mClipMgr.getPrimaryClip().getItemAt(0).getText();
                        String result = encrpto_utils.decryptDES(data.toString(), mDecryptoKey);
                        Log.d(mTag, "decrypto result:[" + result + "] ");
                        mDecrypt_result = result;
                    } catch (Exception e) {
                        mDecrypt_result = "";
                        e.printStackTrace();
                    }
                    Message msg = mHandle.obtainMessage(MSG_FINISH_DECRPT);
                    mHandle.sendMessage(msg);
                }
            });
        }
        else if(message.what == MSG_TIME_TO_ENCRYPT)
        {
            addJob(new Runnable() {
                @Override
                public void run() {
                    try {

                        if(mInputText.isEmpty() || mEncryptoKey.isEmpty())
                        {

                            mEncrypt_result = "";
                        }
                        else {
                            String result = encrpto_utils.encryptDES(mInputText, mEncryptoKey);
                            Log.d(mTag, "encrypto content:[" + mInputText + "] result:" + result);
                            mEncrypt_result = result;
                        }

                    } catch (Exception e) {
                        mEncrypt_result = "";
                        e.printStackTrace();
                    }
                    Message msg = mHandle.obtainMessage(MSG_FINISH_ENCRPT);
                    mHandle.sendMessage(msg);
                }
            });
        }
        else if(message.what == MSG_HIDDEN_DECRYPTO_MSG)
        {
            hiddenDecryptoMsg();
        }
        else
        {
            Log.d(mTag, "last send msg node is null");
        }
        return false;
    }

    protected void showDecryptoMsg(String msg)
    {
        mFloatView3.setText(msg);
        mFloatLayout3.setVisibility(View.VISIBLE);
    }

    protected void showMsg(String msg, long delay)
    {
        showDecryptoMsg(msg);
        addTimer(new TimerTask() {
            @Override
            public void run() {
                Message msg = mHandle.obtainMessage(MSG_HIDDEN_DECRYPTO_MSG);
                mHandle.sendMessage(msg);
            }
        }, delay);
    }

    protected void hiddenDecryptoMsg()
    {
        mFloatLayout3.setVisibility(View.INVISIBLE);
    }



    protected void addJob(Runnable job)
    {
        mJobListLock.lock();
        Log.d(mTag, "add a job");
        mJobList.add(job);
        mJobListLock.unlock();
    }

    protected Runnable getJob()
    {
        Runnable job = null;
        mJobListLock.lock();
        if(mJobList.size() > 0)
        {
            Log.d(mTag, "get a job");
            job = mJobList.get(0);
            mJobList.remove(job);
        }
        mJobListLock.unlock();
        return job;
    }

}
