package model.entity;

import model.Position;
import model.Constant;
import view.MainPanel;

import java.awt.image.BufferedImage;

/**
 * @author Yhaobo
 * @date 2020/10/25
 */
public abstract class Tank extends MovableUnit {
    protected BufferedImage img;
    /**
     * 转向速度
     */
    protected float turnSpeed = Constant.TankConstant.BASIC_TURN_SPEED;
    /**
     * 加速度
     */
    protected float acceleration = Constant.TankConstant.BASIC_ACCELERATION;

    public Tank() {
    }

    public Tank(Position position) {
        super(position, Constant.TankConstant.COLLISION_RADIUS, Constant.TankConstant.COLLISION_DECELERATION_RATE, Constant.TankConstant.DENSITY);
    }

    /**
     * 转向
     *
     * @param isRightTurn true则右转, false则左转
     */
    public void turn(boolean isRightTurn) {
        final float turnSpeed = Math.min(this.turnSpeed * (Constant.TankConstant.MASS / getMass()), Constant.TankConstant.MAX_TURN_SPEED);
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
        final float acceleration = Math.min(this.acceleration * (Constant.TankConstant.MASS / getMass()), Constant.TankConstant.MAX_ACCELERATED_SPEED);
        if (speed >= 0) {
            speed += acceleration;
        } else {
            speed += acceleration * 5;
        }
    }

    /**
     * 刹车
     */
    public void braking() {
        final float acceleratedSpeed = Math.min(this.acceleration * (Constant.TankConstant.MASS / getMass()), Constant.TankConstant.MAX_ACCELERATED_SPEED);
        if (speed > 0) {
            speed = Math.max(speed -= acceleratedSpeed * 10, 0);
        } else if (speed < 0) {
            speed = 0;
        }
    }

    @Override
    public void move() {
        final Position offset = super.displacement();
        float x = offset.getX();
        float y = offset.getY();

        //检查是否越过边界, 越过则反弹并减速
        if (x <= 0 || x >= MainPanel.getDimension().getWidth() - getWidth()) {
            direction = (float) (Math.PI - direction);
//            collisionDeceleration();
        }
        if (y <= 0 || y >= MainPanel.getDimension().getHeight() - getHeight()) {
            direction = -direction;
//            collisionDeceleration();
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
        if (super.collisionRadius < Constant.UnitConstant.ANNIHILATION_RADIUS) {
            return null;
        }
        //处理炮弹出现位置
        final Position position = this.displacement(radius + collisionRadius + 5, direction, getCentrePosition());
        position.setX(position.getX() - radius);
        position.setY(position.getY() - radius);
        //生成炮弹, 炮弹方向为坦克方向
        final Cannonball cannonball = new Cannonball(position, direction, radius);
        //炮弹动能
        final int energy = Constant.TankConstant.SHOT_KINETIC_ENERGY;
        cannonball.speed = energy / cannonball.getMass() + this.speed;
        //反作用力
        this.speed -= energy / this.getMass();
        return cannonball;
    }

}
