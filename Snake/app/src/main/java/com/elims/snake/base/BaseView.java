package com.elims.snake.base;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by smile on 2016/9/7.
 */
public abstract class BaseView extends View {

    private MyThread thread;
    private boolean running = true;

    public BaseView(Context context) {
        super(context);
    }

    public BaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected final void onDraw(Canvas canvas) {

        if(thread == null){
            thread = new MyThread();
            thread.start();
        } else {
            drawSub(canvas);
        }

    }

    @Override
    protected void onDetachedFromWindow() {
        running = false;
        super.onDetachedFromWindow();
    }

    protected abstract void drawSub(Canvas canvas);
    protected abstract void logic();
    protected abstract void init();


    class MyThread extends Thread {
        @Override
        public void run() {
            init();
            while(running){
                logic();
                postInvalidate();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
