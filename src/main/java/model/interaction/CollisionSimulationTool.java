package model.interaction;

import model.Position;
import model.entity.MovableUnit;
import model.Constant;
import model.entity.Tank;
import util.Vector2d;

/**
 * 碰撞模拟工具类
 *
 * @author Yhaobo
 * @date 2021-01-10 14:27
 */
public class CollisionSimulationTool {

    /**
     * 处理撞击 (都当做小球弹性碰撞)
     */
    public static void handleCollision(MovableUnit unit1, MovableUnit unit2) {
            if (unit1.getMass() > 0 && unit2.getMass() > 0 && CollisionSimulationTool.inAdvanceDetectionIntersects(unit1, unit2)) {
                //碰撞
                ballCollide(unit1, unit2);
            }
    }

    /**
     * 两小球弹性碰撞
     * <p>
     * 参考链接: https://www.jianshu.com/p/02ecbbb6afeb
     */
    private static void ballCollide(MovableUnit ball1, MovableUnit ball2) {
        //初始速度向量
        Vector2d ball1SpeedInitial = getVector(ball1);
        Vector2d ball2SpeedInitial = getVector(ball2);

        //两球心弧度
        final double radian = computeRadian(ball1.getPosition(), ball2.getPosition());
        //球心方向单位向量
        Vector2d horizontalVector = getVector(radian, 1);

        //垂直球心方向单位向量
        Vector2d perpendicularVector = getVector(radian + Constant.HALF_PI, 1);

        //速度在球心向量上的分速度投影长度
        double ball1SpeedHorizontalProjectionLength = ball1SpeedInitial.dotProduct(horizontalVector);
        double ball2SpeedHorizontalProjectionLength = ball2SpeedInitial.dotProduct(horizontalVector);

        //速度在垂直球心向量上的分速度投影长度
        double ball1SpeedPerpendicularProjectionLength = ball1SpeedInitial.dotProduct(perpendicularVector);
        double ball2SpeedPerpendicularProjectionLength = ball2SpeedInitial.dotProduct(perpendicularVector);

        //碰撞后球心方向上的分速度投影长度
        double ball1SpeedHorizontalProjectionFinalLength;
        double ball2SpeedHorizontalProjectionFinalLength;
        if (ball1.getMass() == ball2.getMass()) {
            // 质量相等
            ball1SpeedHorizontalProjectionFinalLength = ball2SpeedHorizontalProjectionLength;
            ball2SpeedHorizontalProjectionFinalLength = ball1SpeedHorizontalProjectionLength;
        } else {
            // 质量不相等
            ball1SpeedHorizontalProjectionFinalLength = (ball1SpeedHorizontalProjectionLength * (ball1.getMass() - ball2.getMass()) + 2 * ball2.getMass() * ball2SpeedHorizontalProjectionLength)
                    / (ball1.getMass() + ball2.getMass());
            ball2SpeedHorizontalProjectionFinalLength = (ball2SpeedHorizontalProjectionLength * (ball2.getMass() - ball1.getMass()) + 2 * ball1.getMass() * ball1SpeedHorizontalProjectionLength)
                    / (ball1.getMass() + ball2.getMass());
        }

        //碰撞后球心方向上的分速度向量
        Vector2d ball1SpeedHorizontalProjectionFinalVector = horizontalVector.multiply(ball1SpeedHorizontalProjectionFinalLength);
        Vector2d ball2SpeedHorizontalProjectionFinalVector = horizontalVector.multiply(ball2SpeedHorizontalProjectionFinalLength);

        //碰撞后垂直球心方向上的分速度向量
        Vector2d ball1SpeedPerpendicularProjectionVector = perpendicularVector.multiply(ball1SpeedPerpendicularProjectionLength);
        Vector2d ball2SpeedPerpendicularProjectionVector = perpendicularVector.multiply(ball2SpeedPerpendicularProjectionLength);

        //两个球最终的速度向量
        Vector2d ball1SpeedFinalVector = ball1SpeedHorizontalProjectionFinalVector.add(ball1SpeedPerpendicularProjectionVector);
        Vector2d ball2SpeedFinalVector = ball2SpeedHorizontalProjectionFinalVector.add(ball2SpeedPerpendicularProjectionVector);

        //更新速度
        applyVector(ball1SpeedFinalVector, ball1);
        applyVector(ball2SpeedFinalVector, ball2);

        //坦克与坦克撞击只影响速度
        if (ball1 instanceof Tank && ball2 instanceof Tank) {
            return;
        }

        //球1被撞击后的变化量
        double changeLen1 = ball1SpeedFinalVector.subtract(ball1SpeedInitial).getLength();
        //球2被撞击后的变化量
        double changeLen2 = ball2SpeedFinalVector.subtract(ball2SpeedInitial).getLength();
        //捕获常量
        float captureConstant = Constant.Interaction.COLLISION_CAPTURE_CONSTANT;
        //碰撞伤害或捕获
        if (changeLen1 < captureConstant && ball1.getMass() > ball2.getMass()) {
            //球1被撞击后变化小于指定值, 且球1质量大于球2, 则球1捕获球2 (获得球2的质量)
            ball1.changeMass(ball2.getMass());
            ball2.setCollisionRadius(0);
        } else if (changeLen2 < captureConstant && ball2.getMass() > ball1.getMass()) {
            //球2被撞击后变化小于指定值, 且球2质量大于球1, 则球2捕获球1 (获得球1的质量)
            ball2.changeMass(ball1.getMass());
            ball1.setCollisionRadius(0);
        } else {
            //受到伤害都变小
            collisionalDamage(changeLen1, ball1);
            collisionalDamage(changeLen2, ball2);
        }
    }

    /**
     * 相撞后根据被撞击力度减少半径长度
     */
    private static void collisionalDamage(double impact, MovableUnit unit) {
        unit.setCollisionRadius((float) (unit.getCollisionRadius() - impact * Constant.Interaction.COLLISION_DAMAGE));
    }

    /**
     * 获取平面向量类
     *
     * @param radian 弧度
     * @param length 长度
     */
    public static Vector2d getVector(double radian, double length) {
        return new Vector2d(Math.cos(radian) * length, Math.sin(radian) * length);
    }

    /**
     * 获取可移动单位的速度向量
     */
    public static Vector2d getVector(MovableUnit movableUnit) {
        return new Vector2d(Math.cos(movableUnit.getDirection()) * movableUnit.getSpeed(), Math.sin(movableUnit.getDirection()) * movableUnit.getSpeed());
    }

    /**
     * 根据两个点, 计算弧度(以 position1 为基点)
     */
    public static double computeRadian(Position position1, Position position2) {
        float y1 = position1.getY();
        float y2 = position2.getY();
        float x1 = position1.getX();
        float x2 = position2.getX();

        double radian = Math.atan((y2 - y1) / (x2 - x1));
        if (x1 < x2) {
            return radian;
        } else {
            return radian + Math.PI;
        }
    }

    /**
     * 提前判断下一刻两个单位有没有碰撞(圆形碰撞检测)
     *
     * @param unit1 碰撞单位1
     * @param unit2 碰撞单位2
     * @return 大于0, 则没有碰撞, 小于等于0, 则碰撞
     */
    public static boolean inAdvanceDetectionIntersects(MovableUnit unit1, MovableUnit unit2) {
        final Position centrePosition1 = unit1.getCentrePosition(unit1.displacement());
        final Position centrePosition2 = unit2.getCentrePosition(unit2.displacement());

        return detectionIntersects(unit1.getCollisionRadius(), unit2.getCollisionRadius(), centrePosition1, centrePosition2);
    }

    /**
     * 判断两个单位有没有碰撞(圆形碰撞检测)
     *
     * @param unit1 碰撞单位1
     * @param unit2 碰撞单位2
     * @return 大于0, 则没有碰撞, 小于等于0, 则碰撞
     */
    public static boolean detectionIntersects(MovableUnit unit1, MovableUnit unit2) {
        final Position centrePosition1 = unit1.getCentrePosition();
        final Position centrePosition2 = unit2.getCentrePosition();

        return detectionIntersects(unit1.getCollisionRadius(), unit2.getCollisionRadius(), centrePosition1, centrePosition2);
    }

    /**
     * 检测两球形是否相交
     *
     * @param radius1         球1半径
     * @param radius2         球2半径
     * @param centrePosition1 球1中心位置
     * @param centrePosition2 球2中心位置
     */
    private static boolean detectionIntersects(float radius1, float radius2, Position centrePosition1, Position centrePosition2) {
        return computeDistance(centrePosition1, centrePosition2) <= (radius2 + radius1);
    }

    /**
     * 应用平面向量
     *
     * @param vector 向量
     * @param unit   要应用的单位
     */
    public static void applyVector(Vector2d vector, MovableUnit unit) {
        unit.setDirection((float) vector.getRadian());
        unit.setSpeed((float) vector.getLength());
    }

    /**
     * 根据两点坐标计算距离
     */
    public static double computeDistance(Position a, Position b) {
        float x = a.getX() - b.getX();
        float y = a.getY() - b.getY();
        return Math.sqrt(x * x + y * y);
    }
}
