package com.konomi.elemedemo.ui;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.konomi.elemedemo.R;
import com.konomi.elemedemo.adapter.GoodAdapterJava;
import com.konomi.elemedemo.adapter.TypeAdapter;
import com.konomi.elemedemo.base.BaseActivity;
import com.konomi.elemedemo.utils.DataUtils;
import com.konomi.elemedemo.utils.LogUtils;

/**
 * 创建者     CJR
 * 创建时间   2018-07-13 15:23
 * 描述
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述
 *
 * @author CJR
 */
public class JavaTestMain extends BaseActivity {

    @Override
    public void onContentView() {
        setContentView(R.layout.activity_main);
    }

    @Override
    public void initData() {
        RecyclerView goodRecyclerView = findViewById(R.id.goodRecyclerView);
        goodRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        GoodAdapterJava goodAdapterJava = new GoodAdapterJava();
        goodAdapterJava.setDataList(DataUtils.INSTANCE.getGoodImages(this));
        //   goodRecyclerView.addItemDecoration(AnimatorTitleDecoration(this))
        goodRecyclerView.setAdapter(goodAdapterJava);
    }

    @Override
    public void initListener() {
        TypeAdapter typeAdapter = new TypeAdapter();
        typeAdapter.setOnTypeItemClickListener(
                (typeViewHolder, typeBean, position) -> {
                    LogUtils.myLog("position = " + position);
                    return null;
                });
    }
}
