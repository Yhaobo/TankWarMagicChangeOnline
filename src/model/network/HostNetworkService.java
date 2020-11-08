package model.network;

import model.entity.Cannonball;
import model.entity.Player;
import model.entity.Unit;
import model.network.dto.HostDatagram;
import model.network.dto.SlaveDatagram;
import util.Constant;
import view.MainPanel;

import java.awt.*;
import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yhaobo
 * @date 2020/11/3
 */
public class HostNetworkService extends NetworkService {

    private DataSender sender;

    private DataReceiver<SlaveDatagram> receiver = new DataReceiver<>(SlaveDatagram.class);

    private Map<InetAddress, Player> inetAddressPlayerMap = new HashMap<>();

    private DatagramSocket connectionSocket;

    public HostNetworkService() throws UnknownHostException, SocketException {
        sender = new DataSender(InetAddress.getByName(Constant.Network.MULTICAST_ADDRESS_NAME));
        connectionSocket = new DatagramSocket(Constant.Network.CONNECT_LISTEN_PORT);
    }

    /**
     * 监听主机连接端口
     */
    public void listenConnect(List<Unit> units) {
        try {
            final DatagramPacket packet = new DatagramPacket(new byte[ConnectConst.MAX_LENGTH], ConnectConst.MAX_LENGTH);
            connectionSocket.receive(packet);
            //获取到数据并转换为字符串
            String msg = new String(packet.getData(), 0, packet.getLength());
            if (msg.equals(ConnectConst.CONNECT_FLAG)) {
                //是连接请求, 返回组播地址
                System.out.println("是连接请求, 返回组播地址");
                packet.setData((Constant.Network.MULTICAST_ADDRESS_NAME).getBytes());
                connectionSocket.send(packet);
                connectionSocket.send(packet);

                //*** 异步继续监听, 等待成功回复 (此方式可能会接收到其他请求, 所以要求所有请求都至少连续发送两次) ***

                try {
                    final InetAddress inetAddress = packet.getAddress();
                    connectionSocket.setSoTimeout(Constant.STEP_INTERVAL_TIME);
                    for (; ; ) {
                        connectionSocket.receive(packet);
                        //判断消息标记, 同时比较地址是否一样
                        if (new String(packet.getData(), packet.getOffset(), packet.getLength()).equals(ConnectConst.OK_FLAG)
                                && packet.getAddress().equals(inetAddress)) {
                            //连接成功
                            System.out.println("连接成功, 从机地址为:" + inetAddress);
                            Player newPlayer = new Player(true);
                            newPlayer.setName(inetAddress.getCanonicalHostName());
                            //判断从机是否已连接过(断线重连)
                            final Player player = inetAddressPlayerMap.get(inetAddress);
                            if (player == null) {
                                inetAddressPlayerMap.put(inetAddress, newPlayer);
                                units.add(newPlayer);
                            } else {
                                newPlayer = player;
                            }
                            //给从机返回新控制单位的id以及窗口大小信息
                            final Dimension dimension = MainPanel.getDimension();
                            msg = newPlayer.getId()+";"+ (int)dimension.getWidth()+","+(int)dimension.getHeight();
                            packet.setData(msg.getBytes());
                            connectionSocket.send(packet);
                            connectionSocket.send(packet);
                            break;
                        }
                    }
                } catch (SocketTimeoutException e) {
                    System.out.println("主机等待连接回复超时结束");
                }
            } else if (msg.equals(ConnectConst.FIND_FLAG)) {
                //是寻找主机请求, 直接返回空响应
                System.out.println("是寻找主机请求, 直接返回空响应");
                packet.setData(new byte[0]);
                connectionSocket.send(packet);
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
            sender.send(new HostDatagram(unitList, removeUnitIndex.toArray(new Integer[0])));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 接收并处理从机传来的操作数据
     */
    public void acceptAndHandleOperation(List<Unit> unitList) throws InterruptedException {
        //从机数
        final int size = inetAddressPlayerMap.size();
        //设置超时时间
        receiver.setTimeout(Constant.STEP_INTERVAL_TIME);
        //根据从机数来分配线程接收
//        CountDownLatch latch = new CountDownLatch(size);
//        for (int i = 0; i < size; i++) {
//            TankWarOnlineApplication.SCHEDULED_THREAD_POOL.execute(() -> {
//                acceptAndHandleOperation(unitList, inetAddressPlayerMap);
//                latch.countDown();
//            });
//        }
//        latch.await(Constant.STEP_INTERVAL_TIME, TimeUnit.MILLISECONDS);

        //使用循环方式 (单个从机的情况下操作延迟较低)
        for (int i = 0; i < size; i++) {
            acceptAndHandleOperation(unitList, inetAddressPlayerMap);
        }
    }

    private void acceptAndHandleOperation(List<Unit> unitList, Map<InetAddress, Player> map) {
        try {
            final DatagramPacket packet = new DatagramPacket(new byte[4096], 4096);
            final SlaveDatagram datagram = receiver.receive(packet);
            //将操作应用到对应的玩家单位上
            final Cannonball cannonball = datagram.getOperation().handlePlayerAction(map.get(packet.getAddress()));
            if (cannonball != null) {
                unitList.add(cannonball);
            }
        } catch (SocketTimeoutException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
