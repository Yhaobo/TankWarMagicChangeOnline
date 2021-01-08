package model.network;

import model.Operation;
import model.Position;
import model.entity.Cannonball;
import model.entity.Player;
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
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * @author Yhaobo
 * @date 2020/11/3
 */
public class HostNetworkService extends NetworkService {

    private UdpSender sender;

    private UdpReceiver receiver = new UdpReceiver(Constant.Network.DATA_LISTEN_PORT);

    private Map<InetAddress, Player> inetAddressPlayerMap = new HashMap<>();

    private DatagramSocket connectionSocket;

    public HostNetworkService() throws UnknownHostException, SocketException {
        sender = new UdpSender(InetAddress.getByName(Constant.Network.MULTICAST_ADDRESS_NAME));
        connectionSocket = new DatagramSocket(Constant.Network.CONNECT_LISTEN_PORT);
    }

    /**
     * 使用Tcp响应连接
     */
    public void respondConnect(ExecutorService executorService, List<Unit> units) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(Constant.Network.CONNECT_LISTEN_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (; ; ) {
            try {
                Socket socket = serverSocket.accept();
                executorService.execute(() -> {
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                        String s = reader.readLine();
                        if (ConnectConst.CONNECT_FLAG.equals(s)) {
                            //连接成功
                            InetAddress inetAddress = socket.getInetAddress();
                            System.out.println("连接成功, 从机地址为:" + inetAddress);
                            Player newPlayer = new Player(inetAddress);
                            newPlayer.setName(inetAddress.toString());
                            //判断从机是否已连接过(断线重连)
                            final Player player = inetAddressPlayerMap.get(inetAddress);
                            if (player == null) {
                                inetAddressPlayerMap.put(inetAddress, newPlayer);
                                units.add(newPlayer);
                            } else {
                                newPlayer = player;
                                System.out.println(newPlayer.getName() + " 重新上线");
                            }
                            //给从机返回 组播地址;新控制单位的id;窗口大小信息
                            final Dimension dimension = MainPanel.getDimension();
                            String msg = Constant.Network.MULTICAST_ADDRESS_NAME + ";" + newPlayer.getId() + ";" + (int) dimension.getWidth() + "," + (int) dimension.getHeight();
                            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
                            writer.write(msg);
                            writer.newLine();
                            writer.flush();
                        }
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 监听主机Udp连接端口
     */
    public void listenConnect() {
        try {
            final DatagramPacket packet = new DatagramPacket(new byte[ConnectConst.MAX_LENGTH], ConnectConst.MAX_LENGTH);
            connectionSocket.receive(packet);
            //获取到数据并转换为字符串
            String msg = new String(packet.getData(), 0, packet.getLength());
            if (msg.equals(ConnectConst.FIND_FLAG)) {
                //收到寻找主机请求, 返回本机地址
                System.out.println("收到连接请求，来自：" + packet.getAddress().toString());
                byte[] address = InetAddress.getLocalHost().getAddress();
                packet.setData(address);
                connectionSocket.send(packet);
            }
        } catch (SocketTimeoutException e) {
//            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<InetAddress, Player> getInetAddressPlayerMap() {
        return inetAddressPlayerMap;
    }

    public void sendData(List<Unit> unitList, List<Integer> removeUnitIndex) {
        try {
            StateSyncMessageInfo.HostSendDatagram.Builder builder = StateSyncMessageInfo.HostSendDatagram.newBuilder();
            for (Unit unit : unitList) {
                builder.addUnitSyncState(copyProperties(unit, StateSyncMessageInfo.UnitSyncState.newBuilder()));
            }
            for (Integer index : removeUnitIndex) {
                builder.addDeletedUnitIndexList(index);
            }
            sender.send(builder.build());
        } catch (IOException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private StateSyncMessageInfo.UnitSyncState copyProperties(Unit src, StateSyncMessageInfo.UnitSyncState.Builder dest) throws InvocationTargetException, IllegalAccessException {
        Object result;
        for (Method method : src.getClass().getMethods()) {
            switch (method.getName()) {
                case "getPosition":
                    result = method.invoke(src);
                    Position position = (Position) result;
                    dest.setPosition(StateSyncMessageInfo.Position.newBuilder()
                            .setX(position.getX()).setY(position.getY()).build());
                    break;
                case "getDirection":
                    result = method.invoke(src);
                    dest.setDirection((Float) result);
                    break;
                case "getSpeed":
                    result = method.invoke(src);
                    dest.setSpeed((Float) result);
                    break;
                case "getCollisionRadius":
                    result = method.invoke(src);
                    dest.setCollisionRadius((Float) result);
                    break;
                case "getId":
                    result = method.invoke(src);
                    dest.setId((String) result);
                    break;
                case "getHeroIdx":
                    result = method.invoke(src);
                    dest.setHeroIdx((Integer) result);
                default:
            }
        }
        //全类名，方便反序列化
        dest.setType(src.getClass().getName());
        return dest.build();
    }

    /**
     * 接收并处理从机传来的操作数据
     */
    public void acceptAndHandleOperation(List<Unit> unitList) throws InterruptedException {
        //从机数
        final int size = inetAddressPlayerMap.size();
        //设置超时时间
        receiver.setTimeout(Constant.STEP_INTERVAL_TIME);

        for (int i = 0; i < size; i++) {
            try {
                DatagramPacket packet = receiver.newPacket();
                StateSyncMessageInfo.SlaveSendDatagram slaveDatagram = receiver.receiveFromSlave(packet);
                //将操作应用到对应的玩家单位上
                final Cannonball cannonball = Operation.handlePlayerAction(inetAddressPlayerMap.get(packet.getAddress()), slaveDatagram);
                if (cannonball != null) {
                    unitList.add(cannonball);
                }
            } catch (SocketTimeoutException e) {
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
