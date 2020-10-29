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
 * @since 2020/10/27
 */
public class Cannonball extends MovableUnit {
    private static BufferedImage img;

    static {
        try {
            img = ImageIO.read(Cannonball.class.getResourceAsStream("/嘟嘴滑稽.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加速度
     */
    protected float acceleratedSpeed = Constant.Cannonball.ACCELERATED_SPEED;

    public Cannonball(Position position, float speed, float direction) {
        super(img, position, Constant.Cannonball.WIDTH, Constant.Cannonball.HEIGHT, Constant.Cannonball.MAX_SPEED, Constant.Cannonball.COLLISION_RADIUS, Constant.Cannonball.COLLISION_DECELERATION_RATE,Constant.Cannonball.DENSITY);
        super.speed = speed;
        super.direction = direction;
    }

    @Override
    public void move() {
        final Position offset = super.displacement();
        float x = offset.getX();
        float y = offset.getY();

        //检查是否越过边界, 越过则反弹并减速
        if (x < 0 || x > MainPanel.DIMENSION.getWidth() - width) {
            direction = (float) (Math.PI - direction);
            collisionDeceleration();
        } else if (y < 0 || y > MainPanel.DIMENSION.getHeight() - height) {
            direction = -direction;
            collisionDeceleration();
        }

        //速度不断变慢
        if (this.speed > 0) {
            this.speed += acceleratedSpeed;
        } else {
            this.speed = 0;
        }
        super.setPosition(x, y);
    }



}
