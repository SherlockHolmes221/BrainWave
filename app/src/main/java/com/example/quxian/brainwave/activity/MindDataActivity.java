package com.example.quxian.brainwave.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;

import com.example.quxian.brainwave.R;
import com.example.quxian.brainwave.base.BaseActivity;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;


public class MindDataActivity extends BaseActivity{
//    private RadarView mRadarView;
    private RadarChart radarChart;

    @Override
    public int bindLayout() {
        return R.layout.activity_mind;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setBaseTitle("五大性格极性");

        initView();

        //initLeiDa();
        initRadarChart();
    }

    private void initView() {
        //mRadarView = findById(R.id.mind_act_radar_view);
        radarChart = findById(R.id.mind_act_radar_view);
    }

    private void initRadarChart(){

        radarChart.setDescription(null);
        // 绘制线条宽度，圆形向外辐射的线条
        radarChart.setWebLineWidth(1.5f);
        // 内部线条宽度，外面的环状线条
        radarChart.setWebLineWidthInner(1.0f);
        // 所有线条WebLine透明度
        radarChart.setWebAlpha(100);
        radarChart.setRotationEnabled(false);
//        radarChart.setDrawWeb(false);
        
        setData();

        XAxis xAxis = radarChart.getXAxis();
        // X坐标值字体大小
        xAxis.setTextSize(12f);

        YAxis yAxis = radarChart.getYAxis();
        yAxis.setDrawLabels(false);
        // Y坐标值标签个数
        //yAxis.setLabelCount(6, false);
        // Y坐标值字体大小
        yAxis.setTextSize(15f);
        // Y坐标值是否从0开始
        yAxis.setStartAtZero(true);

        Legend l = radarChart.getLegend();
        // 图例位置
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_CENTER);
        // 图例X间距
        l.setXEntrySpace(2f);
        // 图例Y间距
        l.setYEntrySpace(1f);
    }

    private void setData() {
        float mult = 150;
        int cnt = 5; // 不同的维度Party A、B、C...总个数

        // Y的值，数据填充
        ArrayList<RadarEntry> yVals1 = new ArrayList<RadarEntry>();
        ArrayList<RadarEntry> yVals2 = new ArrayList<RadarEntry>();
        ArrayList<RadarEntry> yVals3 = new ArrayList<RadarEntry>();

        for (int i = 0; i < cnt; i++) {
            yVals1.add(new RadarEntry((float) (Math.random() * mult) + mult / 2, i));
        }

        for (int i = 0; i < cnt; i++) {
            yVals2.add(new RadarEntry((float) (Math.random() * mult) + mult / 2, i));
        }

        for (int i = 0; i < cnt; i++) {
            yVals3.add(new RadarEntry((float) (Math.random() * mult) + mult / 2, i));
        }

        // Party A、B、C..
        ArrayList<String> xVals = new ArrayList<String>();

        XAxis xAxis = radarChart.getXAxis();
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            private String[] tempValues = new String[] {"尽责性","外向型", "宜人性", "神经质","开放性"};
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return tempValues[(int) (value % tempValues.length)];
            }
        });

        RadarDataSet set1 = new RadarDataSet(yVals1, "自评得分");
        // Y数据颜色设置
        set1.setColor(ColorTemplate.VORDIPLOM_COLORS[0]);
        // 是否实心填充区域
        set1.setDrawFilled(false);
        // 数据线条宽度
        set1.setLineWidth(2f);

        RadarDataSet set2 = new RadarDataSet(yVals2, "他评平均分");
        set2.setColor(ColorTemplate.VORDIPLOM_COLORS[1]);
        set2.setDrawFilled(false);
        set2.setLineWidth(2f);

        RadarDataSet set3 = new RadarDataSet(yVals3, "人评平均分");
        set3.setColor(ColorTemplate.VORDIPLOM_COLORS[2]);
        set3.setDrawFilled(false);
        set3.setLineWidth(2f);

        ArrayList<IRadarDataSet> sets = new ArrayList<IRadarDataSet>();
        sets.add(set1);
        sets.add(set2);
        sets.add(set3);

        RadarData data = new RadarData(sets);
        // 数据字体大小
        data.setValueTextSize(8f);
        // 是否绘制Y值到图表
        data.setDrawValues(false);

        radarChart.setData(data);

        radarChart.invalidate();
    }

//    private void initLeiDa() {
//        List<String> vertexText = new ArrayList<>();
//        Collections.addAll(vertexText, "尽责性","外向型", "宜人性", "神经质","开放性");
//        //Collections.addAll(vertexText, "1", "2","3", "4", "5");
//        mRadarView.setVertexText(vertexText);
//
//        List<Integer> res = new ArrayList<>();
//        Collections.addAll(res, R.drawable.blank,R.drawable.blank ,R.drawable.blank,R.drawable.blank,R.drawable.blank);
//        mRadarView.setVertexIconResid(res);
//
//        List<Float> values = new ArrayList<>();
//        Collections.addAll(values, 3f, 6f, 2f, 7f, 5f);
//        RadarData data = new RadarData(values);
//        data.setValueTextEnable(true);
//        data.setVauleTextColor(Color.RED);
//        data.setValueTextSize(dp2px(10));
//        data.setLineWidth(dp2px(1));
//        mRadarView.addData(data);
//
//        List<Float> values2 = new ArrayList<>();
//        Collections.addAll(values2, 7f, 1f, 4f, 2f, 8f);
//        RadarData data2 = new RadarData(values2);
//        data2.setValueTextEnable(true);
//        data2.setVauleTextColor(Color.BLUE);
//        data2.setValueTextSize(dp2px(10));
//        data2.setLineWidth(dp2px(1));
//        mRadarView.addData(data2);
//
//        List<Float> values3 = new ArrayList<>();
//        Collections.addAll(values3, 4f, 5f, 3f, 8f, 4f);
//        RadarData data3 = new RadarData(values3);
//        data3.setValueTextEnable(true);
//        data3.setVauleTextColor(Color.WHITE);
//        data3.setValueTextSize(dp2px(10));
//        data3.setLineWidth(dp2px(1));
//        mRadarView.addData(data3);
//    }
//
//    private float dp2px(float dpValue) {
//        final float scale = getResources().getDisplayMetrics().density;
//        return dpValue * scale + 0.5f;
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_user:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
