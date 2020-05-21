package com.konomi.elemedemo.zdemo;

import org.jetbrains.annotations.NotNull;

/**
 * 创建者     konomi
 * 创建时间   2019/1/3 2:09
 * 描述
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述
 */
public class JavaDemo {

    public void hehe() {
        /*new Worker().setCallback(simpleCallback -> {
            simpleCallback.success(s -> null);
            simpleCallback.failed(integer -> null);
            return null;
        });*/

        new Worker().setCallback(new Callback() {
            @Override
            public void onSuccess(@NotNull String result) {

            }

            @Override
            public void onFailed(int code) {

            }
        });
    }
}
