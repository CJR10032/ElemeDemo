package com.konomi.elemedemo.zdemo


/**
 * 创建者     konomi
 * 创建时间   2019/1/2 20:09
 * 描述
 *
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述
 */
class CompanionDemo {

    companion object Com {

        @JvmField
        var cFlag = false

        @JvmStatic
        fun chehe() {
            System.out.println("chehe")

            var interfaceDemo: InterfaceDemo = object : InterfaceDemo {

                override fun getName(): String = "hehe"
            }
        }
    }
}