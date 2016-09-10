package com.elims.snake.ui;

/**
 * Created by smile on 2016/9/8.
 */
public interface GameInterface {

    /**
     * 设置界面显示的区域
     * @param x
     * @param y
     */
    void setViewMargin(int x, int y);

    /**
     * 显示蛇的长度
     * @param len
     */
    void setSnakeLen(int len);
    /**
     * 显示排名
     * @param rank
     */
    void setRank(String rank);
}
