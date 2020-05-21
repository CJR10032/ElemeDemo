package com.konomi.elemedemo.base

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.IBinder
import android.support.annotation.IntegerRes
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast


/**
 * 创建者     CJR
 * 创建时间   2018-07-04 16:45
 * 描述
 *
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述
 *
 * @author CJR
 */
abstract class BaseActivity : AppCompatActivity() {

    private var mToast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onContentView()
        initData()
        initListener()
    }

    /**
     * 初始化数据
     */
    abstract fun onContentView()

    abstract fun initData()

    /**
     * 初始化监听
     */
    abstract fun initListener()

    @SuppressLint("ShowToast")
    fun doToast(contents: String) {
        if (mToast == null) {
            mToast = Toast.makeText(this, contents, Toast.LENGTH_SHORT)
        } else {
            mToast?.setText(contents)
        }
        mToast?.show()
    }

    @SuppressLint("ResourceType", "ShowToast")
    fun doToast(@IntegerRes contensRes: Int) {
        if (mToast == null) {
            mToast = Toast.makeText(this, contensRes, Toast.LENGTH_SHORT)
        } else {
            mToast?.setText(contensRes)
        }
        mToast?.show()
    }

    /**
     * onStop和onDestroy的时候会调用这个方法, 如果不想在onStop和onDestroy的时候取消吐司, 重写该方法
     */
    private fun cancelToast() {
        mToast?.cancel()
    }

    //------- start 点击空白处隐藏软键盘 -------------------
    //  add CJR 点击空白隐藏软键盘
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v: View? = currentFocus
            if (isShouldHideKeyboard(v, ev)) {
                hideKeyboard(v?.windowToken)
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private fun isShouldHideKeyboard(v: View?, event: MotionEvent): Boolean {
        if (v != null && v is EditText) {
            val l = intArrayOf(0, 0)
            v.getLocationInWindow(l)
            val left = l[0]
            val top = l[1]
            val bottom = top + v.height
            val right = left + v.width
            return !(event.x > left && event.x < right && event.y > top && event.y < bottom)
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，
        // 和用户用轨迹球选择其他的焦点
        return false
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     *
     * @param token
     */
    private fun hideKeyboard(token: IBinder?) {
        if (token != null) {
            val im = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }
    //------- end 点击空白处隐藏软键盘 -------------------

    // ------------ start 生命周期--------------------
    override fun onPause() {
        super.onPause()
        cancelToast()
    }

    // ------------ end 生命周期--------------------
}
