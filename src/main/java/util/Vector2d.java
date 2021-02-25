package util;

/**
 * 平面向量工具类
 * <p>
 * 参考链接: https://www.cnblogs.com/vokie/p/3602063.html
 */
public class Vector2d {
    private double x;
    private double y;

    public Vector2d() {
        x = 0;
        y = 0;
    }

    public Vector2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    //获取弧度
    public double getRadian() {
        return Math.atan2(y, x);
    }

    //获取角度
    public double getAngle() {
        return getRadian() / Math.PI * 180;
    }

    @Override
    public Vector2d clone() {
        return new Vector2d(x, y);
    }

    /**
     * 获取长度
     */
    public double getLength() {
        return Math.sqrt(getLengthSQ());
    }

    public double getLengthSQ() {
        return x * x + y * y;
    }

    //向量置零
    public Vector2d Zero() {
        x = 0;
        y = 0;
        return this;
    }

    /**
     * @return 返回零向量
     */
    public static Vector2d zeroVector() {
        return new Vector2d(0, 0);
    }

    public boolean isZero() {
        return x == 0 && y == 0;
    }

    //向量的长度设置为我们期待的value
    public void setLength(double value) {
        double _angle = getAngle();
        x = Math.cos(_angle) * value;
        y = Math.sin(_angle) * value;
    }

    //向量的标准化（方向不变，长度为1）
    public Vector2d normalize() {
        double length = getLength();
        x = x / length;
        y = y / length;
        return this;
    }

    //是否已经标准化
    public boolean isNormalized() {
        return getLength() == 1.0;
    }

    //向量的方向翻转
    public Vector2d reverse() {
        x = -x;
        y = -y;
        return this;
    }

    //2个向量的数量积(点积)
    public double dotProduct(Vector2d v) {
        return x * v.x + y * v.y;
    }

    //2个向量的向量积(叉积)
    public double crossProduct(Vector2d v) {
        return x * v.y - y * v.x;
    }

    //计算2个向量的夹角弧度
    //参考点积公式:v1 * v2 = cos<v1,v2> * |v1| *|v2|
    public static double radianBetween(Vector2d v1, Vector2d v2) {
        if (!v1.isNormalized()) v1 = v1.clone().normalize(); // |v1| = 1
        if (!v2.isNormalized()) v2 = v2.clone().normalize(); // |v2| = 1
        return Math.acos(v1.dotProduct(v2));
    }

    //弧度 = 角度乘以PI后再除以180、 推理可得弧度换算角度的公式
    //弧度转角度
    public static double radian2Angle(double radian) {
        return radian / Math.PI * 180;
    }

    //向量加
    public Vector2d add(Vector2d v) {
        return new Vector2d(x + v.x, y + v.y);
    }

    //向量减
    public Vector2d subtract(Vector2d v) {
        return new Vector2d(x - v.x, y - v.y);
    }

    //向量乘
    public Vector2d multiply(double value) {
        return new Vector2d(x * value, y * value);
    }

    //向量除
    public Vector2d divide(double value) {
        return new Vector2d(x / value, y / value);
    }

    @Override
    public String toString() {
        return "Vector2d{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}