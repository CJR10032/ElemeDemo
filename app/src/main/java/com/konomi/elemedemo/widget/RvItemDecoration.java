package com.konomi.elemedemo.widget;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;


/**
 * 创建者     CJR
 * 创建时间   2017-08-02 10:48
 * 描述	     万能分割线, 只保留了竖直方向的处理
 * 参考网址   http://blog.csdn.net/pengkv/article/details/50538121
 */
@SuppressWarnings("unused")
public class RvItemDecoration extends RecyclerView.ItemDecoration {

    /**
     * 绘制分割线的画笔
     */
    private Paint mPaint;
    /**
     * 分割线颜色, 默认为#bdbdbd
     */
    private int mDividerColor = 0xFFBDBDBD;
    /**
     * 分割线高度，默认为1px
     */
    private int mDividerHeight = 1;
    /**
     * 分割线到左边的距离, 默认为0
     */
    private float mMarginLeft;
    /**
     * 分割线到右边的距离, 默认为0
     */
    private float mMarginRight;
    /**
     * 最后一条分割线是否绘制, 默认不绘制
     */
    private boolean mIsDrawLastLine;

    /**
     * 空参构造
     */
    public RvItemDecoration() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mDividerColor);
        mPaint.setStyle(Paint.Style.FILL);
    }


    /**
     * 获取分割线尺寸
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        /*
         * 这里最后一个条目, 会有 mDividerHeight 的偏差, 因为这里没法判断...
         * 本来以为在这里加一个判断, 判断是否最后的View, 不是就偏移, 但是发现, 每次添加条目的时候这个方法
         * 只调用一次, 这里的view是新加的view, 按照我们的添加方法, 这里就是最后的view了, 所以每次新加的
         * view都不会有偏移, 就不会有分割线出来了, 除非整个列表刷新一次
         */
        outRect.set(0, 0, 0, mDividerHeight);
    }

    /**
     * 绘制方法
     */
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        drawVertical(c, parent);
    }

    /**
     * RecyclerView是vertical方向的, 绘制 item 分割线(一条横线)
     *
     * @param canvas  画布
     * @param parent RecyclerView实例
     */
    private void drawVertical(Canvas canvas, RecyclerView parent) {
        if (mPaint == null) {
            return;
        }
        final float left = parent.getPaddingLeft() + mMarginLeft;
        final float right = parent.getMeasuredWidth() - parent.getPaddingRight() - mMarginRight;
        final int childSize = parent.getChildCount();
        for (int i = 0; i < childSize - 1; i++) {
            final View child = parent.getChildAt(i);
            drawLine(canvas, child, left, right);
        }
        if (mIsDrawLastLine) {
            //  绘制最后一条分割线
            final View child = parent.getChildAt(childSize - 1);
            drawLine(canvas, child, left, right);
        }
    }

    /**
     * 绘制分割线的线条
     * @param canvas  画布
     * @param child 要绘制分割线的条目的itemView
     * @param left  左边界
     * @param right 右边界
     */
    private void drawLine(Canvas canvas, View child, float left, float right) {
        RecyclerView.LayoutParams layoutParams =
                (RecyclerView.LayoutParams) child.getLayoutParams();
        final int top = child.getBottom() + layoutParams.bottomMargin;
        final int bottom = top + mDividerHeight;
        canvas.drawRect(left, top, right, bottom, mPaint);
    }

    /**
     * 获取分割线颜色
     * @return 分割线颜色
     */
    public int getDividerColor() {
        return mDividerColor;
    }

    /**
     * 设置分割线颜色
     * @param dividerColor 分割线颜色
     * @return 当前对对象, 形成链式调用
     */
    public RvItemDecoration setDividerColor(int dividerColor) {
        mDividerColor = dividerColor;
        return this;
    }

    /**
     * 获取分割线高度
     * @return 分割线高度
     */
    public int getDividerHeight() {
        return mDividerHeight;
    }

    /**
     * 设置分割线高度
     * @param dividerHeight 分割线高度
     * @return 当前对对象, 形成链式调用
     */
    public RvItemDecoration setDividerHeight(int dividerHeight) {
        mDividerHeight = dividerHeight;
        return this;
    }

    /**
     * 获取分割线到左边的距离
     * @return 分割线到左边的距离
     */
    public float getMarginLeft() {
        return mMarginLeft;
    }

    /**
     * 设置分割线到左边的距离
     * @param marginLeft 分割线到左边的距离
     * @return  当前对对象, 形成链式调用
     */
    public RvItemDecoration setMarginLeft(float marginLeft) {
        mMarginLeft = marginLeft;
        return this;
    }

    /**
     * 获取分割线到右边的距离
     * @return 分割线到右边的距离
     */
    public float getMarginRight() {
        return mMarginRight;
    }

    /**
     * 设置分割线到右边的距离
     * @param marginRight 分割线到右边的距离
     * @return 当前对象, 形成链式调用
     */
    public RvItemDecoration setMarginRight(float marginRight) {
        mMarginRight = marginRight;
        return this;
    }

    /**
     * 获取最后一条分割线是否绘制
     * @return 最后一条分割线是否绘制
     */
    public boolean isDrawLastLine() {
        return mIsDrawLastLine;
    }

    /**
     * 设置最后一条分割线是否绘制
     * @param drawLastLine 最后一条分割线是否绘制
     * @return  当前对对象, 形成链式调用
     */
    public RvItemDecoration setDrawLastLine(boolean drawLastLine) {
        mIsDrawLastLine = drawLastLine;
        return this;
    }
}