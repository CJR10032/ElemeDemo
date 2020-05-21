package com.konomi.elemedemo.bean


/**
 * 创建者     CJR
 * 创建时间   2018-07-05 14:58
 * 描述
 *
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述
 *
 * @author CJR
 */
data class FoodBean(var id: Int, var type: String, var title: String, var detail: String,
                    var sale: String, var price: Float, var discount: Float, var icon: Int,
                    var selectCount: Int = 0)