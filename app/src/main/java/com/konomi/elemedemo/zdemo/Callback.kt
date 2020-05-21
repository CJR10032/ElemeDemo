package com.konomi.elemedemo.zdemo


/**
 * 创建者     konomi
 * 创建时间   2019/1/3 1:50
 * 描述       https://blog.csdn.net/cysion1989/article/details/73431003
 *
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述
 */
interface Callback {

    fun onSuccess(result: String)

    fun onFailed(code: Int)
}