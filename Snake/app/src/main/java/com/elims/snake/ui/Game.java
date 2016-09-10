package com.elims.snake.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.elims.snake.R;
import com.elims.snake.base.BaseAvtivity;
import com.elims.snake.bean.Point;
import com.elims.snake.constant.Constant;
import com.elims.snake.view.SnakeView;

import java.util.List;

/**
 * Created by smile on 2016/9/7.
 */
public class Game extends BaseAvtivity implements GameInterface {

    private SnakeView snakeView;
    private ImageView iv_moving;
    private ImageView iv_speeding;
    private TextView tv_len;
    private TextView tv_rank;

    private float x;
    private float y;
    private float x2;
    private float y2;
    private float optf;
    private Point opt;
    private boolean isMove = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Constant.screenWidth = dm.widthPixels;
        Constant.screenHeight = dm.heightPixels;
        Constant.viewWidth = Constant.screenWidth * 2;
        Constant.viewHeight = Constant.screenHeight * 2;
        Constant.snake_d = Constant.screenWidth / 35;
        Constant.snake_len = Constant.snake_d / 2;
        Constant.buttonMargin = Constant.snake_d;
        opt = new Point(Constant.snake_len, 0);
        Log.i("game", "Constant.snake_d:" + Constant.snake_d);

        snakeView = (SnakeView) findViewById(R.id.sv_game);
        snakeView.setGameInterface(this);
        iv_moving = (ImageView) findViewById(R.id.iv_moving);
        iv_speeding = (ImageView) findViewById(R.id.iv_speeding);
        tv_len = (TextView) findViewById(R.id.tv_len);
        tv_rank = (TextView) findViewById(R.id.tv_rank);

        iv_moving.setVisibility(View.INVISIBLE);
        iv_speeding.setVisibility(View.INVISIBLE);

        int mx = (Constant.nullWidth * 2 + Constant.viewWidth) / 2;
        int my = (Constant.nullWidth * 2 + Constant.viewHeight) / 2;
        Log.i("game", "mx--" + mx + "--my--" + my);
        setViewMargin(mx, my);

        findViewById(R.id.iv_move).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (view.getId() == R.id.iv_move) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            x = 40;
                            y = 40;
                            iv_moving.setVisibility(View.VISIBLE);
                            btMoving(0, 0);
                            isMove = false;
                            Log.i("game", "down:" + x + "---" + y);

                            break;
                        case MotionEvent.ACTION_MOVE:
                            isMove = true;
                            moving(motionEvent.getX(), motionEvent.getY());

                            break;
                        case MotionEvent.ACTION_UP:
                            moving(motionEvent.getX(), motionEvent.getY());
                            btMoving(0, 0);
                            iv_moving.setVisibility(View.INVISIBLE);

                            break;
                    }
                }

                return true;
            }
        });

        findViewById(R.id.iv_speed).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (view.getId() == R.id.iv_speed) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            snakeView.setSpeed(2);
                            iv_speeding.setVisibility(View.VISIBLE);

                            break;
                        case MotionEvent.ACTION_UP:
                            snakeView.setSpeed(1);
                            iv_speeding.setVisibility(View.INVISIBLE);

                            break;
                    }
                }

                return true;
            }
        });

    }

    private void moving(float x1, float y1) {

        x2 = x1 - x;
        y2 = y1 - y;
        btMoving(x2, y2);

        if (isMove) {
            optf = (float) Math.sqrt(x2 * x2 + y2 * y2);
            opt.setX(Constant.snake_len * x2 / optf);
            opt.setY(Constant.snake_len * y2 / optf);

            snakeView.setOpt(opt);
        }
    }

    private void btMoving(float mx, float my) {
        float tf = (float) Math.sqrt(mx * mx + my * my);
        RelativeLayout.LayoutParams lP = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        if (mx == 0 && my == 0) {
            lP.setMargins(Constant.buttonMargin, Constant.buttonMargin, Constant.buttonMargin, Constant.buttonMargin);
        } else {
            mx = Constant.buttonMargin * mx / tf;
            my = Constant.buttonMargin * my / tf;

            int left = (int) (Constant.buttonMargin + mx);
            int top = (int) (Constant.buttonMargin + my);
            int right = Constant.buttonMargin * 2 - left;
            int bottom = Constant.buttonMargin * 2 - top;

            lP.setMargins(left, top, right, bottom);
        }
        iv_moving.setLayoutParams(lP);
    }

    @Override
    public void setViewMargin(int x, int y) {
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putInt("x", x);
        bundle.putInt("y", y);
        msg.setData(bundle);
        msg.what = Constant.GAME_VIEW_MARGIN;
        synchronized (mHandler){
            mHandler.sendMessage(msg);
        }
    }

    @Override
    public void setSnakeLen(int len) {
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putInt("len", len);
        msg.setData(bundle);
        msg.what = Constant.SNAKE_LEN;
        synchronized (mHandler){
            mHandler.sendMessage(msg);
        }
    }

    @Override
    public synchronized void setRank(String rank) {
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("rank", rank);
        msg.setData(bundle);
        msg.what = Constant.SNAKE_RANK;
        synchronized (mHandler){
            mHandler.sendMessage(msg);
        }
    }

    private Handler mHandler = new Handler(){
        @Override
        public void dispatchMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what){
                case Constant.GAME_VIEW_MARGIN:
                    setMargin(msg);
                    break;
                case Constant.SNAKE_LEN:
                    int len = bundle.getInt("len");
                    tv_len.setText("长度：" + len);

                    break;
                case Constant.SNAKE_RANK:
                    String rank = bundle.getString("rank");
                    tv_rank.setText("排名" + rank);

                    break;
            }

        }
    };

    private void setMargin(Message msg){

        Bundle bundle = msg.getData();
        int x = bundle.getInt("x");
        int y = bundle.getInt("y");

        RelativeLayout.LayoutParams lP = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        x -= (Constant.screenWidth / 2);
        y -= (Constant.screenHeight / 2);
        if(x < 0){
            x = 0;
        }
        if(x > Constant.nullWidth * 2 + Constant.viewWidth - Constant.screenWidth){
            x = Constant.nullWidth * 2 + Constant.viewWidth - Constant.screenWidth;
        }
        if(y < 0){
            y = 0;
        }
        if(y > Constant.nullWidth * 2 + Constant.viewHeight - Constant.screenHeight){
            y = Constant.nullWidth * 2 + Constant.viewHeight - Constant.screenHeight;
        }

//            Log.i("game", "x--:" + x);
        lP.setMargins(-x, -y, 0, 0);
        snakeView.setLayoutParams(lP);
//            Toast.makeText(getApplicationContext(), x + "--" + y, Toast.LENGTH_SHORT).show();
    }

}
