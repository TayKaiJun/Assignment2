package sample;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.HashSet;

public class P2PConnection {
    public static P2PConnection p2pConnection;

    public final static int PORT_NUMBER = 52000;

    public HashSet<String> discoveredHosts;

    private String sourceAddress;
    private int sourceSubnetValue;
    private DatagramSocket socket;
    private ConnListen connListen;

    public P2PConnection() throws SocketException {
        discoveredHosts = new HashSet<>();
        String[] sourceAddressWithSubnet = Util.getLocalIpAddressWithSubnet().split("/");
        sourceAddress = sourceAddressWithSubnet[0];
        sourceSubnetValue = Integer.parseInt(sourceAddressWithSubnet[1]);

        InetSocketAddress address = new InetSocketAddress(sourceAddress, PORT_NUMBER);
        socket = new DatagramSocket(address);

        connListen = new ConnListen(socket);
        connListen.start();
    }

    public static P2PConnection getConnection(){
        if (p2pConnection == null) {
            try {
                p2pConnection = new P2PConnection();
            } catch (SocketException e) {
                e.printStackTrace();
                return null;
            }
        }
        return p2pConnection;
    }

    public void sendMessage(String destAddress, String message) throws IOException {
        InetSocketAddress receiveraddress = new InetSocketAddress(destAddress, PORT_NUMBER);
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        packet.setSocketAddress(receiveraddress);
        socket.send(packet);
    }

    // Used for discovery
    public void broadcastToAllHostsOnNetwork(String message){
        int firstAddress = Util.convertIpAddressToInt(Util.getFirstAddress(sourceAddress, sourceSubnetValue));
        int lastAddress = Util.convertIpAddressToInt(Util.getLastAddress(sourceAddress, sourceSubnetValue));

        for (int i = firstAddress; Integer.compareUnsigned(i, lastAddress) <= 0; i++){
            String ipAddress = Util.convertIntToIpAddress(i);
            if (ipAddress.equals(sourceAddress)) continue;
            try {
                sendMessage(ipAddress, message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Broadcasted to All Hosts");
    }

    // Used for normal messages and for disconnecting
    public void broadcastToDiscoveredHosts(String message){
        for (String host: discoveredHosts){
            try {
                sendMessage(host, message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Broadcasted to All Discovered Hosts");
    }

    public void addHost(String ipAddress){
        discoveredHosts.add(ipAddress);
    }

    public void removeHost(String ipAddress){
        discoveredHosts.remove(ipAddress);
    }

    public void stop(){
        broadcastToDiscoveredHosts(MessageUtil.getMessage(MessageUtil.MessageType.DISCONNECT, null));
        // TODO: Broadcast disconnect message
        connListen.stop();
        socket.close();
        p2pConnection = null;
        socket = null;
        connListen = null;
    }
}


class ConnListen implements Runnable{

    DatagramSocket socket;
    boolean running;

    public ConnListen(DatagramSocket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        running = true;
        while(running) {
            try {
                socket.receive(packet);
                String message = new String(buffer, 0, packet.getLength());
                System.out.println(packet.getAddress().getHostAddress() + " said: " + message);
                MessageUtil.processMessage(packet, message);
            }
            catch (Exception e) {
            }
        }
    }

    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }

    public void stop() {
        running = false;
        socket.close();
    }
}