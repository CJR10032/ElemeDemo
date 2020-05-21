package com.konomi.elemedemo.utils

import android.content.Context
import com.konomi.elemedemo.app.AppConstant
import com.konomi.elemedemo.bean.FoodBean
import com.konomi.elemedemo.bean.TypeBean
import java.util.*


/**
 * 创建者     CJR
 * 创建时间   2018-07-05 10:28
 * 描述
 *
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述
 *
 * @author CJR
 */
object DataUtils {
    
    /**
     *  单例工具类, Kotlin说我说面向对象的, 没有静态方法...; 这里的单例是用的静态内部类实现的
     */
    fun getDataUtils() {
    }
    
    fun getTypeDatas(): List<TypeBean> {
        return AppConstant.GOOD_TYPES.mapTo(arrayListOf()) { TypeBean(it) }
    }
    
    fun getGoodImages(context: Context): List<FoodBean> {
        val random = Random()
        /*0..42*/
        return (0..42).mapTo(arrayListOf()) {
            //  随机获取一张图片
            val iconRes = context.resources.getIdentifier("food${random.nextInt(8)}", "drawable",
                                                          context.packageName)
            //  获取类型的索引
            val index = it / AppConstant.GOOD_TYPES.size
            
            var list: MutableList<String> = AppConstant.GOOD_NAMES.distinct().toMutableList()
            
            FoodBean(it, AppConstant.GOOD_TYPES[Math.min(index, AppConstant.GOOD_TYPES.size - 1)],
                     if (!list.isEmpty()) {
                         list.removeAt(random.nextInt(list.size))
                     } else {
                         list = AppConstant.GOOD_NAMES.distinct().toMutableList()
                         list.removeAt(random.nextInt(list.size))
                
                     }, "小猪包1个+豆浆1个+鲜肉包1个", "月售${random.nextInt(100)}份",
                     Math.round(random.nextFloat() * 10000) / 100f,
                     4f, iconRes)
        }
    }
}