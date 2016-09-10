package com.elims.snake.view;

import com.elims.snake.bean.Food;
import com.elims.snake.bean.Snake;

import java.util.List;

/**
 * Created by smile on 2016/9/8.
 */
public interface SnakeViewInterface {

    /**
     * 获取所有的蛇
     * @return
     */
    List<Snake> getSnakes();
    /**
     * 获取所有的食物
     * @return
     */
    List<Food> getFoods();

    /**
     * 设置蛇
     * @param flag 蛇的标记
     * @param k 蛇被吃的位置
     */
    void removeSnake(int flag, int k);
    /**
     * 设置食物
     * @param k 食物的位置
     */
    void removeFood(int k);
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



}
