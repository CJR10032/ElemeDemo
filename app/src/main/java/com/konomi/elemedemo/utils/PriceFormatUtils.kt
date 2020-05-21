package com.konomi.elemedemo.utils

import java.text.DecimalFormat


/**
 * 创建者     CJR
 * 创建时间   2018-07-07 16:01
 * 描述
 *
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述
 *
 * @author CJR
 */
object PriceFormatUtils {
    
    val decimalFormat: DecimalFormat = DecimalFormat("#.##")
    
    /**
     *  单例工具类, Kotlin说我说面向对象的, 没有静态方法...; 这里的单例是用的静态内部类实现的
     */
    fun getPriceFormatUtils(): PriceFormatUtils {
        return this
    }
    
    fun format(num: Double): String = decimalFormat.format(num)
    
    fun format(num: Int): String = decimalFormat.format(num)
    
    fun format(num: Float): String = decimalFormat.format(num)
}