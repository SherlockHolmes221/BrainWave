package com.example.quxian.brainwave.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;

import com.example.quxian.brainwave.R;
import com.example.quxian.brainwave.base.BaseActivity;
import com.example.quxian.brainwave.model.LineChartData;
import com.example.quxian.brainwave.utils.SaveAccountUtil;
import com.example.quxian.brainwave.widget.ChartView;
import com.example.quxian.brainwave.widget.DashboardView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
        initDashBoardView();

    }

    private DashboardView dashboardView;
    private void initDashBoardView() {
        dashboardView = findById(R.id.brain_act_dashboardview);
        dashboardView.setCreditValueWithAnim(new Random().nextInt(950 - 350) + 350);
    }

    private void initView() {
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
