package com.konomi.elemedemo.adapter

import android.os.SystemClock
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.konomi.elemedemo.R
import com.konomi.elemedemo.bean.TypeBean
import com.konomi.elemedemo.utils.LogUtils
import com.konomi.elemedemo.viewholder.TypeViewHolder


/**
 * 创建者     CJR
 * 创建时间   2018-07-05 10:12
 * 描述
 *
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述
 *
 * @author CJR
 */
class TypeAdapter(var dataList: List<TypeBean> = arrayListOf(), var selectItem: Int = 0,
                  var listener: ((holder: TypeViewHolder, data: TypeBean, position: Int) -> Unit)? = null) :
        RecyclerView.Adapter<TypeViewHolder>() {
    
    public val TYPEBEAN_COUNT_CHANGE = 1
    public val TYPEBEAN_SELECT_CHANGE = 2
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypeViewHolder {
        
        LogUtils.myLog("TypeAdapter的onCreateViewHolder方法调用了---"
                       + "; itemCount = " + itemCount
                       + "; time = " + SystemClock.elapsedRealtime())
        
        val itemView =
                LayoutInflater.from(parent.context).inflate(R.layout.item_type, parent, false)
        val typeViewHolder = TypeViewHolder(itemView)
        typeViewHolder.itemView.setOnClickListener {
            if (selectItem != typeViewHolder.adapterPosition) {
                val preSelectItem = selectItem
                selectItem = typeViewHolder.adapterPosition
                //  通知之前的item, 它已经不再是天选之人
                notifyItemChanged(preSelectItem, TYPEBEAN_SELECT_CHANGE)
                //  选中点击的条目
                typeViewHolder.setSelect(true)
                listener?.invoke(typeViewHolder, dataList[selectItem], selectItem)
            }
        }
        return typeViewHolder
    }
    
    override fun getItemCount(): Int = dataList.size
    
    override fun onBindViewHolder(holder: TypeViewHolder, position: Int) {
        holder.setData(dataList[position], position == selectItem)
    }
    
    override fun onBindViewHolder(holder: TypeViewHolder, position: Int,
                                  payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            if (payloads[0] == TYPEBEAN_COUNT_CHANGE) {
                //  数字改变了
                holder.setCount(dataList[position].count)
            } else if (payloads[0] == TYPEBEAN_SELECT_CHANGE) {
                //  选择发生了改变
                holder.setSelect(position == selectItem)
            }
        }
    }
    
    fun setSelectTypeName(typeName: String): Int {
        if (typeName != dataList[selectItem].typeName) {
            //  选中的头部已经发生改变
            val newSelectIndex = dataList.indexOfFirst { typeName == it.typeName }
            if (newSelectIndex != -1) {
                val preSelectItem = selectItem
                selectItem = newSelectIndex
                //  通知之前的item, 它已经不再是天选之人
                notifyItemChanged(preSelectItem, TYPEBEAN_SELECT_CHANGE)
                //  通知选中的item
                notifyItemChanged(selectItem, TYPEBEAN_SELECT_CHANGE)
                return newSelectIndex
            }
        }
        return -1
    }
    
    /**
     *kotlin居然没有函数式接口...
     * https://blog.csdn.net/a907691592/article/details/79296008 (坑)
     * https://blog.csdn.net/EthanCo/article/details/54971645 (写法参考)
     * https://blog.csdn.net/cysion1989/article/details/73431003 (写法参考)
     *
     */
    fun setOnTypeItemClickListener(
            l: (holder: TypeViewHolder, data: TypeBean, position: Int) -> Unit) {
        this.listener = l
    }
}