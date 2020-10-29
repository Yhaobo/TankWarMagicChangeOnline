package model;

import model.entity.Cannonball;
import model.entity.Player;
import model.entity.Unit;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

/**
 * @author Yhaobo
 * @since 2020/10/26
 */
public class Acation implements KeyListener {

    private final List<Unit> units;
    private final Player player;

    private boolean front;
    private boolean back;
    private boolean left;
    private boolean right;

    public Acation(List<Unit> units, Player player) {
        this.units = units;
        this.player = player;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                this.front = true;
                break;
            case KeyEvent.VK_S:
                this.back = true;
                break;
            case KeyEvent.VK_A:
                this.left = true;
                break;
            case KeyEvent.VK_D:
                this.right = true;
                break;
            case KeyEvent.VK_SPACE:
                final Cannonball unit = player.shot();
                if (unit != null) {
                    units.add(unit);
                }
                break;
            default:

        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                this.front = false;
                break;
            case KeyEvent.VK_S:
                this.back = false;
                break;
            case KeyEvent.VK_A:
                this.left = false;
                break;
            case KeyEvent.VK_D:
                this.right = false;
                break;
            default:
        }
    }

    public void action() {
        if (this.back) {
            player.retreat();
        }
        if (this.front) {
            player.advance();
        }
        if (this.left) {
            player.turn(false);
        }
        if (this.right) {
            player.turn(true);
        }

    }
}
