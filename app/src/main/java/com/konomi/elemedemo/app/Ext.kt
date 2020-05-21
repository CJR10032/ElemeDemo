package com.konomi.elemedemo.app

import android.app.Activity
import android.content.Intent


/**
 * 创建者     CJR
 * 创建时间   2018-03-13 17:49
 * 描述       放扩展函数的文件
 *
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述
 */
fun Activity.startActivity(cls: Class<*>) {
    this.startActivity(Intent(this, cls))
}