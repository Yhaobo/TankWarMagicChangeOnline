package model;

/**
 * 常量类: 包括一些常量和初始值
 *
 * @author Yhaobo
 * @date 2020/10/27
 */
public interface Constant {
    /**
     * 90度角度对应的弧度
     */
    double HALF_PI = Math.PI / 2;
    /**
     * 每一帧的间隔时间
     */
    int STEP_INTERVAL_TIME = 8;

    interface Interaction {
        /**
         * 引力常量
         */
        float GRAVITATION_CONSTANT = 0.1F;
        /**
         * 撞击捕获常量（值越大越容易捕获）
         */
        float COLLISION_CAPTURE_CONSTANT = 0.1F;
        /**
         * 撞击伤害常量
         */
        float COLLISION_DAMAGE = 0.5F;
    }

    interface Network {
        /**
         * 默认数据监听端口
         */
        int DATA_LISTEN_PORT = 23333;
        /**
         * 默认连接监听端口
         */
        int CONNECT_LISTEN_PORT = 23330;
        /**
         * 默认组播地址
         */
        String MULTICAST_ADDRESS_NAME = "237.0.0.1";
        /**
         * 广播地址
         */
        String BROADCAST_ADDRESS_NAME = "255.255.255.255";
        /**
         * 数据链路层（以太网）的最大传输单元（字节）
         */
        int ETHERNET_MTU = 1472;
        /**
         * udp数据包最大大小
         */
        int UDP_MTU = 65507;
    }

    interface Tank {
        /**
         * 碰撞半径
         */
        float COLLISION_RADIUS = 50;
        /**
         * 宽
         */
        int WIDTH = Math.round(COLLISION_RADIUS * 2);
        /**
         * 高
         */
        int HEIGHT = Math.round(COLLISION_RADIUS * 2);
        /**
         * 密度
         */
        float DENSITY = 2F;
        /**
         * 初始质量
         */
        float MASS = (float) (DENSITY * Math.pow(COLLISION_RADIUS, 3));
        /**
         * 基础转向速度
         */
        float BASIC_TURN_SPEED = (float) (Math.PI / (1000 / STEP_INTERVAL_TIME));
        /**
         * 最大转向速度
         */
        float MAX_TURN_SPEED = BASIC_TURN_SPEED * 2;
        /**
         * 加速度
         */
        float BASIC_ACCELERATION = BASIC_TURN_SPEED / 5;
        /**
         * 最大加速度
         */
        float MAX_ACCELERATED_SPEED = BASIC_ACCELERATION * 30;
        /**
         * 撞击后保留速度的比率
         */
        float COLLISION_DECELERATION_RATE = 0.3F;
        /**
         * 射击动能
         */
        int SHOT_KINETIC_ENERGY = (int) (100000*(DENSITY/Cannonball.DENSITY));
    }

    interface Cannonball {

        /**
         * 加速度
         */
        float BASIC_ACCELERATION = 0F;
//        float BASIC_ACCELERATION = -0.001F;
        /**
         * 碰撞半径
         */
        float COLLISION_RADIUS = 20;
        /**
         * 宽
         */
        int WIDTH = Math.round(COLLISION_RADIUS * 2);
        /**
         * 高
         */
        int HEIGHT = Math.round(COLLISION_RADIUS * 2);
        /**
         * 撞击后保留速度的比率
         */
        float COLLISION_DECELERATION_RATE = .6F;
        /**
         * 密度
         */
        float DENSITY = 1F;
    }
}
