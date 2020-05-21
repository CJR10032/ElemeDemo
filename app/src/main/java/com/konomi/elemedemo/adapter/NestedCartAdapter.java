package com.konomi.elemedemo.adapter;

import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.konomi.elemedemo.bean.FoodBean;
import com.konomi.elemedemo.inter.OnShopButtonClickListener;
import com.konomi.elemedemo.utils.LogUtils;
import com.konomi.elemedemo.viewholder.CartViewHolder;
import com.konomi.shopcart_lib.widget.cart.SimpleAddDelListener;

import java.util.List;

/**
 * 创建者     CJR
 * 创建时间   2018-06-15 16:49
 * 描述       购物车列表的Adapter
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述
 *
 * @author CJR
 */
public class NestedCartAdapter extends RecyclerView.Adapter<CartViewHolder> {

    private OnShopButtonClickListener mOnShopButtonClickListener;
    private List<FoodBean>            mDataList;

    public NestedCartAdapter() {
    }

    public NestedCartAdapter(List<FoodBean> dataList) {
        mDataList = dataList;
    }

    public void setDataList(List<FoodBean> dataList) {
        mDataList = dataList;
    }

    public List<FoodBean> getDataList() {
        return mDataList;
    }

    public void setOnShopButtonClickListener(OnShopButtonClickListener onShopButtonClickListener) {
        mOnShopButtonClickListener = onShopButtonClickListener;
    }

    @Override
    public CartViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        LogUtils.myLog("CartAdapter的onCreateViewHolder方法调用了---" + "; time = "
                + SystemClock.elapsedRealtime());

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(CartViewHolder.LAYOUT_RES, parent, false);
        CartViewHolder cartViewHolder = new CartViewHolder(itemView);

        cartViewHolder.mAnimShopButton.setOnAddDelListener(new SimpleAddDelListener() {

            @Override
            public void onAddClick(int count) {
                if (mOnShopButtonClickListener != null) {
                    int position = cartViewHolder.getAdapterPosition();
                    mOnShopButtonClickListener.onAddButtonClick(cartViewHolder.mAnimShopButton,
                            mDataList.get(position), position, count);
                }
            }


            @Override
            public void onDelClick(int count) {
                if (mOnShopButtonClickListener != null) {
                    int position = cartViewHolder.getAdapterPosition();
                    mOnShopButtonClickListener.onDeleteButtonClick(
                            mDataList.get(position), position, count);
                }
            }

        });

        return cartViewHolder;
    }

    public void notifySelectCountChange() {
        notifyItemRangeChanged(0, getItemCount(), 1);
    }

    public void notifySelectCountChange(int position) {
        notifyItemChanged(position, 1);
    }

    @Override
    public void onBindViewHolder(CartViewHolder holder, int position) {
        holder.setData(mDataList.get(position), position, mDataList);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull
            List<Object> payloads) {
        /*LogUtils.myLog("GoodAdapter的onBindViewHolder方法调用了--- pos = " + position
                + "; payloadSize =" + payloads.size()
                + "; time = " + SystemClock.elapsedRealtime());*/
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            //  局部更新, 只更新购买的数量
            holder.mAnimShopButton.setCount(mDataList.get(position).getSelectCount());
        }
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }
}
