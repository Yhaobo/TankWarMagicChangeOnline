package model.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.network.dto.VersionedDatagram;
import util.Constant;

import java.io.IOException;
import java.net.*;

/**
 * @author Yhaobo
 * @date 2020/10/31
 */
public class DataSender {
    private DatagramSocket socket;

    private DatagramPacket packet;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public DataSender(InetAddress destInetAddress) {
        try {
            socket = new DatagramSocket();
            packet= new DatagramPacket(new byte[0], 0, destInetAddress, Constant.Network.DATA_LISTEN_PORT);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void send(VersionedDatagram datagram) throws IOException {
        packet.setData(objectMapper.writeValueAsBytes(datagram));
//        System.out.println(new String(packet.getData(),packet.getOffset(),packet.getLength()));
        socket.send(packet);
    }

    public void send(Object obj, InetAddress address) throws IOException {
        packet.setData(objectMapper.writeValueAsBytes(obj));
        System.out.println(new String(packet.getData(), 0, packet.getLength()));
        packet.setAddress(address);
        socket.send(packet);
    }
}
