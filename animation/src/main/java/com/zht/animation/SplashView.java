package com.zht.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

public class SplashView extends View {
    public SplashView(Context context) {
        super(context);
        init(context);
    }

    public SplashView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SplashView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private int[] mCircleColors;//源泉颜色的绘制
    private Paint mPaint = new Paint();
    private Paint mPaintBackground = new Paint();
    // 整体的背景颜色
    private int mSplashBgColor = Color.WHITE;
    private ValueAnimator mAnimator;
    // 大圆和小圆旋转的时间
    private long mRotationDuration = 1200; //ms
    //当前大圆旋转角度(弧度)
    private float mCurrentRotationAngle = 0F;

    SplashState mState = null;

    private float mRotationRadius = 90;
    private float mCurrentRotationRadius = mRotationRadius;

    private void init(Context context) {
        //执行前期的初始化操作
        mCircleColors = context.getResources().getIntArray(R.array.splash_circle_colors);
        mPaint.setAntiAlias(true);

        mPaintBackground.setAntiAlias(true);
        mPaintBackground.setStyle(Paint.Style.STROKE);
        mPaintBackground.setColor(mSplashBgColor);
    }

    // 屏幕正中心点坐标
    private float mCenterX;
    private float mCenterY;
    //屏幕对角线一半
    private float mDiagonalDist;
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = w/2;
        mCenterY = h/2;
        mDiagonalDist = (float) (Math.sqrt(w*w+h*h)/2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mState==null){
            mState = new RotateState();
        }
        mState.drawState(canvas);
    }

    //策略模式
    private abstract class SplashState{
        public abstract void drawState(Canvas canvas);
        public void cancel(){
            mAnimator.cancel();
        }
    }


    private class MergingState extends SplashState{

        public MergingState() {
            mAnimator = ValueAnimator.ofFloat(0,mRotationRadius);
            mAnimator.setDuration(mRotationDuration);
            mAnimator.setInterpolator(new OvershootInterpolator(10f));
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mCurrentRotationRadius = (float)animation.getAnimatedValue();
                    invalidate();
                }
            });
            mAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mState = new ExpandState();
                }
            });
            mAnimator.reverse();
        }

        @Override
        public void drawState(Canvas canvas) {
            drawBackGround(canvas);
            //2.绘制小圆
            drawCircles(canvas);
        }
    }


    private class ExpandState extends SplashState{

        public ExpandState() {
            mAnimator = ValueAnimator.ofFloat(mCircleRadius,mDiagonalDist);
            mAnimator.setDuration(mRotationDuration);
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mHoleRadius = (float) animation.getAnimatedValue();
                    invalidate();
                }
            });
            mAnimator.start();
        }

        @Override
        public void drawState(Canvas canvas) {
            drawBackGround(canvas);
        }
    }

    //旋转动画
    private class RotateState extends SplashState{

        public RotateState() {
            mAnimator = ValueAnimator.ofFloat(0f, (float) (Math.PI*2));
            mAnimator.setInterpolator(new LinearInterpolator());
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mCurrentRotationAngle = (float) animation.getAnimatedValue();
                    postInvalidate();
                }
            });
            mAnimator.setDuration(mRotationDuration);
            mAnimator.setRepeatCount(ValueAnimator.INFINITE);
            mAnimator.start();
        }

        @Override
        public void drawState(Canvas canvas) {
            //画背景，画圆圈
            drawBackGround(canvas);
            drawCircles(canvas);
        }
    }

    // 每一个小圆的半径
    private float mCircleRadius = 18;
    private void drawCircles(Canvas canvas) {
        float rotationAngle = (float) (Math.PI*2/mCircleColors.length);
        for (int i = 0; i < mCircleColors.length; i++) {
            double angle = i*rotationAngle+mCurrentRotationAngle;
            float cx = (float) (mCurrentRotationRadius*Math.cos(angle)+mCenterX);
            float cy = (float) (mCurrentRotationRadius*Math.sin(angle)+mCenterY);
            mPaint.setColor(mCircleColors[i]);
            canvas.drawCircle(cx,cy,mCircleRadius,mPaint);
        }
    }


    //空心圆初始半径
    private float mHoleRadius = 0F;
    private void drawBackGround(Canvas canvas) {
        if(mHoleRadius>0){
            float strokeWidth = mDiagonalDist-mHoleRadius;
            mPaintBackground.setStrokeWidth(strokeWidth);

            float radius = mHoleRadius+strokeWidth/2;
            canvas.drawCircle(mCenterX,mCenterY,radius,mPaintBackground);
        }else{
            canvas.drawColor(mSplashBgColor);
        }
    }

    public void splashDisappear(){
        if(mState!=null&&mState instanceof RotateState){
            RotateState rotateState = (RotateState) mState;
            rotateState.cancel();
            post(new Runnable() {
                @Override
                public void run() {
                   mState = new MergingState();
                }
            });
        }
    }

}
