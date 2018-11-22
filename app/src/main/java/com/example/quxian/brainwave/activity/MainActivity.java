package com.example.quxian.brainwave.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

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

        initChart();
        //setViewThree();
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
        }

    }


    private LineChartData lineChartData;
    private LineChartView lineChartView;
    private List<Line> linesList;
    private List<PointValue> pointValueList;
    private List<PointValue> pointValueList1;
    private List<PointValue> points;
    private int position = 0;
    private Timer timer = new Timer();
    private boolean isFinish = true;
    private Axis axisY, axisX;
    private Random random = new Random();
    private void initChart(){
        lineChartView = (LineChartView) findById(R.id.main_act_chartview);

        pointValueList = new ArrayList<>();
        pointValueList1 = new ArrayList<>();
        linesList = new ArrayList<>();

        //初始化坐标轴
        axisY = new Axis();
        //添加坐标轴的名称
        axisY.setLineColor(Color.parseColor("#aab2bd"));
        axisY.setTextColor(Color.parseColor("#aab2bd"));
        axisX = new Axis();
        axisX.setLineColor(Color.parseColor("#aab2bd"));
        lineChartData = initDatas(null);
        lineChartView.setLineChartData(lineChartData);

        Viewport port = initViewPort(0, 50);
        lineChartView.setCurrentViewportWithAnimation(port);
        lineChartView.setInteractive(false);
        lineChartView.setScrollEnabled(true);
        lineChartView.setValueTouchEnabled(true);
        lineChartView.setFocusableInTouchMode(true);
        lineChartView.setViewportCalculationEnabled(false);
        lineChartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lineChartView.startDataAnimation();
        points = new ArrayList<>();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //实时添加新的点
                try{
                    addLine();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }, 1000, 1000);
    }

    private void addLine(){
        PointValue value1 = new PointValue(position * 5, random.nextInt(100) + 40);
        PointValue value2 = new PointValue(position * 5, random.nextInt(100) + 40);
        value1.setLabel("00:00");
        value2.setLabel("00:00");
        pointValueList.add(value1);
        pointValueList1.add(value2);

        float x = value1.getX();
        //根据新的点的集合画出新的线
        Line line = new Line(pointValueList);
        line.setColor(Color.RED);
        line.setStrokeWidth(1);
        line.setShape(ValueShape.CIRCLE);
        line.setCubic(true);//曲线是否平滑，即是曲线还是折线

        float x1 = value2.getX();
        //根据新的点的集合画出新的线
        Line line1 = new Line(pointValueList1);
        line1.setColor(Color.BLUE);
        line1.setStrokeWidth(1);
        line1.setShape(ValueShape.CIRCLE);
        line1.setCubic(true);//曲线是否平滑，即是曲线还是折线

        linesList.clear();
        linesList.add(line);
        linesList.add(line1);
        lineChartData = initDatas(linesList);
        lineChartView.setLineChartData(lineChartData);
        //根据点的横坐实时变幻坐标的视图范围
        Viewport port;
        if (x > 50 || x1 > 50) {
            port = initViewPort(x - 50, x);
        } else {
            port = initViewPort(0, 50);
        }

        lineChartView.setCurrentViewport(port);//当前窗口

        Viewport maPort = initMaxViewPort(Math.max(x,x1));
        lineChartView.setMaximumViewport(maPort);//最大窗口
        position++;
    }

    private LineChartData initDatas(List<Line> lines) {
        LineChartData data = new LineChartData(lines);
        data.setAxisYLeft(axisY);
        data.setAxisXBottom(axisX);
        return data;
    }

    /**
     * 当前显示区域
     *
     * @param left
     * @param right
     * @return
     */
    private Viewport initViewPort(float left, float right) {
        Viewport port = new Viewport();
        port.top = 150;
        port.bottom = 0;
        port.left = left;
        port.right = right;
        return port;
    }

    /**
     * 最大显示区域
     *
     * @param right
     * @return
     */
    private Viewport initMaxViewPort(float right) {
        Viewport port = new Viewport();
        port.top = 150;
        port.bottom = 0;
        port.left = 0;
        port.right = right + 50;
        return port;
    }


    //    private CanvasView canvasView;
//    private void setViewThree() {
//        canvasView = findById(R.id.main_act_chartview);
//        canvasView.setLineCount(9);
//        canvasView.setPointCount(15);
//        ArrayList<Integer> one = new ArrayList<Integer>();
//        ArrayList<Integer> two = new ArrayList<Integer>();
//        ArrayList<Integer> three = new ArrayList<Integer>();
//        for (int i = 0; i < 15; i++) {
//            Random rand = new Random();
//            one.add(rand.nextInt(9));
//            two.add(rand.nextInt(9));
//            three.add(rand.nextInt(9));
//        }
//        canvasView.setData(one, two);
//    }


//    private String[] mChartItems = new String[]{"6:00", "8:00", "10:00", "12:00", "14:00", "16:00", "18:00"};
//    private int[] mWeekPoints = new int[]{100, 150, 80, 40, 90, 50, 150};
//    private List<LineChartData> dataList1 = new ArrayList<>();
//    //x轴坐标对应的数据
//    private List<String> xValue = new ArrayList<>();
//    //y轴坐标对应的数据
//    private List<Integer> yValue = new ArrayList<>();
//    //折线对应的数据
//    private Map<String, Integer> value = new HashMap<>();
//    private Map<String, Integer> value1 = new HashMap<>();
//    private void initChart() {
//        for (int i = 0; i < 12; i++) {
//            xValue.add((i + 1) + "月");
//            value.put((i + 1) + "月", (int) (Math.random() * 181 + 60));//60--240
//            value1.put((i + 1) + "月", (int) (Math.random() * 181 + 100));//60--240
//        }
//
//        for (int i = 0; i < 6; i++) {
//            yValue.add(i * 60);
//        }
//        ChartView chartView = (ChartView) findViewById(R.id.main_act_chartview);
//        chartView.setValue(value, xValue, yValue);
//        chartView.setValue(value1, xValue, yValue);
//    }

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
