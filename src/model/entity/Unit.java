package model.entity;

import model.Position;

import java.awt.*;

/**
 * 游戏中的抽象单位, 坦克和炮弹都是单位
 *
 * @author Yhaobo
 * @since 2020/10/25
 */
public interface Unit {
    /**
     * 返回单位的中心点定位
     *
     * @return 中心定位
     */
    Position getCentrePosition();

    /**
     * 返回单位的碰撞半径
     *
     * @return 碰撞半径
     */
    float getCollisionRadius();

    /**
     * 设置单位的定位
     *
     * @param position 定位
     */
    Unit setPosition(Position position);

    /**
     * 绘制
     *
     * @param g 画布
     */
    void draw(Graphics g);

}
