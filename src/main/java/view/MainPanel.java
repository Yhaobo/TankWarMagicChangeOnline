package view;

import controller.TankWarOnlineApplication;
import model.entity.Unit;

import javax.swing.*;
import java.awt.*;

/**
 * @author Yhaobo
 * @date 2020/10/25
 */
public class MainPanel extends JPanel {
    private static Dimension dimension;

    static {
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        dimension = (new Dimension((int) (screenSize.getWidth() * .8), (int) (screenSize.getHeight() * .8)));
    }

    private TankWarOnlineApplication application;

    public MainPanel(TankWarOnlineApplication application) {
        super(true);
        this.application = application;
        addKeyListener(application.getOperation());
        addMouseListener(application.getOperation());
        setPreferredSize(dimension);
    }

    public static Dimension getDimension() {
        return dimension;
    }

    public static void setDimension(Dimension dimension) {
        MainPanel.dimension = dimension;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int i = 0; i < application.getUnitList().size(); i++) {
            Unit unit = application.getUnitList().get(i);
            if (application.getPlayer() != null && application.getPlayer().getId().equals(unit.getId())) {
                g.setColor(new Color(103, 194, 58));
            } else {
                g.setColor(new Color(245, 108, 108));
            }
            unit.draw(g);
        }
    }
}