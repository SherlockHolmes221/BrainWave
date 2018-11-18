package com.example.quxian.brainwave.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.example.quxian.brainwave.R;
import com.example.quxian.brainwave.base.BaseActivity;

public class WeightActivity extends BaseActivity{
    @Override
    public int bindLayout() {
        return R.layout.activity_weight;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        FrameLayout content = new FrameLayout(this);
//
//        //缩放控件放置在FrameLayout的上层，用于放大缩小图表
//        FrameLayout.LayoutParams frameParm = new FrameLayout.LayoutParams(
//                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
//        frameParm.gravity = Gravity.BOTTOM|Gravity.RIGHT;
//
//		   /*
//		  //缩放控件放置在FrameLayout的上层，用于放大缩小图表
//	       mZoomControls = new ZoomControls(this);
//	       mZoomControls.setIsZoomInEnabled(true);
//	       mZoomControls.setIsZoomOutEnabled(true);
//		   mZoomControls.setLayoutParams(frameParm);
//		   */
//
//        //图表显示范围在占屏幕大小的90%的区域内
//        DisplayMetrics dm = getResources().getDisplayMetrics();
//        int scrWidth = (int) (dm.widthPixels * 0.9);
//        int scrHeight = (int) (dm.heightPixels * 0.9);
//        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
//                scrWidth, scrHeight);
//
//        //居中显示
//        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
//        //图表view放入布局中，也可直接将图表view放入Activity对应的xml文件中
//        final RelativeLayout chartLayout = new RelativeLayout(this);
//
//        chartLayout.addView(new LineChartView(this), layoutParams);
//
//        //增加控件
//        ((ViewGroup) content).addView(chartLayout);
//        //((ViewGroup) content).addView(mZoomControls);
//        setContentView(content);
    }
    //https://github.com/kntryer/LineChart/tree/master/app/src/main/res
}
