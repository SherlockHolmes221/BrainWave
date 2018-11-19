package com.example.quxian.brainwave.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.quxian.brainwave.R;
import com.example.quxian.brainwave.base.BaseActivity;
import com.example.quxian.brainwave.model.UserBean;
import com.example.quxian.brainwave.utils.SharedPreferencesHelper;
import com.rengwuxian.materialedittext.MaterialEditText;

public class RegisterActivity extends BaseActivity{
    private static final String TAG = "RegisterActivity";
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
                }else if(passwordEd.getText().toString().length() < 6 ||
                        passwordEd.getText().toString().length() > 16){
                    showToast("请输入6-16位密码");
                }else if(!passwordEd.getText().toString().equals(passwordConfirmEd.getText().toString())){
                    showToast("两次输入密码不一致");
                }else{
                    register();
                }
            }
        });
    }

    //注册
    private void register() {
        SharedPreferencesHelper.put(getApplication(),
                SharedPreferencesHelper.USER_ACCOUNT,accountEd.getText().toString());
        SharedPreferencesHelper.put(getApplication(),
                SharedPreferencesHelper.USER_PASSWORD,passwordEd.getText().toString());
        SharedPreferencesHelper.put(getApplication(),
                SharedPreferencesHelper.AGE,Integer.parseInt(ageEd.getText().toString()));
        SharedPreferencesHelper.put(getApplication(),
                SharedPreferencesHelper.HEIGHT,Float.parseFloat(heightEd.getText().toString()));
        SharedPreferencesHelper.put(getApplication(),
                SharedPreferencesHelper.WEIGHT,Float.parseFloat(weightEd.getText().toString()));

        Log.e(TAG, accountEd.getText().toString());
        Log.e(TAG, passwordEd.getText().toString());
        Log.e(TAG, ageEd.getText().toString());
        Log.e(TAG, heightEd.getText().toString());
        Log.e(TAG, weightEd.getText().toString());

        showToast("注册成功");
        startActivity(SignInActivity.class);
    }
}
