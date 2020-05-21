package com.konomi.shopcart_lib.widget.cart;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.FloatRange;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.konomi.shopcart_lib.R;


/**
 * 创建者     CJR
 * 创建时间   2018-06-13 17:36
 * 描述       购物车 的自定义控件
 * *         参考 https://www.jianshu.com/p/a9e0a98e4d42
 * *         参考 https://www.jianshu.com/p/d78515acb4c6
 * *         参考 SlidingMenu
 * *
 * *         使用 ViewDragHelper 进行拖拽时发现的问题, 在MotionEvent.ACTION_MOVE的时候, 没法
 * *         改变backgroundColor, 根布局用的merge, 拖动的时候没法改变背景色就无法做出渐变的效果(可以选择
 * *         不用merge加一层, 拖动的时候改变这一层的效果)
 * *         目前: 这里使用属性动画进行操作
 * *
 * *         关于事件冲突, 如果不重写onInterceptTouchEvent方法, 在RecyclerView上的触摸事件是由
 * *         RecyclerView处理的, 当前控件和RecyclerView没法同时拿到焦点, 做不到无缝切换;重写intercept
 * *         方法,将事件拦截,在onTouchEvent统一处理触摸事件  发现清空购物车的点击事件会不灵敏, 要自己对
 * *         点击事件的范围做判断进行处理, 比较麻烦; 在onInterceptTouchEvent方法中, 如果按父类的方法处理,
 * *         这里的action_move 只接收到一次事件, 也没法统一处理; (还有一个问题, 对RecyclerView添加子条目
 * *         触摸监听, 拦截事件时, 会发现子条目的onClick在拦截后第一次点击是不触发click事件的, 这和重写
 * *         onInterceptTouchEvent时遇到了一样的问题, 归类起来就是父控件进行事件拦截之后子控件或者子控件
 * *         中的元素的点击监听监听不到之后触发的第一次点击事件??)
 * *         目前: 对RecyclerView进行onTouchListener, 在里面处理
 * *
 * *         购物车条目删除后的动画, 这里不进行RecyclerView高度的动态设置, RecyclerView的高度一开始就固定,
 * *         如果进行了条目的删减, 相应的把购物车的打开高度减小, 并向下进行transY就可以做出动画效果
 * *
 * *         传统的事件冲突解决方案没法解决滑动RecyclerView的时候关闭购物车的问题, 给RecyclerView设置
 * *         onTouch监听这个方案会造成子条目中控件的View的点击不灵敏; 才想起来, 这就类似下拉刷新的效果, 当
 * *         列表控件到顶部还继续下拉的时候, 出现下拉下载更多;
 * *         要解决这个问题, 有两个方案:
 * *         方案一: 是重写 dispatchTouchEvent方法和onInterceptTouchEvent方法,
 * *         当事件从子控件过度到父控件去控制时, 将事件复制, 分发cancel的event, 然后分发down的event; 该方案
 * *         可参考SpringView (在过度的时候有点不自然, 因为设置的系数是0.5), 这个下拉刷新的控件就是这么做的
 * *         方案二: 使用 NestedScrollingParent , 可参考 官方的SwipeRefreshLayout
 * *         https://blog.csdn.net/lmj623565791/article/details/52204039 方案一二都有提及, 方案一的git
 * *         没看出不自然
 * *         http://www.cnblogs.com/anni-qianqian/p/6761151.html
 * *         https://blog.csdn.net/al4fun/article/details/53888990
 * *         https://www.jianshu.com/p/9c59b5c81737
 * *         https://blog.csdn.net/tellh/article/details/50782653
 * *         https://www.jianshu.com/p/0b39e255b2fc (NestedScroll的事件传递)
 * *
 * *         使用NestedScrollingParent解决嵌套的滑动冲突; NestedScrollingParent用法
 * *         看NestedScrollParentDemo
 * *
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述
 *
 * @author CJR
 */
public class CartFoodPopupView extends ViewGroup implements NestedScrollingParent,
        NestedScrollingChild {

    private static final float DRAG_RATE = 0.8f;

    private View         mTopContainer;
    private View         mBottomContainer;
    private RecyclerView mRecyclerView;

    private int mBackgroundColor;

    private float mLastTouchY;

    /**
     * 开关购物车的动画时长, 这个时间是getHeight()的运动时间, 具体的时间要按比例动态计算
     */
    private int mAnimaDuration;

    /**
     * 动画的标识, start的时候++, end的时候-- 不等于0的时候说明在进行动画
     */
    private int mAnimateFlag;

    /**
     * 购物车展开时的高度(最大高度)
     */
    private int mMaxOpenHeight;

    /**
     * 购物车关闭时的高度, 默认为0
     */
    private int mCloseHeight;

    /**
     * 在onTouch的action_down做判断, 如果在down的时候正在进行动画, 则不处理拖动
     */
    private boolean mNeedPerformDrag;

    /**
     * 用于执行购物车的开关动画
     */
    private ValueAnimator mValueAnimator;

    /**
     * 条目移除的动画(这里是购物车往下移动的动画, 这个动画不包含背景色的渐变)
     */
    private ValueAnimator mRemoveItemAnimator;

    /**
     * 抄 SwipeRefreshLayout 的, 功能就是onNestedScrollAccepted的时候记录 axes;
     * getNestedScrollAxes的时候给出axes; onStopNestedScroll 的时候将 axes 置为 0
     */
    private NestedScrollingParentHelper mNestedScrollingParentHelper;
    private NestedScrollingChildHelper  mNestedScrollingChildHelper;

    private OnCartStateListener mOnCartStateListener;

    public CartFoodPopupView(Context context) {
        this(context, null);
    }

    public CartFoodPopupView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public CartFoodPopupView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //  这里需要注意, 如果 R.layout.view_cart_food_popup 布局中根布局不是使用的merge, getChildCount
        //  获得的值是1, 最外面会包一层, 这时候直接调用 mRecyclerView mTopContainer 的layout方法是
        //  不会有东西显示的

        //  顶部栏的高度
        int topHeight = mTopContainer.getMeasuredHeight();
        //  底部栏的高度
        int bottomHeight = mBottomContainer.getMeasuredHeight();
        //  RecyclerView 的高度
        int recyclerViewHeight = mRecyclerView.getMeasuredHeight();

        //  顶部栏的top和bottom
        int topContainerTop = getHeight() - mCloseHeight;
        int topContainerBottom = topContainerTop + topHeight;

        //  RecyclerView的top和bottom
        int rvTop = topContainerBottom;
        int rvBottom = rvTop + recyclerViewHeight;

        int bottomTop = rvBottom;
        int bottomBottom = bottomTop + bottomHeight;

        //  将布局隐藏
        mTopContainer.layout(l, topContainerTop, r, topContainerBottom);
        mRecyclerView.layout(l, rvTop, r, rvBottom);
        mBottomContainer.layout(l, bottomTop, r, bottomBottom);
    }

    private void init(Context context, AttributeSet attrs) {
        initView(context);
        initData(context, attrs);
        initListener();
    }

    private void initView(Context context) {
        inflate(context, R.layout.view_cart_food_popup, this);

        mTopContainer = findViewById(R.id.cart_top_container_rl);
        mBottomContainer = findViewById(R.id.cart_bottom_container_rl);
        mRecyclerView = (RecyclerView) findViewById(R.id.cart_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    private void initData(Context context, AttributeSet attrs) {
        //  关闭RecyclerVieww的动画效果
        closeDefaultAnimator();
        //  mRecyclerView.setItemAnimator(new FadeItemAnimator());

        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);

        int maxOpenHeight = 0;
        //  购物车最大显示的高度
        if (attrs != null) {
            @SuppressLint("CustomViewStyleable") TypedArray ta =
                    context.obtainStyledAttributes(attrs, R.styleable.CartFoodPopupView_DragHelper);
            //  获取购物车显示的高度
            maxOpenHeight = (int) ta.getDimension(R.styleable
                    .CartFoodPopupView_DragHelper_cart_pop_open_height, 0);
            //  获取购物车关闭时的高度
            mCloseHeight = (int) ta.getDimension(R.styleable
                    .CartFoodPopupView_DragHelper_cart_pop_close_height, 0);
            //  获取动画时长
            mAnimaDuration = ta.getInt(R.styleable
                    .CartFoodPopupView_DragHelper_cart_animate_time, 450);
            ta.recycle();
        } else {
            mAnimaDuration = 450;
        }

        Drawable background = getBackground();
        if (background != null && background instanceof ColorDrawable) {
            mBackgroundColor = ((ColorDrawable) background).getColor();
        } else {
            //  给定默认值
            mBackgroundColor = 0x80000000;
        }
        //  初始化透明
        setBackgroundColor(Color.TRANSPARENT);

        mValueAnimator = new ValueAnimator();
        mRemoveItemAnimator = new ValueAnimator();
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //  获取百分比值
                float fraction = (float) animation.getAnimatedValue();
                //  根据fraction 计算背景色和移动布局
                computeAnimateByFraction(fraction);
            }
        });
        mRemoveItemAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //  获取百分比值
                float fraction = (float) animation.getAnimatedValue();
                //  购物车打开后的最终高度
                int openHeight = getCartPopOpenHeight();
                int transY = -Math.round(openHeight * fraction);
                if (mTopContainer.getTranslationY() != transY) {
                    //  移动购物车
                    mTopContainer.setTranslationY(transY);
                    mRecyclerView.setTranslationY(transY);
                }
            }
        });

        AnimatorListenerAdapter animatorListener = new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                mAnimateFlag++;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimateFlag--;
                if (mOnCartStateListener != null) {
                    if (mTopContainer.getTranslationY() == getCartPopCloseHeight()) {
                        mOnCartStateListener.onCartClose();
                    } else if (mTopContainer.getTranslationY() == getCartPopOpenHeight()) {
                        mOnCartStateListener.onCartOpen();
                    }
                }
            }
        };
        mValueAnimator.addListener(animatorListener);
        mRemoveItemAnimator.addListener(animatorListener);


        if (maxOpenHeight == 0) {
            //  如果没有设置展开高度, 默认用屏幕高度的60%
            //  获取屏幕高度
            int screenHeight = getContext().getResources().getDisplayMetrics().heightPixels;
            mMaxOpenHeight = Math.round(screenHeight * 0.6f);
        } else {
            mMaxOpenHeight = maxOpenHeight;
        }
        post(new Runnable() {

            @Override
            public void run() {
                //  设置购物车最大高度
                LayoutParams layoutParams = mRecyclerView.getLayoutParams();
                //  设置RecyclerView的高度
                layoutParams.height = mMaxOpenHeight - getCartPopCloseHeight()
                        - mTopContainer.getHeight() - mBottomContainer.getHeight();
            }
        });
    }

    private void initListener() {
        findViewById(R.id.cart_clear_container_rl).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mAnimateFlag != 0) {
            //  正在进行动画中则拦截事件
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 这里的onTouchEvent是对CartFoodPopupView的触摸, 可以用 dy(当前y - 上次y) 与 transY的和来计算
     * 要位移的距离, RecyclerView的onTouch中, 因为不是整个父控件的触摸, 所以不能这么干
     *
     * @param event 事件
     * @return 是否消费事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!isShow()) {
                    //  如果购物车没打开, 不处理触摸事件
                    return false;
                }

                //  在onTouch的action_down做判断, 如果在down的时候正在进行动画, 则不处理拖动
                mNeedPerformDrag = mAnimateFlag == 0;
                //  如果正在进行动画, 就不用管down事件
                if (mNeedPerformDrag && y <
                        getHeight() - getCartPopOpenHeight() - getCartPopCloseHeight()) {
                    //  购物车打开, 并且点击在购物车之外的区域
                    mNeedPerformDrag = false;
                    //  关闭购物车
                    cancel();
                    //  消费事件
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mNeedPerformDrag) {
                    //  需要处理拖动
                    int transY = Math.round(mTopContainer.getTranslationY() + y - mLastTouchY);
                    //  根据transY 计算背景色和移动布局
                    computeAnimateByTransY(transY);
                }
                break;
            case MotionEvent.ACTION_UP:
                //  through out
            case MotionEvent.ACTION_CANCEL:
                //  关于ACTION_CANCEL https://blog.csdn.net/ldwtill/article/details/10162819
                //  购物车打开后的最终高度
                int openHeight = getCartPopOpenHeight();
                //  已经移动的距离
                float topHasTransY = mTopContainer.getTranslationY();
                boolean needAnimate = topHasTransY != -openHeight && topHasTransY != 0;
                if (mNeedPerformDrag && needAnimate) {
                    float dy = topHasTransY + y - mLastTouchY;


                    if (dy <= 0 && dy >= -openHeight) {
                        //  以购物车打开高度的一半作为临界值, 大于这个值就打开购物车, 小于就关闭
                        int threshold = Math.round(getHeight() - openHeight * 0.5f);
                        if (getHeight() + topHasTransY > threshold) {
                            // 不到临界值(Android坐标是从左上角开始的), 关闭购物车
                            cancel();
                        } else {
                            //  超过临界值, 打开购物车
                            show();
                        }
                    }
                }
                break;

            default:
                break;
        }

        mLastTouchY = y;
        return true;
    }

    /**
     * @param transY TopContainer 移动的距离
     */
    private void computeAnimateByTransY(int transY) {
        if (mTopContainer.getTranslationY() != transY) {
            int openHeight = getCartPopOpenHeight();

            //  边界修正
            transY = Math.max(Math.min(0, transY), -openHeight);

            //  计算移动的百分比
            float fraction = Math.abs(transY * 1f / openHeight);
            //  根据百分比计算背景的颜色
            int color = evaluate(fraction, Color.TRANSPARENT, mBackgroundColor);
            setBackgroundColor(color);
            //  移动购物车
            mTopContainer.setTranslationY(transY);
            mRecyclerView.setTranslationY(transY);
            if (openHeight == mMaxOpenHeight) {
                mBottomContainer.setTranslationY(transY);
            } else {
                //  需要另外算bottom的transY
                mBottomContainer.setTranslationY(
                        (getCartPopCloseHeight() - mMaxOpenHeight) * fraction);
            }
        }
    }

    /**
     * @param fraction TopContainer 移动的比例
     */
    private void computeAnimateByFraction(
            @FloatRange(from = 0.0, to = 1.0) float fraction) {
        //  购物车打开后的最终高度
        int openHeight = getCartPopOpenHeight();
        int transY = -Math.round(openHeight * fraction);
        if (mTopContainer.getTranslationY() != transY) {
            //  根据百分比计算背景的颜色
            int color = evaluate(fraction, Color.TRANSPARENT, mBackgroundColor);
            setBackgroundColor(color);

            //  移动购物车
            mTopContainer.setTranslationY(transY);
            mRecyclerView.setTranslationY(transY);
            if (openHeight == mMaxOpenHeight) {
                mBottomContainer.setTranslationY(transY);
            } else {
                //  需要另外算bottom的transY
                mBottomContainer.setTranslationY(
                        (getCartPopCloseHeight() - mMaxOpenHeight) * fraction);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (mValueAnimator != null) {
            mValueAnimator.cancel();
        }
        if (mRemoveItemAnimator != null) {
            mRemoveItemAnimator.cancel();
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 该方法拷贝自 {@link android.animation.ArgbEvaluator#evaluate(float, Object, Object)}
     * <p>
     * This function returns the calculated in-between value for a color
     * given integers that represent the start and end values in the four
     * bytes of the 32-bit int. Each channel is separately linearly interpolated
     * and the resulting calculated values are recombined into the return value.
     *
     * @param fraction   The fraction from the starting to the ending values
     * @param startValue A 32-bit int value representing colors in the
     *                   separate bytes of the parameter
     * @param endValue   A 32-bit int value representing colors in the
     *                   separate bytes of the parameter
     * @return A value that is calculated to be the linearly interpolated
     * result, derived by separating the start and end values into separate
     * color channels and interpolating each one separately, recombining the
     * resulting values in the same way.
     */
    public int evaluate(float fraction, int startValue, int endValue) {
        int startInt = startValue;
        int startA = (startInt >> 24) & 0xff;
        int startR = (startInt >> 16) & 0xff;
        int startG = (startInt >> 8) & 0xff;
        int startB = startInt & 0xff;

        int endInt = endValue;
        int endA = (endInt >> 24) & 0xff;
        int endR = (endInt >> 16) & 0xff;
        int endG = (endInt >> 8) & 0xff;
        int endB = endInt & 0xff;

        return ((startA + (int) (fraction * (endA - startA))) << 24) |
                ((startR + (int) (fraction * (endR - startR))) << 16) |
                ((startG + (int) (fraction * (endG - startG))) << 8) |
                ((startB + (int) (fraction * (endB - startB))));
    }

    /**
     * 参考 SpringView 和 SwipeRefreshLayout
     * <p>
     * https://www.jianshu.com/p/c138055af5d2
     * <p>
     * canScrollVertically(1)的值表示是否能向上滚动，false表示已经滚动到底部
     * canScrollVertically(-1)的值表示是否能向下滚动，false表示已经滚动到顶部
     *
     * @return 在顶部
     */
    public boolean isChildScrollToTop() {
        return !ViewCompat.canScrollVertically(mRecyclerView, -1);
    }

    //======================start NestedScrollingParent的实现方法 ======================

    /**
     * 当子视图调用 startNestedScroll(View, int) 后调用该方法。返回 true 表示响应子视图的滚动。
     * 实现这个方法来声明支持嵌套滚动，如果返回 true，那么这个视图将要配合子视图嵌套滚动。当嵌套滚动结束时
     * 会调用到 onStopNestedScroll(View)。
     *
     * @param child            可滚动的子视图
     * @param target           NestedScrollingParent 的直接可滚动的视图，一般情况就是 child
     * @param nestedScrollAxes 包含 ViewCompat#SCROLL_AXIS_HORIZONTAL,
     *                         ViewCompat#SCROLL_AXIS_VERTICAL 或者两个值都有。
     * @return 返回 true 表示响应子视图的滚动。
     */
    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    /**
     * 如果 onStartNestedScroll 返回 true ，然后走该方法，这个方法里可以做一些初始化。
     */
    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        // Reset the counter of how much leftover scroll needs to be consumed.
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes);
    }

    /**
     * 响应嵌套滚动结束
     * <p>
     * 当一个嵌套滚动结束后（如MotionEvent#ACTION_UP， MotionEvent#ACTION_CANCEL）会调用该方法，
     * 在这里可有做一些收尾工作，比如变量重置
     */
    @Override
    public void onStopNestedScroll(View child) {
        //   if (isShowScrollLog)
        mNestedScrollingParentHelper.onStopNestedScroll(child);

        boolean needAnimate = mTopContainer.getTranslationY() !=
                -getCartPopOpenHeight() && mTopContainer.getTranslationY() != 0;
        if (needAnimate) {
            float hasTransY = mTopContainer.getTranslationY();

            //  购物车打开后的最终高度
            int openHeight = getCartPopOpenHeight();
            if (hasTransY <= 0 && hasTransY >= -openHeight) {
                //  以购物车打开高度的一半作为临界值, 大于这个值就打开购物车, 小于就关闭
                int threshold = Math.round(getHeight() - openHeight * 0.5f);
                if (getHeight() + hasTransY > threshold) {
                    // 不到临界值(Android坐标是从左上角开始的), 关闭购物车
                    cancel();
                } else {
                    //  超过临界值, 打开购物车
                    show();
                }
            }
        }
    }


    /**
     * onStartNestedScroll 返回 true 才会调用此方法。此方法表示子View将滚动事件分发到父
     * View（SwipeRefreshLayout）
     * <p>
     * 这个方法表示子视图正在滚动，并且把滚动距离回调用到该方法，前提是 onStartNestedScroll 返回了 true。
     * <p>Both the consumed and unconsumed portions of the scroll distance are reported to the
     * ViewParent. An implementation may choose to use the consumed portion to match or chase scroll
     * position of multiple child elements, for example. The unconsumed portion may be used to
     * allow continuous dragging of multiple scrolling or draggable elements, such as scrolling
     * a list within a vertical drawer where the drawer begins dragging once the edge of inner
     * scrolling content is reached.</p>
     *
     * @param target       滚动的子视图
     * @param dxConsumed   手指产生的触摸距离中，子视图消耗的x方向的距离
     * @param dyConsumed   手指产生的触摸距离中，子视图消耗的y方向的距离 ，如果 onNestedPreScroll 中
     *                     dy = 20， consumed[0] = 8，那么 dy = 12
     * @param dxUnconsumed 手指产生的触摸距离中，未被子视图消耗的x方向的距离
     * @param dyUnconsumed 手指产生的触摸距离中，未被子视图消耗的y方向的距离
     */
    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed,
                               int dxUnconsumed, int dyUnconsumed) {
    }

    /**
     * 子视图开始滚动前会调用这个方法。这时候父布局（也就是当前的 NestedScrollingParent 的实现类）
     * 可以通过这个方法来配合子视图同时处理滚动事件。
     *
     * @param target   滚动的子视图
     * @param dx       绝对值为手指在x方向滚动的距离，dx<0 表示手指在屏幕向右滚动
     * @param dy       绝对值为手指在y方向滚动的距离，dy<0 表示手指在屏幕向下滚动
     * @param consumed 一个数组，值用来表示父布局消耗了多少距离，未消耗前为[0,0],
     *                 如果父布局想处理滚动事件，就可以在这个方法的实现中为consumed[0]，consumed[1]赋值。
     *                 分别表示x和y方向消耗的距离。如父布局想在竖直方向（y）完全拦截子视图，那么让
     *                 consumed[1] = dy，就把手指产生的触摸事件给拦截了，子视图便响应不到触摸事件了 。
     */
    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        if (isChildScrollToTop()) {
            //  在RecyclerView顶部

            int openHeight = getCartPopOpenHeight();
            //  已经移动的距离
            float topHasTransY = mTopContainer.getTranslationY();
            //  手指向下滑动, 要关闭购物车
            boolean isDownMove = dy < 0 && topHasTransY >= -openHeight;
            //  手指向上滑动, 最终transY = 0, 再往上就是负数了
            boolean needUpMove = dy > 0 && topHasTransY <= 0 && topHasTransY > -openHeight;

            if (isDownMove || needUpMove) {
                int transY = Math.round(topHasTransY - dy * DRAG_RATE);
                computeAnimateByTransY(transY);
                //  父控件消费所有的滑动事件
                if (dy < 0) {
                    dy *= DRAG_RATE;
                }
                consumed[1] = dy;
            }
        }

    }


    /**
     * 子视图fling 时回调，父布局可以选择监听子视图的 fling。
     * true 表示父布局处理 fling，false表示父布局监听子视图的fling
     *
     * @param target    View that initiated the nested scroll
     * @param velocityX Horizontal velocity in pixels per second
     * @param velocityY Vertical velocity in pixels per second
     * @param consumed  true 表示子视图处理了fling
     */
    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return dispatchNestedFling(velocityX, velocityY, consumed);
    }

    /**
     * 手指在屏幕快速滑触发Fling前回调，如果前面 onNestedPreScroll 中父布局消耗了事件，那么这个也会被触发
     * 返回true表示父布局完全处理 fling 事件
     * <p>
     * fling的处理 https://blog.csdn.net/lmj121212/article/details/53046582
     *
     * @param target    滚动的子视图
     * @param velocityX x方向的速度（px/s）
     * @param velocityY y方向的速度
     * @return true if this parent consumed the fling ahead of the target view
     */
    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return dispatchNestedPreFling(velocityX, velocityY);
    }

    /**
     * 返回当前 NestedScrollingParent 的滚动方向，
     *
     * @see ViewCompat#SCROLL_AXIS_HORIZONTAL
     * @see ViewCompat#SCROLL_AXIS_VERTICAL
     * @see ViewCompat#SCROLL_AXIS_NONE
     */
    @Override
    public int getNestedScrollAxes() {
        //   return ViewCompat.SCROLL_AXIS_VERTICAL;
        return mNestedScrollingParentHelper.getNestedScrollAxes();
    }
    //======================end NestedScrollingParent的实现方法 ======================

    //======================start NestedScrollingChildren的实现方法 ======================
    //  参考  https://blog.csdn.net/al4fun/article/details/53888990
    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mNestedScrollingChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mNestedScrollingChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return mNestedScrollingChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        mNestedScrollingChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mNestedScrollingChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int
            dyUnconsumed, int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed,
                dxUnconsumed,
                dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed,
                offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mNestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mNestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }
    //======================end NestedScrollingChildren的实现方法 ======================

    /**
     * 获取RecyclerView展示的高度
     *
     * @return 购物车显示时RecyclerView展示的高度
     */
    private int getRecyclerViewShowHeight() {
        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
        if (adapter == null || adapter.getItemCount() == 0) {
            //  如果没有设置adapter, 或者条目为0, 则不显示购物车
            return 0;
        }
        int recyclerViewHeight = mRecyclerView.getHeight();
        //  这里是把所有条目按高度相等来处理的, 如果高度有差别这里需要修改
        int allItemHeight = mRecyclerView.getChildAt(0).getHeight() * adapter.getItemCount();
        return Math.min(recyclerViewHeight, allItemHeight);
    }

    /**
     * 获取购物车展示时的高度
     *
     * @return 购物车展示时的高度
     */
    private int getCartPopOpenHeight() {
        return mTopContainer.getHeight() + mBottomContainer.getHeight()
                + getRecyclerViewShowHeight() - getCartPopCloseHeight();
    }

    /**
     * 获取购物车关闭时的高度
     *
     * @return 购物车关闭时的高度(默认为0)
     */
    private int getCartPopCloseHeight() {
        return mCloseHeight;
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        mRecyclerView.setAdapter(adapter);
    }

    public void setOnCartStateListener(OnCartStateListener onCartStateListener) {
        mOnCartStateListener = onCartStateListener;
    }

    /**
     * 设置购物车显示的最大高度
     *
     * @param maxOpenHeight 购物车显示的最大高度
     */
    public void setMaxOpenHeight(int maxOpenHeight) {
        if (mMaxOpenHeight != maxOpenHeight) {
            mMaxOpenHeight = maxOpenHeight;
            post(new Runnable() {

                @Override
                public void run() {
                    //  设置购物车最大高度
                    LayoutParams layoutParams = mRecyclerView.getLayoutParams();
                    //  设置RecyclerView的高度
                    layoutParams.height = mMaxOpenHeight
                            - mTopContainer.getHeight() - mBottomContainer.getHeight();
                    requestLayout();
                }
            });
        }
    }

    /**
     * 设置购物车关闭时的高度
     *
     * @param closeHeight 购物车关闭时的高度
     */
    public void setCloseHeight(int closeHeight) {
        if (mCloseHeight != closeHeight) {
            mCloseHeight = closeHeight;

            requestLayout();
        }
    }

    /**
     * 删除条目
     */
    public void removeItem() {
        if (mAnimateFlag != 0) {
            //  进行动画中
            return;
        }

        //  现在应该展示的购物车高度
        int curOpenHeight = getCartPopOpenHeight();
        if (mRecyclerView.getAdapter() == null || mRecyclerView.getAdapter().getItemCount() == 0) {
            mTopContainer.setTranslationY(-curOpenHeight);
            mRecyclerView.setTranslationY(-curOpenHeight);
            //  条目删除后, 没有东西, 关闭购物车
            cancel();
            return;
        }
        int preRecyclerViewH = mRecyclerView.getHeight();

        //  这里是把所有条目按高度相等来处理的, 如果高度有差别这里需要修改, 这里不用判断adapter是否为null
        //  getCartPopOpenHeight() 已经判断了, 如果为null会关闭购物车
        int preAllItemHeight = mRecyclerView.getChildAt(0).getHeight()
                * (mRecyclerView.getAdapter().getItemCount() + 1);
        //  计算之前RecyclerView的高度
        preRecyclerViewH = Math.min(preRecyclerViewH, preAllItemHeight);
        int preOpenHeight = mTopContainer.getHeight() + mBottomContainer.getHeight()
                + preRecyclerViewH - getCartPopCloseHeight();

        if (curOpenHeight == preOpenHeight) {
            //  高度没发生改变, 不需要进行动画
            return;
        }
        //  已经移动的距离
        float hasTransY = Math.abs(mTopContainer.getTranslationY());
        //  fraction不一定是从0开始的
        float start = hasTransY / curOpenHeight;
        //  mAnimaDuration是getHeight的时间，根据比例算出当前所处位置的动画时间
        mRemoveItemAnimator.setFloatValues(start, 1);
        //  查看RecyclerView的 ItemAnimator发现mRemoveDuration = 120
        mRemoveItemAnimator.setDuration(200);
        mRemoveItemAnimator.start();
    }

    public boolean isShowComplete() {
        return mTopContainer.getTranslationY() == -getCartPopOpenHeight();
    }

    public boolean isShow() {
        return mTopContainer.getTranslationY() != 0;
    }

    /**
     * 显示购物车
     */
    public void show() {
        if (mAnimateFlag != 0) {
            //  进行动画中
            return;
        }

        if (mRecyclerView.getAdapter() == null || mRecyclerView.getAdapter().getItemCount() == 0) {
            //  高度为0, 没东西直接结束
            return;
        }
        //  购物车打开后的最终高度
        int openHeight = getCartPopOpenHeight();
        //  已经移动的距离
        float hasTransY = Math.abs(mTopContainer.getTranslationY());
        //  fraction不一定是从0开始的
        float start = hasTransY / openHeight;
        //  mAnimaDuration是getHeight的时间，根据比例算出当前所处位置的动画时间
        int duration = Math.round(mAnimaDuration * Math.abs(openHeight - hasTransY) / getHeight());
        mValueAnimator.setFloatValues(start, 1);
        mValueAnimator.setDuration(duration);
        mValueAnimator.start();
    }

    /**
     * 关闭购物车
     */
    public void cancel() {
        if (mAnimateFlag != 0) {
            //  进行动画中
            return;
        }

        //  购物车打开后的最终高度
        int openHeight = getCartPopOpenHeight();

        //  需要移动的距离(关闭的时候 需要移动的距离等于已经移动的距离)
        float needTrans = Math.abs(mTopContainer.getTranslationY());
        //  fraction不一定是从1开始的
        float start = needTrans / openHeight;
        //  mAnimaDuration是getHeight的时间，根据比例算出当前所处位置的动画时间
        int duration = Math.round(mAnimaDuration * needTrans / getHeight());
        mValueAnimator.setFloatValues(start, 0);
        mValueAnimator.setDuration(duration);
        mValueAnimator.start();
    }

    public void setOnClearClicklistener(View.OnClickListener l) {
        if (l != null) {
            findViewById(R.id.cart_clear_container_rl).setOnClickListener(l);
        }
    }

    /**
     * 关闭默认局部刷新动画
     */
    public void closeDefaultAnimator() {
        //   mRecyclerView.getItemAnimator().setAddDuration(0);
        mRecyclerView.getItemAnimator().setChangeDuration(0);
        mRecyclerView.getItemAnimator().setMoveDuration(200);
        mRecyclerView.getItemAnimator().setRemoveDuration(0);
        /*((SimpleItemAnimator) mRecyclerView.getItemAnimator())
                .setSupportsChangeAnimations(false);*/
    }
}
