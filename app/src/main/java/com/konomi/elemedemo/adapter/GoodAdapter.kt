package com.konomi.elemedemo.adapter

import android.os.SystemClock
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.konomi.elemedemo.R
import com.konomi.elemedemo.bean.FoodBean
import com.konomi.elemedemo.inter.OnShopButtonClickListener
import com.konomi.elemedemo.utils.LogUtils
import com.konomi.elemedemo.viewholder.GoodViewHolder
import com.konomi.shopcart_lib.widget.cart.SimpleAddDelListener


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
class GoodAdapter(var dataList: List<FoodBean> = arrayListOf(),
                  var listener: ((holder: GoodViewHolder, data: FoodBean, position: Int) -> Unit)
                  ? = null, var onShopButtonClickListener: OnShopButtonClickListener? = null) :
        RecyclerView.Adapter<GoodViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoodViewHolder {
        LogUtils.myLog("GoodAdapter的onCreateViewHolder方法调用了---"
                       + "; itemCount = " + itemCount
                       + "; time = " + SystemClock.elapsedRealtime())
        val itemView = LayoutInflater
            .from(parent.context).inflate(R.layout.item_food, parent, false)
        val goodViewHolder = GoodViewHolder(itemView)
        goodViewHolder.itemView.setOnClickListener {
            listener?.invoke(goodViewHolder, dataList[goodViewHolder.adapterPosition],
                             goodViewHolder.adapterPosition)
        }

        goodViewHolder.shopButtom.onAddDelListener = object : SimpleAddDelListener() {

            override fun onAddClick(count: Int) {
                val position = goodViewHolder.adapterPosition
                onShopButtonClickListener?.onAddButtonClick(
                        goodViewHolder.shopButtom, dataList[position], position, count)
            }


            override fun onDelClick(count: Int) {
                val position = goodViewHolder.adapterPosition
                onShopButtonClickListener?.onDeleteButtonClick(dataList[position], position, count)
            }

        }
        return goodViewHolder
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: GoodViewHolder, position: Int) {
        holder.setData(dataList[position], position)
    }

    override fun onBindViewHolder(holder: GoodViewHolder, position: Int,
                                  payloads: MutableList<Any>) {
       /* LogUtils.myLog("GoodAdapter的onBindViewHolder方法调用了--- pos = " + position
                       + "; payloadSize =" + payloads.size
                       + "; time = " + SystemClock.elapsedRealtime())*/
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            //  局部更新, 只更新购买的数量
            holder.shopButtom.count = dataList[position].selectCount
        }
    }

    fun setOnGoodItemClickListener(
            l: (holder: GoodViewHolder, data: FoodBean, position: Int) -> Unit) {
        this.listener = l
    }

    fun notifySelectCountChange() {
        notifyItemRangeChanged(0, itemCount, 1)
    }

    fun notifySelectCountChange(position: Int) {
        notifyItemChanged(position, 1)
    }
}