package model.entity;

import model.Position;
import util.Constant;
import view.MainPanel;

import java.awt.image.BufferedImage;

/**
 * @author Yhaobo
 * @date 2020/10/25
 */
public class Tank extends MovableUnit {
    protected BufferedImage img;
    /**
     * 转向速度
     */
    protected float turnSpeed = Constant.Tank.BASIC_TURN_SPEED;
    /**
     * 加速度
     */
    protected float acceleratedSpeed = Constant.Tank.BASIC_ACCELERATED_SPEED;

    public Tank() {
    }

    public Tank(Position position) {
        super( position, Constant.Tank.WIDTH, Constant.Tank.HEIGHT, Constant.Tank.COLLISION_RADIUS, Constant.Tank.COLLISION_DECELERATION_RATE, Constant.Tank.DENSITY);
    }

    /**
     * 转向
     *
     * @param isRightTurn true则右转, false则左转
     */
    public void turn(boolean isRightTurn) {
        final float turnSpeed = Math.min(this.turnSpeed * (Constant.Tank.WEIGHT / getWeight()), Constant.Tank.MAX_TURN_SPEED);
        if (isRightTurn) {
            this.direction += turnSpeed;
        } else {
            this.direction -= turnSpeed;
        }
    }

    /**
     * 前进
     */
    public void advance() {
        final float acceleratedSpeed = Math.min(this.acceleratedSpeed * (Constant.Tank.WEIGHT / getWeight()), Constant.Tank.MAX_ACCELERATED_SPEED);
        if (speed >= 0) {
            speed += acceleratedSpeed;
        } else {
            speed += acceleratedSpeed * 5;
        }
    }

    /**
     * 后退
     */
    public void retreat() {
        final float acceleratedSpeed = Math.min(this.acceleratedSpeed * (Constant.Tank.WEIGHT / getWeight()), Constant.Tank.MAX_ACCELERATED_SPEED);
        if (speed > 0) {
            speed -= acceleratedSpeed * 5;
        } else {
            speed -= acceleratedSpeed;
        }
    }

    @Override
    public void renew() {
    }

    @Override
    public void move() {
        final Position offset = super.displacement();
        float x = offset.getX();
        float y = offset.getY();

        //检查是否越过边界, 越过则反弹并减速
        if (x <= 0 || x >= MainPanel.getDimension().getWidth() - getWidth()) {
            direction = (float) (Math.PI - direction);
            collisionDeceleration();
        }
        if (y <= 0 || y >= MainPanel.getDimension().getHeight() - getHeight()) {
            direction = -direction;
            collisionDeceleration();
        }

        super.setPositionAndVerify(x, y);
    }

    /**
     * 射击
     *
     * @param radius 炮弹半径
     * @return
     */
    public Cannonball shot(float radius) {
        //处理炮弹出现位置
        final Position position = this.displacement(radius + collisionRadius + 5, direction, getCentrePosition());
        position.setX(position.getX() - radius);
        position.setY(position.getY() - radius);
        //生成炮弹, 炮弹方向为坦克方向
        final Cannonball cannonball = new Cannonball(position, direction);
        cannonball.setCollisionRadiusAndCorrelationField(radius);
        //炮弹动能
        final int energy = 500000;
        cannonball.speed = energy / cannonball.getWeight() + this.speed;
        this.speed -= energy / this.getWeight();
        return cannonball;
    }

}
