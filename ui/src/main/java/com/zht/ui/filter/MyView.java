package com.zht.ui.filter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.zht.ui.R;

public class MyView extends View {
    private Paint paint = new Paint();
    private Bitmap bitmap;
    public MyView(Context context) {
        super(context);
        bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.xyjy2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setAntiAlias(true);
        paint.setARGB(255,200,100,100);
//        canvas.drawRect(0,0,500,500,paint);
        canvas.drawBitmap(bitmap,null,new Rect(0,0,500,500*bitmap.getHeight()/bitmap.getWidth()),paint);
        canvas.translate(550,0);
        //生成色彩矩阵
        ColorMatrix colorMatrix = new ColorMatrix(new float[]{
                1.2f,0,0,0,0,
                0,1.2f,0,0,0,
                0,0,1.2f,0,0,
                0,0,0,1.2f,0
        });
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(bitmap,null,new Rect(0,0,500,500*bitmap.getHeight()/bitmap.getWidth()),paint);
    }
}
