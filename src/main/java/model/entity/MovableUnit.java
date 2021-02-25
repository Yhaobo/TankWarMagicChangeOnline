package model.entity;

import model.Position;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author Yhaobo
 * @date 2020/10/27
 */

public abstract class MovableUnit extends CollisionableUnit {

    /**
     * 方向 (弧度)
     */
    protected float direction;
    /**
     * 前进速度
     */
    protected float speed;
    /**
     * 碰撞后保留速度的比率
     */
    protected float collisionDecelerationRate;

    public MovableUnit() {
    }

    public MovableUnit(BufferedImage img, Position position, float collisionRadius, float collisionDecelerationRate, float density) {
        this(position, collisionRadius, collisionDecelerationRate, density);
        setImgAndTempImg(img);
    }

    public MovableUnit(Position position, float collisionRadius, float collisionDecelerationRate, float density) {
        super(position,collisionRadius,density);
        this.collisionDecelerationRate = collisionDecelerationRate;
        this.direction = (float) (Math.PI * 2 * Math.random());

    }



    public void setImgAndTempImg(BufferedImage img) {
        super.img = img;
        this.tempImg = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
    }

    /**
     * 真正用于显示的图片(涉及图片旋转操作)
     */
    private BufferedImage tempImg;
    /**
     * 方向变化之前的方向
     */
    private float previousDirection;
    private boolean initialFlag = true;

    @Override
    public void draw(Graphics g) {
        //判断方向是否发生改变, 改变则重新生成图片, 不改变则复用图片
        if (previousDirection != direction || initialFlag) {
            previousDirection = direction;
            initialFlag = false;
            final Graphics2D tempImgGraphics = tempImg.createGraphics();
            //覆盖背景
            tempImgGraphics.setColor(new Color(238, 238, 238));
            final int offset = 15;
            tempImgGraphics.fillOval(-offset, -offset, img.getWidth() + offset * 2, img.getHeight() + offset * 2);
            //旋转
            tempImgGraphics.rotate(direction, img.getWidth() >> 1, img.getHeight() >> 1);
            //最后把图片放上去
            tempImgGraphics.drawImage(img, null, null);
        }
        g.drawImage(tempImg, Math.round(position.getX()), Math.round(position.getY()), width, height, null);
    }

    public Position displacement() {
        return displacement(speed, direction);
    }

    /**
     * 返回经过位移计算后的定位
     *
     * @param length    位移长度
     * @param direction 方向
     * @return 新定位
     */
    protected Position displacement(float length, float direction) {
        float x = this.position.getX();
        float y = this.position.getY();
        x += Math.cos(direction) * length;
        y += Math.sin(direction) * length;
        return new Position(x, y);
    }

    /**
     * 返回经过位移计算后的新定位
     *
     * @param length    位移长度
     * @param direction 方向
     * @param position  原定位
     * @return 新定位
     */
    protected Position displacement(float length, float direction, Position position) {
        float x = position.getX();
        float y = position.getY();
        x += Math.cos(direction) * length;
        y += Math.sin(direction) * length;
        return new Position(x, y);
    }

    /**
     * 移动
     */
    public abstract void move();

    /**
     * 碰撞减速
     */
    protected void collisionDeceleration() {
        this.speed *= collisionDecelerationRate;
    }



    public float getDirection() {
        return direction;
    }

    public void setDirection(float direction) {
        this.direction = direction;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getCollisionDecelerationRate() {
        return collisionDecelerationRate;
    }

    public void setCollisionDecelerationRate(float collisionDecelerationRate) {
        this.collisionDecelerationRate = collisionDecelerationRate;
    }

    @Override
    public String toString() {
        return "MovableUnit{" +
                "id=" + getId() +
                ", position=" + position +
                ", width=" + width +
                ", height=" + height +
                ", direction=" + direction +
                ", speed=" + speed +
                ", collisionRadius=" + collisionRadius +
                ", collisionDecelerationRate=" + collisionDecelerationRate +
                ", density=" + density +
                ", mass=" + mass +
                ", tempImg=" + tempImg +
                ", previousDirection=" + previousDirection +
                ", initialFlag=" + initialFlag +
                '}';
    }
}
