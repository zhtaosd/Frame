package com.zht.beizer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.PointFEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

public class QQView extends View {

    //气泡的集中状态
    private final int VIEW_DEFAULT = 0; //默认状态静止

    private final int VIEW_CONNECT = 1;// 相连的状态

    private final int VIEW_APART = 2;//分离的状态

    private final int VIEW_DISMISS = 3;//消失的状态

    private float viewRadius;//半径
    private int viewColor;//颜色
    private String textStr;//气泡的文本
    private float textColor;//气泡文本颜色
    private float textSize;//气泡文本大小

    private float viewStillRadius;//不动气泡的半径
    private float viewMoveRadius;//动的气泡的半径

    private PointF viewStillCenter;//不东气泡的圆心
    private PointF viewMoveCenter;//移动气泡的圆心

    private Paint viewPaint;//气泡的画笔
    private Path bezierPath;//贝塞尔曲线画笔
    private Paint textPaint;

    //文本绘制区域
    private Rect textRect;

    private Paint burstPaint;
    private Rect burstRect;


    //气泡状态标志
    private int bubbleState = VIEW_DEFAULT;
    //两气泡圆心距离
    private float dist;
    //两气泡最大圆心距离
    private float maxDist;
    //气泡爆炸的bitmap数组
    private Bitmap[] burstBitmapsArray;
    //是否在执行气泡爆炸动画
    private boolean isBurstAnimStart = false;
    //当前气泡爆炸图片index
    private int currentIndex;
    //气泡爆炸的图片ID数组
    private int[] mBurstDrawablesArray = {R.drawable.burst_1, R.drawable.burst_2
            , R.drawable.burst_3, R.drawable.burst_4, R.drawable.burst_5};

    public QQView(Context context) {
        this(context, null);
    }

    public QQView(Context context, @Nullable AttributeSet attrs) {
        this(context, null, 0);
    }

    public QQView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DragBubbleView, defStyleAttr, 0);
        viewRadius = array.getDimension(R.styleable.DragBubbleView_bubble_radius, viewRadius);
        viewColor = array.getColor(R.styleable.DragBubbleView_bubble_color, Color.RED);
        textStr = array.getString(R.styleable.DragBubbleView_bubble_text);
        textSize = array.getDimension(R.styleable.DragBubbleView_bubble_textSize, textSize);
        textColor = array.getColor(R.styleable.DragBubbleView_bubble_textColor, Color.WHITE);
        array.recycle();

        viewStillRadius = viewRadius;
        viewMoveRadius = viewStillRadius;
        maxDist = 8 * viewMoveRadius;

        //抗锯齿
        viewPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        viewPaint.setColor(viewColor);
        viewPaint.setTextSize(textSize);
        textRect = new Rect();

        //爆炸画笔
        burstPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        burstPaint.setFilterBitmap(true);
        burstRect = new Rect();
        burstBitmapsArray = new Bitmap[mBurstDrawablesArray.length];

        //将drawable转化为bitmap
        for (int i = 0; i < burstBitmapsArray.length; i++) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), mBurstDrawablesArray[i]);
            burstBitmapsArray[i] = bitmap;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initView(w, h);
    }

    private void initView(int w, int h) {
        //设置两个气泡圆心初始化坐标
        if (viewStillCenter == null) {
            viewStillCenter = new PointF(w / 2, h / 2);
        } else {
            viewStillCenter.set(w / 2, h / 2);
        }

        if (viewMoveCenter == null) {
            viewMoveCenter = new PointF(w / 2, h / 2);
        } else {
            viewMoveCenter.set(w / 2, h / 2);
        }

        bubbleState = VIEW_DEFAULT;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (bubbleState != VIEW_DISMISS) {
            canvas.drawCircle(viewMoveCenter.x, viewMoveCenter.y, viewMoveRadius, viewPaint);
            textPaint.getTextBounds(textStr, 0, textStr.length(), textRect);
            canvas.drawText(textStr, viewMoveCenter.x - textRect.width() / 2, viewMoveCenter.y + textRect.height() / 2, textPaint);
        }

        //画相连的气泡状态
        if (bubbleState == VIEW_CONNECT) {
//            画静止的气泡
            canvas.drawCircle(viewStillCenter.x, viewStillCenter.y, viewStillRadius, viewPaint);
//            画相连的曲线
            //计算两个圆心左边的中点
            int iAnchorX = (int) ((viewStillCenter.x + viewStillCenter.x) / 2);
            int iAnchorY = (int) ((viewStillCenter.y + viewStillCenter.y) / 2);

//            计算三角函数
            float cosTheta = (viewMoveCenter.x - viewStillCenter.x) / dist;
            float sinTheta = (viewMoveCenter.y - viewMoveCenter.y) / dist;

            float iStillStartX = viewStillCenter.x - viewStillRadius * sinTheta;
            float iStillStartY = viewStillCenter.y + viewStillRadius * cosTheta;

            float iStillEndX = viewStillCenter.x + viewStillRadius * sinTheta;
            float iStillEndY = viewStillCenter.y - viewStillRadius * cosTheta;

            float iMoveStartX = viewMoveCenter.x - viewMoveRadius * sinTheta;
            float iMoveStartY = viewMoveCenter.y + viewMoveRadius * cosTheta;

            float iMoveEndX = viewMoveCenter.x + viewMoveRadius * sinTheta;
            float iMoveEndY = viewMoveCenter.y - viewMoveRadius * cosTheta;

            bezierPath.reset();

            //画上半弧线
            bezierPath.moveTo(iStillStartX, iStillStartY);
            bezierPath.quadTo(iAnchorX, iAnchorY, iMoveStartX, iMoveStartY);
            //画下半弧线
            bezierPath.moveTo(iMoveEndX, iMoveEndY);
            bezierPath.quadTo(iAnchorX, iAnchorY, iStillEndX, iStillEndY);

            bezierPath.close();
            canvas.drawPath(bezierPath, burstPaint);
        }

        if (isBurstAnimStart) {
            burstRect.set((int) (viewMoveCenter.x - viewMoveRadius),
                    (int) (viewMoveCenter.y - viewMoveRadius),
                    (int) (viewMoveCenter.x + viewMoveRadius),
                    (int) (viewMoveCenter.y + viewMoveRadius));
            canvas.drawBitmap(burstBitmapsArray[currentIndex], null, burstRect, viewPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (bubbleState != VIEW_DISMISS) {
                    dist = (float) Math.hypot(event.getX() - viewStillCenter.x, event.getY() - viewStillCenter.y);
                    if (dist < viewRadius) {
                        bubbleState = VIEW_CONNECT;
                    } else {
                        bubbleState = VIEW_DEFAULT;
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                viewMoveCenter.x = event.getX();
                viewMoveCenter.y = event.getY();
                dist = (float) Math.hypot(event.getX() - viewStillCenter.x, event.getY() - viewStillCenter.y);
                if (bubbleState == VIEW_CONNECT) {
                    if (dist < maxDist) {
                        viewStillRadius = viewRadius - dist / 8;
                    } else {
                        bubbleState = VIEW_APART;
                    }
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (bubbleState == VIEW_CONNECT) {
                    //弹回的动画
                    startBubbleResetAnim();
                } else if (bubbleState == VIEW_APART) {
                    if (dist < 2 * viewRadius) {
                        //弹回的动画
                        startBubbleResetAnim();
                    } else {
                        //炸裂的动画
                        startBubbleBurstAnim();
                    }
                }
                break;
        }
        return true;
    }

    private void startBubbleResetAnim() {
        ValueAnimator anim = ValueAnimator.ofObject
                (new PointFEvaluator(), new PointF(viewMoveCenter.x, viewMoveCenter.y),
                        new PointF(viewStillCenter.x, viewStillCenter.y));
        anim.setDuration(200);
        anim.setInterpolator(new OvershootInterpolator(5f));
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                viewMoveCenter = (PointF) animation.getAnimatedValue();
                invalidate();
            }
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                bubbleState = VIEW_DEFAULT;
            }
        });
        anim.start();
    }

    private void startBubbleBurstAnim(){
        bubbleState = VIEW_DISMISS;
        isBurstAnimStart = true;
        ValueAnimator anim = ValueAnimator.ofInt(0,burstBitmapsArray.length);
        anim.setInterpolator(new LinearInterpolator());
        anim.setDuration(500);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentIndex = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
               isBurstAnimStart = false;
            }
        });
        anim.start();
    }
}
