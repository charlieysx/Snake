package com.elims.snake.bean;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import com.elims.snake.constant.Constant;
import com.elims.snake.view.SnakeViewInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by smile on 2016/9/7.
 */
public class Snake {
    private Point head;
    private Point[] eyes;
    private List<Point> snake;
    private Paint paint;
    private int length;
    private int speed = 1;
    private int snakeColor;
    private Point opt;
    private boolean isDeath = false;
    private SnakeViewInterface snakeView;
    private boolean isAI = false;
    private List<Snake> snakes;
    private List<Food> foods;
    private int isEat = 0;
    private int eat;
    private int flag;
    private String name;
    private int[] colors = {Color.GREEN, Color.BLUE, Color.YELLOW, Color.RED, Color.GRAY};

    public Snake(SnakeViewInterface snakeView, int flag, boolean isAI, String name) {
        this.snakeView = snakeView;
        this.flag = flag;
        this.isAI = isAI;
        if (isAI) {
            this.name = "AI蛇" + flag;
        } else {
            this.name = name;
        }
        init();
    }

    private void init() {
        snake = new ArrayList<>();
        snakes = new ArrayList<>();
        foods = new ArrayList<>();

        Random random = new Random();
//        Log.e("snake", screen.getX() + "---" + screen.getY());
        float sx = random.nextInt(Constant.viewWidth - Constant.nullWidth - Constant.snake_d) + Constant.nullWidth + Constant.snake_d;
        float sy = random.nextInt(Constant.viewHeight - Constant.nullWidth - Constant.snake_d) + Constant.nullWidth + Constant.snake_d;

        eat = Constant.snake_score * 5;
        if (!isAI) {
            sx = (Constant.viewWidth + Constant.nullWidth * 2) / 2 - Constant.snake_d;
            sy = (Constant.viewHeight + Constant.nullWidth * 2) / 2 - Constant.snake_d;
            snakeView.setSnakeLen(eat);
        }
        head = new Point(sx, sy);
        opt = new Point(Constant.snake_len, 0);

        for (int i = 0; i < 5; ++i) {
            aiOpt();
            sx += opt.getX();
            sy += opt.getY();
            while (isOut(sx, sy)){
                sx -= opt.getX();
                sy -= opt.getY();
                aiOpt();
            }
            snake.add(0, new Point(sx, sy));
        }
        eyes = new Point[2];
        eyes[0] = new Point(head.getX() + (Constant.snake_d - 4) / 2, head.getY() + (Constant.snake_d - 4) / 3);
//        eyes[1] = new Point(sx + 26 / 2, sy + 26 / 3 * 2);
        paint = new Paint();
        paint.setAntiAlias(true);
        int color = random.nextInt(colors.length);
        snakeColor = colors[color];
        aiOpt();
    }

    public void draw(Canvas canvas) {
        if(snake == null){
            return;
        }
        length = snake.size();
        for (int i = 0; i < length; ++i) {

            if(snake == null){
                return;
            }
            float x = snake.get(i).getX();
            float y = snake.get(i).getY();
            if (isDeath) {
                paint.setColor(snakeColor);
                canvas.drawOval(new RectF(x, y, x + Constant.snake_d / 2, y + Constant.snake_d / 2), paint);
            } else {
                paint.setColor(Color.BLACK);
                canvas.drawOval(new RectF(x, y, x + Constant.snake_d, y + Constant.snake_d), paint);
                paint.setColor(snakeColor);
                canvas.drawOval(new RectF(x + 2, y + 2, x + Constant.snake_d - 2, y + Constant.snake_d - 2), paint);
                if (i == length - 1) {
                    paint.setColor(Color.BLACK);
                    paint.setTextSize(Constant.snake_len);
                    canvas.drawText(name, x - 2, y - 2, paint);
                    for (int j = 0; j < 1; ++j) {
                        float ex = eyes[j].getX();
                        float ey = eyes[j].getY();
                        canvas.drawOval(new RectF(ex - 2, ey - 2, ex + 2, ey + 2), paint);
                    }
                }
            }
            length = snake.size();
        }
    }


    public void move() {
        if (!isDeath) {
            if (isAI && speed == 1) {

                Random random = new Random();
                int change = random.nextInt(200);

                if (change % 101 == 0) {
                    aiOpt();
                }
                searchMove();
            }
            for (int i = 0; i < speed; ++i) {
                if(isDeath){
                    break;
                }
                if (isEat < Constant.snake_score) {
                    snake.remove(0);
                } else {
                    isEat -= Constant.snake_score;
                    length++;
                }
                head.setX(head.getX() + opt.getX());
                head.setY(head.getY() + opt.getY());
                snake.add(new Point(head.getX(), head.getY()));
                eyes[0].setX(head.getX() + Constant.snake_d / 2);
                eyes[0].setY(head.getY() + Constant.snake_d / 2);
                if (!isAI) {
                    int mx = (int) (head.getX() + Constant.snake_d / 2);
                    int my = (int) (head.getY() + Constant.snake_d / 2);
                    snakeView.setViewMargin(mx, my);
                }
                isDeath = judgeIsDeath();
                if(isDeath && !isAI){
                    snakeView.setSnakeLen(0);
                }
            }
            if(isAI){
                speed = 1;
            }
        }
    }

    private void searchMove() {

        snakes = snakeView.getSnakes();
        boolean isok = false;
        int c = 0;
        while (!isok) {
            c++;
            if (c == 5) {
                break;
            }

            float thisx = head.getX() + opt.getX() + Constant.snake_d / 2;
            float thisy = head.getY() + opt.getY() + Constant.snake_d / 2;

            if (thisx <= 20 + Constant.nullWidth || thisx >= Constant.viewWidth - 20 - Constant.nullWidth || thisy <=
                    20 + Constant.nullWidth || thisy >= Constant.viewHeight - 20 - Constant.nullWidth) {
                aiOpt();
                continue;
            }
            boolean is = true;
            for (int i = 0; i < snakes.size(); ++i) {
                Snake tSnake = snakes.get(i);
                if (tSnake.getFlag() == this.flag || tSnake.getIsDeath()) {
                    continue;
                }
                for (Point point : tSnake.getSnake()) {
                    if (judge(new Point(thisx, thisy), point, Constant.snake_d / 2, false)) {
                        i = snakes.size();
                        is = false;
                        aiOpt();
                        break;
                    }
                }
            }
            if (is) {
                isok = true;
            }
        }
    }

    private void aiOpt() {
        Random random = new Random();
        int rx = random.nextInt(100);
        int ry = random.nextInt(100);
        int fx = random.nextInt(100);
        int fy = random.nextInt(100);
        if (fx % 2 == 0) {
            rx *= -1;
        }
        if (fy % 2 == 0) {
            ry *= -1;
        }
        if (rx == 0 && ry == 0) {
            rx++;
        }
        float optf = (float) Math.sqrt(rx * rx + ry * ry);
        opt.setX(Constant.snake_len * rx / optf);
        opt.setY(Constant.snake_len * ry / optf);
    }

    public void setOpt(Point opt) {
        this.opt = opt;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public List<Point> getSnake() {
        List<Point> tSnake = new ArrayList<>();
        tSnake.addAll(snake);
        return tSnake;
    }

    public void removeSnake(int k) {
        this.snake.remove(k);
    }

    public int getFlag() {
        return this.flag;
    }

    public int getEat(){
        return this.eat;
    }

    public String getName(){
        return this.name;
    }

    public boolean getIsDeath() {
        return this.isDeath;
    }

    private boolean judge(Point p1, Point p2, int r, boolean isFood) {

        Point point = new Point(p1.getX() - (p2.getX() + r), p1.getY() - (p2.getY() + r));
        float pLen = (float) Math.sqrt(point.getX() * point.getX() + point.getY() * point.getY());
        if(isFood){
            pLen -= r * 3 / 2;
        }
        if (pLen <= r + Constant.snake_d / 2) {
//            Log.i("snake", "pLen---" + pLen);
            return true;
        }

        return false;
    }

    private boolean isOut(float x, float y) {
        if (x <= Constant.nullWidth + Constant.snake_d / 2 || x >= Constant.viewWidth + Constant.nullWidth -
                Constant.snake_d / 2 || y <=
                Constant.nullWidth + Constant.snake_d / 2 || y >= Constant.viewHeight + Constant.nullWidth -
                Constant.snake_d / 2) {
            return true;
        }
        return false;
    }

    private boolean judgeIsDeath() {
        snakes = snakeView.getSnakes();
        foods = snakeView.getFoods();
        float thisx = head.getX() + Constant.snake_d / 2;
        float thisy = head.getY() + Constant.snake_d / 2;

        if(isOut(thisx, thisy)){
            Point p = this.snake.get(snake.size() - 1);
            this.snake.remove(snake.size() - 1);
            if(p.getX() <= Constant.nullWidth + Constant.snake_d / 2){
                p.setX(Constant.nullWidth + Constant.snake_d / 2);
            } else if(p.getX() >= Constant.viewWidth + Constant.nullWidth - Constant.snake_d / 2){
                p.setX(Constant.viewWidth + Constant.nullWidth - Constant.snake_d / 2);
            }
            if(p.getY() <= Constant.nullWidth + Constant.snake_d / 2){
                p.setY(Constant.nullWidth + Constant.snake_d / 2);
            } else if(p.getY() >= Constant.viewWidth + Constant.nullWidth - Constant.snake_d / 2){
                p.setY(Constant.viewHeight + Constant.nullWidth - Constant.snake_d / 2);
            }
            this.snake.add(new Point(p.getX(), p.getY()));
            head.setX(p.getX());
            head.setY(p.getY());
            eyes[0].setX(head.getX() + Constant.snake_d / 2);
            eyes[0].setY(head.getY() + Constant.snake_d / 2);

            return true;
        }

        int fSize = foods.size();
        for (int i = 0; i < fSize; ++i) {
            Food food = foods.get(i);
            if(food == null){
                continue;
            }
            if (judge(new Point(thisx, thisy), new Point(food.getX(), food.getY()), Constant.snake_d / 6, true)) {
                isEat += Constant.snake_score / 15;
                eat += Constant.snake_score / 15;
                if(!isAI) {
                    snakeView.setSnakeLen(eat);
                }
                foods.remove(i);
                fSize--;
                snakeView.removeFood(i);
            }
        }
        for (int i = 0; i < snakes.size(); ++i) {
            Snake tSnake = snakes.get(i);
            if (tSnake.getFlag() == this.flag) {
                continue;
            }
            if (tSnake.getIsDeath()) {
                int size = tSnake.getSnake().size();
                for (int k = 0; k < size; ++k) {
                    Point p = tSnake.getSnake().get(k);
                    if (judge(new Point(thisx, thisy), p, Constant.snake_d / 4, true)) {

                        isEat += Constant.snake_score;
                        eat += Constant.snake_score;
                        if(isAI){
                            if(size > 1) {
                                speed = 2;
                                int f = k;
                                if(f < size / 2){
                                    f += 1;
                                } else {
                                    f -= 1;
                                }
                                p = tSnake.getSnake().get(f);
                                int rx = (int) (p.getX() - head.getX());
                                int ry = (int) (p.getY() - head.getY());
                                float optf = (float) Math.sqrt(rx * rx + ry * ry);
                                opt.setX(Constant.snake_len * rx / optf);
                                opt.setY(Constant.snake_len * ry / optf);
                            }
                        }
                        if(!isAI) {
                            snakeView.setSnakeLen(eat);
                        }
                        tSnake.getSnake().remove(k);
                        size--;
                        snakeView.removeSnake(tSnake.getFlag(), k);
                    }
                }

                continue;
            }

            for (Point point : tSnake.getSnake()) {
                if (judge(new Point(thisx, thisy), point, Constant.snake_d / 2, false)) {
                    Log.i("snake", this.name + "-----");
                    return true;
                }
            }
        }

        return false;
    }

}
