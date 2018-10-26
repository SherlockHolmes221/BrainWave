package com.example.quxian.brainwave.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import com.example.quxian.brainwave.R;
import com.example.quxian.brainwave.base.BaseActivity;
import com.rengwuxian.materialedittext.MaterialEditText;

public class SignInActivity extends BaseActivity {


    private MaterialEditText heightEd;
    private MaterialEditText weightEd;
    private MaterialEditText ageEd;
    private Button signInBtn;

    private double height = 0.0;
    private double weight = 0.0;
    private int age = 0 ;

    @Override
    public int bindLayout() {
        return R.layout.activity_signin;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hideToolbar();

        //绑定控件
        heightEd = findById(R.id.signin_act_height_ed);
        weightEd = findById(R.id.signin_act_weight_ed);
        ageEd = findById(R.id.signin_act_age_ed);
        signInBtn = findById(R.id.signin_act_sign_btn);

        //获取数据
        SharedPreferences preferences = getSharedPreferences("data",MODE_PRIVATE);
        String h = preferences.getString("height","") ;
        String w = preferences.getString("weight","") ;
        int a = preferences.getInt("age",0) ;

        if(!h.equals("")) {
            heightEd.setText(h);
            height = Double.parseDouble(h);
        }
        if(!w.equals("")) {
            weightEd.setText(w);
            weight = Double.parseDouble(w);
        }
        if(a > 0) {
            ageEd.setText(String.valueOf(a));
            age = a;
        }

        //检测身高数据合法性
        heightEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    height =Double.parseDouble(heightEd.getText().toString());
                    //合法性检查
                    if(height < 0.0){
                        heightEd.setError("请输入合法身高数据");
                        height = 0.0;
                    }
                }catch (Exception e){
                    height = 0.0;
                    if(!heightEd.getText().toString().equals(""))
                        heightEd.setError("请输入合法身高数据");
                }
            }
        });

        //检测体重数据合法性
        weightEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    weight =Double.parseDouble(weightEd.getText().toString());
                    //合法性检查
                    if(weight < 0.0){
                        weightEd.setError("请输入合法体重数据");
                        weight = 0.0;
                    }
                }catch (Exception e){
                    weight = 0;
                    if(!weightEd.getText().toString().equals(""))
                        weightEd.setError("请输入合法体重数据");
                }
            }
        });

        //检测年龄数据合法性
        ageEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    age =Integer.parseInt(ageEd.getText().toString());
                    //合法性检查
                    if(age <= 0 || age > 110){
                        ageEd.setError("请输入合法年龄数据");
                        age = 0;
                    }
                }catch (Exception e){
                    age = 0;
                    if(!ageEd.getText().toString().equals(""))
                        ageEd.setError("请输入合法年龄数据");
                }
            }
        });


        //跳转到蓝牙连接页面
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(height > 0 && weight > 0 && age >0){
                    //保存数据
                    SharedPreferences.Editor editor = getSharedPreferences(
                            "data",MODE_PRIVATE).edit();
                    editor.putInt("age",age);
                    editor.putString("height", String.valueOf(height));
                    editor.putString("weight", String.valueOf(weight));
                    editor.apply();

                    showToast("登录成功");
                    startActivity(ConnectActivity.class);
                }else
                    showToast("请填写完整正确的基本信息");
            }
        });

    }
}
