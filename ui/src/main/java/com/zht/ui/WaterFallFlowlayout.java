package com.zht.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class WaterFallFlowlayout extends ViewGroup {
    public WaterFallFlowlayout(Context context) {
        super(context);
    }

    public WaterFallFlowlayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WaterFallFlowlayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left,top,right,bottom;
        int curLeft = 0;
        int curTop = 0;

        for (int i = 0; i < lstLineView.size(); i++) {
            List<View> lineViews = lstLineView.get(i);
            for (int j=0;j<lineViews.size();j++) {
                View view = lineViews.get(j);
                MarginLayoutParams layoutParams = (MarginLayoutParams) view.getLayoutParams();
                left = curLeft+layoutParams.leftMargin;
                right = left + view.getMeasuredWidth();
                top = curTop + layoutParams.topMargin;
                bottom = top+view.getMeasuredHeight();
                view.layout(left,top,right,bottom);
                curLeft += view.getMeasuredWidth()+layoutParams.leftMargin+layoutParams.rightMargin;
            }

            curLeft = 0;
            curTop += lstLineheight.get(i);
        }
        lstLineView.clear();
        lstLineheight.clear();
    }

    List<Integer> lstLineheight = new ArrayList<>();
    List<List<View>> lstLineView = new ArrayList<>();

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        lstLineView.clear();
        lstLineheight.clear();


        //获取父容器的宽高模式
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        //获取父容器的宽高尺寸
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heigthSize = MeasureSpec.getSize(heightMeasureSpec);

        //当前控件宽高自己
        int measureWidth = 0;
        int measureHeigth = 0;

        if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
            measureWidth = widthSize;
            measureHeigth = heigthSize;
        } else {
            //当前行高宽
            int iChildWidth = 0;
            int iChildHeigth = 0;

            int iCurWidth = 0;
            int iCurHeigth = 0;

            int childCount = getChildCount();

            List<View> viewList = new ArrayList<>();
            for (int i = 0; i < childCount; i++) {
                //确定两个事情当前行高和当前行宽
                View child = getChildAt(i);
                //先让子控件测量自己
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
                //获取到当前的layoutparams
                MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();
                iChildWidth = child.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin;
                iChildHeigth = child.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin;
                //是否需要换行
                if(iChildWidth+iCurWidth>widthSize){
                    //保存当前的行信息
                    measureWidth = Math.max(measureWidth,iCurWidth);
                    measureHeigth += iCurHeigth;
                    lstLineheight.add(iCurHeigth);
                    lstLineView.add(viewList);


                    //更新的行信息
                    iCurWidth = iChildWidth;
                    iCurHeigth = iChildHeigth;

                    viewList = new ArrayList<>();
                    viewList.add(child);
                }else {
                    iCurWidth+=iChildWidth;
                    iCurHeigth = Math.max(iCurHeigth,iChildHeigth);
                    viewList.add(child);
                }

                //处理最后一行换行的情况
                if(i==childCount-1){
                    measureWidth = Math.max(measureWidth,iCurWidth);
                    measureHeigth += iCurHeigth;

                    lstLineView.add(viewList);
                    lstLineheight.add(iCurHeigth);
                }
            }
        }
        setMeasuredDimension(measureWidth,measureHeigth);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }
}
