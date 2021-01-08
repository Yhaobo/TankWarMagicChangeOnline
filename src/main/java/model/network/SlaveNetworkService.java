package model.network;

import model.Operation;
import model.Position;
import model.entity.Unit;
import model.network.dto.StateSyncMessageInfo;
import model.network.tool.UdpReceiver;
import model.network.tool.UdpSender;
import util.Constant;
import view.MainPanel;

import java.awt.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author Yhaobo
 * @date 2020/11/3
 */
public class SlaveNetworkService extends NetworkService {

    private UdpReceiver receiver;

    private UdpSender sender;

    /**
     * 连接主机
     *
     * @param hostAddress 主机地址
     * @return 控制单位的id; 为 null 则连接失败
     */
    public String connectHost(InetAddress hostAddress) {
        try (Socket socket = new Socket(hostAddress, Constant.Network.CONNECT_LISTEN_PORT)) {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
            writer.write(ConnectConst.CONNECT_FLAG);
            writer.newLine();
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            String msg = reader.readLine();
            System.out.println("得到主机回复的信息: " + msg);
            String[] split = msg.split(";");
            this.receiver = new UdpReceiver(InetAddress.getByName(split[0]));
            final String playerId = split[1];
            split = split[2].split(",");
            MainPanel.setDimension(new Dimension(Integer.parseInt(split[0]), Integer.parseInt(split[1])));
            this.sender = new UdpSender(socket.getInetAddress());
            return playerId;
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
            final StateSyncMessageInfo.HostSendDatagram datagram = receiver.receiveFromHost();
            final List<StateSyncMessageInfo.UnitSyncState> newUnits = datagram.getUnitSyncStateList();

            final List<Integer> deleteUnitIndexList = datagram.getDeletedUnitIndexListList();
            //新删除的单位
            if (deleteUnitIndexList.size() > 0) {
                for (int i : deleteUnitIndexList) {
                    oldUnits.remove(i);
                }
            }

            final int oldSize = oldUnits.size();
            //新增的单位
            if (oldSize < newUnits.size()) {
                for (int i = oldSize; i < newUnits.size(); i++) {
                    try {
                        oldUnits.add(genericUnit(newUnits.get(i)));
                    } catch (ClassNotFoundException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
            //更新部分属性
            for (int i = 0; i < oldSize; i++) {
                final StateSyncMessageInfo.UnitSyncState newUnit = newUnits.get(i);
                final Unit oldUnit = oldUnits.get(i);

                copyProperties(newUnit, oldUnit, false);
            }

        } catch (IOException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private Unit genericUnit(StateSyncMessageInfo.UnitSyncState unitSyncState) throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Class<?> type = Class.forName(unitSyncState.getType());
        Unit newInstance = (Unit) type.newInstance();
        copyProperties(unitSyncState, newInstance, true);
        newInstance.renew();
        return newInstance;
    }

    private void copyProperties(StateSyncMessageInfo.UnitSyncState src, Object dest, boolean isNewInstance) {
        for (Method method : dest.getClass().getMethods()) {
            try {
                switch (method.getName()) {
                    case "setPosition":
                        method.invoke(dest, new Position(src.getPosition()));
                        break;
                    case "setDirection":
                        method.invoke(dest, src.getDirection());
                        break;
                    case "setSpeed":
                        method.invoke(dest, src.getSpeed());
                        break;
                    case "setCollisionRadius":
                        method.invoke(dest, src.getCollisionRadius());
                        break;
                    case "setId":
                        if (isNewInstance) {
                            method.invoke(dest, src.getId());
                        }
                        break;
                    case "setHeroIdx":
                        if (isNewInstance) {
                            method.invoke(dest, src.getHeroIdx());
                        }
                        break;
                    default:
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sendOperation(Operation operation) {
        try {
            sender.send(StateSyncMessageInfo.SlaveSendDatagram.newBuilder()
                    .setCannonballSize(operation.getCannonballSize())
                    .setFront(operation.isFront())
                    .setBack(operation.isBack())
                    .setLeft(operation.isLeft())
                    .setRight(operation.isRight())
                    .build());
            //重置
            operation.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
