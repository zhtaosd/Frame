package com.zht.ui.revealview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class GallaryHorizontalScrollView extends HorizontalScrollView implements View.OnTouchListener {
    private LinearLayout container;
    private int centerX;
    private int icon_width;

    public GallaryHorizontalScrollView(Context context) {
        super(context);
        init();
    }

    public GallaryHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GallaryHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        container = new LinearLayout(getContext());
        container.setLayoutParams(params);
        setOnTouchListener(this);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        View v = container.getChildAt(0);
        icon_width = v.getWidth();
        centerX = getWidth() / 2;
        centerX = centerX - icon_width / 2;
        container.setPadding(centerX, 0, centerX, 0);//中间变动区域的矩形框
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            reveal();
        }
        return false;
    }

    private void reveal() {
        int scrollX = getScrollX();
        int index_left = scrollX / icon_width;
        int index_right = index_left + 1;
        for (int i=0;i<container.getChildCount();i++){
            if(i==index_left||i==index_right){
                float ratio = 5000f/icon_width;
                ImageView iv_left = (ImageView) container.getChildAt(index_left);
                iv_left.setImageLevel((int)(5000-scrollX%icon_width*ratio));

                if(index_right<container.getChildCount()){
                    ImageView iv_right = (ImageView) container.getChildAt(index_right);
                    iv_right.setImageLevel((int) (10000-scrollX%icon_width*ratio));
                }
            }else{
                ImageView iv = (ImageView) container.getChildAt(i);
                iv.setImageLevel(0);
            }
        }
    }

    public void addImageViews(Drawable[] drawables){
        for (int i=0;i<drawables.length;i++){
            ImageView img = new ImageView(getContext());
            img.setImageDrawable(drawables[i]);
            container.addView(img);
            if(i==0){
                img.setImageLevel(5000);
            }
        }

        addView(container);
    }
}
