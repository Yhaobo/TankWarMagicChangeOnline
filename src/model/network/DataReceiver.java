package model.network;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.network.dto.VersionedDatagram;
import util.Constant;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.Arrays;

/**
 * @author Yhaobo
 * @date 2020/10/31
 */
public class DataReceiver<T extends VersionedDatagram> {
    private MulticastSocket socket;

    {
        try {
            socket = new MulticastSocket(Constant.Network.DATA_LISTEN_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Class<T> clazz;

    private ObjectMapper objectMapper = new ObjectMapper();

    public DataReceiver(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * 接收数据
     *
     * @param packet 使用此 packet 来接收数据
     * @return 将数据反序列化成指定的类并返回
     */
    public T receive(DatagramPacket packet) throws IOException {
        socket.receive(packet);
        final byte[] bytes = Arrays.copyOf(packet.getData(), packet.getLength());
        return objectMapper.readValue(bytes, clazz);
    }

    /**
     * 加入组播
     *
     * @param address 组播地址
     */
    public void joinGroup(InetAddress address) throws IOException {
        socket.joinGroup(address);
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
