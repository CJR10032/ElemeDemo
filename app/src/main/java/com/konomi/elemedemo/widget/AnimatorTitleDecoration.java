package com.konomi.elemedemo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import com.konomi.elemedemo.adapter.GoodAdapter;
import com.konomi.elemedemo.bean.FoodBean;

import java.util.List;

/**
 * 创建者     CJR
 * 创建时间   2017-08-03 11:21
 * 描述	     直接拷贝的TitleItemDecoration的,添加了悬浮层的动画效果
 * *         从RecyclerViewWiki那里拷贝过来的
 * *         https://blog.csdn.net/zxt0601/article/details/52355199
 * </p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述
 */
public class AnimatorTitleDecoration extends RecyclerView.ItemDecoration {

    private Paint mPaint;

    /**
     * title 的高度
     */
    private int mTitleHeight;

    /**
     * title的背景色
     */
    private static final int COLOR_TITLE_BG = 0xfff8f8f8;

    /**
     * title文字的颜色
     */
    private static final int COLOR_TITLE_FONT = 0xff333333;

    /**
     * title文字的大小
     */
    private float mTitleFontSize;

    /**
     * 文字到左边的padding, 上下是居中的就不用
     */
    private float mTextPaddingLeft;

    public AnimatorTitleDecoration(Context context) {
        mPaint = new Paint();
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        mTitleHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30,
                displayMetrics);
        mTitleFontSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, displayMetrics);
        mTextPaddingLeft = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12,
                displayMetrics);
        mPaint.setTextSize(mTitleFontSize);
        //  anti 反对, 这里是设置抗锯齿
        mPaint.setAntiAlias(true);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State
            state) {
        super.getItemOffsets(outRect, view, parent, state);

        int pos = parent.getChildAdapterPosition(view);
        if (pos == 0) {
            //  第一个条目肯定有title
            outRect.set(0, mTitleHeight, 0, 0);
        } else {
            List<FoodBean> dataList = ((GoodAdapter) parent.getAdapter()).getDataList();
            FoodBean cur = dataList.get(pos);
            FoodBean pre = dataList.get(pos - 1);
            if (!TextUtils.isEmpty(cur.getType()) && !cur.getType().equals(pre.getType())) {
                //  首字母不为空, 并且和前一个不一样,说明是新的分类
                outRect.set(0, mTitleHeight, 0, 0);
            } else {
                //                outRect.set(0, 0, 0, 0);      //   这个必要么? 不需要
            }
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);

        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            //  这里也和ListView一样是屏幕+1个childCount?
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            List<FoodBean> dataList = ((GoodAdapter) parent.getAdapter()).getDataList();
            int pos = layoutParams.getViewLayoutPosition();
            FoodBean cur = dataList.get(pos);
            if (pos == 0) {
                //  第一个条目肯定有title
                drawTitleArea(c, cur.getType(), left, right, child, layoutParams, pos);
            } else {
                //  不是第一个条目, 要根据下一个数据是不是新条目做判断
                FoodBean pre = dataList.get(pos - 1);
                if (!TextUtils.isEmpty(cur.getType()) && !cur.getType().equals(pre.getType())) {
                    //  首字母不为空, 并且和前一个不一样,说明是新的分类
                    drawTitleArea(c, cur.getType(), left, right, child, layoutParams, pos);
                }
            }
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        //  这里的firstVisiblePos是第一个可见的位置,这个位置是在mDataList里的位置,
        //  所以用getChildAt(firstVisiblePos)肯定不对的; 这里用getChildAt(0); 当从后面的位置快速滑动到前面
        //  时, 刚好到header的位置, 这时候getChildAt(0)拿到的是上一个header的typeName, 就会出现bug
        //  手动滑没太大问题, 但是代码滑的时候就有bug了, 代码滑动参考(https://www.jianshu.com/p/bae9e516aace)
        LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
        final int firstVisiblePos = layoutManager.findFirstVisibleItemPosition();
        RecyclerView.ViewHolder holder = parent.findViewHolderForLayoutPosition(firstVisiblePos);
        if (holder == null) {
            return;
        }
        final View child = holder.itemView;
        // 这里和getChildAdapterPosition是一模一样的,ViewHolder的那两个方法才有区别
        final List<FoodBean> dataList = ((GoodAdapter) parent.getAdapter()).getDataList();
        final String text = dataList.get(firstVisiblePos).getType();

        //  这里不考虑弹性的情况, 所以不会是最后一个 item

        boolean isTrans = false;
        if (!text.equals(dataList.get(firstVisiblePos + 1).getType()) && child.getBottom() <
                mTitleHeight) {
            isTrans = true;
            //  保存canvas状态
            c.save();

            //一种头部折叠起来的视效，个人觉得也还不错~
            //可与123行 c.drawRect 比较，只有bottom参数不一样，由于 child.getHeight() + child.getTop() <
            // mTitleHeight，所以绘制区域是在不断的减小，有种折叠起来的感觉
            //  CJR ex: 这里其实就是clip了一个长方形区域
            /*c.clipRect(parent.getPaddingLeft(), parent.getPaddingTop(), parent
                    .getRight() - parent.getPaddingRight(), parent.getPaddingTop() + child
                    .getHeight()
                    + child.getTop());*/

            //  顶上去的动画效果
            //  上移为负,所以这里是bot - titleH
            c.translate(0, child.getBottom() - mTitleHeight);
        }

        //  设置title背景色
        mPaint.setColor(COLOR_TITLE_BG);
        c.drawRect(parent.getPaddingLeft(), parent.getPaddingTop(), parent.getWidth() - parent
                .getPaddingRight(), parent.getPaddingTop() + mTitleHeight, mPaint);
        mPaint.setColor(COLOR_TITLE_FONT);
        //  开始画文字的起始坐标, 注意, canvas画文字的时候,坐标是以左下角为0,0点的,
        float x = child.getPaddingLeft() + mTextPaddingLeft;
        //  这里的paddingleft要和itemView的一致
        final float y = parent.getTop() + mTitleHeight * 0.5f - (mPaint.descent() + mPaint.ascent
                ()) * 0.5f;
        c.drawText(text, x, y, mPaint);

        if (isTrans)
            c.restore();
    }

    private void drawTitleArea(Canvas c, String text, int left, int right, View child,
                               RecyclerView.LayoutParams layoutParams, int pos) {
        //  TODO: 这里有个疑问, 万能分割线的写法这里是加的, 参考链接的博客是减的? 先具体看看效果
        final int bottom = child.getTop()/* - layoutParams.topMargin*/;
        //  这里是分类title, 画在item之上的, 所以用getTop +
        final int top = bottom - mTitleHeight;
        // topMargin, 如果是分割线, 就用getBottom + bottomMargin
        //  设置title背景色
        mPaint.setColor(COLOR_TITLE_BG);
        //  画title的背景
        c.drawRect(left, top, right, bottom, mPaint);
        //  设置title文字颜色
        mPaint.setColor(COLOR_TITLE_FONT);
        //  开始画文字的起始坐标, 注意, canvas画文字的时候,坐标是以左下角为0,0点的
        final float x = child.getPaddingLeft() + mTextPaddingLeft;
        final float y = child.getTop() - mTitleHeight * 0.5f - (mPaint.descent() + mPaint.ascent
                ()) * 0.5f;
        c.drawText(text, x, y, mPaint);
    }
}
