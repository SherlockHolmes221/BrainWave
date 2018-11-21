package com.example.quxian.brainwave.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;

import com.example.quxian.brainwave.R;
import com.example.quxian.brainwave.base.BaseActivity;
import com.example.quxian.brainwave.model.LineChartData;
import com.example.quxian.brainwave.utils.SaveAccountUtil;
import com.example.quxian.brainwave.widgt.ChartView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrainActivity extends BaseActivity{
    @Override
    public int bindLayout() {
        return R.layout.activity_brain;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBaseTitle(SaveAccountUtil.getUserBean().getAccount());
        initView();
        initChart();

    }

    private void initView() {
    }


    private String[] mChartItems = new String[]{"6:00", "8:00", "10:00", "12:00", "14:00", "16:00", "18:00"};
    private int[] mWeekPoints = new int[]{100, 150, 80, 40, 90, 50, 150};
    private List<LineChartData> dataList1 = new ArrayList<>();
    //x轴坐标对应的数据
    private List<String> xValue = new ArrayList<>();
    //y轴坐标对应的数据
    private List<Integer> yValue = new ArrayList<>();
    //折线对应的数据
    private Map<String, Integer> value = new HashMap<>();
    private void initChart() {
        for (int i = 0; i < 12; i++) {
            xValue.add((i + 1) + "月");
            value.put((i + 1) + "月", (int) (Math.random() * 181 + 60));//60--240
        }

        for (int i = 0; i < 6; i++) {
            yValue.add(i * 60);
        }
        ChartView chartView = (ChartView) findViewById(R.id.main_act_chartview);
        chartView.setValue(value, xValue, yValue);
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
                //showToast("user");
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
