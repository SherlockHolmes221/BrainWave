package com.example.quxian.brainwave.base;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.example.quxian.brainwave.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 定义一些通用方法的BaseActivity
 */

public abstract class BaseActivity extends AppCompatActivity {

    private AppBarLayout appBar;
    private TextView tvTitle;

    protected View mContextView;
    private static final int PERMISSION_REQUESTCODE = 100;
    private static PermissionListener mListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        Toolbar toolbar = findById(R.id.base_act_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        appBar = findById(R.id.base_act_appbar);
        tvTitle = findById(R.id.base_act_tv_title);
        FrameLayout frameLayout = findById(R.id.base_act_frame);
        mContextView = LayoutInflater.from(this).inflate(bindLayout(), null);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        frameLayout.addView(mContextView, params);
    }

    //返回键
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //权限调用结果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUESTCODE)
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //用户同意授权
                mListener.onGranted();
            } else {
                new AlertDialog.Builder(this)
                        .setMessage("需要开启权限才能使用此功能")
                        .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //引导用户到设置中去进行设置
                                Intent intent = new Intent();
                                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                intent.setData(Uri.fromParts("package", getPackageName(), null));
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //用户拒绝授权
                                mListener.onDenied();
                            }
                        })
                        .create()
                        .show();
            }
    }

    //隐藏标题栏
    protected void hideToolbar() {
        appBar.setVisibility(View.GONE);
    }

    //fbi简化findViewById
    protected <T extends View> T findById(int resId) {
        return (T) super.findViewById(resId);
    }

    //弹出提示
    protected void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    protected void showToast(int id) {
        Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
    }

    //设置layout
    public abstract int bindLayout();

    //设置标题
    protected void setBaseTitle(String title) {
        tvTitle.setText(title);
    }

    //启动activity
    public void startActivity(Class<?> clz) {
        startActivity(new Intent(BaseActivity.this, clz));
    }

    //带bundle的启动activity
    public void startActivity(Class<?> clz, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    //带返回结果的启动activity
    public void startActivityForResult(Class<?> cls, Bundle bundle,
                                       int requestCode) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }


    //动态权限
    public void requestRunPermission(String[] permissions, PermissionListener listener) {
        mListener = listener;
        List<String> requestedPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                requestedPermissions.add(permission);
            }
        }
        if (requestedPermissions.size() != 0) {
            ActivityCompat.requestPermissions(this,
                    requestedPermissions.toArray(new String[requestedPermissions.size()])
                    , PERMISSION_REQUESTCODE);
        } else {
            // 都已经授权
            mListener.onGranted();
        }
    }

    //加载圆形图片
    public static void loadCirclePic(final Context context, int resourceId, final ImageView imageView) {
        Glide.with(context)
                .load(resourceId)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL) //设置缓存
                .into(new BitmapImageViewTarget(imageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        imageView.setImageDrawable(circularBitmapDrawable);
                    }
                });

    }

}