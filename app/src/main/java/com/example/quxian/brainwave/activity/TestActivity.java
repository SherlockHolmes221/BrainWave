package com.example.quxian.brainwave.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.quxian.brainwave.R;
import com.example.quxian.brainwave.base.BaseActivity;
import com.example.quxian.brainwave.model.LineChartData;
import com.example.quxian.brainwave.widgt.ChartView;
import com.example.quxian.brainwave.widgt.LineChart;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestActivity extends BaseActivity{

    private LineChart mLineChart;

    private String[] mChartItems = new String[]{"6:00", "8:00", "10:00", "12:00", "14:00", "16:00", "18:00"};
    private int[] mWeekPoints = new int[]{100, 150, 80, 40, 90, 50, 150};
    private List<LineChartData> dataList1 = new ArrayList<>();


    //x轴坐标对应的数据
    private List<String> xValue = new ArrayList<>();
    //y轴坐标对应的数据
    private List<Integer> yValue = new ArrayList<>();
    //折线对应的数据
    private Map<String, Integer> value = new HashMap<>();


    @Override
    public int bindLayout() {
        return R.layout.activity_weight;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBaseTitle("12232");
        initChart();
    }

    private void initChart() {
        for (int i = 0; i < 12; i++) {
            xValue.add((i + 1) + "月");
            value.put((i + 1) + "月", (int) (Math.random() * 181 + 60));//60--240
        }

        for (int i = 0; i < 6; i++) {
            yValue.add(i * 60);
        }

        ChartView chartView = (ChartView) findViewById(R.id.chartview);
        chartView.setValue(value, xValue, yValue);

//        mLineChart = findViewById(R.id.weight_act_line_char);
//        for (int i = 0; i < mChartItems.length; i++) {
//            LineChartData data = new LineChartData();
//            data.setItem(mChartItems[i]);
//            data.setPoint(mWeekPoints[i]);
//            dataList1.add(data);
//        }
//        mLineChart.setData(dataList1);
    }




}
