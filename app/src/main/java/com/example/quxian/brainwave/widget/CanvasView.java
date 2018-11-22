package com.example.quxian.brainwave.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.example.quxian.brainwave.R;

import java.util.ArrayList;

public class CanvasView extends View {
    int poY[];
    int poX[];
    int widthLeftRight = 50;//图标和左右变的距离
    int widthTopBottom = 50;//图标和上下边的距离
    private int widthBg, heightBg;//控件的总宽高
    private Paint mPaintLineOne, mPaintLineTwo, mPaintLineThree,mPaintLineFour,
            mPaintLineX, mPaintLineY, mPaintLineDefault, mPaintText,
            mPaintLineOneRound, mPaintLineTwoRound,mPaintLineThreeRound,mPaintLineFourRound;
    private int lineCount = 6;//虚线条数
    private int pointCount = 12;//总共点数
    ArrayList<Integer> lineTwo = new ArrayList<>();//线条2的坐标
    ArrayList<Integer> lineOne = new ArrayList<>();//线条1的坐标
    ArrayList<Integer> lineThree = new ArrayList<>();//线条3的坐标
    ArrayList<Integer> lineFour = new ArrayList<>();//线条4的坐标


    public CanvasView(Context context, AttributeSet set) {
        this(context, set, 0);
    }

    public CanvasView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.chenliuliuT, defStyleAttr, 0);
        int n = array.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = array.getIndex(i);
            switch (attr) {
                case R.styleable.chenliuliuT_lineCount:
                    // 默认颜色设置为黑色
                    lineCount = array.getInt(attr, 0);
                    break;
                case R.styleable.chenliuliuT_pointCount:
                    pointCount = array.getInt(attr, 0);
                    break;
            }
        }
        array.recycle();
        init();
    }

    private void init() {

        mPaintLineOne = new Paint();
        mPaintLineOne.setAntiAlias(true);
        mPaintLineOne.setColor(Color.BLUE);
        mPaintLineOne.setStyle(Paint.Style.STROKE);
        mPaintLineOne.setStrokeWidth(4);


        mPaintLineTwo = new Paint();
        mPaintLineTwo.setAntiAlias(true);
        mPaintLineTwo.setColor(Color.RED);
        mPaintLineTwo.setStyle(Paint.Style.STROKE);
        mPaintLineTwo.setStrokeWidth(4);

        mPaintLineThree = new Paint();
        mPaintLineThree.setAntiAlias(true);
        mPaintLineThree.setColor(Color.GREEN);
        mPaintLineThree.setStyle(Paint.Style.STROKE);
        mPaintLineThree.setStrokeWidth(4);

        mPaintLineFour = new Paint();
        mPaintLineFour.setAntiAlias(true);
        mPaintLineFour.setColor(Color.YELLOW);
        mPaintLineFour.setStyle(Paint.Style.STROKE);
        mPaintLineFour.setStrokeWidth(4);


        mPaintLineX = new Paint();
        mPaintLineX.setAntiAlias(true);
        mPaintLineX.setColor(Color.GRAY);
        mPaintLineX.setStyle(Paint.Style.STROKE);
        mPaintLineX.setStrokeWidth(3);
        mPaintLineX.setStyle(Paint.Style.FILL);

        mPaintLineY = new Paint();
        mPaintLineY.setAntiAlias(true);
        mPaintLineY.setColor(Color.GRAY);
        mPaintLineY.setStyle(Paint.Style.STROKE);
        mPaintLineY.setStrokeWidth(3);
        mPaintLineY.setStyle(Paint.Style.FILL);

        mPaintLineDefault = new Paint();
        mPaintLineDefault.setColor(Color.GRAY);
        mPaintLineDefault.setAntiAlias(true);
        mPaintLineDefault.setStyle(Paint.Style.STROKE);
        mPaintLineDefault.setStrokeWidth(1);
        mPaintLineDefault.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));


        mPaintText = new Paint();
        mPaintText.setAntiAlias(true);
        mPaintText.setColor(Color.GRAY);
        mPaintText.setStyle(Paint.Style.STROKE);
        mPaintText.setTextSize(14);

        mPaintLineOneRound = new Paint();
        mPaintLineOneRound.setColor(Color.BLUE);

        mPaintLineTwoRound = new Paint();
        mPaintLineTwoRound.setColor(Color.RED);

        mPaintLineThreeRound = new Paint();
        mPaintLineThreeRound.setColor(Color.GREEN);

        mPaintLineFourRound = new Paint();
        mPaintLineFourRound.setColor(Color.YELLOW);
    }

    @Override
    //重写该方法,进行绘图
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //把整张画布绘制成背景色
        canvas.drawColor(0xCC524c97);
        //画Y轴
        canvas.drawLine(widthLeftRight, heightBg - widthTopBottom, widthLeftRight, 25, mPaintLineY);
        Path path1 = new Path();
        path1.moveTo(widthLeftRight, 0);
        path1.lineTo(widthLeftRight - 15, 25);
        path1.lineTo(widthLeftRight + 15, 25);
        path1.close();
        canvas.drawPath(path1, mPaintLineY);
        //画X轴
        canvas.drawLine(widthLeftRight, heightBg - widthTopBottom, widthBg - 25, heightBg - widthTopBottom, mPaintLineX);
        Path path2 = new Path();
        path2.moveTo(widthBg, heightBg - widthTopBottom);
        path2.lineTo(widthBg - 25, heightBg - (widthTopBottom - 15));
        path2.lineTo(widthBg - 25, heightBg - (widthTopBottom + 15));
        path2.close();
        canvas.drawPath(path2, mPaintLineX);
        //画虚线
        for (int i = 0; i < lineCount; i++) {
            Path temp = new Path();
            temp.moveTo(widthLeftRight, poY[i]);
            temp.lineTo(widthBg - 50, poY[i]);
            canvas.drawPath(temp, mPaintLineDefault);
        }
        //画X标签
        for (int i = 0; i < pointCount; i++) {
            canvas.drawText("tagX" + i, poX[i] - 10, heightBg - (widthTopBottom - 35), mPaintText);
        }
        //画Y标签
        for (int i = 0; i < lineCount; i++) {
            canvas.drawText("tagY" + i, 10, poY[i], mPaintText);
        }
//        Path pathLineone = new Path();
//        pathLineone.moveTo(widthLeftRight * 2, 30);
//        pathLineone.lineTo(widthLeftRight * 2 + 40, 30);
//        canvas.drawPath(pathLineone, mPaintLineOne);
//        canvas.drawText("收入", widthLeftRight * 2 + 44, 35, mPaintText);
//        Path pathLineTwo = new Path();
//        pathLineTwo.moveTo(widthLeftRight * 4, 30);
//        pathLineTwo.lineTo(widthLeftRight * 4 + 40, 30);
//        canvas.drawPath(pathLineTwo, mPaintLineTwo);
//        canvas.drawText("支出", widthLeftRight * 4 + 44, 35, mPaintText);

        //画折线图1
        Path path3 = new Path();
        for (int i = 0; i < pointCount; i++) {
            //当数据不足时，停止绘制
            if (i >= lineOne.size()) {
                break;
            }

            if (lineOne.size() <= 0) {
                break;
            }
            if (i == 0) {
                path3.moveTo(poX[i], lineOne.get(i));
            } else {
                path3.lineTo(poX[i], lineOne.get(i));
            }
            canvas.drawCircle(poX[i], lineOne.get(i), 10, mPaintLineOneRound);
        }
        canvas.drawPath(path3, mPaintLineOne);
        //画折线图2
        Path path4 = new Path();
        for (int i = 0; i < pointCount; i++) {
            //当数据不足时，停止绘制
            if (i >= lineTwo.size()) {
                break;
            }
            if (lineTwo.size() <= 0) {
                break;
            }
            if (i == 0) {
                path4.moveTo(poX[i], lineTwo.get(i));
            } else {
                path4.lineTo(poX[i], lineTwo.get(i));
            }
            canvas.drawCircle(poX[i], lineTwo.get(i), 10, mPaintLineTwoRound);
        }
        canvas.drawPath(path4, mPaintLineTwo);

        //画折线图3
        Path path5 = new Path();
        for (int i = 0; i < pointCount; i++) {
            //当数据不足时，停止绘制
            if (i >= lineThree.size()) {
                break;
            }

            if (lineThree.size() <= 0) {
                break;
            }
            if (i == 0) {
                path3.moveTo(poX[i], lineThree.get(i));
            } else {
                path3.lineTo(poX[i], lineThree.get(i));
            }
            canvas.drawCircle(poX[i], lineThree.get(i), 10, mPaintLineThreeRound);
        }
        canvas.drawPath(path5, mPaintLineThreeRound);
        //画折线图4
        Path path6 = new Path();
        for (int i = 0; i < pointCount; i++) {
            //当数据不足时，停止绘制
            if (i >= lineFour.size()) {
                break;
            }
            if (lineFour.size() <= 0) {
                break;
            }
            if (i == 0) {
                path4.moveTo(poX[i], lineFour.get(i));
            } else {
                path4.lineTo(poX[i], lineFour.get(i));
            }
            canvas.drawCircle(poX[i], lineFour.get(i), 10, mPaintLineFourRound);
        }
        canvas.drawPath(path6, mPaintLineFourRound);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;
        //如果布局里面设置的是固定值,这里取布局里面的固定值;如果设置的是match_parent,则取父布局的大小
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            //如果布局里面没有设置固定值,这里取布局的宽度的1/2
            width = widthSize * 1 / 2;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            //如果布局里面没有设置固定值,这里取布局的高度的3/4
            height = heightSize * 3 / 4;
        }
        widthBg = width;
        heightBg = height;
        setMeasuredDimension(width, height);
        initTemp();
    }

    public void setData(ArrayList<Integer> one) {
        this.lineOne.clear();
        this.lineTwo.clear();
        this.lineThree.clear();
        this.lineFour.clear();
        for (int temp : one) {
            this.lineOne.add(poY[temp]);
        }
        invalidate();
    }

    public void setData(ArrayList<Integer> one, ArrayList<Integer> two) {
        this.lineOne.clear();
        this.lineTwo.clear();
        this.lineThree.clear();
        this.lineFour.clear();
        for (int temp : one) {
            this.lineOne.add(poY[temp]);
        }
        for (int temp2 : two) {
            this.lineTwo.add(poY[temp2]);
        }
        invalidate();
    }

    public void setData(ArrayList<Integer> one, ArrayList<Integer> two,ArrayList<Integer> three) {
        this.lineOne.clear();
        this.lineTwo.clear();
        this.lineThree.clear();
        this.lineFour.clear();
        for (int temp : one) {
            this.lineOne.add(poY[temp]);
        }
        for (int temp2 : two) {
            this.lineTwo.add(poY[temp2]);
        }
        for (int temp3 : three) {
            this.lineThree.add(poY[temp3]);
        }
        invalidate();
    }

    public void setData(ArrayList<Integer> one, ArrayList<Integer> two,ArrayList<Integer> three,ArrayList<Integer> four) {
        this.lineOne.clear();
        this.lineTwo.clear();
        this.lineThree.clear();
        this.lineFour.clear();
        for (int temp : one) {
            this.lineOne.add(poY[temp]);
        }
        for (int temp2 : two) {
            this.lineTwo.add(poY[temp2]);
        }
        for (int temp3 : three) {
            this.lineThree.add(poY[temp3]);
        }
        for (int temp4 : four) {
            this.lineFour.add(poY[temp4]);
        }
        invalidate();
    }

    /**
     * 设置虚线条数
     *
     * @param lineCount
     */
    public void setLineCount(int lineCount) {
        this.lineCount = lineCount;
        int hightTemp = (heightBg - widthTopBottom * 2 - 40) / (lineCount - 1);
        poY = new int[lineCount];
        for (int i = 0; i < lineCount; i++) {
            poY[i] = heightBg - widthTopBottom - 20 - hightTemp * i;
        }
        invalidate();
    }

    /**
     * 设置点的个数
     *
     * @param pointCount
     */
    public void setPointCount(int pointCount) {
        this.pointCount = pointCount;
        int widthTemp = (widthBg - widthLeftRight * 2 - 40) / (pointCount - 1);
        poX = new int[pointCount];
        for (int i = 0; i < pointCount; i++) {
            poX[i] = widthLeftRight + 20 + widthTemp * i;
        }
        invalidate();
    }


    /**
     * 初始化零时变量
     */
    public void initTemp() {
        int hightTemp = (heightBg - widthTopBottom * 2 - 40) / (lineCount - 1);
        poY = new int[lineCount];
        for (int i = 0; i < lineCount; i++) {
            poY[i] = heightBg - widthTopBottom - 20 - hightTemp * i;
        }
        int widthTemp = (widthBg - widthLeftRight * 2 - 40) / (pointCount - 1);
        poX = new int[pointCount];
        for (int i = 0; i < pointCount; i++) {
            poX[i] = widthLeftRight + 20 + widthTemp * i;
        }
    }
}