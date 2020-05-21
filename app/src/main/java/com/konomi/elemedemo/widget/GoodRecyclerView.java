package com.konomi.elemedemo.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * 创建者     CJR
 * 创建时间   2018-07-13 17:17
 * 描述
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述
 *
 * @author CJR
 */
public class GoodRecyclerView extends RecyclerView {

    public GoodRecyclerView(Context context) {
        super(context);
    }

    public GoodRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GoodRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        //   LogUtils.myLog("GoodRecyclerView的onMeasure方法调用了---");
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        //   LogUtils.myLog("GoodRecyclerView的onMeasure方法调用了~~~");
    }
}
