package com.example.quxian.brainwave.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.example.quxian.brainwave.R;
import com.neurosky.AlgoSdk.NskAlgoDataType;
import com.neurosky.AlgoSdk.NskAlgoSdk;
import com.neurosky.AlgoSdk.NskAlgoSignalQuality;
import com.neurosky.AlgoSdk.NskAlgoState;
import com.neurosky.AlgoSdk.NskAlgoType;
import com.neurosky.connection.ConnectionStates;
import com.neurosky.connection.DataType.MindDataType;
import com.neurosky.connection.TgStreamHandler;
import com.neurosky.connection.TgStreamReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class NewBrainActivity extends Activity {

    final String TAG = "MainActivityTag";
    /*static {
        System.loadLibrary("NskAlgoAndroid");
    }*/

    // graph plot variables
    private final static int X_RANGE = 50;

    private SimpleXYSeries bp_deltaSeries = null;
    private SimpleXYSeries bp_thetaSeries = null;
    private SimpleXYSeries bp_alphaSeries = null;
    private SimpleXYSeries bp_betaSeries = null;
    private SimpleXYSeries bp_gammaSeries = null;

    // COMM SDK handles
    private TgStreamReader tgStreamReader;  //集成
    private BluetoothAdapter mBluetoothAdapter;//集成

    // internal variables
    private boolean bInited = false;
    private boolean bRunning = false;
    private NskAlgoType currentSelectedAlgo;

    // canned data variables
    private short raw_data[] = {0};
    private int raw_data_index= 0;
    private float output_data[];
    private int output_data_count = 0;
    private int raw_data_sec_len = 85;

    // UI components
    private XYPlot plot;

    private Button headsetButton;
    private Button cannedButton;
    private Button setAlgosButton;
    private Button setIntervalButton;
    private Button startButton;
    private Button stopButton;

    private SeekBar intervalSeekBar;
    private TextView intervalText;

    private Button bpText;

    private TextView attValue;
    private TextView medValue;

    private CheckBox attCheckBox;
    private CheckBox medCheckBox;
    private CheckBox blinkCheckBox;
    private CheckBox bpCheckBox;

    private TextView stateText;
    private TextView sqText;

    private ImageView blinkImage;

    private NskAlgoSdk nskAlgoSdk;

    private int bLastOutputInterval = 1;

    //private WriteToSD writeToSD;

    private TextView stateTv;
    private TextView DnText1;
    private TextView DnText2;
    private int currentXA;
    private int currentYM;
    private boolean getDataAttention;
    private boolean getDataMeditation;
    private double d_before1;
    private double d_before2;
    private boolean isFirstGetD = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);

        nskAlgoSdk = new NskAlgoSdk();//集成

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
            Log.d(TAG, "error:" + e.getMessage());
            return;
        }

        headsetButton = (Button)this.findViewById(R.id.headsetButton);
        cannedButton = (Button)this.findViewById(R.id.cannedDataButton);
        setAlgosButton = (Button)this.findViewById(R.id.setAlgosButton);
        setIntervalButton = (Button)this.findViewById(R.id.setIntervalButton);
        startButton = (Button)this.findViewById(R.id.startButton);
        stopButton = (Button)this.findViewById(R.id.stopButton);

        intervalSeekBar = (SeekBar)this.findViewById(R.id.intervalSeekBar);
        intervalText = (TextView)this.findViewById(R.id.intervalText);

        bpText = (Button)this.findViewById(R.id.bpTitle);

        attValue = (TextView)this.findViewById(R.id.attText);
        medValue = (TextView)this.findViewById(R.id.medText);

        attCheckBox = (CheckBox)this.findViewById(R.id.attCheckBox);
        medCheckBox = (CheckBox)this.findViewById(R.id.medCheckBox);
        blinkCheckBox = (CheckBox)this.findViewById(R.id.blinkCheckBox);
        bpCheckBox = (CheckBox)this.findViewById(R.id.bpCheckBox);

        blinkImage = (ImageView)this.findViewById(R.id.blinkImage);

        stateText = (TextView)this.findViewById(R.id.stateText);
        sqText = (TextView)this.findViewById(R.id.sqText);

        //初始化，点击headsetButton开始进行蓝牙扫描
        headsetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                output_data_count = 0;
                output_data = null;

                raw_data = new short[512];
                raw_data_index = 0;

                cannedButton.setEnabled(false);
                headsetButton.setEnabled(false);
                startButton.setEnabled(false);

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
        });

        //读取输出的数据和原始数据
        cannedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                output_data_count = 0;
                output_data = null;

                System.gc();

                headsetButton.setEnabled(false);
                cannedButton.setEnabled(false);

                AssetManager assetManager = getAssets();
                InputStream inputStream = null;

                Log.d(TAG, "Reading output data");
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

                        }
                        inputStream.close();
                    }
                } catch (IOException e) {
                }

                Log.d(TAG, "Reading raw data");
                try {
                    inputStream = assetManager.open("raw_data_em.bin");
                    raw_data = readData(inputStream, 512*raw_data_sec_len);
                    raw_data_index = 512*raw_data_sec_len;
                    inputStream.close();
                    nskAlgoSdk.NskAlgoDataStream(NskAlgoDataType.NSK_ALGO_DATA_TYPE_BULK_EEG.value, raw_data, 512 * raw_data_sec_len);
                } catch (IOException e) {

                }
                Log.d(TAG, "Finished reading data");
            }
        });

        //开始按钮，开始nskAlgoSdk
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bRunning == false) {
                    nskAlgoSdk.NskAlgoStart(false);
                } else {
                    nskAlgoSdk.NskAlgoPause();
                }
            }
        });

        //停止按钮，结束nskAlgoSdk
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nskAlgoSdk.NskAlgoStop();
            }
        });

        setAlgosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check selected algos
                int algoTypes = 0;// = NskAlgoType.NSK_ALGO_TYPE_CR.value;

                startButton.setEnabled(false);
                stopButton.setEnabled(false);
                //清理所有的图像
                clearAllSeries();
//                text.setVisibility(View.INVISIBLE);
//                text.setText("");

                bpText.setEnabled(false);

                //清空数据吗？？
                currentSelectedAlgo = NskAlgoType.NSK_ALGO_TYPE_INVALID;
                intervalSeekBar.setEnabled(false);
                setIntervalButton.setEnabled(false);
                intervalText.setText("--");

                attValue.setText("--");
                medValue.setText("--");

                stateText.setText("");
                sqText.setText("");

                if (medCheckBox.isChecked()) {
                    algoTypes += NskAlgoType.NSK_ALGO_TYPE_MED.value;
                }
                if (attCheckBox.isChecked()) {
                    algoTypes += NskAlgoType.NSK_ALGO_TYPE_ATT.value;
                }
                if (blinkCheckBox.isChecked()) {
                    algoTypes += NskAlgoType.NSK_ALGO_TYPE_BLINK.value;
                }
                if (bpCheckBox.isChecked()) {
                    algoTypes += NskAlgoType.NSK_ALGO_TYPE_BP.value;
                    bpText.setEnabled(true);
                    bp_deltaSeries = createSeries("Delta");
                    bp_thetaSeries = createSeries("Theta");
                    bp_alphaSeries = createSeries("Alpha");
                    bp_betaSeries = createSeries("Beta");
                    bp_gammaSeries = createSeries("Gamma");
                }


                if (algoTypes == 0) {
                    showDialog("Please select at least one algorithm");
                } else {
                    if (bInited) {
                        nskAlgoSdk.NskAlgoUninit();
                        bInited = false;
                    }
                    int ret = nskAlgoSdk.NskAlgoInit(algoTypes, getFilesDir().getAbsolutePath());
                    if (ret == 0) {
                        bInited = true;
                    }

                    Log.d(TAG, "NSK_ALGO_Init() " + ret);
                    String sdkVersion = "SDK ver.: " + nskAlgoSdk.NskAlgoSdkVersion();

                    if ((algoTypes & NskAlgoType.NSK_ALGO_TYPE_ATT.value) != 0) {
                        sdkVersion += "\nATT ver.: " + nskAlgoSdk.NskAlgoAlgoVersion(NskAlgoType.NSK_ALGO_TYPE_ATT.value);
                    }
                    if ((algoTypes & NskAlgoType.NSK_ALGO_TYPE_MED.value) != 0) {
                        sdkVersion += "\nMED ver.: " + nskAlgoSdk.NskAlgoAlgoVersion(NskAlgoType.NSK_ALGO_TYPE_MED.value);
                    }
                    if ((algoTypes & NskAlgoType.NSK_ALGO_TYPE_BLINK.value) != 0) {
                        sdkVersion += "\nBlink ver.: " + nskAlgoSdk.NskAlgoAlgoVersion(NskAlgoType.NSK_ALGO_TYPE_BLINK.value);
                    }
                    if ((algoTypes & NskAlgoType.NSK_ALGO_TYPE_BP.value) != 0) {
                        sdkVersion += "\nEEG Bandpower ver.: " + nskAlgoSdk.NskAlgoAlgoVersion(NskAlgoType.NSK_ALGO_TYPE_BP.value);
                    }
                    showToast(sdkVersion, Toast.LENGTH_LONG);
                }
            }
        });

        bpText.setEnabled(false);
        bpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeAllSeriesFromPlot();
                setupPlot(-20, 20, "EEG Bandpower");
                addSeries(plot, bp_deltaSeries,R.xml.line_point_formatter_with_plf1);
                addSeries(plot, bp_thetaSeries, R.xml.line_point_formatter_with_plf2);
                addSeries(plot, bp_alphaSeries, R.xml.line_point_formatter_with_plf3);
                addSeries(plot, bp_betaSeries, R.xml.line_point_formatter_with_plf4);
                addSeries(plot, bp_gammaSeries, R.xml.line_point_formatter_with_plf5);
                plot.redraw();

//                text.setVisibility(View.INVISIBLE);

                currentSelectedAlgo = NskAlgoType.NSK_ALGO_TYPE_BP;

                intervalSeekBar.setMax(1);
                intervalSeekBar.setProgress(0);
                intervalSeekBar.setEnabled(false);
                intervalText.setText(String.format("%d", 1));
                setIntervalButton.setEnabled(false);
            }
        });

        intervalSeekBar.setEnabled(false);
        intervalSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                bLastOutputInterval = seekBar.getProgress();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        setIntervalButton.setEnabled(false);
        setIntervalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ret = -1;
                String toastStr = "";

                if (ret == 0) {
                    showToast(toastStr + ": success", Toast.LENGTH_SHORT);
                } else {
                    showToast(toastStr + ": fail", Toast.LENGTH_SHORT);
                }
            }
        });

        //信号强度
        nskAlgoSdk.setOnSignalQualityListener(new NskAlgoSdk.OnSignalQualityListener() {
            @Override
            public void onSignalQuality(int level) {
                Log.d(TAG, "NskAlgoSignalQualityListener: level: " + level);
                final int fLevel = level;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // change UI elements here
                        String sqStr = NskAlgoSignalQuality.values()[fLevel].toString();
                        sqText.setText(sqStr);
                    }
                });
            }
        });

        //使用状态
        nskAlgoSdk.setOnStateChangeListener(new NskAlgoSdk.OnStateChangeListener() {
            @Override
            public void onStateChange(int state, int reason) {
                String stateStr = "";
                String reasonStr = "";
                for (NskAlgoState s : NskAlgoState.values()) {
                    if (s.value == state) {
                        stateStr = s.toString();
                    }
                }
                for (NskAlgoState r : NskAlgoState.values()) {
                    if (r.value == reason) {
                        reasonStr = r.toString();
                    }
                }
                Log.d(TAG, "NskAlgoSdkStateChangeListener: state: " + stateStr + ", reason: " + reasonStr);
                final String finalStateStr = stateStr + " | " + reasonStr;
                final int finalState = state;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // change UI elements here
                        stateText.setText(finalStateStr);

                        if (finalState == NskAlgoState.NSK_ALGO_STATE_RUNNING.value || finalState == NskAlgoState.NSK_ALGO_STATE_COLLECTING_BASELINE_DATA.value) {
                            bRunning = true;
                            startButton.setText("Pause");
                            startButton.setEnabled(true);
                            stopButton.setEnabled(true);
                        } else if (finalState == NskAlgoState.NSK_ALGO_STATE_STOP.value) {
                            bRunning = false;
                            raw_data = null;
                            raw_data_index = 0;
                            startButton.setText("Start");
                            startButton.setEnabled(true);
                            stopButton.setEnabled(false);

                            headsetButton.setEnabled(true);
                            cannedButton.setEnabled(true);

                            if (tgStreamReader != null && tgStreamReader.isBTConnected()) {

                                // Prepare for connecting
                                tgStreamReader.stop();
                                tgStreamReader.close();
                            }

                            output_data_count = 0;
                            output_data = null;

                            System.gc();
                        } else if (finalState == NskAlgoState.NSK_ALGO_STATE_PAUSE.value) {
                            bRunning = false;
                            startButton.setText("Start");
                            startButton.setEnabled(true);
                            stopButton.setEnabled(true);
                        } else if (finalState == NskAlgoState.NSK_ALGO_STATE_ANALYSING_BULK_DATA.value) {
                            bRunning = true;
                            startButton.setText("Start");
                            startButton.setEnabled(false);
                            stopButton.setEnabled(true);
                        } else if (finalState == NskAlgoState.NSK_ALGO_STATE_INITED.value || finalState == NskAlgoState.NSK_ALGO_STATE_UNINTIED.value) {
                            bRunning = false;
                            startButton.setText("Start");
                            startButton.setEnabled(true);
                            stopButton.setEnabled(false);
                        }
                    }
                });
            }
        });

        //信号强度
        nskAlgoSdk.setOnSignalQualityListener(new NskAlgoSdk.OnSignalQualityListener() {
            @Override
            public void onSignalQuality(final int level) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // change UI elements here
                        String sqStr = NskAlgoSignalQuality.values()[level].toString();
                        sqText.setText(sqStr);
                    }
                });
            }
        });

        //信号变化吗？？
        nskAlgoSdk.setOnBPAlgoIndexListener(new NskAlgoSdk.OnBPAlgoIndexListener() {
            @Override
            public void onBPAlgoIndex(float delta, float theta, float alpha, float beta, float gamma) {
                Log.d(TAG, "NskAlgoBPAlgoIndexListener: BP: D[" + delta + " dB] T[" + theta + " dB] A[" + alpha + " dB] B[" + beta + " dB] G[" + gamma + "]");

                final float fDelta = delta, fTheta = theta, fAlpha = alpha, fBeta = beta, fGamma = gamma;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // change UI elements here
                        AddValueToPlot(bp_deltaSeries, fDelta);
                        AddValueToPlot(bp_thetaSeries, fTheta);
                        AddValueToPlot(bp_alphaSeries, fAlpha);
                        AddValueToPlot(bp_betaSeries, fBeta);
                        AddValueToPlot(bp_gammaSeries, fGamma);
                    }
                });
            }
        });

        //专注度变化
        nskAlgoSdk.setOnAttAlgoIndexListener(new NskAlgoSdk.OnAttAlgoIndexListener() {
            @Override
            public void onAttAlgoIndex(int value) {
                Log.d(TAG, "NskAlgoAttAlgoIndexListener: Attention:" + value);
                String attStr = "[" + value + "]";
                final String finalAttStr = attStr;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // change UI elements here
                        attValue.setText(finalAttStr);
                    }
                });
            }
        });

        //放松度变化
        nskAlgoSdk.setOnMedAlgoIndexListener(new NskAlgoSdk.OnMedAlgoIndexListener() {
            @Override
            public void onMedAlgoIndex(int value) {
                Log.d(TAG, "NskAlgoMedAlgoIndexListener: Meditation:" + value);
                String medStr = "[" + value + "]";
                final String finalMedStr = medStr;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // change UI elements here
                        medValue.setText(finalMedStr);
                    }
                });
            }
        });

        //指示灯变化
        nskAlgoSdk.setOnEyeBlinkDetectionListener(new NskAlgoSdk.OnEyeBlinkDetectionListener() {
            @Override
            public void onEyeBlinkDetect(int strength) {
                Log.d(TAG, "NskAlgoEyeBlinkDetectionListener: Eye blink detected: " + strength);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        blinkImage.setImageResource(R.drawable.led_on);
                        Timer timer = new Timer();

                        timer.schedule(new TimerTask() {
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        blinkImage.setImageResource(R.drawable.led_off);
                                    }
                                });
                            }
                        }, 500);
                    }
                });
            }
        });

        // initialize our XYPlot reference:
        plot = (XYPlot) findViewById(R.id.myPlot);
        plot.setVisibility(View.INVISIBLE);
        setPlot();

        //writeToSD = new WriteToSD(this);
        //requestPermission();

        //add
        stateTv = findViewById(R.id.state);
        DnText1 = findViewById(R.id.dn1);
        DnText2 = findViewById(R.id.dn2);
    }

    private void setPlot() {
        plot.setTitle("");
        plot.setDomainLabel("");
        plot.setRangeLabel("");
    }

    private void removeAllSeriesFromPlot () {
        if (bp_deltaSeries != null) {
            plot.removeSeries(bp_deltaSeries);
        }
        if (bp_thetaSeries != null) {
            plot.removeSeries(bp_thetaSeries);
        }
        if (bp_alphaSeries != null) {
            plot.removeSeries(bp_alphaSeries);
        }
        if (bp_betaSeries != null) {
            plot.removeSeries(bp_betaSeries);
        }
        if (bp_gammaSeries != null) {
            plot.removeSeries(bp_gammaSeries);
        }
        System.gc();
    }

    private void clearAllSeries () {
        if (bp_deltaSeries != null) {
            plot.removeSeries(bp_deltaSeries);
            bp_deltaSeries = null;
        }
        if (bp_thetaSeries != null) {
            plot.removeSeries(bp_thetaSeries);
            bp_thetaSeries = null;
        }
        if (bp_alphaSeries != null) {
            plot.removeSeries(bp_alphaSeries);
            bp_alphaSeries = null;
        }
        if (bp_betaSeries != null) {
            plot.removeSeries(bp_betaSeries);
            bp_betaSeries = null;
        }
        if (bp_gammaSeries != null) {
            plot.removeSeries(bp_gammaSeries);
            bp_gammaSeries = null;
        }
        plot.setVisibility(View.INVISIBLE);
        System.gc();
    }

    private XYPlot setupPlot (Number rangeMin, Number rangeMax, String title) {
        // initialize our XYPlot reference:
        plot = (XYPlot) findViewById(R.id.myPlot);

//        plot.setDomainLeftMax(0);
//        plot.setDomainRightMin(X_RANGE);
//        plot.setDomainRightMax(X_RANGE);

        if ((rangeMax.intValue() - rangeMin.intValue()) < 10) {
            plot.setRangeStepValue((rangeMax.intValue() - rangeMin.intValue() + 1));
        } else {
            plot.setRangeStepValue(11);
        }
        plot.setRangeBoundaries(rangeMin.intValue(), rangeMax.intValue(), BoundaryMode.FIXED);

//        plot.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);
//
//        plot.setTicksPerDomainLabel(10);
//        plot.getGraphWidget().setDomainLabelOrientation(-45);

        plot.setPlotPadding(0, 0, 0, 0);
        plot.setTitle(title);

        plot.setVisibility(View.VISIBLE);

        return plot;
    }

    private SimpleXYSeries createSeries (String seriesName) {
        // Turn the above arrays into XYSeries':
        SimpleXYSeries series = new SimpleXYSeries(
                null,          // SimpleXYSeries takes a List so turn our array into a List
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, // Y_VALS_ONLY means use the element index as the x value
                seriesName);                             // Set the display title of the series

        series.useImplicitXVals();

        return series;
    }

    //绘图函数
    private SimpleXYSeries addSeries (XYPlot plot, SimpleXYSeries series, int formatterId) {
        // Create a formatter to use for drawing a series using LineAndPointRenderer
        // and configure it from xml:
//        LineAndPointFormatter seriesFormat = new LineAndPointFormatter(Color.rgb(0, 200, 0), // line color
//                Color.rgb(0, 100, 0), // point color
//                Color.rgb(150, 190, 150),new PointLabelFormatter( Color.rgb(150, 190, 150))); // fill color (optional)
        LineAndPointFormatter seriesFormat = new LineAndPointFormatter();
        seriesFormat.setPointLabelFormatter(null);

        seriesFormat.configure(getApplicationContext(), formatterId);
        seriesFormat.setVertexPaint(null);
        series.useImplicitXVals();
        // add a new series' to the xyplot:
        plot.addSeries(series, seriesFormat);
        return series;
    }

    private int gcCount = 0;
    private void AddValueToPlot (SimpleXYSeries series, float value) {
        if (series.size() >= X_RANGE) {
            series.removeFirst();
        }
        Number num = value;
        series.addLast(null, num);
        plot.redraw();
        gcCount++;
        if (gcCount >= 20) {
            System.gc();
            gcCount = 0;
        }
    }

    private short [] readData(InputStream is, int size) {
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

        }
        return data;
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

    public static String Datetime()
    {
        Calendar c = Calendar.getInstance();

        String sDate = "[" + c.get(Calendar.YEAR) + "/"
                + (c.get(Calendar.MONTH)+1)
                + "/" + c.get(Calendar.DAY_OF_MONTH)
                + " " + c.get(Calendar.HOUR_OF_DAY)
                + ":" + String.format("%02d", c.get(Calendar.MINUTE))
                + ":" + String.format("%02d", c.get(Calendar.SECOND)) + "]";
        return sDate;
    }

    private TgStreamHandler callback = new TgStreamHandler() {

        @Override
        public void onStatesChanged(int connectionStates) {
            // TODO Auto-generated method stub
            Log.d(TAG, "connectionStates change to: " + connectionStates);
            switch (connectionStates) {
                case ConnectionStates.STATE_CONNECTING:
                    // Do something when connecting
                    break;
                case ConnectionStates.STATE_CONNECTED:
                    // Do something when connected
                    tgStreamReader.start();
                    showToast("Connected", Toast.LENGTH_SHORT);
                    break;
                case ConnectionStates.STATE_WORKING:
                    // Do something when working

                    //(9) demo of recording raw data , stop() will call stopRecordRawData,
                    //or you can add a button to control it.
                    //You can change the save path by calling setRecordStreamFilePath(String filePath) before startRecordRawData
                    //tgStreamReader.startRecordRawData();

                    NewBrainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Button startButton = (Button) findViewById(R.id.startButton);
                            startButton.setEnabled(true);
                        }

                    });

                    break;
                case ConnectionStates.STATE_GET_DATA_TIME_OUT:
                    // Do something when getting data timeout

                    //(9) demo of recording raw data, exception handling
                    //tgStreamReader.stopRecordRawData();

                    showToast("Get data time out!", Toast.LENGTH_SHORT);

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
            Log.d(TAG,"onRecordFail: " +flag);

        }

        @Override
        public void onChecksumFail(byte[] payload, int length, int checksum) {
            // You can handle the bad packets here.
        }

        @Override
        public void onDataReceived(int datatype, int data, Object obj) {
                        // You can handle the received data here
            // You can feed the raw data to algo sdk here if necessary.
            Log.e(TAG,"onDataReceived data"+data);
            switch (datatype) {
                case MindDataType.CODE_ATTENTION:
                    Log.e(TAG,"CODE_ATTENTION");
                    short[] attValue = {(short)data};

                    for(short a : attValue)
                        Log.e(TAG, "onDataReceived:CODE_ATTENTION "+a);


                    Log.e(TAG, "onDataReceived:CODE_ATTENTION "+data);
                    if(data!= 0 && !getDataAttention) {
                        currentXA = attValue[0];
                        getDataAttention = true;
                    }


                    //将数据写入SD卡
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

                    for(short a : medValue)
                        Log.e(TAG, "onDataReceived:CODE_ATTENTION "+a);

                    Log.e(TAG, "onDataReceived:CODE_MEDITATION "+data);
                    if(data!= 0 && !getDataMeditation){
                        currentYM = medValue[0];
                        getDataMeditation = true;
                    }

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

                    //将数据写入SD卡
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
            }//switch

            if(getDataMeditation && getDataAttention){
                double d = ((double) currentXA/(double) currentYM)*(currentXA-currentYM);

                Log.e(TAG, "d: "+d);

                if(isFirstGetD) {
                    d_before1 = d;
                    d_before2 = d;
                    isFirstGetD = false;
                }
                else{
                    double d1 = Math.max(d,d_before1) * d;
                    double d2 = Math.max(d,d_before2) + d;
                    d_before1 = d1;
                    d_before2 = d2;
                }

                Log.e(TAG, "d_before1: "+d_before1);
                Log.e(TAG, "d_before2: "+d_before2);

                //UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DnText1.setText(String.valueOf(d_before1));
                        DnText2.setText(String.valueOf(d_before2));
                        if(d_before1 > Math.pow(10,10) ||
                                d_before2 > Math.pow(10,10)){
                            stateTv.setText("专注");
                        }else{
                            stateTv.setText("放松");
                        }
                    }
                });

                getDataMeditation = false;
                getDataAttention = false;
            }
        }

    };

    public void showToast(final String msg, final int timeStyle) {
        NewBrainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), msg, timeStyle).show();
            }

        });
    }

    private void showDialog (String message) {
        new AlertDialog.Builder(this)
                .setTitle("")
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

//    private final static int REQUEST_CODE = 1;
//    @TargetApi(Build.VERSION_CODES.M)
//    private void requestPermission() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//            new AlertDialog.Builder(this)
//                    .setMessage("申请写sd卡权限")
//                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            //申请相机权限
//                            ActivityCompat.requestPermissions(NewBrainActivity.this,
//                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
//                        }
//                    })
//                    .show();
//        }else
//            Toast.makeText(this, "获得了写sd卡权限", Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == REQUEST_CODE) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "sd卡权限已申请", Toast.LENGTH_SHORT).show();
//            } else {
//                //用户勾选了不再询问
//                //提示用户手动打开权限
//                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                    Toast.makeText(this, "sd卡权限已被禁止", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
//    }

}
