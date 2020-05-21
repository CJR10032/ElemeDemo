package com.konomi.elemedemo.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.konomi.elemedemo.R;
import com.konomi.elemedemo.bean.FoodBean;
import com.konomi.elemedemo.utils.PriceFormatUtils;
import com.konomi.shopcart_lib.widget.cart.AnimShopButton;

import java.util.List;

/**
 * 创建者     CJR
 * 创建时间   2018-06-15 16:51
 * 描述       NestedCartAdapter 的ViewHolder
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述
 *
 * @author CJR
 */
public class CartViewHolder extends RecyclerView.ViewHolder {

    public static final int LAYOUT_RES = R.layout.item_car_popup;

    /**
     * 加减的动画按钮
     */
    public final AnimShopButton mAnimShopButton;

    /**
     * 价格的TextView
     */
    public final TextView mPriceTv;

    /**
     * 底部分割线 分割线用ItemDecoration来做, 不然局部更新的时候分割线的更新不好处理
     */
    //    public final View mSplitView;

    /**
     * 商品名称
     */
    public final TextView mGoodNameTv;

    public CartViewHolder(View itemView) {
        super(itemView);

        mAnimShopButton = itemView.findViewById(R.id.item_cart_popup_add_subsctact_btn);
        mPriceTv = itemView.findViewById(R.id.item_cart_popup_tv_price);
        //   mSplitView = itemView.findViewById(R.id.item_cart_popup_split);
        mGoodNameTv = itemView.findViewById(R.id.item_cart_popup_tv_name);

    }

    public void setData(FoodBean data, int pos, List<FoodBean> dataList) {
        //  设置名称
        mGoodNameTv.setText(data.getTitle());
        //  设置价格
        mPriceTv.setText("¥" + PriceFormatUtils.INSTANCE.format(data.getPrice()));
        //  设置数量
        mAnimShopButton.setCount(data.getSelectCount());
        //  设置分割线是否显示, 最后一个条目不显示分割线
        //  mSplitView.setVisibility(pos == dataList.size() - 1 ? View.INVISIBLE : View.VISIBLE);
    }
}
