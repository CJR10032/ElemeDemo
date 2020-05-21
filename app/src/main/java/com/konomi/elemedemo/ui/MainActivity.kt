package com.konomi.elemedemo.ui

import android.animation.*
import android.graphics.Color
import android.graphics.Path
import android.graphics.PathMeasure
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSmoothScroller
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.RelativeLayout
import com.konomi.elemedemo.R
import com.konomi.elemedemo.adapter.GoodAdapter
import com.konomi.elemedemo.adapter.NestedCartAdapter
import com.konomi.elemedemo.adapter.TypeAdapter
import com.konomi.elemedemo.base.BaseActivity
import com.konomi.elemedemo.bean.FoodBean
import com.konomi.elemedemo.inter.OnShopButtonClickListener
import com.konomi.elemedemo.utils.DataUtils
import com.konomi.elemedemo.utils.LogUtils
import com.konomi.elemedemo.utils.PriceFormatUtils
import com.konomi.elemedemo.widget.AnimatorTitleDecoration
import com.konomi.elemedemo.widget.CartRecycleViewDivider
import com.konomi.shopcart_lib.widget.cart.OnCartStateListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.view_bottom_cart.*


class MainActivity : BaseActivity() {
    
    private lateinit var typeAdapter: TypeAdapter
    private lateinit var goodAdapter: GoodAdapter
    private lateinit var cartAdapter: NestedCartAdapter
    private lateinit var onGoodRvScrollListener: RecyclerView.OnScrollListener
    private lateinit var cartDataList: ArrayList<FoodBean>
    
    //  购物车的商品数量
    private var cartCount: Int = 0
    
    //  购物车总价
    private var sumPrice: Float = 0f
    
    //  是否需要清空购物车
    private var clearCartList: Boolean = false
    
    /**
     * 用于 Good RecyclerView的滚动 https://www.jianshu.com/p/bae9e516aace
     */
    private lateinit var goodRvSmoothScroller: LinearSmoothScroller
    
    override fun onContentView() {
        setContentView(R.layout.activity_main)
    }
    
    override fun initData() {
        goodRvSmoothScroller = object : LinearSmoothScroller(this@MainActivity) {
            override fun getVerticalSnapPreference(): Int = LinearSmoothScroller.SNAP_TO_START
        }
        
        //  设置类别的RecyclerView
        typeRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        typeAdapter = TypeAdapter(DataUtils.getTypeDatas())
        typeRecyclerView.adapter = typeAdapter
        
        
        goodRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        goodRecyclerView.addItemDecoration(AnimatorTitleDecoration(this@MainActivity))
        goodAdapter = GoodAdapter(DataUtils.getGoodImages(this@MainActivity))
        goodRecyclerView.adapter = goodAdapter
        
        onGoodRvScrollListener = object : RecyclerView.OnScrollListener() {
            
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                
                if (goodRecyclerView.scrollState != RecyclerView.SCROLL_STATE_SETTLING
                    && typeRecyclerView.scrollState != RecyclerView.SCROLL_STATE_SETTLING) {
                    //  不是代码设置的滚动
                    val firstVisiblePosition = (goodRecyclerView.layoutManager as
                            LinearLayoutManager).findFirstVisibleItemPosition()
                    
                    //  设置头部类型
                    typeAdapter.setSelectTypeName(goodAdapter.dataList[firstVisiblePosition].type)
                }
            }
            
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //  静止状态
                    val firstVisiblePosition = (goodRecyclerView.layoutManager as
                            LinearLayoutManager).findFirstVisibleItemPosition()
                    val setTypeResult: Int = typeAdapter.setSelectTypeName(
                            goodAdapter.dataList[firstVisiblePosition].type)
                    
                    if (setTypeResult != -1) {
                        scrollTypeRvAnimate(setTypeResult)
                    }
                }
            }
        }
        
        //  创建购物车的数据容器
        cartDataList = arrayListOf()
        cartAdapter = NestedCartAdapter(cartDataList)
        //  购物车View设置adapter
        cartPopView.setAdapter(cartAdapter)
        //  添加分割线, 这里的分割线不能在item中使用View画, 不然局部更新不好处理
        cartPopView.recyclerView.addItemDecoration(CartRecycleViewDivider(
                this@MainActivity, 1,
                ContextCompat.getColor(this, R.color.dividerColor)))
        
        main_root_rl.post {
            initBazeAnimateValue()
        }
    }
    
    override fun initListener() {
        goodRecyclerView.addOnScrollListener(onGoodRvScrollListener)
        
        typeAdapter.setOnTypeItemClickListener { holder, data, position ->
            //  滚动typeRecyclerView到选中的位置
            scrollTypeRvAnimate(position)
            //  滚动商品到选中的位置
            val goodIndex: Int = goodAdapter.dataList.indexOfFirst {
                it.type == typeAdapter.dataList[position].typeName
            }
            
            if (goodIndex != -1) {
                //  手动调用的代码也会有监听, 怎么都消除不掉
                scrollGoodRvAnimate(goodIndex)
            }
        }
        
        goodAdapter.setOnGoodItemClickListener { holder, data, position ->
            doToast("点击了--${data.title}")
        }
        
        //  商品列表的商品加减按钮的点击事件
        goodAdapter.onShopButtonClickListener = object : OnShopButtonClickListener {
            
            override fun onAddButtonClick(v: View, bean: FoodBean, position: Int, count: Int) {
                val newCartCount = cartCount + count - bean.selectCount
                sumPrice += (count - bean.selectCount) * bean.price
                bean.selectCount = count
                val cartIndex = cartDataList.indexOfFirst { it.id == bean.id }
                if (cartIndex == -1) {
                    cartDataList.add(bean)
                    cartAdapter.notifyItemInserted(cartAdapter.itemCount - 1)
                } else {
                    //  更新购物车的选择 商品数量
                    cartAdapter.notifySelectCountChange(cartIndex)
                }
                //  获取TypeBean相应的index, 修改TypeBean的数量
                val typeIndex =
                        typeAdapter.dataList.indexOfFirst { it.typeName.equals(bean.type) }
                //  当前类型的数量+1
                typeAdapter.dataList[typeIndex].count++
                typeAdapter.notifyItemChanged(typeIndex, typeAdapter.TYPEBEAN_COUNT_CHANGE)
                addCart(v)
                checkCartState(newCartCount)
            }
            
            override fun onDeleteButtonClick(bean: FoodBean, position: Int, count: Int) {
                val newCartCount = cartCount + count - bean.selectCount
                sumPrice += (count - bean.selectCount) * bean.price
                bean.selectCount = count
                if (count == 0) {
                    //  移除购物车
                    val removeIndex = cartDataList.indexOfFirst { bean.id == it.id }
                    if (removeIndex != -1) {
                        cartDataList.removeAt(removeIndex)
                        cartAdapter.notifyItemRemoved(removeIndex)
                    }
                } else {
                    //  不需要移除购物车, 更新购物车的选择 商品数量
                    cartAdapter.notifySelectCountChange()
                }
                //  获取TypeBean相应的index, 修改TypeBean的数量
                val typeIndex =
                        typeAdapter.dataList.indexOfFirst { it.typeName.equals(bean.type) }
                //  当前类型的数量-1
                typeAdapter.dataList[typeIndex].count--
                typeAdapter.notifyItemChanged(typeIndex, typeAdapter.TYPEBEAN_COUNT_CHANGE)
                checkCartState(newCartCount)
            }
        }
        
        //  购物车列表商品的加减按钮点击事件
        cartAdapter.setOnShopButtonClickListener(object : OnShopButtonClickListener {
            override fun onAddButtonClick(v: View, bean: FoodBean, position: Int, count: Int) {
                val newCartCount = cartCount + count - bean.selectCount
                sumPrice += (count - bean.selectCount) * bean.price
                bean.selectCount = count
                //  购物车列表的加减按钮事件会影响商品列表, 但是商品列表的加减按键不需要更新购物车,
                //  因为购物车打开的情况下是没法按到商品列表的控件的
                //  更新商品列表选择的数量
                //  goodAdapter.notifySelectCountChange()
                val goodIndex = goodAdapter.dataList.indexOfFirst { it.id == bean.id }
                if (goodIndex != -1) {
                    goodAdapter.notifySelectCountChange(goodIndex)
                }
                //  获取TypeBean相应的index, 修改TypeBean的数量
                val typeIndex =
                        typeAdapter.dataList.indexOfFirst { it.typeName.equals(bean.type) }
                //  当前类型的数量+1
                typeAdapter.dataList[typeIndex].count++
                typeAdapter.notifyItemChanged(typeIndex, typeAdapter.TYPEBEAN_COUNT_CHANGE)
                checkCartState(newCartCount)
            }
            
            override fun onDeleteButtonClick(bean: FoodBean, position: Int, count: Int) {
                val newCartCount = cartCount + count - bean.selectCount
                sumPrice += (count - bean.selectCount) * bean.price
                bean.selectCount = count
                if (count == 0) {
                    cartDataList.remove(bean)
                    cartAdapter.notifyItemRemoved(position)
                    //  购物车的移除动画
                    cartPopView.removeItem()
                }
                
                //  购物车列表的加减按钮事件会影响商品列表, 但是商品列表的加减按键不需要更新购物车,
                //  因为购物车打开的情况下是没法按到商品列表的控件的
                //  更新商品列表选择的数量
                //  goodAdapter.notifySelectCountChange()
                val goodIndex = goodAdapter.dataList.indexOfFirst { it.id == bean.id }
                if (goodIndex != -1) {
                    goodAdapter.notifySelectCountChange(goodIndex)
                }
                //  获取TypeBean相应的index, 修改TypeBean的数量
                val typeIndex =
                        typeAdapter.dataList.indexOfFirst { it.typeName.equals(bean.type) }
                //  当前类型的数量-1
                typeAdapter.dataList[typeIndex].count--
                typeAdapter.notifyItemChanged(typeIndex, typeAdapter.TYPEBEAN_COUNT_CHANGE)
                checkCartState(newCartCount)
            }
            
        })
        
        cartIconContainerRl.setOnClickListener {
            //  购物车的点击事件
            if (cartAdapter.itemCount > 0) {
                showCartView()
            }
        }
        
        cartSubmitTv.setOnClickListener {
            if (cartCount > 0) {
                doToast("跳转结算界面")
            }
        }
        
        cartPopView.setOnClearClicklistener {
            if (cartAdapter.itemCount > 0 && cartPopView.isShowComplete
                && cartPopView.recyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                doToast("需要二次确认么? 这里目前直接清了")
                clearCartList = true
                cartPopView.cancel()
            }
        }
        
        cartPopView.setOnCartStateListener(object : OnCartStateListener {
            
            override fun onCartOpen() {
            }
            
            override fun onCartClose() {
                LogUtils.myLog("onCartClose方法调用了")
                if (clearCartList) {
                    clearCartList = false
                    cartAdapter.dataList.forEach { it.selectCount = 0 }
                    typeAdapter.dataList.forEach { it.count = 0 }
                    val cartCount = cartAdapter.itemCount
                    cartAdapter.dataList.clear()
                    cartAdapter.notifyItemRangeRemoved(0, cartCount)
                    typeAdapter.notifyItemRangeChanged(0, typeAdapter.itemCount)
                    goodAdapter.notifySelectCountChange()
                    sumPrice = 0f
                    checkCartState(0)
                }
            }
            
        })
    }
    
    /**
     *  显示购物车
     */
    private fun showCartView() {
        if (cartPopView.isShow) {
            cartPopView.cancel()
        } else {
            cartPopView.show()
        }
    }
    
    //  动画滚动RecyclerView到指定位置
    private fun scrollGoodRvAnimate(position: Int) {
        goodRvSmoothScroller.targetPosition = position
        goodRecyclerView.layoutManager?.startSmoothScroll(goodRvSmoothScroller)
    }
    
    private fun scrollTypeRvAnimate(position: Int) {
        //  这里不一定要置顶, 只要能看见就行了
        typeRecyclerView.smoothScrollToPosition(position)
    }
    
    override fun onBackPressed() {
        //  如果购物车开着的情况, 先关闭购物车
        if (cartPopView.isShow) {
            cartPopView.cancel()
        } else {
            super.onBackPressed()
        }
    }
    
    private fun checkCartState(count: Int) {
        if (count == 0) {
            //  数量为0时
            //  隐藏购物车数字
            cartCountTv.visibility = View.INVISIBLE
            cartIconIv.setImageResource(R.drawable.car_empty)
            cartSubmitTv.text = "无订单"
            cartSubmitTv.setTextColor(Color.parseColor("#ffa8a8a8"))
            cartSubmitTv.setBackgroundColor(
                    ContextCompat.getColor(this, R.color.order_submit_empty_bg))
            cartPriceTv.visibility = View.INVISIBLE
        } else {
            if (cartCount == 0) {
                //  从0变到1的过程
                //  数量不为0时
                cartCountTv.visibility = View.VISIBLE
                cartIconIv.setImageResource(R.drawable.car_full)
                cartSubmitTv.text = "去结算"
                cartSubmitTv.setTextColor(Color.WHITE)
                cartSubmitTv.setBackgroundColor(
                        ContextCompat.getColor(this, R.color.order_submit_full_bg))
            }
            //  从1到后面数字的过程只需要改这些就够了
            //  显示购物车数字
            cartCountTv.text = "$count"
            cartPriceTv.text = "￥${PriceFormatUtils.format(sumPrice)}"
            cartPriceTv.visibility = View.VISIBLE
        }
        cartCount = count
    }
    
    /**
     * ★★★★★把商品添加到购物车的动画效果★★★★★
     *
     * @param iv
     */
    private fun addCart(iv: View) {
        //  一、创造出执行动画的主题---imageview
        //  代码new一个imageview，图片资源是上面的imageview的图片
        //  (这个图片就是执行动画的图片，从开始位置出发，经过一个抛物线（贝塞尔曲线），移动到购物车里)
        val goods = ImageView(this)
        goods.setImageResource(R.drawable.shape_buy_ball)
        
        mRlrootView.addView(goods, animateBallParams)
        
        iv.getLocationInWindow(startLoc)
        
        //   三、正式开始计算动画开始/结束的坐标
        //   开始掉落的商品的起始点：商品起始点-父布局起始点+该商品图片的一半
        val startX = (startLoc[0] - rootLocation[0]).toFloat()
        val startY = (startLoc[1] - rootLocation[1]).toFloat()
        
        //  四、计算中间动画的插值坐标（贝塞尔曲线）（其实就是用贝塞尔曲线来完成起终点的过程）
        //  开始绘制贝塞尔曲线
        animatePath.reset()
        //  移动到起始点（贝塞尔曲线的起点）
        animatePath.moveTo(startX, startY)
        //  使用二次萨贝尔曲线：注意第一个起始坐标越大，贝塞尔曲线的横向距离就会越大，一般按照下面的式子取即可
        animatePath.quadTo((startX + endX) / 2, startY, endX.toFloat(), endY.toFloat())
        //  mPathMeasure用来计算贝塞尔曲线的曲线长度和贝塞尔曲线中间插值的坐标，
        // 如果是true，path会形成一个闭环
        val mPathMeasure = PathMeasure(animatePath, false)
        
        //  ★★★属性动画实现（从0到贝塞尔曲线的长度之间进行插值计算，获取中间过程的距离值）
        val bazeValueAnimator = ValueAnimator.ofFloat(0f, mPathMeasure.length)
        //  设置动画时间
        bazeValueAnimator.duration = 500
        //  匀速线性插值器
        bazeValueAnimator.interpolator = interpolator
        bazeValueAnimator.addUpdateListener { animation ->
            //  当插值计算进行时，获取中间的每个值，
            //  这里这个值是中间过程中的曲线长度（下面根据这个值来得出中间点的坐标值）
            val value = animation.animatedValue as Float
            //  ★★★★★获取当前点坐标封装到mCurrentPosition
            //  boolean getPosTan(float distance, float[] pos, float[] tan) ：
            //  传入一个距离distance(0<=distance<=getLength())，然后会计算当前距
            //  离的坐标点和切线，pos会自动填充上坐标，这个方法很重要。
            //  mCurrentPosition此时就是中间距离点的坐标值
            mPathMeasure.getPosTan(value, ballCurPosition, null)
            //  移动的商品图片（动画图片）的坐标设置为该中间点的坐标
            goods.translationX = ballCurPosition[0]
            goods.translationY = ballCurPosition[1]
        }
        //      六、动画结束后的处理
        bazeValueAnimator.addListener(object : AnimatorListenerAdapter() {
            
            //当动画结束后：
            override fun onAnimationEnd(animation: Animator) {
                //   ViewPropertyAnimator.animate(mIvCarIcon).scaleX(1.2f).scaleY(1.2f).setDuration(200);
                ObjectAnimator.ofPropertyValuesHolder(
                        cartIconContainerRl,
                        PropertyValuesHolder.ofFloat("scaleX", 1.1f, 1.2f, 1.1f, 1.0f),
                        PropertyValuesHolder.ofFloat("scaleY", 1.1f, 1.2f, 1.1f, 1.0f)
                ).start()
                // 把移动的图片imageview从父布局里移除zg
                mRlrootView.removeView(goods)
            }
        })
        
        //  五、 开始执行动画
        bazeValueAnimator.start()
    }
    
    private lateinit var mRlrootView: RelativeLayout
    private lateinit var animateBallParams: RelativeLayout.LayoutParams
    private lateinit var rootLocation: IntArray
    private lateinit var startLoc: IntArray
    private lateinit var endLoc: IntArray
    private var endX: Int = 0
    private var endY: Int = 0
    private lateinit var animatePath: Path
    //    private lateinit var bazeValueAnimator: ValueAnimator
    private lateinit var ballCurPosition: FloatArray
    private lateinit var interpolator: LinearInterpolator
    
    //  添加购物车时的贝赛尔曲线初始化操作
    private fun initBazeAnimateValue() {
        mRlrootView = main_root_rl
        val ballSize = Math.round(resources.getDimension(R.dimen.ball_size))
        animateBallParams = RelativeLayout.LayoutParams(ballSize, ballSize)
        
        //  二、计算动画开始/结束点的坐标的准备工作 得到父布局的起始点坐标
        // （用于辅助计算动画开始/结束时的点的坐标）
        rootLocation = IntArray(2)
        mRlrootView.getLocationInWindow(rootLocation)
        
        //  得到商品图片的坐标（用于计算动画开始的坐标）
        startLoc = IntArray(2)
        //  得到购物车图片的坐标(用于计算动画结束后的坐标)
        endLoc = IntArray(2)
        cartIconIv.getLocationInWindow(endLoc)
        
        //商品掉落后的终点坐标：购物车起始点-父布局起始点+购物车图片的1/5
        endX = endLoc[0] - rootLocation[0] + cartIconIv.width * 2 / 5
        endY = endLoc[1] - rootLocation[1]
        ballCurPosition = FloatArray(2)
        
        interpolator = LinearInterpolator()
        animatePath = Path()
    }
}
