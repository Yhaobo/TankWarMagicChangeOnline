package model.network;

import model.Operation;
import model.entity.MovableUnit;
import model.entity.Unit;
import model.network.dto.HostDatagram;
import model.network.dto.SlaveDatagram;
import util.Constant;
import view.MainPanel;

import java.awt.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.List;

/**
 * @author Yhaobo
 * @date 2020/11/3
 */
public class SlaveNetworkService extends NetworkService {

    private final DataReceiver<HostDatagram> receiver = new DataReceiver<>(HostDatagram.class);

    private DataSender sender;

    /**
     * 连接主机
     *
     * @param hostAddress 主机地址
     * @return 控制单位的id; 为 null 则连接失败
     */
    public String connectHost(InetAddress hostAddress) {
        try {
            final byte[] bytes = ConnectConst.CONNECT_FLAG.getBytes();
            DatagramSocket socket = new DatagramSocket();
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, hostAddress, Constant.Network.CONNECT_LISTEN_PORT);
            socket.send(packet);
            socket.send(packet);
            socket.setSoTimeout(100);
            packet.setData(new byte[ConnectConst.MAX_LENGTH]);
            socket.receive(packet);
            final String multicastAddress = new String(packet.getData(), 0, packet.getLength());
            System.out.println("得到主机回复的组播地址: " + multicastAddress);
            receiver.joinGroup(InetAddress.getByName(multicastAddress));
            packet.setData(ConnectConst.OK_FLAG.getBytes());
            socket.send(packet);
            socket.send(packet);
            packet.setData(new byte[ConnectConst.MAX_LENGTH]);
            socket.receive(packet);
            while (packet.getLength() < 32) {
                socket.receive(packet);
            }
            final String msg = new String(packet.getData(), 0, packet.getLength());
            System.out.println("得到主机回复的控制单位id及窗口大小信息: " + msg);
            String[] split = msg.split(";");
            final String playerId = split[0];
            split = split[1].split(",");
            MainPanel.setDimension(new Dimension(Integer.parseInt(split[0]), Integer.parseInt(split[1])));
            this.sender = new DataSender(packet.getAddress());
            return playerId;
        } catch (SocketTimeoutException e) {
            System.out.println("连接主机超时");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 接收来自主机的数据
     * <p>
     * 将旧数据更新 (不是替换)
     *
     * @param oldUnits 旧数据
     */
    public void acceptData(List<Unit> oldUnits) {
        try {
            final HostDatagram datagram = receiver.receive(new DatagramPacket(new byte[64 * 1024], 64 * 1024));
            final List<Unit> newUnits = datagram.getUnits();

            final Integer[] deleteUnitIndexList = datagram.getDeleteUnitIndexList();
            //新删除的单位
            if (deleteUnitIndexList != null && deleteUnitIndexList.length > 0) {
                for (int i : deleteUnitIndexList) {
                    oldUnits.remove(i);
                }
            }

            final int oldSize = oldUnits.size();
            //新增的单位
            if (oldSize < newUnits.size()) {
                for (int i = oldSize; i < newUnits.size(); i++) {
                    final Unit unit = newUnits.get(i);
                    unit.renew();
                    oldUnits.add(unit);
                }
            }
            //更新部分属性
            for (int i = 0; i < oldSize; i++) {
                final Unit newUnit = newUnits.get(i);
                final Unit oldUnit = oldUnits.get(i);
                if (newUnit.getId().equals(oldUnit.getId())) {
                    oldUnit.setPosition(newUnit.getPosition());
                    if (newUnit instanceof MovableUnit && oldUnit instanceof MovableUnit) {
                        ((MovableUnit) oldUnit).setDirection(((MovableUnit) newUnit).getDirection());
                        ((MovableUnit) oldUnit).setSpeed(((MovableUnit) newUnit).getSpeed());
                        ((MovableUnit) oldUnit).setCollisionRadius(((MovableUnit) newUnit).getCollisionRadius());
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendOperation(Operation operation) {
        try {
            final SlaveDatagram slaveDatagram = new SlaveDatagram();
            slaveDatagram.setOperation(operation);
            sender.send(slaveDatagram);
            //重置
            operation.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
