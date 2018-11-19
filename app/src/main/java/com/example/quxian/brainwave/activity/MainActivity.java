package com.example.quxian.brainwave.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.quxian.brainwave.R;
import com.example.quxian.brainwave.base.BaseActivity;
import com.example.quxian.brainwave.utils.SaveAccountUtil;
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

    @Override
    public int bindLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBaseTitle(SaveAccountUtil.getUserBean().getAccount());

        nskAlgoSdk = new NskAlgoSdk();//集成

        checkBLE();

        initView();

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
        findById(R.id.main_act_weight_cv).setOnClickListener(this);

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
            case R.id.main_act_brain_cv:
                startActivity(BrainActivity.class);
                break;
            case R.id.main_act_weight_cv:
                startActivity(WeightActivity.class);
                break;
            case R.id.main_act_mind_cv:
                startActivity(MindDataActivity.class);
                break;
            case R.id.main_act_song_cv:
                startActivity(MusicListActivity.class);
                break;

        }

    }
}
