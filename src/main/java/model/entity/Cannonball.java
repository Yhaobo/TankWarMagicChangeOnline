package model.entity;

import model.Position;
import util.Constant;
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
    protected float acceleratedSpeed = Constant.Cannonball.ACCELERATED_SPEED;

    public Cannonball() {
    }

    public Cannonball(Position position, float direction) {
        super(img, position, Constant.Cannonball.WIDTH, Constant.Cannonball.HEIGHT, Constant.Cannonball.COLLISION_RADIUS, Constant.Cannonball.COLLISION_DECELERATION_RATE, Constant.Cannonball.DENSITY);
        super.direction = direction;
    }

    @Override
    public void renew() {
        super.setImgAndTempImg(Cannonball.img);
        super.setCollisionRadiusAndCorrelationField(collisionRadius);
        super.collisionDecelerationRate = Constant.Cannonball.COLLISION_DECELERATION_RATE;
    }

    @Override
    public void move() {
        final Position offset = super.displacement();
        float x = offset.getX();
        float y = offset.getY();

        //检查是否越过边界, 越过则反弹并减速
        if (x < 0 || x > MainPanel.getDimension().getWidth() - getWidth()) {
            direction = (float) (Math.PI - direction);
            collisionDeceleration();
        } else if (y < 0 || y > MainPanel.getDimension().getHeight() - getHeight()) {
            direction = -direction;
            collisionDeceleration();
        }

        //速度不断变慢
        if (this.speed > 0) {
            this.speed += this.acceleratedSpeed * Math.pow(speed, 2);
        } else {
            this.speed = 0;
        }
        super.setPositionAndVerify(x, y);
    }

    @Override
    public String toString() {
        return "Cannonball{" +
                "acceleratedSpeed=" + acceleratedSpeed +
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
