package com.example.quxian.brainwave.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;

import com.example.quxian.brainwave.R;
import com.example.quxian.brainwave.base.BaseActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rorbin.q.radarview.RadarData;
import rorbin.q.radarview.RadarView;

public class MindDataActivity extends BaseActivity{
    private RadarView mRadarView;

    @Override
    public int bindLayout() {
        return R.layout.activity_mind;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setBaseTitle("五大性格极性");

        initView();
    }

    private void initView() {
        mRadarView = findById(R.id.mind_act_radar_view);


        List<String> vertexText = new ArrayList<>();
        Collections.addAll(vertexText, "尽责性","外向型", "宜人性", "神经质","开放性");
        //Collections.addAll(vertexText, "1", "2","3", "4", "5");
        mRadarView.setVertexText(vertexText);

        List<Integer> res = new ArrayList<>();
        Collections.addAll(res, R.drawable.blank,R.drawable.blank ,R.drawable.blank,R.drawable.blank,R.drawable.blank);
        mRadarView.setVertexIconResid(res);

        List<Float> values = new ArrayList<>();
        Collections.addAll(values, 3f, 6f, 2f, 7f, 5f);
        RadarData data = new RadarData(values);
        data.setValueTextEnable(true);
        data.setVauleTextColor(Color.RED);
        data.setValueTextSize(dp2px(10));
        data.setLineWidth(dp2px(1));
        mRadarView.addData(data);

        List<Float> values2 = new ArrayList<>();
        Collections.addAll(values2, 7f, 1f, 4f, 2f, 8f);
        RadarData data2 = new RadarData(values2);
        data2.setValueTextEnable(true);
        data2.setVauleTextColor(Color.BLUE);
        data2.setValueTextSize(dp2px(10));
        data2.setLineWidth(dp2px(1));
        mRadarView.addData(data2);

        List<Float> values3 = new ArrayList<>();
        Collections.addAll(values3, 4f, 5f, 3f, 8f, 4f);
        RadarData data3 = new RadarData(values3);
        data3.setValueTextEnable(true);
        data3.setVauleTextColor(Color.WHITE);
        data3.setValueTextSize(dp2px(10));
        data3.setLineWidth(dp2px(1));
        mRadarView.addData(data3);

    }

    private float dp2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return dpValue * scale + 0.5f;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_user:
                showToast("user");
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
