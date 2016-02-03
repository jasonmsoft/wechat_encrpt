package com.jasonmsoft.wechat_encrpt;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;

/**
 * Created by cdmaji1 on 2016/2/2.
 */
public class wechat_encrpt extends AccessibilityService {
    private String mTag = wechat_encrpt.class.getSimpleName();
    private String mInputText = "";
    private int    mEffectMsgCount = 0;
    private ArrayList<AffectMsgObj> mEffectMsgList =  new ArrayList<AffectMsgObj>();


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

    @Override
    public void onCreate() {
        super.onCreate();
        if(getServiceInfo() != null)
        {
            Log.d(mTag, "touch explore mode");
            getServiceInfo().flags = AccessibilityServiceInfo.FLAG_REQUEST_TOUCH_EXPLORATION_MODE;
        }
        else
        {
            Log.d(mTag, "can't open touch explore mode");
        }
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
        if((eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) && ((iInputType & InputType.TYPE_CLASS_TEXT) != 0))
        {
            mInputText = text;
        }


        if(eventType == AccessibilityEvent.TYPE_VIEW_CLICKED)
        {
            if(text != null && text.equals("发送"))
            {
                Bundle args = new Bundle();
                args.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_START_INT, 1);
                args.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_SELECTION_END_INT, -1);

                int windowid = node.getWindowId();

                AccessibilityNodeInfo parent = node.getParent();
                String className = node.getClassName().toString();

                //Log.d(mTag, "send Text: "+mInputText + " windowid:"+ windowid + " parent windowid:"+ parent.getWindowId() +
                 //       " "+className);

                //noteInfo.performAction(AccessibilityNodeInfo.ACTION_SET_SELECTION, args);


                //noteInfo.performAction(AccessibilityNodeInfo.ACTION_CUT);
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
                                    String content = child_child_node.getText().toString();
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
    }



    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(mTag, "start command  flags:"+flags + " startid: "+startId );
        return START_NOT_STICKY;
    }

    public void onDestroy() {

        Log.d(mTag, "onDestroy");
    }

    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(mTag, "onServiceConnected");
    }

    protected boolean onGesture(int gestureId)
    {
        super.onGesture(gestureId);
        Log.d(mTag, "onGesture");
        return true;
    }


    protected boolean onKeyEvent(KeyEvent event)
    {
        Log.d(mTag, "onKeyEvent");
        return true;
    }


}
