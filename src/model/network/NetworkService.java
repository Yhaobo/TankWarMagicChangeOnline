package model.network;

import controller.TankWarOnlineApplication;
import util.Constant;

import java.io.IOException;
import java.net.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Yhaobo
 * @date 2020/11/3
 */
public class NetworkService {
    /**
     * 主机地址列表(房间列表)
     */
    private Set<InetAddress> HostAddressList = new HashSet<>();

    public NetworkService() {
    }

    public Set<InetAddress> getHostAddressList() {
        return HostAddressList;
    }

    /**
     * 通过udp广播来寻找主机
     */
    public Set<InetAddress> discoverHost() {
        try {
            HostAddressList.clear();
            final byte[] bytes = ConnectConst.FIND_FLAG.getBytes();
            DatagramSocket socket = new DatagramSocket();
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(Constant.Network.BROADCAST_ADDRESS_NAME), Constant.Network.CONNECT_LISTEN_PORT);
            socket.send(packet);
            socket.send(packet);
            for (; ; ) {
                final InetAddress inetAddress = getAddress(socket, packet);
                TankWarOnlineApplication.SCHEDULED_THREAD_POOL.execute(() -> HostAddressList.add(inetAddress));
            }
        } catch (SocketTimeoutException e) {
            System.out.println("寻找主机超时结束");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return HostAddressList;
    }

    /**
     * 返回发送端的网络地址
     *
     * @return 另一端的网络地址
     */
    public static InetAddress getAddress(DatagramSocket socket, DatagramPacket packet) throws IOException {
        socket.setSoTimeout(200);
        socket.receive(packet);
        return packet.getAddress();
    }

    /**
     * 主机和从机连接的相关常量
     */
    protected interface ConnectConst {
        String FIND_FLAG = "find";
        String CONNECT_FLAG = "connect";
        int MAX_LENGTH = 64;
        String OK_FLAG = "ok";
    }
}
