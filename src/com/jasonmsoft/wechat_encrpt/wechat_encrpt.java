package com.jasonmsoft.wechat_encrpt;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

/**
 * Created by cdmaji1 on 2016/2/2.
 */
public class wechat_encrpt extends AccessibilityService {
    private String mTag = wechat_encrpt.class.getSimpleName();
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        int eventType = accessibilityEvent.getEventType();
        String eventText = "";
        String pkgName = "";
        String text = "";

        AccessibilityNodeInfo noteInfo = accessibilityEvent.getSource();
        if(noteInfo == null)
            return;
        String viewName = noteInfo.getViewIdResourceName();
        if(noteInfo.getPackageName() != null)
            pkgName = noteInfo.getPackageName().toString();
        if(noteInfo.getText() != null)
            text = noteInfo.getText().toString();
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

        eventText = eventText + ":" + eventType;
        Log.i(mTag, eventText);
        Log.d(mTag, "view name :"+ viewName +  " pkgName :" + pkgName + " text:"+text);

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



}
