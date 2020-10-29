package util;

/**
 * 常量类: 包括一些常量和初始值
 *
 * @author Yhaobo
 * @since 2020/10/27
 */
public interface Constant {
    double HALF_PI = Math.PI / 2;

    interface Tank {
        int WIDTH = 100;
        int HEIGHT = 100;
        //        float MAX_SPEED = 10;
        float MAX_SPEED = 100;
        /**
         * 碰撞半径
         */
        float COLLISION_RADIUS = 50;
        /**
         * 基础转弯速度
         */
        float TURN_SPEED = (float) (Math.PI / 50);
        /**
         * 加速度
         */
//        float ACCELERATED_SPEED = 0.05F;
        float ACCELERATED_SPEED = 0.5F;
        /**
         * 撞击后保留速度的比率
         */
        float COLLISION_DECELERATION_RATE = 0.3F;
//        float COLLISION_DECELERATION_RATE =1F;
        /**
         * 密度
         */
        float DENSITY = 100;
    }

    interface Cannonball {
        /**
         * 加速度
         */
//        float ACCELERATED_SPEED = -0.005F;
        float ACCELERATED_SPEED = 0F;
        float MAX_SPEED = 15;
        int WIDTH = 50;
        int HEIGHT = 50;
        /**
         * 碰撞半径
         */
        float COLLISION_RADIUS = 25;
        /**
         * 撞击后保留速度的比率
         */
//        float COLLISION_DECELERATION_RATE =0.7F;
        float COLLISION_DECELERATION_RATE = 1F;
        /**
         * 密度
         */
        float DENSITY = 5;
    }
}
