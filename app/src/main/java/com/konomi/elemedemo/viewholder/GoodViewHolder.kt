package com.konomi.elemedemo.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.konomi.elemedemo.R
import com.konomi.elemedemo.bean.FoodBean
import com.konomi.elemedemo.utils.PriceFormatUtils
import com.konomi.shopcart_lib.widget.cart.AnimShopButton


/**
 * 创建者     CJR
 * 创建时间   2018-07-05 15:08
 * 描述
 *
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述
 *
 * @author CJR
 */
class GoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    
    val goodIconIv = itemView.findViewById(R.id.item_good_iv) as ImageView
    val goodTitleTv = itemView.findViewById(R.id.item_good_title) as TextView
    val goodDetailTv = itemView.findViewById(R.id.item_goods_detail_tv) as TextView
    val goodSaleTv = itemView.findViewById(R.id.item_good_sale_tv) as TextView
    val goodDiscountTv = itemView.findViewById(R.id.item_good_discount_tv) as TextView
    val goodPriceTv = itemView.findViewById(R.id.item_good_price_tv) as TextView
    val shopButtom = itemView.findViewById(R.id.item_business_asb) as AnimShopButton
    
    init {
    
    }
    
    fun setData(data: FoodBean, position: Int) {
        goodIconIv.setImageResource(data.icon)
        goodTitleTv.text = data.title
        goodDetailTv.text = data.detail
        goodSaleTv.text = data.sale
        goodDiscountTv.text = "${String.format("%.1f", data.discount)}折"
        goodPriceTv.text = "￥${PriceFormatUtils.format(data.price)}"
        shopButtom.count = data.selectCount
    }
}