package model.entity;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import model.Position;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.UUID;

/**
 * 游戏中的抽象单位, 坦克和炮弹都是单位
 *
 * @author Yhaobo
 * @date 2020/10/25
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Tank.class),
        @JsonSubTypes.Type(value = Player.class),
        @JsonSubTypes.Type(value = MovableUnit.class)
})
public abstract class Unit implements Identification {
    /**
     * 单位图片
     */
    protected BufferedImage img;
    /**
     * 定位
     */
    protected Position position;

    protected String id = UUID.randomUUID().toString();

    /**
     * 返回单位的定位
     *
     * @return 定位
     */
    public Position getPosition() {
        return position;
    }


    public void setPosition(Position position) {
        this.position = position;
    }

    /**
     * 绘制
     *
     * @param g 画布
     */
    public void draw(Graphics g) {
        g.drawImage(img, Math.round(position.getX()), Math.round(position.getY()), img.getWidth(), img.getHeight(), null);
    }

    /**
     * 反序列化生成的此类对象(只有部分属性赋值), 可以使用此方法来初始化其他必须的属性(从而成为可用的实体)
     */
    public abstract void renew();

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Unit unit = (Unit) o;
        return id.equals(unit.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
