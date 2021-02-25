package model.network;

import controller.TankWarOnlineApplication;
import model.Constant;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * @author Yhaobo
 * @date 2020/11/3
 */
public class NetworkService {
    /**
     * 数据包同步版本号
     */
    protected int version;
    /**
     * 主机地址列表(房间列表)
     */
    private final Set<InetAddress> hostAddressList = new HashSet<>();

    public static final Map<InetAddress, NetworkInterface> AVAILABLE_INET_ADDRESS_NETWORK_INTERFACE_MAP = findAvailableInetAddress();

    public NetworkService() {
    }

    public Set<InetAddress> getHostAddressList() {
        return hostAddressList;
    }

    /**
     * 通过udp广播来寻找主机
     */
    public Set<InetAddress> discoverHost() throws SocketException, UnknownHostException {
        hostAddressList.clear();

        final byte[] bytes = ConnectConst.FIND_FLAG.getBytes();
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(Constant.NetworkConstant.BROADCAST_ADDRESS_NAME), Constant.NetworkConstant.CONNECT_LISTEN_PORT);

        Map<InetAddress, NetworkInterface> availableInetAddress = findAvailableInetAddress();
        CountDownLatch countDownLatch = new CountDownLatch(availableInetAddress.size());

        for (InetAddress inetAddress : availableInetAddress.keySet()) {
            TankWarOnlineApplication.SCHEDULED_THREAD_POOL.execute(() -> {
                DatagramSocket socket = null;
                try {
                    socket = new DatagramSocket(0, inetAddress);
                    socket.send(packet);
                    for (; ; ) {
                        socket.setSoTimeout(Constant.STEP_INTERVAL_TIME * 10);
                        socket.receive(packet);

                        hostAddressList.add(packet.getAddress());
                        System.out.println("发现的主机：" + packet.getAddress());
                    }
                } catch (SocketTimeoutException e) {
                    System.out.println("寻找主机超时结束 - " + inetAddress);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                    if (socket != null) {
                        socket.close();
                    }
                }
            });
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
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

    /**
     * 获取本机所有网络接口中可用的IP地址及其网络接口（不是虚拟网络接口且以192.或172.或10.开头的IP地址）
     */
    private static Map<InetAddress, NetworkInterface> findAvailableInetAddress() {
        Map<InetAddress, NetworkInterface> availableInetAddress = new HashMap<>();
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                //忽略回环接口，子接口，未运行接口
                if (networkInterface.isVirtual() || !networkInterface.isUp() || networkInterface.isLoopback()) {
                    continue;
                }
                System.out.print("发现网络接口：" + networkInterface.getName());
                System.out.println(" - " + networkInterface.getDisplayName());
                //忽略虚拟网络接口
                if (networkInterface.getDisplayName().contains("Virtual")) {
                    continue;
                }
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (inetAddress.isSiteLocalAddress()) {
                        String hostAddress = inetAddress.getHostAddress();
                        if (hostAddress.startsWith("192.") || hostAddress.startsWith("172.") || hostAddress.startsWith("10.")) {
                            availableInetAddress.put(inetAddress, networkInterface);
                        }
                    }

                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        for (NetworkInterface value : availableInetAddress.values()) {
            System.out.println("选择的网络接口：" + value.getName());
        }
        return availableInetAddress;
    }
}