package view;

import model.entity.Unit;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Yhaobo
 * @since 2020/10/25
 */
public class MainPanel extends JPanel {
    public static final Dimension DIMENSION = new Dimension(1600, 900);
    private List<Unit> units;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public MainPanel(List<Unit> units) {
        super(true);
        this.units = units;
        setPreferredSize(DIMENSION);
        scheduler.scheduleAtFixedRate(this::repaint, 16, 16, TimeUnit.MILLISECONDS);
    }

    public void setUnits(List<Unit> units) {
        this.units = units;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Unit unit : units) {
            unit.draw((Graphics2D) g);
        }
    }
}