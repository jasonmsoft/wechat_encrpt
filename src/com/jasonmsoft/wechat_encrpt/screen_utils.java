package com.jasonmsoft.wechat_encrpt;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by cdmaji1 on 2016/2/4.
 */
public class screen_utils {

    public static int dip2px(Context context, float dipValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5f);
    }
    public static int px2dip(Context context, float pxValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(pxValue / scale + 0.5f);
    }

    public static int getScreenPixWidth(Context context)
    {
        DisplayMetrics dm =context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    public static int getScreenPixHeight(Context context)
    {
        DisplayMetrics dm =context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }
}
