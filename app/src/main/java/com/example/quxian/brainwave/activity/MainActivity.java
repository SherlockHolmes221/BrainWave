package com.example.quxian.brainwave.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quxian.brainwave.R;
import com.example.quxian.brainwave.adapter.MusicListAdapter;
import com.example.quxian.brainwave.base.BaseActivity;
import com.example.quxian.brainwave.model.MusicData;
import com.example.quxian.brainwave.service.MusicService;
import com.example.quxian.brainwave.utils.SaveAccountUtil;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.neurosky.AlgoSdk.NskAlgoDataType;
import com.neurosky.AlgoSdk.NskAlgoSdk;
import com.neurosky.connection.ConnectionStates;
import com.neurosky.connection.DataType.MindDataType;
import com.neurosky.connection.TgStreamHandler;
import com.neurosky.connection.TgStreamReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.quxian.brainwave.activity.MusicActivity.PARAM_MUSIC_LIST;

public class MainActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "MainActivity";

    private TgStreamReader tgStreamReader;
    private BluetoothAdapter mBluetoothAdapter;

    private NskAlgoSdk nskAlgoSdk;

    // canned data variables
    private short raw_data[] = {0};
    private int raw_data_index= 0;
    private float output_data[];
    private int output_data_count = 0;
    private int raw_data_sec_len = 85;

    // internal variables
    private boolean bInited = false;
    private boolean bRunning = false;

    private LineChart lineChart;

    private MusicReceiver mMusicReceiver = new MusicReceiver();
    private List<MusicData> mMusicDatas = new ArrayList<>();

    private ImageView musicPlayIv;
    private ImageView musicNextIv;
    private ImageView musicLastIv;
    private TextView mucisTv;
    private CircleImageView musicIv;

    private int currentMusicPosition = 0;
    private boolean isPlaying;

    @Override
    public int bindLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setBaseTitle(SaveAccountUtil.getUserBean().getAccount());

        nskAlgoSdk = new NskAlgoSdk();//集成

        checkBLE();

        initView();
        initChart();

        initMusicDatas();
        initMusicReceiver();

        //connect();
    }

    private void checkBLE() {
        try {
            // (1) Make sure that the device supports Bluetooth and Bluetooth is on
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                Toast.makeText(
                        this,
                        "Please enable your Bluetooth and re-run this program !",
                        Toast.LENGTH_LONG).show();
                //finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast(R.string.ble_not_supported);
            return;
        }
    }


    private void initView() {
        findById(R.id.main_act_song_cv).setOnClickListener(this);
        findById(R.id.main_act_brain_cv).setOnClickListener(this);
        findById(R.id.main_act_mind_cv).setOnClickListener(this);

        findById(R.id.main_act_music_ly).setOnClickListener(this);
        findById(R.id.main_act_brain_ly).setOnClickListener(this);
        findById(R.id.main_act_mind_ly).setOnClickListener(this);
        findById(R.id.main_act_faq_ly).setOnClickListener(this);

        lineChart = findById(R.id.main_act_linechart);

        mucisTv = findById(R.id.main_act_music_tv);
        musicIv = findById(R.id.main_act_music_iv);
        musicLastIv = findById(R.id.main_act_music_last);
        musicNextIv = findById(R.id.main_act_music_next);
        musicPlayIv = findById(R.id.main_act_music_play);

        musicLastIv.setOnClickListener(this);
        musicNextIv.setOnClickListener(this);
        musicPlayIv.setOnClickListener(this);
    }

    private void connect() {

        //清空和初始化数据
        output_data_count = 0;
        output_data = null;

        raw_data = new short[512];
        raw_data_index = 0;

        // Example of constructor public TgStreamReader(BluetoothAdapter ba, TgStreamHandler tgStreamHandler)
        tgStreamReader = new TgStreamReader(mBluetoothAdapter,callback);

        if(tgStreamReader != null && tgStreamReader.isBTConnected()){

            // Prepare for connecting
            tgStreamReader.stop();
            tgStreamReader.close();
        }

        // (4) Demo of  using connect() and start() to replace connectAndStart(),
        // please call start() when the state is changed to STATE_CONNECTED
        tgStreamReader.connect();
    }

    //接收数据
    private TgStreamHandler callback = new TgStreamHandler() {

        @Override
        public void onStatesChanged(int connectionStates) {
            // TODO Auto-generated method stub
            Log.e(TAG, "connectionStates change to: " + connectionStates);
            switch (connectionStates) {
                case ConnectionStates.STATE_CONNECTING:
                    // Do something when connecting
                    break;
                case ConnectionStates.STATE_CONNECTED:
                    // Do something when connected
                    tgStreamReader.start();
                    showToast("已连接蓝牙");
                    break;
                case ConnectionStates.STATE_WORKING:
                    // Do something when working
                    //(9) demo of recording raw data , stop() will call stopRecordRawData,
                    //or you can add a button to control it.
                    //You can change the save path by calling setRecordStreamFilePath(String filePath) before startRecordRawData
                    //tgStreamReader.startRecordRawData();

                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            //???
                            //可以开始start
//                            Button startButton = (Button) findViewById(R.id.startButton);
//                            startButton.setEnabled(true);
                        }

                    });

                    break;
                case ConnectionStates.STATE_GET_DATA_TIME_OUT:
                    // Do something when getting data timeout

                    //(9) demo of recording raw data, exception handling
                    //tgStreamReader.stopRecordRawData();

                    showToast("连接超时");

                    if (tgStreamReader != null && tgStreamReader.isBTConnected()) {
                        tgStreamReader.stop();
                        tgStreamReader.close();
                    }

                    break;
                case ConnectionStates.STATE_STOPPED:
                    // Do something when stopped
                    // We have to call tgStreamReader.stop() and tgStreamReader.close() much more than
                    // tgStreamReader.connectAndstart(), because we have to prepare for that.

                    break;
                case ConnectionStates.STATE_DISCONNECTED:
                    // Do something when disconnected
                    break;
                case ConnectionStates.STATE_ERROR:
                    // Do something when you get error message
                    break;
                case ConnectionStates.STATE_FAILED:
                    // Do something when you get failed message
                    // It always happens when open the BluetoothSocket error or timeout
                    // Maybe the device is not working normal.
                    // Maybe you have to try again
                    break;
            }
        }

        @Override
        public void onRecordFail(int flag) {
            // You can handle the record error message here
            Log.e(TAG,"onRecordFail: " +flag);

        }

        @Override
        public void onChecksumFail(byte[] payload, int length, int checksum) {
            // You can handle the bad packets here.
        }

        @Override
        public void onDataReceived(int datatype, int data, Object obj) {
            // You can handle the received data here
            // You can feed the raw data to algo sdk here if necessary.
            Log.e(TAG,"onDataReceived");
            switch (datatype) {
                case MindDataType.CODE_ATTENTION:
                    Log.e(TAG,"CODE_ATTENTION");
                    short[] attValue = {(short)data};

//                    //将数据写入SD卡
//                    try {
//                        writeToSD.saveToSDCard("ATTENTION",attValue);
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }

                    nskAlgoSdk.NskAlgoDataStream(NskAlgoDataType.NSK_ALGO_DATA_TYPE_ATT.value, attValue, 1);
                    break;

                case MindDataType.CODE_MEDITATION:
                    Log.e(TAG,"CODE_MEDITATION");
                    short medValue[] = {(short)data};

//                    //将数据写入SD卡
//                    try {
//                        writeToSD.saveToSDCard("MEDITATION",medValue);
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }

                    nskAlgoSdk.NskAlgoDataStream(NskAlgoDataType.NSK_ALGO_DATA_TYPE_MED.value, medValue, 1);
                    break;

                case MindDataType.CODE_POOR_SIGNAL:
                    Log.e(TAG,"CODE_POOR_SIGNAL");
                    short pqValue[] = {(short)data};
                    //将数据写入SD卡
//                    try {
//                        writeToSD.saveToSDCard("POOR_SIGNAL",pqValue);
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }

                    nskAlgoSdk.NskAlgoDataStream(NskAlgoDataType.NSK_ALGO_DATA_TYPE_PQ.value, pqValue, 1);
                    break;

                case MindDataType.CODE_RAW:
                    Log.e(TAG,"CODE_RAW");
                    raw_data[raw_data_index++] = (short)data;

//                    //将数据写入SD卡
//                    try {
//                        writeToSD.saveToSDCard("CODE_RAW",raw_data);
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }

                    if (raw_data_index == 512) {
                        nskAlgoSdk.NskAlgoDataStream(NskAlgoDataType.NSK_ALGO_DATA_TYPE_EEG.value, raw_data, raw_data_index);
                        raw_data_index = 0;
                    }
                    break;
                default:
                    break;
            }
        }

    };

    private void canData(){
        output_data_count = 0;
        output_data = null;

        System.gc();

//        headsetButton.setEnabled(false);
//        cannedButton.setEnabled(false);

        AssetManager assetManager = getAssets();
        InputStream inputStream = null;

        Log.e(TAG, "Reading output data");
        try {
            int j;
            // check the output count first
            inputStream = assetManager.open("output_data.bin");
            output_data_count = 0;
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            try {
                String line = reader.readLine();
                while (!(line == null || line.isEmpty())) {
                    output_data_count++;
                    line = reader.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            inputStream.close();

            if (output_data_count > 0) {
                inputStream = assetManager.open("output_data.bin");
                output_data = new float[output_data_count];
                //ap = new float[output_data_count];
                j = 0;
                reader = new BufferedReader(new InputStreamReader(inputStream));
                try {
                    String line = reader.readLine();
                    while (j < output_data_count) {
                        output_data[j++] = Float.parseFloat(line);
                        line = reader.readLine();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.e(TAG, "Reading raw data");
        try {
            inputStream = assetManager.open("raw_data_em.bin");
            raw_data = readData(inputStream, 512*raw_data_sec_len);
            raw_data_index = 512*raw_data_sec_len;
            inputStream.close();
            nskAlgoSdk.NskAlgoDataStream(NskAlgoDataType.NSK_ALGO_DATA_TYPE_BULK_EEG.value, raw_data, 512 * raw_data_sec_len);
        } catch (IOException e) {

        }
        Log.e(TAG, "Finished reading data");
    }

    private short[] readData(InputStream is, int size) {
        short data[] = new short[size];
        int lineCount = 0;
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        try {
            while (lineCount < size) {
                String line = reader.readLine();
                if (line == null || line.isEmpty()) {
                    Log.d(TAG, "lineCount=" + lineCount);
                    break;
                }
                data[lineCount] = Short.parseShort(line);
                lineCount++;
            }
            Log.d(TAG, "lineCount=" + lineCount);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    private void start(){
        nskAlgoSdk.NskAlgoStop();
    }

    private void stop(){
        if (bRunning == false) {
            nskAlgoSdk.NskAlgoStart(false);
        } else {
            nskAlgoSdk.NskAlgoPause();
        }
    }


    @Override
    public void onBackPressed() {
        nskAlgoSdk.NskAlgoUninit();
        finish();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.main_act_brain_ly:
                startActivity(BrainActivity.class);
                break;
            case R.id.main_act_mind_ly:
                startActivity(MindDataActivity.class);
                break;
            case R.id.main_act_music_ly:
                startActivity(MusicListActivity.class);
                break;
            case R.id.main_act_faq_ly:
                startActivity(FAQActivity.class);
                break;
            case R.id.main_act_music_last:
                optMusic(MusicService.ACTION_OPT_MUSIC_LAST);
                break;
            case R.id.main_act_music_play:
                if(!isPlaying)
                    optMusic(MusicService.ACTION_OPT_MUSIC_PLAY);
                else
                    optMusic(MusicService.ACTION_OPT_MUSIC_PAUSE);
                break;
            case R.id.main_act_music_next:
                optMusic(MusicService.ACTION_OPT_MUSIC_NEXT);
                break;
        }
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


    private void initChart() {
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(10);
        xAxis.setLabelCount(10, false);

        lineChart.getAxisRight().setEnabled(false);

        //获得 YAxis 类实例
        YAxis leftAxis = lineChart.getAxisLeft();

        // 设置y轴数据的位置
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMinimum(0);
        leftAxis.setAxisMaximum(60);
        leftAxis.setLabelCount(3, false);

        // 不显示图例
        Legend legend = lineChart.getLegend();
        legend.setEnabled(false);
        lineChart.invalidate();

        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            float val = (float) (Math.random() * 40) + 3;
            entries.add(new Entry(i, val));
        }
        List<Entry> entries1 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            float val = (float) (Math.random() * 40) + 3;
            entries1.add(new Entry(i, val));
        }
        setChartData(lineChart,entries,entries1);
    }

    public static void setChartData(LineChart chart, List<Entry> values, List<Entry> values1) {
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();

        LineDataSet lineDataSet;
        lineDataSet = new LineDataSet(values, "");
        // 设置曲线颜色
        lineDataSet.setColor(Color.GREEN);
        // 设置平滑曲线
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        // 不显示坐标点的小圆点
        lineDataSet.setDrawCircles(false);
        // 不显示坐标点的数据
        lineDataSet.setDrawValues(false);
        // 不显示定位线
        lineDataSet.setHighlightEnabled(false);

        LineDataSet lineDataSet1;
        lineDataSet1 = new LineDataSet(values1, "");
        // 设置曲线颜色
        lineDataSet1.setColor(Color.YELLOW);
        // 设置平滑曲线
        lineDataSet1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        // 不显示坐标点的小圆点
        lineDataSet1.setDrawCircles(false);
        // 不显示坐标点的数据
        lineDataSet1.setDrawValues(false);
        // 不显示定位线
        lineDataSet1.setHighlightEnabled(false);

        dataSets.add(lineDataSet);

        dataSets.add(lineDataSet1);

        LineData data = new LineData(dataSets);
        chart.setData(data);
        chart.invalidate();
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

    private void initMusicDatas() {
        MusicData musicData1 = new MusicData(R.raw.music1, R.raw.music_ic, "成全", "伦桑");
        MusicData musicData2 = new MusicData(R.raw.music2, R.raw.music_ic, "无问", "毛不易");
        mMusicDatas.add(musicData1);
        mMusicDatas.add(musicData2);

        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra(PARAM_MUSIC_LIST, (Serializable) mMusicDatas);
        startService(intent);

        refreshMusicView();
    }

    private void refreshMusicView() {
        mucisTv.setText(mMusicDatas.get(currentMusicPosition).getMusicName());
        musicIv.setImageResource(mMusicDatas.get(currentMusicPosition).getMusicPicRes());
        if(isPlaying){
            musicPlayIv.setImageResource(R.drawable.ic_pause);
        }else {
            musicPlayIv.setImageResource(R.drawable.ic_play);
        }
    }

    private void optMusic(final String action) {
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(action));
    }


    public class MusicReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(MusicService.ACTION_STATUS_MUSIC_PLAY)) {
                currentMusicPosition = intent.getIntExtra(MusicService.PARAM_MUSIC_CURRENT_POSITION_INDEX, 0);
                Log.e(TAG, "onReceive: "+ currentMusicPosition);
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
}
