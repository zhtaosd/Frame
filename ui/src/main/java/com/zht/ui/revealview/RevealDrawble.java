package com.zht.ui.revealview;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.PixelCopy;

public class RevealDrawble extends Drawable{
    private final Rect mRect = new Rect();
    private Drawable mUnselectedDrawable;
    private Drawable mSelectedDrawble;

    public RevealDrawble(Drawable mUnselectedDrawable, Drawable mSelectedDrawble) {
        this.mUnselectedDrawable = mUnselectedDrawable;
        this.mSelectedDrawble = mSelectedDrawble;

        setLevel(100);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        Rect r = getBounds();
        Rect temp = new Rect();
        //抠图
        Gravity.apply(Gravity.LEFT,r.width()/2,r.height(),r,temp);

        canvas.save();
        canvas.clipRect(temp);
        mUnselectedDrawable.draw(canvas);
        canvas.restore();

        Gravity.apply(Gravity.RIGHT,r.width()/2,r.height(),r,temp);
        canvas.clipRect(temp);
        mSelectedDrawble.draw(canvas);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        mUnselectedDrawable.setBounds(bounds);
        mSelectedDrawble.setBounds(bounds);
    }

    @Override
    public int getIntrinsicWidth() {
        return Math.max(mSelectedDrawble.getIntrinsicWidth(),mUnselectedDrawable.getIntrinsicWidth());
    }

    @Override
    public int getIntrinsicHeight() {
        return Math.max(mSelectedDrawble.getIntrinsicHeight(),mUnselectedDrawable.getIntrinsicHeight());
    }

    @Override
    protected boolean onLevelChange(int level) {
        invalidateSelf();
        return true;
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }
}
