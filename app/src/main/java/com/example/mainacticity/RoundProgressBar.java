package com.example.mainacticity;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

public class RoundProgressBar extends View {

    /**
     * 环形
     */
    private static final int STROKE = 0;

    /**
     * 环形带字
     */
    private static final int STROKE_TEXT = 1;

    /**
     * 填充
     */
    private static final int STROKE_FILL = 2;

    private Paint mPaint;
    private int mBackColor;
    private int mFontColor;
    private int mTextColor;
    private float mTextSize;
    private float mBorderWidth;
    private int mMode;

    private float mHalfBorder;
    private int max;
    private int value;
    private int startPos = -90;
    private Paint.Style style;
    private boolean isFill;
    private int textHalfSize;

    public RoundProgressBar(Context context) {
        super(context, null);
    }

    public RoundProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();

        TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
                R.styleable.RoundProgressBar);

        mBackColor = mTypedArray.getColor(R.styleable.RoundProgressBar_backColor, Color.WHITE);
        mFontColor = mTypedArray.getColor(R.styleable.RoundProgressBar_frontColor, Color.GRAY);
        mTextColor = mTypedArray.getColor(R.styleable.RoundProgressBar_textColor, Color.GRAY);
        mTextSize = mTypedArray.getDimension(R.styleable.RoundProgressBar_textSize, getResources().getDimensionPixelSize(R.dimen.round_progress_text_size));
        textHalfSize = (int) (mTextSize*0.4f);
        mBorderWidth = mTypedArray.getDimension(R.styleable.RoundProgressBar_borderWidth, getResources().getDimensionPixelSize(R.dimen.round_progress_border_width));
        mHalfBorder = mBorderWidth * 0.5f;
        mMode = mTypedArray.getInteger(R.styleable.RoundProgressBar_mode, STROKE_TEXT);
        isFill = mMode == STROKE_FILL;
        style = isFill ? Paint.Style.FILL_AND_STROKE : Paint.Style.STROKE;
        mTypedArray.recycle();
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getValue() {
        return value;
    }

    public synchronized void setValue(int newValue) {
        newValue = Math.max(0, newValue);
        newValue = Math.min(max, newValue);

        this.value = newValue;
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //刻度背景
        int center = (int) (getWidth() * 0.5f);
        int radius = (int) (center - mHalfBorder); //圆环的半径
        mPaint.setColor(mBackColor); //设置圆环的颜色
        mPaint.setStyle(style); //设置空心
        mPaint.setStrokeWidth(mBorderWidth); //设置圆环的宽度
        mPaint.setAntiAlias(true);  //消除锯齿
        canvas.drawCircle(center, center, radius, mPaint); //画出圆环

        int percent = (int) (value * 100f / max);
        //文本
        if (mMode == STROKE_TEXT) {
            mPaint.setStrokeWidth(0);
            mPaint.setColor(mTextColor);
            mPaint.setTextSize(mTextSize);
            mPaint.setTypeface(Typeface.DEFAULT_BOLD); //设置字体

            float textHalfWidth = mPaint.measureText(percent + "%")*0.5f;
            canvas.drawText(percent + "%", center - textHalfWidth, center +textHalfSize,
                    mPaint);
        }

        //进度
        if (value>0) {

            mPaint.setStrokeWidth(mBorderWidth);
            mPaint.setColor(mFontColor);
            RectF oval = new RectF(center - radius, center - radius, center + radius, center + radius);
            int angle = (int) (360 * percent / 100f);
            mPaint.setStyle(style);
            canvas.drawArc(oval, startPos, angle, isFill, mPaint);
        }
    }
}

