package model.entity;

import model.Position;
import util.Constant;
import util.Vector2d;
import view.MainPanel;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author Yhaobo
 * @since 2020/10/27
 */
public abstract class MovableUnit implements Unit {
    /**
     * 单位图片
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
    /**
     * 方向 (弧度)
     */
    protected float direction = (float) (Math.PI * 2 * Math.random());
    /**
     * 前进速度
     */
    protected float speed;
    /**
     * 最大速度
     */
    protected float maxSpeed;
    /**
     * 碰撞半径
     */
    protected float collisionRadius;
    /**
     * 碰撞后保留速度的比率
     */
    protected float collisionDecelerationRate;
    /**
     * 密度
     */
    protected float density;
    /**
     * 重量
     */
    protected float weight;

    public MovableUnit(BufferedImage img, Position position, int width, int height, float maxSpeed, float collisionRadius, float collisionDecelerationRate, float density) {
        this.img = img;
        setPosition(position);
        this.width = width;
        this.height = height;
        this.maxSpeed = maxSpeed;
        this.collisionRadius = collisionRadius;
        this.collisionDecelerationRate = collisionDecelerationRate;
        this.density = density;
        tempImg = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        computeAndSetWeight();
    }

    private void computeAndSetWeight() {
        this.weight = (float) (density * Math.pow(collisionRadius, 3));
    }

    protected void setcollisionRadius(float radius) {
        this.collisionRadius = radius;
        computeAndSetWeight();
    }

    public static class CollisionHandler {

        /**
         * 撞击处理 (都当做小球弹性碰撞)
         */
        public static void handleCollision(MovableUnit unit1, MovableUnit unit2, double intersectsLength) {
            //碰撞分离(防止粘连)
//            float radian = (float) computeRadian(unit1.position, unit2.position);
//            unit1.setPosition(unit1.displacement(intersectsLength / 2, (float) (Math.PI + radian)));
//            unit2.setPosition(unit2.displacement(intersectsLength / 2, radian));
            //碰撞
            ballCollide(unit1, unit2);
            //速度受损
            //        unit1.collisionDeceleration();
            //        unit2.collisionDeceleration();
        }

        /**
         * 两小球弹性碰撞
         * <p>
         * 参考链接: https://www.jianshu.com/p/02ecbbb6afeb
         */
        private static void ballCollide(MovableUnit ball1, MovableUnit ball2) {
            // 初始速度向量
            Vector2d ball1SpeedInitial = ball1.getVector();
            Vector2d ball2SpeedInitial = ball2.getVector();

            // 球心方向单位向量
            final double radian = computeRadian(ball1.getCentrePosition(), ball2.getCentrePosition());
            Vector2d horizontalVector = getVector(radian, 1);

            // 垂直球心方向单位向量
            Vector2d perpendicularVector = getVector(radian + Constant.HALF_PI, 1);

            // 速度在球心向量上的分速度投影长度
            double ball1SpeedHorizontalProjectionLength = ball1SpeedInitial.dotProduct(horizontalVector);
            double ball2SpeedHorizontalProjectionLength = ball2SpeedInitial.dotProduct(horizontalVector);

            // 速度在垂直球心向量上的分速度投影长度
            double ball1SpeedPerpendicularProjectionLength = ball1SpeedInitial.dotProduct(perpendicularVector);
            double ball2SpeedPerpendicularProjectionLength = ball2SpeedInitial.dotProduct(perpendicularVector);

            // 碰撞后球心方向上的分速度投影长度
            double ball1SpeedHorizontalProjectionFinalLength;
            double ball2SpeedHorizontalProjectionFinalLength;
            if (ball1.weight == ball2.weight) {
                // 质量相等
                ball1SpeedHorizontalProjectionFinalLength = ball2SpeedHorizontalProjectionLength;
                ball2SpeedHorizontalProjectionFinalLength = ball1SpeedPerpendicularProjectionLength;
            } else {
                // 质量不相等
                ball1SpeedHorizontalProjectionFinalLength = (ball1SpeedHorizontalProjectionLength * (ball1.weight - ball2.weight) + 2 * ball2.weight * ball2SpeedHorizontalProjectionLength)
                        / (ball1.weight + ball2.weight);
                ball2SpeedHorizontalProjectionFinalLength= (ball2SpeedHorizontalProjectionLength * (ball2.weight - ball1.weight) + 2 * ball1.weight * ball1SpeedHorizontalProjectionLength)
                        / (ball1.weight + ball2.weight);
            }

            // 碰撞后球心方向上的分速度向量
            Vector2d ball1SpeedHorizontalProjectionFinalVector = horizontalVector.multiply(ball1SpeedHorizontalProjectionFinalLength);
            Vector2d ball2SpeedHorizontalProjectionFinalVector = horizontalVector.multiply(ball2SpeedHorizontalProjectionFinalLength);

            // 碰撞后垂直球心方向上的分速度向量
            Vector2d ball1SpeedPerpendicularProjectionVector = perpendicularVector.multiply(ball1SpeedPerpendicularProjectionLength);
            Vector2d ball2SpeedPerpendicularProjectionVector = perpendicularVector.multiply(ball2SpeedPerpendicularProjectionLength);

            // 两个球最终的速度向量
            Vector2d ball1SpeedFinalVector = ball1SpeedHorizontalProjectionFinalVector.add(ball1SpeedPerpendicularProjectionVector);
            Vector2d ball2SpeedFinalVector = ball2SpeedHorizontalProjectionFinalVector.add(ball2SpeedPerpendicularProjectionVector);

            // 更新速度
            applyVector(ball1SpeedFinalVector, ball1);
            applyVector(ball2SpeedFinalVector, ball2);
        }

        /**
         * 获取平面向量类
         */
        protected static Vector2d getVector(double radian, double length) {
            return new Vector2d(Math.cos(radian) * length, Math.sin(radian) * length);
        }

        /**
         * 根据两个点, 计算弧度
         */
        public static double computeRadian(Position position1, Position position2) {
            float y1 = position1.getY();
            float y2 = position2.getY();
            float x1 = position1.getX();
            float x2 = position2.getX();
            return Math.atan((y2 - y1) / (x2 - x1));
        }

        /**
         * 提前判断下一刻两个单位有没有碰撞(圆形碰撞检测)
         *
         * @param unit1 碰撞单位1
         * @param unit2 碰撞单位2
         * @return 大于0, 则没有碰撞, 小于等于0, 则碰撞
         */
        public static double inAdvanceDetectionIntersects(MovableUnit unit1, MovableUnit unit2) {
            final Position centrePosition1 = unit2.getCentrePosition(unit2.displacement());
            final Position centrePosition2 = unit1.getCentrePosition(unit1.displacement());

            float x = centrePosition1.getX() - centrePosition2.getX();
            float y = centrePosition1.getY() - centrePosition2.getY();
            double distance = Math.sqrt(x * x + y * y);

            return distance - (unit2.collisionRadius + unit1.collisionRadius);
        }

        /**
         * 应用平面向量
         *
         * @param vector
         * @param unit
         */
        private static void applyVector(Vector2d vector, MovableUnit unit) {
            unit.direction = (float) vector.getRadian();
            unit.speed = (float) vector.getLength();
        }
    }

    @Override
    public Position getCentrePosition() {
        return getCentrePosition(position);
    }

    protected Position getCentrePosition(Position position) {
        return new Position(position.getX() + (width >> 1), position.getY() + (height >> 1));
    }

    @Override
    public float getCollisionRadius() {
        return collisionRadius;
    }

    @Override
    public Unit setPosition(Position position) {
        if (this.position == null) {
            this.position = position;
        }
        return setPosition(position.getX(), position.getY());
    }

    protected Unit setPosition(float x, float y) {
        if (x < 0) {
            x = 0;
        } else {
            final float width = (float) (MainPanel.DIMENSION.getWidth() - this.width);
            if (x > width) {
                x = width;
            }
        }
        if (y < 0) {
            y = 0;
        } else {
            final float height = (float) (MainPanel.DIMENSION.getHeight() - this.height);
            if (y > height) {
                y = height;
            }
        }

        position.setX(x);
        position.setY(y);

        return this;
    }

    /**
     * 真正显示的图片
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
            tempImgGraphics.setColor(new Color(238, 238, 238));
            tempImgGraphics.fillOval(-30, -30, img.getWidth() + 60, img.getHeight() + 60);
            tempImgGraphics.rotate(direction, img.getWidth() >> 1, img.getHeight() >> 1);
            tempImgGraphics.drawImage(img, null, null);
//            tempImg= RotateImageUtil.rotateImage(img, direction);
        }
//        g.fillOval(Math.round(position.getX()), Math.round(position.getY()), width, height);
        g.drawImage(tempImg, Math.round(position.getX()), Math.round(position.getY()), width, height, null);
    }

    protected Position displacement() {
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

    protected Vector2d getVector() {
        return CollisionHandler.getVector(this.direction, this.speed);
    }

    /**
     * 移动
     */
    public abstract void move();

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
}
