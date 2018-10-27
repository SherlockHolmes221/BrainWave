package com.example.quxian.brainwave.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.quxian.brainwave.R;
import com.example.quxian.brainwave.base.BaseActivity;

public class MainActivity extends BaseActivity {


    @Override
    public int bindLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
