package model.entity;

import model.Position;
import util.Constant;
import view.MainPanel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @author Yhaobo
 * @since 2020/10/25
 */
public class Tank extends MovableUnit {
    private static BufferedImage img;

    static {
        try {
            img = ImageIO.read(Cannonball.class.getResourceAsStream("/hero1.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 转向速度
     */
    protected float turnSpeed = Constant.Tank.TURN_SPEED;
    /**
     * 前进加速度
     */
    protected float acceleratedSpeed = Constant.Tank.ACCELERATED_SPEED;

    public Tank(Position position) {
        super(img, position, Constant.Tank.WIDTH, Constant.Tank.HEIGHT, Constant.Tank.MAX_SPEED, Constant.Tank.COLLISION_RADIUS, Constant.Tank.COLLISION_DECELERATION_RATE,Constant.Tank.DENSITY);
    }

    public void turn(boolean isRightTurn) {
        float turnSpeed = (float) (this.turnSpeed * (maxSpeed / (Math.pow(speed, 2) + maxSpeed)));
        if (isRightTurn) {
            direction += turnSpeed;
        } else {
            direction -= turnSpeed;
        }
    }

    public void advance() {
        if (speed >= 0) {
            if (speed <= maxSpeed) {
                speed += acceleratedSpeed;
            } else {
                speed = maxSpeed;
            }
        } else {
            speed += acceleratedSpeed * 2;
        }
    }

    public void retreat() {
        if (speed > 0) {
            speed -= acceleratedSpeed * 2;
        } else {
            if (speed >= -maxSpeed) {
                speed -= acceleratedSpeed;
            } else {
                speed = -maxSpeed;
            }
        }
    }

    @Override
    public void move() {
        final Position offset = super.displacement();
        float x = offset.getX();
        float y = offset.getY();

        //检查是否越过边界, 越过则反弹并减速
        if (x <= 0 || x >= MainPanel.DIMENSION.getWidth() - width) {
            direction = (float) (Math.PI - direction);
            collisionDeceleration();
        }
        if (y <= 0 || y >= MainPanel.DIMENSION.getHeight() - height) {
            direction = -direction;
            collisionDeceleration();
        }

        super.setPosition(x, y);
    }

    public Cannonball shot() {
        float x = getCentrePosition().getX();
        float y = getCentrePosition().getY();
        final double radio = 2;
        x += Math.cos(direction) * collisionRadius* radio - (Constant.Cannonball.WIDTH >> 1);
        y += Math.sin(direction) * collisionRadius* radio - (Constant.Cannonball.HEIGHT >> 1);
        final Cannonball cannonball = new Cannonball(new Position(x, y), 10, direction);
        cannonball.setcollisionRadius((float) (Math.random() * 50)+10);
        cannonball.width = (int) (cannonball.collisionRadius*2);
        cannonball.height = (int) (cannonball.collisionRadius*2);
        return cannonball;
    }
}
