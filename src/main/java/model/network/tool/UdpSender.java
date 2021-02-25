package model.network.tool;

import model.network.dto.StateSyncMessageInfo;

import java.io.IOException;
import java.net.*;

/**
 * 数据发送者
 *
 * @author Yhaobo
 * @date 2020/10/31
 */
public class UdpSender {
    private MulticastSocket socket;

    private DatagramPacket packet;

    public UdpSender(InetAddress destInetAddress,int port) {
        try {
            socket = new MulticastSocket();
            packet = new DatagramPacket(new byte[0], 0, destInetAddress, port);
        } catch (IOException e) {
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
