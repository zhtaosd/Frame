package com.zht.pathmeasure;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class LoadingView1 extends View {

    private Path mPath;
    private Paint mPaint;
    private PathMeasure mPathMeasure;
    private float mAnimatorValue;
    private Path mDst;
    private float mLength;

    public LoadingView1(Context context) {
        this(context,null);
    }

    public LoadingView1(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LoadingView1(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPathMeasure = new PathMeasure();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5);

        mPath  = new Path();
        mPath.addCircle(400,400,100,Path.Direction.CCW);
        mPathMeasure.setPath(mPath,true);
        mLength = mPathMeasure.getLength();
        mDst = new Path();

        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0,1);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAnimatorValue = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });

        valueAnimator.setDuration(2000);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mDst.reset();
        mDst.lineTo(0,0);
        float stop = mLength*mAnimatorValue;
        float start = (float) (stop - ((0.5 - Math.abs(mAnimatorValue - 0.5)) * mLength));
        mPathMeasure.getSegment(start, stop, mDst, true);
        canvas.drawPath(mDst, mPaint);
    }
}
