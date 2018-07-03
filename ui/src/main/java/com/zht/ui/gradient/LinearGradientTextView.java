package com.zht.ui.gradient;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextPaint;
import android.util.AttributeSet;

public class LinearGradientTextView extends AppCompatTextView {

    private TextPaint mPaint;
    private LinearGradient mLinearGradient;
    private Matrix mMatrix;

    private float mTranslate;
    private float DELTAX = 20;

    int row;
    int curRow = 1;


    public LinearGradientTextView(Context context) {
        super(context);
    }

    public LinearGradientTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LinearGradientTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPaint = getPaint();
        String text = getText().toString();
        float textWidth = mPaint.measureText(text);
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        float height = fontMetrics.bottom - fontMetrics.top;
        float size = getTextSize();
        row = (int) (height / size);

        int gradientSize = (int) (textWidth / text.length() * 3);
        mLinearGradient = new LinearGradient(
                -gradientSize, size * curRow, 0, size * curRow,
                new int[]{0x22ffffff, 0xffffffff, 0x22ffffff},
                null, Shader.TileMode.CLAMP);
        mPaint.setShader(mLinearGradient);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mTranslate += DELTAX;
        float textWidth = getPaint().measureText(getText().toString());
        if(mTranslate>textWidth+1||mTranslate<1){
            DELTAX = 0;
            curRow++;
            if(curRow>row){
                curRow = 0;
            }
        }

        mMatrix = new Matrix();
        mMatrix.setTranslate(mTranslate,0);
        mLinearGradient.setLocalMatrix(mMatrix);
        postInvalidateDelayed(50);
    }
}
