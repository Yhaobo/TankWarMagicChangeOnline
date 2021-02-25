package model.entity;

import model.Position;
import model.Constant;
import view.MainPanel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * 炮弹
 *
 * @author Yhaobo
 * @date 2020/10/27
 */
public class Cannonball extends MovableUnit {
    private static BufferedImage img;

    static {
        try {
            img = ImageIO.read(Cannonball.class.getResourceAsStream("/滑稽.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加速度
     */
    protected float acceleration = Constant.CannonballConstant.BASIC_ACCELERATION;

    public Cannonball() {
    }

    public Cannonball(Position position, float direction,float collisionRadius) {
        super(img, position,collisionRadius, Constant.CannonballConstant.COLLISION_DECELERATION_RATE, Constant.CannonballConstant.DENSITY);
        this.direction = direction;
    }

    @Override
    public void renew() {
        super.setImgAndTempImg(Cannonball.img);
        super.setCollisionRadius(collisionRadius);
        super.collisionDecelerationRate = Constant.CannonballConstant.COLLISION_DECELERATION_RATE;
    }

    @Override
    public void move() {
        final Position displacement = super.displacement();
        float x = displacement.getX();
        float y = displacement.getY();

        //检查是否越过边界, 越过则反弹并减速
        if (x < 0 || x > MainPanel.getDimension().getWidth() - getWidth()) {
            direction = (float) (Math.PI - direction);
//            collisionDeceleration();
        } else if (y < 0 || y > MainPanel.getDimension().getHeight() - getHeight()) {
            direction = -direction;
//            collisionDeceleration();
        }

        //速度不断变慢
//        if (this.acceleration != 0) {
//            if (this.speed > 0) {
//                this.speed += this.acceleration * Math.pow(speed, 2);
//            } else {
//                this.speed = 0;
//            }
//        }
        super.setPositionAndVerify(x, y);
    }

    @Override
    public String toString() {
        return "Cannonball{" +
                "acceleratedSpeed=" + acceleration +
                ", direction=" + direction +
                ", speed=" + speed +
                ", collisionRadius=" + collisionRadius +
                ", collisionDecelerationRate=" + collisionDecelerationRate +
                ", density=" + density +
                ", img=" + img +
                ", position=" + position +
                '}';
    }
}
