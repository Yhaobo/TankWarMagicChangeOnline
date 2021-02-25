package model.entity;

import model.Position;

/**
 * 可碰撞单位
 * @author Yhaobo
 * @date 2021-01-27 12:38
 */
public abstract class CollisionableUnit extends Unit{
    /**
     * 碰撞半径
     */
    protected float collisionRadius;
    /**
     * 密度
     */
    protected float density;
    /**
     * 质量
     */
    protected float mass;

    public CollisionableUnit() {
    }

    public CollisionableUnit( Position position, float collisionRadius, float density) {
        super(position, Math.round(collisionRadius * 2), Math.round(collisionRadius * 2));
        this.collisionRadius = collisionRadius;
        this.density = density;
        computeAndSetMass();

    }

    /**
     * 变化质量, 同时变化碰撞半径和显示大小
     *
     * @param variation 变化量
     */
    public void changeMass(float variation) {
        mass += variation;
        collisionRadius = (float) Math.pow(mass / density, 1.0 / 3);
        width = Math.round(collisionRadius * 2);
        height = Math.round(collisionRadius * 2);
    }

    private void computeAndSetMass() {
        mass = (float) (density * Math.pow(collisionRadius, 3));
    }


    /**
     * 设置碰撞半径, 同时设置显示大小以及质量
     *
     * @param radius
     */
    public void setCollisionRadius(float radius) {
        collisionRadius = radius;
        width = Math.round(radius * 2);
        height = Math.round(radius * 2);
        computeAndSetMass();
    }


    /**
     * 返回单位的碰撞半径
     *
     * @return 碰撞半径
     */
    public float getCollisionRadius() {
        return collisionRadius;
    }

    public void setDensity(float density) {
        this.density = density;
    }

    public float getMass() {
        return mass;
    }
    public float getDensity() {
        return density;
    }
}
