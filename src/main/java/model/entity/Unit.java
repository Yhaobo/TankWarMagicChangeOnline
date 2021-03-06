package model.entity;

import model.Constant;
import model.Position;
import view.MainPanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

/**
 * 游戏中的抽象单位, 坦克和炮弹都是单位
 *
 * @author Yhaobo
 * @date 2020/10/25
 */

public abstract class Unit implements Identification {
    /**
     * 单位显示图片
     */
    protected BufferedImage img;
    /**
     * 定位
     */
    protected Position position;
    /**
     * 宽
     */
    protected int width;
    /**
     * 高
     */
    protected int height;
    protected String id = UUID.randomUUID().toString().replace("-", "");

    public Unit() {
    }

    public Unit(Position position, int width, int height) {
        setPositionAndVerify(position);
        this.width = width;
        this.height = height;
    }

    /**
     * @return 返回一个随机定位
     */
    public static Position randomPosition() {
        final Random random = new Random();
        return new Position(random.nextInt((int) MainPanel.getDimension().getWidth() - Constant.TankConstant.WIDTH), random.nextInt((int) MainPanel.getDimension().getHeight() - Constant.TankConstant.HEIGHT));
    }

    /**
     * 返回单位的定位
     *
     * @return 定位
     */
    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Unit setPositionAndVerify(Position position) {
        if (this.position == null) {
            this.position = position;
        }
        return setPositionAndVerify(position.getX(), position.getY());
    }

    protected Unit setPositionAndVerify(float x, float y) {
        if (x < 0) {
            x = 0;
        } else {
            final float width = (float) (MainPanel.getDimension().getWidth() - this.width);
            if (x > width) {
                x = width;
            }
        }
        if (y < 0) {
            y = 0;
        } else {
            final float height = (float) (MainPanel.getDimension().getHeight() - this.height);
            if (y > height) {
                y = height;
            }
        }

        position.setX(x);
        position.setY(y);

        return this;
    }
    public Position getCentrePosition(Position position) {
        return new Position(position.getX() + (width >> 1), position.getY() + (height >> 1));
    }
    /**
     * 返回单位的中心点定位
     *
     * @return 中心定位
     */
    public Position getCentrePosition() {
        return getCentrePosition(position);
    }
    /**
     * 绘制
     *
     * @param g 画布
     */
    public void draw(Graphics g) {
        g.drawImage(img, Math.round(position.getX()), Math.round(position.getY()), width, height, null);
    }

    /**
     * 反序列化生成的此类对象只有部分属性赋值, 可以使用此方法来初始化其他必须的属性(从而成为可用的实体)
     */
    public abstract void renew();

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Unit unit = (Unit) o;
        return id.equals(unit.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
