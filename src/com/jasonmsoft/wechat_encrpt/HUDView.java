package com.jasonmsoft.wechat_encrpt;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Created by cdmaji1 on 2016/2/3.
 */
public class HUDView extends ViewGroup {

        private Paint mLoadPaint;

        public HUDView(Context context) {
            super(context);
            Toast.makeText(getContext(), "HUDView", Toast.LENGTH_LONG).show();

            mLoadPaint = new Paint();
            mLoadPaint.setAntiAlias(true);
            mLoadPaint.setTextSize(10);
            mLoadPaint.setARGB(255, 255, 0, 0);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawText("Hello World", 5, 15, mLoadPaint);
        }

        @Override
        protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            //return super.onTouchEvent(event);
            Toast.makeText(getContext(),"onTouchEvent", Toast.LENGTH_LONG).show();
            return true;
        }

}
