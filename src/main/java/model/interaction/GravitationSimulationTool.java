package model.interaction;

import model.Constant;
import model.entity.CollisionableUnit;
import model.entity.MovableUnit;
import util.Vector2d;

/**
 * 万有引力模拟工具类
 *
 * @author Yhaobo
 * @date 2021-01-10 15:07
 */
public class GravitationSimulationTool {
    public static void handleGravitation(CollisionableUnit unit1, CollisionableUnit unit2) {
        if (unit1.getMass() > 0 && unit2.getMass() > 0) {
            //计算引力F
            double f = computeGravitation(unit1, unit2) * Constant.InteractionConstant.GRAVITATION_CONSTANT;

            //单位1所受到的引力加速度和方向(弧度)
            double a1 = f / unit1.getMass();
            double radian1 = CollisionSimulationTool.computeRadian(unit1.getCentrePosition(), unit2.getCentrePosition());
            //单位2所收到的引力加速度和方向(弧度)
            double a2 = f / unit2.getMass();
            double radian2 = radian1 + Math.PI;

            //处理单位1的速度变化
            if (unit1 instanceof MovableUnit) {
                handleSpeedChange((MovableUnit) unit1, a1, radian1);
            }
            //处理单位2的速度变化
            if (unit2 instanceof MovableUnit) {
                handleSpeedChange((MovableUnit) unit2, a2, radian2);
            }
        }
    }

    /**
     * 处理单位受到加速度后的速度(包括方向)变化
     *
     * @param unit            可移动单位
     * @param accelerationLen 加速度大小
     * @param radian          弧度
     */
    private static void handleSpeedChange(MovableUnit unit, double accelerationLen, double radian) {
        //单位所受到的引力加速度的平面向量
        Vector2d acceleration = CollisionSimulationTool.getVector(radian, accelerationLen);
        //单位的速度向量
        Vector2d speed = CollisionSimulationTool.getVector(unit);
        //最终速度向量
        Vector2d finalSpeed = speed.add(acceleration);
        //应用最终向量
        CollisionSimulationTool.applyVector(finalSpeed, unit);
    }

    private static double computeGravitation(CollisionableUnit unit1, CollisionableUnit unit2) {
        return (unit1.getMass() * unit2.getMass()) / Math.pow(CollisionSimulationTool.computeDistance(unit1.getCentrePosition(), unit2.getCentrePosition()), 2);
    }
}
