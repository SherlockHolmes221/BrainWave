package com.example.quxian.brainwave.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.quxian.brainwave.R;
import com.example.quxian.brainwave.base.BaseActivity;
import com.example.quxian.brainwave.utils.SaveAccountUtil;
import com.example.quxian.brainwave.widget.DashboardView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BrainActivity extends BaseActivity{
    private DashboardView dashboardView;
    private LineChart lineChart1;
    private LineChart lineChart2;
    private TextView dataTv;


    @Override
    public int bindLayout() {
        return R.layout.activity_brain;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBaseTitle(SaveAccountUtil.getUserBean().getAccount());
        initView();
        initDashBoardView();
        initLineChart1();
        initLineChart2();
    }

    private void initLineChart2() {
        XAxis xAxis = lineChart2.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(20);
        xAxis.setLabelCount(10, false);

        lineChart2.getAxisRight().setEnabled(false);

        //获得 YAxis 类实例
        YAxis leftAxis = lineChart2.getAxisLeft();

        // 设置y轴数据的位置
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMinimum(0);
        leftAxis.setAxisMaximum(60);
        leftAxis.setLabelCount(3, false);

        // 不显示图例
        Legend legend = lineChart2.getLegend();
        legend.setTextColor(Color.WHITE);
        legend.setEnabled(true);
        lineChart2.invalidate();

        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            float val = (float) (Math.random() * 40) + 3;
            entries.add(new Entry(i, val));
        }
        List<Entry> entries1 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            float val = (float) (Math.random() * 40) + 3;
            entries1.add(new Entry(i, val));
        }
        setChartData2(lineChart2,entries,entries1);
    }

    private void initLineChart1() {
        XAxis xAxis = lineChart1.getXAxis();
        // 不显示x轴
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);

        //获得 YAxis 类实例
        lineChart1.getAxisRight().setEnabled(false);

        YAxis leftAxis = lineChart1.getAxisLeft();
        // 不显示y轴
        leftAxis.setDrawAxisLine(false);
        // 设置y轴数据的位置

        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setTextColor(Color.GREEN);
        leftAxis.setAxisMinimum(0);
        leftAxis.setAxisMaximum(20);
        leftAxis.setGridColor(Color.GRAY);
        leftAxis.setLabelCount(4, false);
        leftAxis.enableGridDashedLine(20f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);

        // 不显示图例
        Legend legend = lineChart1.getLegend();
        legend.setEnabled(false);
        lineChart1.invalidate();

        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            float val = (float) (Math.random() * 10) + 3;
            entries.add(new Entry(i, val));
        }
        List<Entry> entries1 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            float val = (float) (Math.random() * 10) + 3;
            entries1.add(new Entry(i, val));
        }
        setChartData(lineChart1,entries,entries1);
    }

    /**
     * 设置图表数据
     *
     * @param chart  图表
     * @param values 数据
     */
    private static void setChartData2(LineChart chart, List<Entry> values,List<Entry> values1) {
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();

        LineDataSet lineDataSet;
        lineDataSet = new LineDataSet(values, "专注度");
        // 设置曲线颜色
        lineDataSet.setColor(Color.GREEN);
        // 设置平滑曲线
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        // 不显示坐标点的小圆点
        lineDataSet.setDrawCircles(false);
        // 不显示坐标点的数据
        lineDataSet.setDrawValues(false);
        // 不显示定位线
        lineDataSet.setHighlightEnabled(false);

        LineDataSet lineDataSet1;
        lineDataSet1 = new LineDataSet(values1, "放松度");
        // 设置曲线颜色
        lineDataSet1.setColor(Color.YELLOW);
        // 设置平滑曲线
        lineDataSet1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        // 不显示坐标点的小圆点
        lineDataSet1.setDrawCircles(false);
        // 不显示坐标点的数据
        lineDataSet1.setDrawValues(false);
        // 不显示定位线
        lineDataSet1.setHighlightEnabled(false);

        dataSets.add(lineDataSet);

        dataSets.add(lineDataSet1);

        LineData data = new LineData(dataSets);
        chart.setData(data);
        chart.invalidate();
    }

    /**
     * 设置图表数据
     *
     * @param chart  图表
     * @param values 数据
     */
    public static void setChartData(LineChart chart, List<Entry> values,List<Entry> values1) {
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();

        LineDataSet lineDataSet;
        lineDataSet = new LineDataSet(values, "");
        // 设置曲线颜色
        lineDataSet.setColor(Color.RED);
        // 设置平滑曲线
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        // 不显示坐标点的小圆点
        lineDataSet.setDrawCircles(false);
        // 不显示坐标点的数据
        lineDataSet.setDrawValues(false);
        // 不显示定位线
        lineDataSet.setHighlightEnabled(false);

        LineDataSet lineDataSet1;
        lineDataSet1 = new LineDataSet(values1, "");
        // 设置曲线颜色
        lineDataSet1.setColor(Color.BLUE);
        // 设置平滑曲线
        lineDataSet1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        // 不显示坐标点的小圆点
        lineDataSet1.setDrawCircles(false);
        // 不显示坐标点的数据
        lineDataSet1.setDrawValues(false);
        // 不显示定位线
        lineDataSet1.setHighlightEnabled(false);

        dataSets.add(lineDataSet);

        dataSets.add(lineDataSet1);

        LineData data = new LineData(dataSets);
        chart.setData(data);
        chart.invalidate();
    }


    private void initDashBoardView() {
        dashboardView.setCreditValueWithAnim(75);
    }

    private void initView() {
        dashboardView = findById(R.id.brain_act_dashboardview);
        lineChart1 = findById(R.id.brain_act_linechart1);
        lineChart2 = findById(R.id.brain_act_linechart2);
        dataTv = findById(R.id.brain_act_date_tv);
        dataTv.setText(getDate());
    }

    private String getDate(){
        Calendar cal=Calendar.getInstance();
        return cal.get(Calendar.YEAR)+"年"+(cal.get(Calendar.MONTH)+1)
                +"月"+cal.get(Calendar.DATE)+"日";
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
