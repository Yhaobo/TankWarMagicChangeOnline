package util;

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
    /**
     * 物体重叠获得加速度
     */
    float INTERSECTION_SPEED_UP = 0.3F;

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
        float DENSITY = 5F;
        /**
         * 重量
         */
        float WEIGHT = (float) (DENSITY * Math.pow(COLLISION_RADIUS, 3));
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
        float BASIC_ACCELERATED_SPEED = BASIC_TURN_SPEED / 5;
        /**
         * 最大加速度
         */
        float MAX_ACCELERATED_SPEED = BASIC_ACCELERATED_SPEED * 30;
        /**
         * 撞击后保留速度的比率
         */
        float COLLISION_DECELERATION_RATE = 0.3F;
    }

    interface Cannonball {
        /**
         * 加速度
         */
        float ACCELERATED_SPEED = -0.001F;
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
        float COLLISION_DECELERATION_RATE = 1F;
        /**
         * 密度
         */
        float DENSITY = 1F;
    }
}
