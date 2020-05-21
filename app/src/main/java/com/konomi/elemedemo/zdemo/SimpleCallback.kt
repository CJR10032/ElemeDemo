package com.konomi.elemedemo.zdemo


/**
 * 创建者     konomi
 * 创建时间   2019/1/3 1:53
 * 描述
 *
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述
 */
class SimpleCallback : Callback {

    private var mOnSuccess: ((result: String) -> Unit) = {}

    fun success(listener: (result: String) -> Unit) {
        mOnSuccess = listener
    }

    override fun onSuccess(result: String) {
        //  mOnSuccess?.invoke(result)
        mOnSuccess(result)
    }

    private var mOnFailed: ((code: Int) -> Unit)? = null

    fun failed(listener: (code: Int) -> Unit) {
        mOnFailed = listener
    }

    override fun onFailed(code: Int) {
        mOnFailed?.invoke(code)
    }
}