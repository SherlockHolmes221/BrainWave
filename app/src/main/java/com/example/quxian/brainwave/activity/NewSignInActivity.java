package com.example.quxian.brainwave.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.quxian.brainwave.R;
import com.example.quxian.brainwave.base.BaseActivity;
import com.example.quxian.brainwave.model.UserBean;
import com.example.quxian.brainwave.utils.SaveAccountUtil;
import com.rengwuxian.materialedittext.MaterialEditText;

public class NewSignInActivity extends BaseActivity{
    private ImageView iconIv;
    private MaterialEditText accountEd;
    private MaterialEditText passwordEd;
    private Button signBtn;
    private TextView registerTv;
    private TextView forgetTv;

    @Override
    public int bindLayout() {
        return R.layout.activity_sign_new;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();

        initListener();

    }

    private void initView() {
        hideToolbar();
        iconIv = findById(R.id.sign_act_icon_iv);
        accountEd = findById(R.id.sign_act_account_ed);
        passwordEd = findById(R.id.sign_act_password_ed);
        signBtn = findById(R.id.sign_act_sign_btn);
        registerTv = findById(R.id.sign_act_register_tv);
        forgetTv = findById(R.id.sign_act_forget_tv);

        //加载圆形图片
        loadCirclePic(this,R.drawable.icon,iconIv);
    }

    private void initListener() {

        signBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String account = accountEd.getText().toString();
                String password = passwordEd.getText().toString();
                //检查账号密码是否正确
                if(account.equals("")){
                    showToast("请填写账户");
                }else if(password.equals("")){
                    showToast("请填写密码");
                }else {
                    UserBean userBean = new UserBean();
                    userBean.setAccount(account);
                    userBean.setPassword(password);
                    SaveAccountUtil.setUserBean(userBean);
                    startActivity(ConnectActivity.class);
                }
            }
        });

        registerTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(RegisterActivity.class);
            }
        });

        forgetTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showToast("暂未推出");
            }
        });
    }
}
