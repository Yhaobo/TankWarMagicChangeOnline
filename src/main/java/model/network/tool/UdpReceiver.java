package model.network.tool;

import model.network.dto.StateSyncMessageInfo;
import model.Constant;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 数据接收者
 *
 * @author Yhaobo
 * @date 2020/10/31
 */
public class UdpReceiver {
    private MulticastSocket socket;
    /**
     * UDP数据包的长度
     */
    protected AtomicInteger datagramPacketLen = new AtomicInteger(Constant.NetworkConstant.ETHERNET_MTU);

    public UdpReceiver(int port) {
        try {
            socket = new MulticastSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 扩容
     */
    private void dilatation() {
        datagramPacketLen.addAndGet(Constant.NetworkConstant.ETHERNET_MTU);
        System.out.println("datagramPacketLen："+datagramPacketLen);
    }

    /**
     * 检查数据长度是否超过指定长度的八成，如果超过则扩容
     */
    private void inspect(DatagramPacket packet) {
        if (packet.getLength() > datagramPacketLen.get() * 0.8) {
            dilatation();
            //再次检查
            inspect(packet);
        }
    }

    public DatagramPacket newPacket() {
        byte[] buf = new byte[datagramPacketLen.get()];
        return new DatagramPacket(buf, buf.length);
    }

    public StateSyncMessageInfo.HostSendDatagram receiveFromHost() throws IOException {
        DatagramPacket packet = newPacket();
        socket.receive(packet);
        inspect(packet);
        return StateSyncMessageInfo.HostSendDatagram.parseFrom(Arrays.copyOf(packet.getData(), packet.getLength()));
    }

    public StateSyncMessageInfo.SlaveSendDatagram receiveFromSlave(DatagramPacket packet) throws IOException {
        socket.receive(packet);
        inspect(packet);
        return StateSyncMessageInfo.SlaveSendDatagram.parseFrom(Arrays.copyOf(packet.getData(), packet.getLength()));
    }

    /**
     * 加入组播
     *
     * @param address 组播地址
     */
    public void joinGroup(InetAddress address, NetworkInterface networkInterface) throws IOException {
        if (networkInterface == null) {
            System.out.println("joinGroup方法的参数 NetworkInterface 为 null !!!");
            System.exit(0);
        }
        socket.joinGroup(new InetSocketAddress(address,Constant.NetworkConstant.DATA_LISTEN_PORT),networkInterface);
    }

    /**
     * 离开组播
     *
     * @param address 组播地址
     */
    public void leaveGroup(InetAddress address) throws IOException {
        socket.leaveGroup(address);
    }

    public void setTimeout(int timeout) {
        try {
            socket.setSoTimeout(timeout);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}