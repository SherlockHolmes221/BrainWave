package com.example.quxian.brainwave.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.quxian.brainwave.R;
import com.example.quxian.brainwave.adapter.MusicListAdapter;
import com.example.quxian.brainwave.base.BaseActivity;
import com.example.quxian.brainwave.model.MusicData;
import com.example.quxian.brainwave.service.MusicService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.example.quxian.brainwave.activity.MusicActivity.PARAM_MUSIC_LIST;

public class MusicListActivity extends BaseActivity{
    ServiceConnection serviceConnection = null;
    MusicService.MyBinder myBinder;


    private TextView mNumTv;
    private ListView listView;
    private MusicListAdapter musicListAdapter;
    private List<MusicData> mMusicDatas = new ArrayList<>();
    private int currentPlayId = -1;

    private static final String TAG = "MusicListActivity";

    @Override
    public int bindLayout() {
        return R.layout.activity_music_list;
    }

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setBaseTitle("当前无歌曲播放");

        mNumTv = findById(R.id.act_music_list_num_tv);
        listView = findById(R.id.act_music_list_list);

        initMusicDatas();
        initMusicReceiver();

        musicListAdapter = new MusicListAdapter(this,mMusicDatas);
        listView.setAdapter(musicListAdapter);

        //点击
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {

                if(myBinder != null){

                    if(currentPlayId == i){
                        if(myBinder.isPlaying()){
                            myBinder.pause();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setBaseTitle("已暂停");
                                }
                            });
                            mMusicDatas.get(i).setPlaying(false);
                        }else {
                            myBinder.play(i);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setBaseTitle("已暂停");
                                }
                            });
                            mMusicDatas.get(i).setPlaying(true);
                        }
                    }else {
                        mMusicDatas.get(i).setPlaying(true);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setBaseTitle( mMusicDatas.get(i).getMusicName());
                            }
                        });
                        if(currentPlayId >= 0)
                            mMusicDatas.get(currentPlayId).setPlaying(false);
                        myBinder.play(i);
                        currentPlayId = i;
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            musicListAdapter.notifyDataSetChanged();
                        }
                    });
                }//if
            }
        });
    }

    private void initMusicDatas() {
//        MusicData musicData1 = new MusicData(R.raw.music1, R.raw.ic_music1, "コネクト", "ClariS");
//        MusicData musicData2 = new MusicData(R.raw.music2, R.raw.ic_music2, "朋友关系", "龚子婕JessieG");
//        MusicData musicData3 = new MusicData(R.raw.music3, R.raw.ic_music3, "烟熏妆", "邓紫棋");
//
//        mMusicDatas.add(musicData1);
//        mMusicDatas.add(musicData2);
//        mMusicDatas.add(musicData3);


        mNumTv.setText("所有单曲，共"+mMusicDatas.size()+"首");

        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra(PARAM_MUSIC_LIST, (Serializable) mMusicDatas);
        startService(intent);

        if(serviceConnection == null) {
            serviceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                    myBinder = (MusicService.MyBinder) iBinder;
                }

                @Override
                public void onServiceDisconnected(ComponentName componentName) {

                }
            };
        }

        //以绑定方式连接服务
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

    }

    private void initMusicReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicService.ACTION_STATUS_MUSIC_PLAY);
        intentFilter.addAction(MusicService.ACTION_STATUS_MUSIC_PAUSE);
        intentFilter.addAction(MusicService.ACTION_STATUS_MUSIC_DURATION);
        intentFilter.addAction(MusicService.ACTION_STATUS_MUSIC_COMPLETE);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        serviceConnection = null;
        myBinder = null;
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
