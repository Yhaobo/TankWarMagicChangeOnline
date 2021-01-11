package model.network.tool;

import model.network.dto.StateSyncMessageInfo;
import model.Constant;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * 数据发送者
 *
 * @author Yhaobo
 * @date 2020/10/31
 */
public class UdpSender {
    private DatagramSocket socket;

    private DatagramPacket packet;

    public UdpSender(InetAddress destInetAddress) {
        try {
            socket = new DatagramSocket();
            packet = new DatagramPacket(new byte[0], 0, destInetAddress, Constant.Network.DATA_LISTEN_PORT);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void send(StateSyncMessageInfo.HostSendDatagram datagram) throws IOException {
        sendBytes(datagram.toByteArray());
    }

    public void send(StateSyncMessageInfo.SlaveSendDatagram datagram) throws IOException {
        sendBytes(datagram.toByteArray());
    }

    private void sendBytes(byte[] bytes) throws IOException {
        packet.setData(bytes);
        socket.send(packet);
    }
}
