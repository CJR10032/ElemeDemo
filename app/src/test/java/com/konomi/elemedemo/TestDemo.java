package com.konomi.elemedemo;

import org.junit.Test;

import java.util.ArrayList;

/**
 * 创建者     CJR
 * 创建时间   2018-07-07 16:17
 * 描述
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述
 *
 * @author CJR
 */
public class TestDemo {

    @Test
    public void test() {
        ArrayList<TestBean> list1 = new ArrayList<>();
        ArrayList<TestBean> list2 = new ArrayList<>();

        list1.add(new TestBean());
        list1.get(0).name = "ayase";
        list1.get(0).age = 13;

        list2.add(list1.get(0));
        System.out.println(list1.toString());
        System.out.println(list2.toString());

        list2.get(0).name = "kirino";
        list2.get(0).age = 12;

        System.out.println("============");
        System.out.println(list1.toString());
        System.out.println(list2.toString());
    }
}
