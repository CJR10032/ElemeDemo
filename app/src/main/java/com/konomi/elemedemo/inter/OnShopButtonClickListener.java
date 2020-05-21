package com.konomi.elemedemo.inter;

import android.view.View;

import com.konomi.elemedemo.bean.FoodBean;

/**
 * 创建者     CJR
 * 创建时间   2018-07-07 14:54
 * 描述
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述
 *
 * @author CJR
 */
public interface OnShopButtonClickListener {

    void onAddButtonClick(View v, FoodBean bean, int position, int count);

    void onDeleteButtonClick(FoodBean bean, int position, int count);
}
