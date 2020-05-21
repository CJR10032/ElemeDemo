package com.konomi.elemedemo.zdemo


/**
 * 创建者     konomi
 * 创建时间   2019/1/3 2:02
 * 描述
 *
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述
 */
class Worker {

    var mCallback: Callback? = null

    fun setCallback(callback: Callback) {
        mCallback = callback
    }

    fun success(result: String) {
        mCallback?.onSuccess(result)
    }

    fun failed(code: Int) {
        mCallback?.onFailed(code)
    }

    /*fun setCallback(listener: SimpleCallback.() -> Unit) {
        val ca = SimpleCallback()
        ca.listener()
        setCallback(ca)
    }*/
}