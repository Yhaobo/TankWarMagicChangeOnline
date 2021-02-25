package view;

import controller.TankWarOnlineApplication;
import model.entity.Player;
import model.entity.Unit;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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
        List<Player> playerList = new ArrayList<>();
        for (int i = 0; i < application.getUnitList().size(); i++) {
            Unit unit = application.getUnitList().get(i);
            if (application.getPlayer() != null && unit instanceof Player) {
                playerList.add((Player) unit);
            } else {
                unit.draw(g);
            }
        }
        //将玩家最后渲染以保证玩家上方显示信息不被遮盖
        playerList.sort((o1, o2) -> (int) (o1.getPosition().getY()-o2.getPosition().getY()));
        for (Player player : playerList) {
            if (application.getPlayer().getId().equals(player.getId())) {
                g.setColor(new Color(103, 194, 58));
            } else {
                g.setColor(new Color(245, 108, 108));
            }
            player.draw(g);
        }
        if (application.getPlayer()!=null&&application.getPlayer().getMass() <= 0) {
            g.setColor(Color.RED);
            g.setFont(new Font("楷体", 1, 50));
            g.drawString("犹豫就会败北！果断就会白给！", (int) (dimension.getWidth() / 4) ,  (int)(dimension.getHeight() / 3));
        }
    }
}