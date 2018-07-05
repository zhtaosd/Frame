package com.zht.pathmeasure;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.graphics.drawable.Animatable2;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class LoadingView extends View{

    /**
     * 左眼距离左边的距离（控件宽度＊EYE_PERCENT_W），
     * 右眼距离右边的距离（控件宽度＊EYE_PERCENT_W）
     */
    private static final float EYE_PERCENT_W = 0.35F;
    /**
     *眼睛距离top的距离（控件的高度＊EYE_PERCENT_H）
     */
    private static final float EYE_PERCENT_H = 0.38F;
    /**
     * 嘴巴左边跟右边距离top的距离（控件的高度＊MOUCH_PERCENT_H）
     */
    private static final float MOUCH_PERCENT_H = 0.55F;
    /**
     * 嘴巴中间距离top的距离（控件的高度＊MOUCH_PERCENT_H2）
     */
    private static final float MOUCH_PERCENT_H2 = 0.7F;
    /**
     * 嘴巴左边跟右边距离边缘的位置（控件宽度＊MOUCH_PERCENT_W）
     */
    private static final float MOUCH_PERCENT_W = 0.23F;
    /**
     * 眼睛跟嘴巴摆动的区间范围
     */
    private static final float DURATION_AREA = 0.15F;

    private Paint reachedPaint;//用来进行基本图形的绘制
    private Paint unReachedPaint;

    private float mRadius;//眼睛的半径

    private Path mouthPath=new Path();//用来画嘴的路径

    private Path unreachedPath;//用来画外部边框的path
    private Path reachedPath;

    private float mProgress=0.1f;

    private boolean isStart = true;

    private float mMouchH=MOUCH_PERCENT_H;

    private float mMouchH2=MOUCH_PERCENT_H2;

    private float mEyesH=EYE_PERCENT_H;

    Animation ainm = new Animation() {
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            float offset = interpolatedTime*DURATION_AREA;
            mMouchH=MOUCH_PERCENT_H+offset;
            mMouchH2=MOUCH_PERCENT_H2+offset;
            mEyesH=EYE_PERCENT_H+offset;
            postInvalidate();
        }
    };





    private float lineWidth=dp2px(2);

    public LoadingView(Context context) {
        this(context,null);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        reachedPaint = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DITHER_FLAG);
        reachedPaint.setStyle(Paint.Style.STROKE);
        reachedPaint.setStrokeWidth(lineWidth);
        reachedPaint.setColor(Color.WHITE);
        reachedPaint.setStrokeJoin(Paint.Join.ROUND);
        reachedPaint.setStrokeCap(Paint.Cap.ROUND);

        unReachedPaint = new Paint(reachedPaint);
        unReachedPaint.setColor(Color.GRAY);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if(isStart){
            startAnim();
            isStart = false;
        }
        mRadius = getWidth()/7/2;
        if(unreachedPath==null){
            unreachedPath = new Path();
        }
        unreachedPath.addRoundRect(new RectF(lineWidth,lineWidth,w-lineWidth,h-lineWidth),w/6,w/6, Path.Direction.CW);
        if(reachedPath==null){
            reachedPath = new Path();
        }
        reachedPath.addRoundRect(new RectF(lineWidth,lineWidth,w-lineWidth,h-lineWidth),w/6,w/6, Path.Direction.CW);
    }

    private void startAnim() {
        ainm.setDuration(500);
        ainm.setRepeatCount(Animation.INFINITE);
        ainm.setRepeatMode(Animation.REVERSE);
        startAnimation(ainm);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);
        canvas.save();
        drawFace(canvas);
        drawReachedRect(canvas);
        canvas.restore();
    }

    private void drawReachedRect(Canvas canvas) {
        unReachedPaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(unreachedPath,unReachedPaint);

        PathMeasure measure = new PathMeasure(unreachedPath,false);
        float length = measure.getLength();
        float curLength = length*mProgress;
        Path path = new Path();
        measure.getSegment(length*1/3f,curLength*2+length*1/3f,path,true);
        canvas.drawPath(path,reachedPaint);
    }

    private void drawFace(Canvas canvas) {
        unReachedPaint.setStyle(Paint.Style.FILL);
        //画左边的眼睛，画右边的眼睛，画嘴
        canvas.drawCircle(getWidth()*EYE_PERCENT_W,getHeight()*mEyesH,mRadius,unReachedPaint);
        canvas.drawCircle(getWidth()*(1-EYE_PERCENT_W),getHeight()*mEyesH,mRadius,unReachedPaint);

        mouthPath.reset();

        mouthPath.moveTo(getWidth()*MOUCH_PERCENT_W,getHeight()*mMouchH);
        mouthPath.quadTo(getWidth()/2,getHeight()*mMouchH2,getWidth()*(1-MOUCH_PERCENT_W),getHeight()*mMouchH);
        unReachedPaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(mouthPath,unReachedPaint);
    }

    public float dp2px(float dpValue){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dpValue,getResources().getDisplayMetrics());
    }
}
