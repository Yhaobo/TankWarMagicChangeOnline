package model.entity;

import model.Constant;
import model.Position;
import view.MainPanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * @author Yhaobo
 * @date 2021-01-27 12:30
 */
public class BlackHole extends CollisionableUnit {
    private static final BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_3BYTE_BGR);

    static {
        Graphics graphics = img.getGraphics();
        graphics.setColor(new Color(238, 238, 238));
        graphics.fillRect(0, 0, img.getWidth(), img.getHeight());
        graphics.setColor(Color.BLACK);
        graphics.fillOval(0, 0, img.getWidth(), img.getHeight());
    }

    public BlackHole() {
    }

    public BlackHole(Position position, float collisionRadius, float density) {
        super(position, collisionRadius, density);
        super.img = img;
    }
    /**
     * @return 返回一个随机定位（屏幕中心区域）
     */
    public static Position randomPosition() {
        final Random random = new Random();
        return new Position((float) ((random.nextInt((int) MainPanel.getDimension().getWidth() - Constant.TankConstant.WIDTH)) / 2 + MainPanel.getDimension().getWidth() / 4),
                (float) ((random.nextInt((int) MainPanel.getDimension().getHeight() - Constant.TankConstant.HEIGHT))/2+ MainPanel.getDimension().getHeight() / 4));
    }
    @Override
    public void renew() {
        super.img = BlackHole.img;
        super.setCollisionRadius(collisionRadius);
//        super.collisionDecelerationRate = Constant.CannonballConstant.COLLISION_DECELERATION_RATE;
    }
}
