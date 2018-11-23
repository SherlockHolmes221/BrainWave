package com.example.quxian.brainwave.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
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
import static com.example.quxian.brainwave.service.MusicService.PARAM_MUSIC_CURRENT_POSITION_INDEX;

public class MusicListActivity extends BaseActivity implements MusicListAdapter.OnMusicStateChangeListener{
    private TextView mNumTv;
    private ListView listView;
    private MusicListAdapter musicListAdapter;
    private MusicReceiver mMusicReceiver = new MusicReceiver();
    private List<MusicData> mMusicDatas = new ArrayList<>();
    private int beforePlayId = 0;
    private int currentPlayId = 0;
    private boolean isPlaying;

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
        musicListAdapter.setOnMusicStateChangeListener(this);
        listView.setAdapter(musicListAdapter);

        //点击
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                Bundle bundle = new Bundle();
                bundle.putInt("id",i);
                startActivity(MusicActivity.class,bundle);

//                if(myBinder != null){
//                    if(currentPlayId == i){
//                        if(myBinder.isPlaying()){
//                            myBinder.pause();
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    setBaseTitle("已暂停");
//                                }
//                            });
//                            mMusicDatas.get(i).setPlaying(false);
//                        }else {
//                            myBinder.play(i);
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    setBaseTitle("已暂停");
//                                }
//                            });
//                            mMusicDatas.get(i).setPlaying(true);
//                        }
//                    }else {
//                        mMusicDatas.get(i).setPlaying(true);
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                setBaseTitle( mMusicDatas.get(i).getMusicName());
//                            }
//                        });
//                        if(currentPlayId >= 0)
//                            mMusicDatas.get(currentPlayId).setPlaying(false);
//                        myBinder.play(i);
//                        currentPlayId = i;
//                    }
//
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            musicListAdapter.notifyDataSetChanged();
//                        }
//                    });
//                }//if
            }
        });
    }

    private void initMusicDatas() {
        MusicData musicData1 = new MusicData(R.raw.music1, R.raw.music_ic, "成全", "伦桑");
        MusicData musicData2 = new MusicData(R.raw.music2, R.raw.music_ic, "无问", "毛不易");
        mMusicDatas.add(musicData1);
        mMusicDatas.add(musicData2);

        mNumTv.setText("所有单曲，共"+mMusicDatas.size()+"首");

        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra(PARAM_MUSIC_LIST, (Serializable) mMusicDatas);
        startService(intent);
    }

    private void initMusicReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicService.ACTION_STATUS_MUSIC_PLAY);
        intentFilter.addAction(MusicService.ACTION_STATUS_MUSIC_PAUSE);
        intentFilter.addAction(MusicService.ACTION_STATUS_MUSIC_DURATION);
        intentFilter.addAction(MusicService.ACTION_STATUS_MUSIC_COMPLETE);
        /*注册本地广播*/
        LocalBroadcastManager.getInstance(this).registerReceiver(mMusicReceiver,intentFilter);
    }

    private void optMusic(final String action,int positon) {
        Intent intent = new Intent(action);
        if (action.equals(MusicService.ACTION_OPT_MUSIC_PLAY_WITH_POSITION)) {
            intent.putExtra(PARAM_MUSIC_CURRENT_POSITION_INDEX,positon);
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public class MusicReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(MusicService.ACTION_STATUS_MUSIC_PLAY)) {
                beforePlayId = currentPlayId;
                currentPlayId = intent.getIntExtra(PARAM_MUSIC_CURRENT_POSITION_INDEX, 0);
                Log.e(TAG, "onReceive: ");
                isPlaying = true;
                refreshMusicView();
            } else if (action.equals(MusicService.ACTION_STATUS_MUSIC_PAUSE)) {
                isPlaying = false;
                refreshMusicView();
            } else if (action.equals(MusicService.ACTION_STATUS_MUSIC_COMPLETE)) {
                isPlaying = false;
                refreshMusicView();
            }
        }
    }

    private void refreshMusicView() {
        Log.e(TAG, "refreshMusicView: ");
        if(isPlaying){
            Log.e(TAG, "playing");
            setBaseTitle(mMusicDatas.get(currentPlayId).getMusicName());
            if(currentPlayId == beforePlayId){
                mMusicDatas.get(currentPlayId).setPlaying(true);
            }else {
                mMusicDatas.get(beforePlayId).setPlaying(false);
                mMusicDatas.get(currentPlayId).setPlaying(true);
            }
        }
        else {
            setBaseTitle("已暂停");
            mMusicDatas.get(currentPlayId).setPlaying(false);
        }
        musicListAdapter.notifyDataSetChanged();
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
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void playOrPause(int position) {
        Log.e(TAG, "playOrPause: "+currentPlayId);
        Log.e(TAG, "playOrPause: "+position);
        if(!isPlaying)
            optMusic(MusicService.ACTION_OPT_MUSIC_PLAY_WITH_POSITION,position);
        else {//playing
            if(position == currentPlayId)
                optMusic(MusicService.ACTION_OPT_MUSIC_PAUSE,position);
            else{
                Log.e(TAG, "playOrPause: ");
                optMusic(MusicService.ACTION_OPT_MUSIC_PLAY_WITH_POSITION,position);
            }
        }
    }
}
