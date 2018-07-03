package com.zht.ui.filter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Xfermode;
import android.view.View;

import com.zht.ui.R;

public class AvoidView extends View {
    private Paint mPaint;
    private Bitmap mBitmap;
    public AvoidView(Context context) {
        super(context);
        mPaint = new Paint();
        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.xyjy2);
        setLayerType(View.LAYER_TYPE_SOFTWARE,null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = 500;
        int height = width*mBitmap.getHeight()/mBitmap.getWidth();
        mPaint.setColor(Color.RED);

        int layerID = canvas.saveLayer(0,0,width,height,mPaint,Canvas.ALL_SAVE_FLAG);
        canvas.drawBitmap(mBitmap,null,new Rect(0,0,width,height),mPaint);
//        mPaint.setXfermode(new PorterDuffXfermode(Color.WHITE,100, PorterDuff.Mode.));
    }
}
