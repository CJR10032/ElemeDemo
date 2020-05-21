package com.konomi.elemedemo.adapter;

import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.konomi.elemedemo.R;
import com.konomi.elemedemo.bean.FoodBean;
import com.konomi.elemedemo.utils.LogUtils;
import com.konomi.elemedemo.viewholder.GoodViewHolder;

import java.util.List;

/**
 * 创建者     CJR
 * 创建时间   2018-07-13 15:27
 * 描述
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述
 *
 * @author CJR
 */
public class GoodAdapterJava extends RecyclerView.Adapter<GoodViewHolder> {

    private List<FoodBean> mDataList;

    public GoodAdapterJava() {
    }

    public GoodAdapterJava(List<FoodBean> dataList) {
        mDataList = dataList;
    }

    public List<FoodBean> getDataList() {
        return mDataList;
    }

    public void setDataList(List<FoodBean> dataList) {
        mDataList = dataList;
    }

    @NonNull
    @Override
    public GoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LogUtils.myLog("GoodAdapterJava的onCreateViewHolder方法调用了~~~"
                + "; itemCount = " + getItemCount()
                + "; time = " + SystemClock.elapsedRealtime());

        View itemView = LayoutInflater.from(
                parent.getContext()).inflate(R.layout.item_food, parent, false);
        return new GoodViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GoodViewHolder holder, int position) {

    }

    @Override
    public void onBindViewHolder(@NonNull GoodViewHolder holder, int position, @NonNull
            List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
         /*LogUtils.myLog("GoodAdapter的onBindViewHolder方法调用了~~~ pos = " + position
                       + "; payloadSize =" + payloads.size
                       + "; time = " + SystemClock.elapsedRealtime())*/
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }
}
