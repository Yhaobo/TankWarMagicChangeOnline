package model.entity;

import model.Position;
import model.Constant;
import view.MainPanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 玩家控制单位
 *
 * @author Yhaobo
 * @date 2020/10/25
 */
public class Player extends Tank {
    /**
     * 被使用的图片序号
     */
    private static final List<Integer> USED_HERO_INDEX_LIST = new ArrayList<>();
    private int heroIdx;
    private BufferedImage img;

    /**
     * 玩家名, 默认为本地主机域名
     */
    private String name;

    /**
     * 用于反序列化创建对象
     */
    public Player() {
    }

    /**
     * 创建可使用的Player
     *
     * @param manual 非反序列化创建
     */
    public Player(InetAddress inetAddress) {
        super(randomPosition());
        randomAndSetImg();
        name = inetAddress.getHostName();
    }

    public static Player newPlayer() {
        try {
            return new Player(InetAddress.getLocalHost());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void randomAndSetImg() {
        try {
            //每次创建对象时随机使用一张英雄图片(不重复)
            if (USED_HERO_INDEX_LIST.size() >= 12) {
                throw new RuntimeException("最多创建12个Player对象");
            }
            heroIdx = (int) (Math.random() * 12);
            while (USED_HERO_INDEX_LIST.contains(heroIdx)) {
                heroIdx++;
                if (heroIdx >= 12) {
                    heroIdx = 0;
                }
            }
            USED_HERO_INDEX_LIST.add(heroIdx);
            img = ImageIO.read(Player.class.getResourceAsStream("/hero" + heroIdx + ".png"));
            super.setImgAndTempImg(img);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Position randomPosition() {
        final Random random = new Random();
        return new Position(random.nextInt((int) MainPanel.getDimension().getWidth() - Constant.Tank.WIDTH), random.nextInt((int) MainPanel.getDimension().getHeight() - Constant.Tank.HEIGHT));
    }

    @Override
    public void renew() {
        try {
            this.img = ImageIO.read(Player.class.getResourceAsStream("/hero" + heroIdx + ".png"));
            super.setImgAndTempImg(this.img);
            super.setDensity(Constant.Tank.DENSITY);
            super.setCollisionRadius(collisionRadius);
            super.collisionDecelerationRate = Constant.Tank.COLLISION_DECELERATION_RATE;
            this.name = InetAddress.getLocalHost().getCanonicalHostName();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void draw(Graphics g) {
        g.setFont(new Font("宋体", Font.BOLD, 20));
        g.drawString(name, Math.round(this.position.getX()), Math.round(this.position.getY() - 35));
        g.drawRect(Math.round(this.position.getX()), Math.round(this.position.getY() - 30), this.getWidth(), 10);
        g.fillRect(Math.round(this.position.getX()), Math.round(this.position.getY() - 30), Math.round(this.getWidth() * (getMass() / Constant.Tank.MASS)), 10);
        super.draw(g);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public int getHeroIdx() {
        return heroIdx;
    }

    public void setHeroIdx(int heroIdx) {
        this.heroIdx = heroIdx;
    }

    @Override
    public String toString() {
        return "Player{" +
                "turnSpeed=" + turnSpeed +
                ", acceleratedSpeed=" + acceleration +
                ", id=" + getId() +
                ", position=" + position +
                ", direction=" + direction +
                ", speed=" + speed +
                ", collisionRadius=" + collisionRadius +
                ", collisionDecelerationRate=" + collisionDecelerationRate +
                ", density=" + density +
                ", mass=" + getMass() +
                '}';
    }

    public void setId(String playerId) {
        this.id = playerId;
    }

}
