package com.konomi.elemedemo.viewholder

import android.graphics.Color
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.konomi.elemedemo.R
import com.konomi.elemedemo.bean.TypeBean
import com.konomi.elemedemo.utils.LogUtils


/**
 * 创建者     CJR
 * 创建时间   2018-07-05 10:13
 * 描述
 *
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述
 *
 * @author CJR
 */
class TypeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    
    val typeNameTv = itemView.findViewById(R.id.tv_name) as TextView
    val countTv = itemView.findViewById(R.id.item_badge_tv) as TextView
    
    val normalSize: Int = 13
    val selectSize: Int = 15
    
    init {
        
        LogUtils.myLog("normalSize = $normalSize; selectSize = $selectSize")
    }
    
    fun setData(data: TypeBean, select: Boolean) {
        //  设置种类的名字
        typeNameTv.text = data.typeName
        //  设置数字气泡
        setCount(data.count)
        setSelect(select)
    }
    
    fun setCount(count: Int) {
        if (count > 0) {
            //  该种类需要显示数字
            if (countTv.visibility == View.INVISIBLE) {
                countTv.visibility = View.VISIBLE
            }
            countTv.text = "$count"
        } else {
            //  该种类不需要显示数字
            countTv.visibility = View.INVISIBLE
        }
    }
    
    fun setSelect(select: Boolean) {
        if (select) {
            //  设置选中背景色
            itemView.setBackgroundColor(Color.WHITE)
            //  设置选中的字体颜色
            typeNameTv.setTextColor(Color.BLACK)
            //  选中条目字体加粗
            typeNameTv.typeface = Typeface.DEFAULT_BOLD
            typeNameTv.textSize = selectSize.toFloat()
        } else {
            itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.type_gray))
            typeNameTv.setTextColor(ContextCompat.getColor(itemView.context, R.color.type_normal))
            typeNameTv.typeface = Typeface.DEFAULT
            typeNameTv.textSize = normalSize.toFloat()
        }
    }
}