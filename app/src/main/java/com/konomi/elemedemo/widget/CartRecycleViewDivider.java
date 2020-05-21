package com.konomi.elemedemo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;


/**
 * 创建者     CJR
 * 创建时间   2017-08-02 10:48
 * 描述	     万能分割线, 这个是直接从网站拷贝过来的, 把drawVertical和drawHorizontal的内容替换了, 与官方的保持一致
 * 参考网址   http://blog.csdn.net/pengkv/article/details/50538121
 */
public class CartRecycleViewDivider extends RecyclerView.ItemDecoration {

    private Paint mPaint;

    /**
     * 分割线高度，默认为1px
     */
    private int mDividerHeight = 1;

    /**
     * 分割线到左边的距离, 最后一个条目不显示分割线
     */
    private int mMarginLeft;

    /**
     * 构造方法
     * 默认分割线：高度为2px，颜色为灰色
     *
     * @param context 上下文
     */
    public CartRecycleViewDivider(Context context) {
        init(context);
    }

    /**
     * 自定义分割线
     *
     * @param context
     * @param dividerHeight 分割线高度
     * @param dividerColor  分割线颜色
     */
    public CartRecycleViewDivider(Context context, int dividerHeight, int dividerColor) {
        init(context);
        mDividerHeight = dividerHeight;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(dividerColor);
        mPaint.setStyle(Paint.Style.FILL);
    }

    private void init(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        mMarginLeft = Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 21.82f, displayMetrics));
    }


    /**
     * 获取分割线尺寸
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        /*
        这里最后一个条目, 会有 mDividerHeight 的偏差, 因为这里没法判断...
        本来以为在这里加一个判断, 判断是否最后的View, 不是就偏移, 但是发现, 每次添加条目的时候这个方法
        只调用一次, 这里的view是新加的view, 按照我们的添加方法, 这里就是最后的view了, 所以每次新加的
        view都不会有偏移, 就不会有分割线出来了, 除非整个列表刷新一次
        */
        outRect.set(0, 0, 0, mDividerHeight);
    }

    /**
     * 绘制分割线
     */
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        drawVertical(c, parent);
    }

    /**
     * RecyclerView是vertical方向的, 绘制 item 分割线(一条横线)
     */
    private void drawVertical(Canvas canvas, RecyclerView parent) {
        final int left = parent.getPaddingLeft() + mMarginLeft;
        final int right = parent.getMeasuredWidth() - parent.getPaddingRight();
        final int childSize = parent.getChildCount();
        for (int i = 0; i < childSize - 1; i++) {
            final View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams =
                    (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + layoutParams.bottomMargin;
            final int bottom = top + mDividerHeight;
            if (mPaint != null) {
                canvas.drawRect(left, top, right, bottom, mPaint);
            }
        }
    }
}