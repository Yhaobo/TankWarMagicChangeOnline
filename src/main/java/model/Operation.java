package model;

import model.entity.Cannonball;
import model.entity.Player;
import model.network.dto.StateSyncMessageInfo;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * @author Yhaobo
 * @date 2020/10/26
 */
public class Operation implements KeyListener, MouseListener {
    private boolean front;
    private boolean back;
    private boolean left;
    private boolean right;

    private byte cannonballSize = CannonballSize.NONE;

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

            default:

        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        Cannonball unit;
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
            case KeyEvent.VK_J:
                this.cannonballSize = CannonballSize.BIG;
                break;
            case KeyEvent.VK_K:
                this.cannonballSize = CannonballSize.MEDIUM;
                break;
            case KeyEvent.VK_L:
                this.cannonballSize = CannonballSize.SMALL;
                break;
            default:
        }
    }

    /**
     * 处理玩家的所有操作
     *
     * @param player 当前玩家
     * @return null 没有射击；Cannonball的实例 射击
     */
    public Cannonball handlePlayerAction(Player player) {
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

        final byte size = this.cannonballSize;
        if (size != CannonballSize.NONE) {
            reset();
            return player.shot(size);
        }
        return null;
    }

    public static Cannonball handlePlayerAction(Player player, StateSyncMessageInfo.SlaveSendDatagram slaveSendDatagram) {
        if (slaveSendDatagram.getBack()) {
            player.retreat();
        }
        if (slaveSendDatagram.getFront()) {
            player.advance();
        }
        if (slaveSendDatagram.getLeft()) {
            player.turn(false);
        }
        if (slaveSendDatagram.getRight()) {
            player.turn(true);
        }

        final byte size = (byte) slaveSendDatagram.getCannonballSize();
        if (size != CannonballSize.NONE) {
            return player.shot(size);
        }
        return null;
    }

    public boolean isFront() {
        return front;
    }

    public void setFront(boolean front) {
        this.front = front;
    }

    public boolean isBack() {
        return back;
    }

    public void setBack(boolean back) {
        this.back = back;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public byte getCannonballSize() {
        return cannonballSize;
    }

    public void setCannonballSize(byte cannonballSize) {
        this.cannonballSize = cannonballSize;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        e.getComponent().requestFocus();
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    public void reset() {
        this.cannonballSize = CannonballSize.NONE;
    }

    private interface CannonballSize {
        byte BIG = 60;
        byte MEDIUM = 40;
        byte SMALL = 25;
        byte NONE = 0;
    }

    @Override
    public String toString() {
        return "Operation{" +
                "front=" + front +
                ", back=" + back +
                ", left=" + left +
                ", right=" + right +
                ", cannonballSize=" + cannonballSize +
                '}';
    }
}
