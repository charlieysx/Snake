package com.elims.snake.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.elims.snake.base.BaseView;
import com.elims.snake.bean.Food;
import com.elims.snake.bean.Point;
import com.elims.snake.bean.Snake;
import com.elims.snake.constant.Constant;
import com.elims.snake.ui.GameInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Created by smile on 2016/9/7.
 */
public class SnakeView extends BaseView implements SnakeViewInterface {

    private List<Snake> snakes;
    private List<Food> foods;
    private static int sFlag = 0;
    private GameInterface gameInterface;
    private int[] colors = {Color.GREEN, Color.BLUE, Color.YELLOW, Color.RED, Color.GRAY};


    public SnakeView(Context context) {
        super(context);
    }

    public SnakeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void drawSub(Canvas canvas) {

        Paint paint = new Paint();
        paint.setARGB(100, 100, 100, 100);

        for (int i = Constant.nullWidth; i < Constant.nullWidth + Constant.viewHeight; i += Constant.gridWidth) {
            canvas.drawLine(Constant.nullWidth, i, Constant.nullWidth + Constant.viewWidth, i, paint);
        }
        for (int i = Constant.nullWidth; i < Constant.nullWidth + Constant.viewWidth; i += Constant.gridWidth) {
            canvas.drawLine(i, Constant.nullWidth, i, Constant.nullWidth + Constant.viewHeight, paint);
        }
        paint.setARGB(50, 100, 100, 100);
        canvas.drawRect(0, 0, Constant.nullWidth, Constant.nullWidth + Constant.viewHeight, paint);
        canvas.drawRect(Constant.nullWidth, 0, Constant.nullWidth + Constant.viewWidth, Constant.nullWidth, paint);
        canvas.drawRect(Constant.nullWidth + Constant.viewWidth, 0, Constant.nullWidth * 2 + Constant.viewWidth,
                Constant.nullWidth + Constant.viewHeight, paint);
        canvas.drawRect(0, Constant.nullWidth + Constant.viewHeight, Constant.nullWidth * 2 + Constant.viewWidth,
                Constant.nullWidth * 2 + Constant.viewHeight, paint);

        int fSize = foods.size();
        for (int i = 0;i < fSize;++i) {
            Food food = foods.get(i);
            if(food.isNull()){
                foods.remove(i);
                addFood();
            } else {
                paint.setColor(food.getColor());
                canvas.drawOval(new RectF(food.getX(), food.getY(), food.getX() + Constant.snake_d / 3, food.getY() +
                        Constant.snake_d / 3), paint);
            }

        }

        int sSize = snakes.size();
        for (int i = 0;i < sSize;++i) {
            Snake snake = snakes.get(i);
            if(snake != null) {
                snake.draw(canvas);
            }
            sSize = snakes.size();
        }
    }

    @Override
    protected void logic() {
        int size = snakes.size();
        for (int i = 0; i < size; ++i) {
            Snake snake = snakes.get(i);
            if (snake.getSnake().size() == 0) {
                snakes.remove(snake);
                snakes.add(new Snake(this, sFlag++, true, ""));
            } else {
                snake.move();
            }
        }
        List<Snake> tSnakes = new ArrayList<>();
        tSnakes.addAll(snakes);
        Collections.sort(tSnakes, new Comparator<Snake>() {
            @Override
            public int compare(Snake s1, Snake s2) {

                return ((Integer)s2.getEat()).compareTo((s1.getEat()));
            }
        });

        StringBuilder builder = new StringBuilder();
        int tSize = 5;
        for(int i = 1;i <= tSize;++i) {
            if (tSnakes.get(i - 1).getIsDeath()) {
                tSize++;
            } else {
                builder.append("\n" + i + "--" + tSnakes.get(i - 1).getName() + "--" + tSnakes.get(i - 1).getEat());
            }
        }

        gameInterface.setRank(builder.toString());
//        addFood();
    }

    @Override
    protected void init() {

        snakes = new ArrayList<>();
        foods = new ArrayList<>();
        snakes.add(new Snake(this, sFlag++, false, "小熊"));
        for (int i = 0; i < Constant.snake_num; ++i) {
            snakes.add(new Snake(this, sFlag++, true, ""));
        }
        addFood();
    }

    private void addFood() {
        Random random = new Random();
        int fNum = Constant.food_num - foods.size();
        for (int i = 0; i < fNum; ++i) {
            int x = random.nextInt(Constant.viewWidth - Constant.nullWidth / 2) + Constant.snake_d + Constant.nullWidth;
            int y = random.nextInt(Constant.viewHeight - Constant.nullWidth / 2) + Constant.snake_d + Constant.nullWidth;
            int c = random.nextInt(colors.length);
            foods.add(new Food(x, y, colors[c]));
        }
    }

    public void setGameInterface(GameInterface gameInterface) {
        this.gameInterface = gameInterface;
    }

    public void setOpt(Point opt) {
        snakes.get(0).setOpt(opt);
    }

    public void setSpeed(int speed) {
        snakes.get(0).setSpeed(speed);
    }

    @Override
    public List<Snake> getSnakes() {
        List<Snake> tSnakes = new ArrayList<>();
        tSnakes.addAll(snakes);
        return tSnakes;
    }

    @Override
    public List<Food> getFoods() {
        List<Food> tFoods = new ArrayList<>();
        tFoods.addAll(foods);
        return tFoods;
    }

    @Override
    public void removeSnake(int flag, int k) {
        for (Snake snake : snakes) {
            if (snake.getFlag() == flag) {
                snake.removeSnake(k);
                break;
            }
        }
    }

    @Override
    public void removeFood(int k) {
        foods.get(k).setNull(true);
    }

    @Override
    public void setViewMargin(int x, int y) {
        gameInterface.setViewMargin(x, y);
    }

    @Override
    public void setSnakeLen(int len) {
        gameInterface.setSnakeLen(len);
    }
}
