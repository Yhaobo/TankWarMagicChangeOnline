package model.network;

import controller.TankWarOnlineApplication;
import model.Operation;
import model.Position;
import model.entity.Player;
import model.entity.Unit;
import model.network.dto.StateSyncMessageInfo;
import model.network.tool.UdpReceiver;
import model.network.tool.UdpSender;
import model.Constant;
import view.MainPanel;

import java.awt.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
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
        try (Socket socket = new Socket(hostAddress, Constant.NetworkConstant.CONNECT_LISTEN_PORT)) {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
            writer.write(ConnectConst.CONNECT_FLAG);
            writer.newLine();
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            String msg = reader.readLine();
            System.out.println("得到主机回复的信息: " + msg);
            String[] split = msg.split(";");
            initReceiver(split[0]);
            final String playerId = split[1];
            split = split[2].split(",");
            MainPanel.setDimension(new Dimension(Integer.parseInt(split[0]), Integer.parseInt(split[1])));
            this.sender = new UdpSender(socket.getInetAddress(), Constant.NetworkConstant.DATA_LISTEN_PORT);
            return playerId;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initReceiver(String host) throws IOException {
        this.receiver = new UdpReceiver(Constant.NetworkConstant.DATA_LISTEN_PORT);
        InetAddress address = InetAddress.getByName(host);
        Collection<NetworkInterface> networkInterfaces = AVAILABLE_INET_ADDRESS_NETWORK_INTERFACE_MAP.values();
        if (networkInterfaces.size() != 1) {
            if (networkInterfaces.isEmpty()) {
                System.out.println("无选中的网络接口！");
            } else {
                System.out.println("选择的网络接口数量大于一， 具体为：");
                for (NetworkInterface networkInterface : networkInterfaces) {
                    System.out.print("\t" + networkInterface.getName());
                    System.out.println(" - " + networkInterface.getDisplayName());
                }
            }
        } else {
            for (NetworkInterface networkInterface : networkInterfaces) {
                this.receiver.joinGroup(address, networkInterface);
            }
        }
    }

    /**
     * 接收来自主机的数据
     * <p>
     * 将旧数据更新 (不是替换)
     *
     */
    public void acceptData(TankWarOnlineApplication application) {
        List<Unit> oldUnits = application.getUnitList();
        try {
            final StateSyncMessageInfo.HostSendDatagram datagram = receiver.receiveFromHost();
            this.version = datagram.getVersion();
            final List<StateSyncMessageInfo.UnitSyncState> newUnits = datagram.getUnitSyncStateList();

            //新增的单位处理
            if (oldUnits.size() < newUnits.size()) {
                for (int i =  oldUnits.size(); i < newUnits.size(); i++) {
                    try {
                        Unit newUnit = generateUnit(newUnits.get(i));
                        oldUnits.add(newUnit);

                        if (application.getPlayer().getDensity() == 0 && application.getPlayer().getId().equals(newUnit.getId())) {
                            //赋值controller.TankWarOnlineApplication.player 使之重新持有当前玩家
                            System.out.println("重新持有当前玩家");
                            application.setPlayer((Player) newUnit);
                        }
                    } catch (ClassNotFoundException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
            //更新属性
            for (int i = 0; i < oldUnits.size(); i++) {
                final StateSyncMessageInfo.UnitSyncState newUnit = newUnits.get(i);
                final Unit oldUnit = oldUnits.get(i);

                copyProperties(newUnit, oldUnit, false);
            }
            //新删除单位处理
            final List<Integer> deleteUnitIndexList = datagram.getDeletedUnitIndexListList();
            if (deleteUnitIndexList.size() > 0) {
                //注意：必须从后往前删除，防止删除前面的导致后面元素索引发生变化
                for (int i = deleteUnitIndexList.size() - 1; i >= 0; i--) {
                    oldUnits.remove((int) deleteUnitIndexList.get(i));
                }
            }

        } catch (IOException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private Unit generateUnit(StateSyncMessageInfo.UnitSyncState unitSyncState) throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException {
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
                    .setVersion(++this.version)
                    .build());
            //重置
            operation.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
