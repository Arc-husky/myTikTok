package com.example.mainacticity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Canvas;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.lang.reflect.Array;
import java.util.Random;

public class Love extends RelativeLayout {
    private long mHits[] = new long[2];
    private Context mContext;
    float[] num = {-30, -20, 0, 20, 30};//随机心形图片角度
    public Love(Context context) {
        super(context);
        initView(context);
    }
    public Love(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }
    public Love(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }
    private void initView(Context context) {
        mContext = context;
    }
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //双击检测，对比第二次点击和第一次点击的时间，如果小于0.5s则为双击
        System.arraycopy(mHits,1,mHits,0,mHits.length-1);
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();
        if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
            final ImageView imageView = new ImageView(mContext);
            LayoutParams params = new LayoutParams(300, 300);
            params.leftMargin = (int) event.getX() - 150;
            params.topMargin = (int) event.getY() - 300;
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.heart_red));
            imageView.setLayoutParams(params);
            addView(imageView);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(scale(imageView, "scaleX", 2f, 0.9f, 100, 0))
                    .with(scale(imageView, "scaleY", 2f, 0.9f, 100, 0))
                    .with(rotation(imageView, 0, 0, num[new Random().nextInt(4)]))
                    .with(alpha(imageView, 0, 1, 100, 0))
                    .with(scale(imageView, "scaleX", 0.9f, 1, 50, 150))
                    .with(scale(imageView, "scaleY", 0.9f, 1, 50, 150))
                    .with(translationY(imageView, 0, -600, 800, 400))
                    .with(alpha(imageView, 1, 0, 300, 400))
                    .with(scale(imageView, "scaleX", 1, 3f, 700, 400))
                    .with(scale(imageView, "scaleY", 1, 3f, 700, 400));
            animatorSet.start();
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    removeViewInLayout(imageView);
                }
            });
        }
        return super.onTouchEvent(event);
    }
    public static ObjectAnimator scale(View view, String propertyName, float from, float to, long time, long delayTime) {
        ObjectAnimator translation = ObjectAnimator.ofFloat(view
                , propertyName
                , from, to);
        translation.setInterpolator(new LinearInterpolator());
        translation.setStartDelay(delayTime);
        translation.setDuration(time);
        return translation;
    }
    public static ObjectAnimator translationX(View view, float from, float to, long time, long delayTime) {
        ObjectAnimator translation = ObjectAnimator.ofFloat(view
                , "translationX"
                , from, to);
        translation.setInterpolator(new LinearInterpolator());
        translation.setStartDelay(delayTime);
        translation.setDuration(time);
        return translation;
    }
    public static ObjectAnimator translationY(View view, float from, float to, long time, long delayTime) {
        ObjectAnimator translation = ObjectAnimator.ofFloat(view
                , "translationY"
                , from, to);
        translation.setInterpolator(new LinearInterpolator());
        translation.setStartDelay(delayTime);
        translation.setDuration(time);
        return translation;
    }
    public static ObjectAnimator alpha(View view, float from, float to, long time, long delayTime) {
        ObjectAnimator translation = ObjectAnimator.ofFloat(view
                , "alpha"
                , from, to);
        translation.setInterpolator(new LinearInterpolator());
        translation.setStartDelay(delayTime);
        translation.setDuration(time);
        return translation;
    }
    public static ObjectAnimator rotation(View view, long time, long delayTime, float... values) {
        ObjectAnimator rotation = ObjectAnimator.ofFloat(view, "rotation", values);
        rotation.setDuration(time);
        rotation.setStartDelay(delayTime);
        rotation.setInterpolator(new TimeInterpolator() {
            @Override
            public float getInterpolation(float input) {
                return input;
            }
        });
        return rotation;
    }
}
