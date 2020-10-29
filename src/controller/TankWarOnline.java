package controller;

import model.Acation;
import model.Position;
import model.entity.MovableUnit;
import model.entity.Player;
import model.entity.Unit;
import view.MainFrame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Yhaobo
 * @since 2020/10/25
 */
public class TankWarOnline {
    private final List<Unit> units = Collections.synchronizedList(new ArrayList<>());
    private final Player player = new Player(new Position(50, 50));

    private final Acation acation = new Acation(units, player);

    {
        units.add(player);
    }

    MainFrame frame = new MainFrame("坦克大战联机版", units, acation);

    public static void main(String[] args) throws InterruptedException {
        final TankWarOnline tankWarOnline = new TankWarOnline();
        tankWarOnline.start();
    }

    private void start() throws InterruptedException {
        for (; ; ) {
            step();
            TimeUnit.MILLISECONDS.sleep(16);
        }
    }

    public void step() {
        acation.action();
        for (int i = 0; i < units.size(); i++) {
            Unit unit1 = units.get(i);

            if (unit1 instanceof MovableUnit) {
                final MovableUnit movableUnit1 = (MovableUnit) unit1;
                for (int j = i + 1; j < units.size(); j++) {
                    Unit unit2 = units.get(j);
                    if (unit2 instanceof MovableUnit) {
                        final double intersectsLength = MovableUnit.CollisionHandler.inAdvanceDetectionIntersects(movableUnit1, (MovableUnit) unit2);
                        if (intersectsLength <= 0) {
                            //相撞
                            MovableUnit.CollisionHandler.handleCollision(((MovableUnit) unit1), (MovableUnit) unit2, intersectsLength);
                        }
                    }
                }
                movableUnit1.move();
            }
        }
    }
}
