package model;

import view.MainPanel;

/**
 * @author Yhaobo
 * @date 2020/10/25
 */
public class Position {
    private float x;
    private float y;

    public Position() {
    }

    public Position(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        if (x >= 0 && x <= MainPanel.getDimension().getWidth()) {
            this.x = x;
        }
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        if (y >= 0 && y <= MainPanel.getDimension().getHeight()) {
            this.y = y;
        }
    }



    @Override
    public String toString() {
        return "Position{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
