package model;

import view.MainPanel;

/**
 * @author Yhaobo
 * @since 2020/10/25
 */
public class Position {
    private float x;
    private float y;

    public Position(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        if (x >= 0 && x <= MainPanel.DIMENSION.getWidth()) {
            this.x = x;
        }
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        if (y >= 0 && y <= MainPanel.DIMENSION.getHeight()) {
            this.y = y;
        }
    }
}
