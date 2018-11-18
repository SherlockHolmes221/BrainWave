package com.example.quxian.brainwave.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.example.quxian.brainwave.R;
import com.example.quxian.brainwave.base.BaseActivity;
import com.rengwuxian.materialedittext.MaterialEditText;

public class RegisterActivity extends BaseActivity{
    private MaterialEditText accountEd;
    private MaterialEditText passwordEd;
    private MaterialEditText passwordConfirmEd;
    private MaterialEditText ageEd;
    private MaterialEditText heightEd;
    private MaterialEditText weightEd;
    private Button registerBtn;

    @Override
    public int bindLayout() {
        return R.layout.activity_register;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);
        setBaseTitle("注册");

        initView();

        initListener();
    }

    private void initView() {
        accountEd = findById(R.id.register_act_account_ed);
        passwordConfirmEd = findById(R.id.register_act_confirm_ed);
        passwordEd = findById(R.id.register_act_password_ed);
        heightEd = findById(R.id.register_act_height_ed);
        ageEd = findById(R.id.register_act_age_ed);
        weightEd = findById(R.id.register_act_weight_ed);
        registerBtn = findById(R.id.register_act_register_btn);
    }

    private void initListener() {
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(accountEd.getText().toString().equals("") ||
                        passwordConfirmEd.getText().toString().equals("") ||
                        passwordEd.getText().toString().equals("") ||
                        accountEd.getText().toString().equals("") ||
                        weightEd.getText().toString().equals("") ||
                        heightEd.getText().toString().equals("") ){
                    showToast("请填写完整的个人信息");
                }else{
                    register();
                }
            }
        });
    }

    //注册
    private void register() {
        showToast("注册成功");
        startActivity(SignInActivity.class);
    }
}
