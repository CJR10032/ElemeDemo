package com.konomi.shopcart_lib.widget.cart;

/**
 * 创建者     konomi
 * 创建时间   2018/7/8 13:46
 * 描述	      监听购物车的打开和关闭; 目前只监听了动画的开关, 如果手动的关闭也要监听需要去CartFoodPopupView
 * *          去自己处理
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述
 */
public interface OnCartStateListener {

    /**
     * 购物车完全展开时的回调
     */
    void onCartOpen();

    /**
     * 购物车完全关闭时的回调
     */
    void onCartClose();
}
