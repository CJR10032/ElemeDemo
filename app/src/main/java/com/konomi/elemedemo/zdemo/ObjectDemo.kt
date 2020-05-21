package com.konomi.elemedemo.zdemo


/**
 * 创建者     konomi
 * 创建时间   2019/1/2 20:08
 * 描述
 *
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述
 */
object ObjectDemo {

    @JvmField
    var flag = false

    @JvmStatic
    fun hehe() {
        System.out.println("hehe")

//        Worker().apply {
//            setCallback {
//                success { LogUtils.myLog("result = $it") }
//                failed { LogUtils.myLog("code = $it") }
//            }
//        }
    }
}