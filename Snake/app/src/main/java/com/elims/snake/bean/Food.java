package com.elims.snake.bean;

/**
 * Created by smile on 2016/9/8.
 */
public class Food {
    private int x;
    private int y;
    private int color;
    private boolean isNull;

    public Food(int x, int y, int color) {
        this.x = x;
        this.y = y;
        this.color = color;
        isNull = false;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isNull() {
        return isNull;
    }

    public void setNull(boolean aNull) {
        isNull = aNull;
    }
}
