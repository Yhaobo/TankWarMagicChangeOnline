package model.network;

import controller.TankWarOnlineApplication;
import util.Constant;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
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
    private Set<InetAddress> hostAddressList = new HashSet<>();

    public NetworkService() {
    }

    public Set<InetAddress> getHostAddressList() {
        return hostAddressList;
    }

    /**
     * 通过udp广播来寻找主机
     */
    public Set<InetAddress> discoverHost() {
        try {
            hostAddressList.clear();
            final byte[] bytes = ConnectConst.FIND_FLAG.getBytes();
            DatagramSocket socket = new DatagramSocket();
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(Constant.Network.BROADCAST_ADDRESS_NAME), Constant.Network.CONNECT_LISTEN_PORT);
            socket.send(packet);
            for (; ; ) {
                socket.setSoTimeout(Constant.STEP_INTERVAL_TIME*10);
                socket.receive(packet);
                final InetAddress inetAddress =packet.getAddress();
                TankWarOnlineApplication.SCHEDULED_THREAD_POOL.execute(() -> hostAddressList.add(inetAddress));
            }
        } catch (SocketTimeoutException e) {
            System.out.println("寻找主机超时结束");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hostAddressList;
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
