package com.example.administrator.myapplication;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;

public class ClockView extends View {
    private Paint mViewPaint, mViewPaint2, mViewPaint3, mViewPaint4;
    private float Margin = 10;
    private boolean isFrast = true;


    public ClockView(Context context) {
        super(context);
        init();
    }

    public ClockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ClockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
/*
        mShaderMatrix = new Matrix();
*/
        mViewPaint = new Paint();
        mViewPaint.setColor(Color.WHITE);
        mViewPaint.setStrokeWidth(3f);
        mViewPaint.setAntiAlias(true);


        mViewPaint2 = new Paint();
        mViewPaint2.setColor(Color.BLACK);
        mViewPaint2.setStrokeWidth(3f);
        mViewPaint2.setAntiAlias(true);

        mViewPaint3 = new Paint();
        mViewPaint3.setColor(Color.BLACK);
        mViewPaint3.setAntiAlias(true);


        mViewPaint4 = new Paint();
        mViewPaint4.setStrokeWidth(10f);
        mViewPaint4.setStyle(Paint.Style.STROKE);
        mViewPaint4.setColor(Color.BLACK);
        mViewPaint4.setAntiAlias(true);


        //  start();
    }

    public void startA() {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, "rotation", 0f, 360f);//添加旋转动画，旋转中心默认为控件中点
        objectAnimator.setDuration(3000);//设置动画时间
        objectAnimator.setInterpolator(new LinearInterpolator());//动画时间线性渐变
        objectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        objectAnimator.setRepeatMode(ObjectAnimator.RESTART);


    }

    Point startPoint, endPoint, currentPoint;

    private void start() {
        startPoint = new Point(lineSX, lineSY);

        endPoint = new Point(getWidth() - getWidth() / 6, getHeight() / 2- getWidth() / 6);
        ValueAnimator animator = ValueAnimator.ofObject(new PointEvaluator(), startPoint, endPoint, startPoint);
        animator.setDuration(5000);
       //   animator.setRepeatCount(-1);
      // animator.setRepeatMode(ValueAnimator.REVERSE);
      //  animator.setInterpolator(new Interpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentPoint = (Point) animation.getAnimatedValue();
                CircleSX = currentPoint.getX();
                CircleSY = currentPoint.getY();
                lineSX = currentPoint.getX();
                lineSY = currentPoint.getY();
                invalidate();

            }
        });
        animator.start();
    }

    public class Interpolator implements TimeInterpolator {
        @Override
        public float getInterpolation(float input) {
            float result;
            if (input <= 0.5) {
                return  input;
              //  result = (float) (Math.sin(Math.PI /2* input)) / 2;
                // 使用正弦函数来实现先减速后加速的功能，逻辑如下：
                // 因为正弦函数初始弧度变化值非常大，刚好和余弦函数是相反的
                // 随着弧度的增加，正弦函数的变化值也会逐渐变小，这样也就实现了减速的效果。
                // 当弧度大于π/2之后，整个过程相反了过来，现在正弦函数的弧度变化值非常小，渐渐随着弧度继续增加，变化值越来越大，弧度到π时结束，这样从0过度到π，也就实现了先减速后加速的效果
            } else {
                return input * input;

          //      result = (float) (2 - Math.sin(Math.PI /2* input)) / 2;
            }
          //  return result;
            /*if (mFactor == 1.0f) {
                return input * input;
            } else {
                return (float)Math.pow(input, mDoubleFactor);
            }*/

        }
    }

    private class PointEvaluator implements TypeEvaluator {

        @Override
        public Object evaluate(float fraction, Object startValue, Object endValue) {
            Point startPoint = (Point) startValue;
            Point endPoint = (Point) endValue;
            float x = startPoint.getX() + (fraction * (endPoint.getX() - startPoint.getX()));

            float l = (endPoint.getX() - startPoint.getX()) / 2;
            double y = getHeight() / 2 + Math.sqrt(l * l - Math.pow((l - x + startPoint.getX()), 2));
            Log.e("-------------",y+"======="+fraction);

            return new Point(x, (float) y);
        }
    }

    private float lineSX;
    private float lineSY;
    private float lineEX;
    private float lineEY;

    private float CircleSX;
    private float CircleSY;
    private float CircleRX;

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        lineSX = getWidth() / 6;
        lineSY = getHeight() / 2;
        lineEX = getWidth() / 2;
        lineEY = getHeight() / 2;


        CircleSX = getWidth() / 6;
        CircleSY = getHeight() / 2;
        CircleRX = getHeight() / 10;
        super.onLayout(changed, left, top, right, bottom);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, getHeight() / 2, mViewPaint);
        RectF oval = new RectF(Margin, Margin,
                getWidth() - Margin, getHeight() - Margin);
        canvas.drawLine(lineSX, lineSY, lineEX, lineEY, mViewPaint2);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, getHeight() / 20, mViewPaint3);
        canvas.drawCircle(CircleSX, CircleSY, CircleRX, mViewPaint3);
        canvas.drawArc(oval, 0, 180, false, mViewPaint4);
        if (isFrast) {
            start();
            isFrast = false;
        }
    }

    public class Point {
        float x;
        float y;

        public Point(float x, float y) {
            this.x = x;
            this.y = y;

        }

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }

    }


}
