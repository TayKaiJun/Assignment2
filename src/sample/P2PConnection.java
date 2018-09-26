package sample;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class P2PConnection {
    public static P2PConnection p2pConnection;

    public final static int PORT_NUMBER = 52000;

    public Set<String> discoveredHosts;

    private String sourceAddress;
    private int sourceSubnetValue;
    private DatagramSocket socket;
    private ConnListen connListen;

    public P2PConnection() throws SocketException {
        discoveredHosts = new HashSet<>();
        String[] sourceAddressWithSubnet = Util.getLocalIpAddressWithSubnet().split("/");
        sourceAddress = sourceAddressWithSubnet[0];
        sourceSubnetValue = Integer.parseInt(sourceAddressWithSubnet[1]);

        Log.printLog("Using " + sourceAddress + " as Source Address");

        InetSocketAddress address = new InetSocketAddress(sourceAddress, PORT_NUMBER);
        socket = new DatagramSocket(address);

        connListen = new ConnListen(socket);
        connListen.start();
    }

    /**
     * Gets a static P2PConnection (only allows 1 connection per application)
     *
     * @return P2PConnection used for the application
     */
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

    /**
     * Sends a tagged message to a destination address
     * Use MessageUtil.getMessage to get a tagged message
     * Generally used for discovery
     *
     * @param message tagged message (needs to contain the tag)
     */
    public void sendMessage(String destAddress, String message) throws IOException {
        InetSocketAddress receiveraddress = new InetSocketAddress(destAddress, PORT_NUMBER);
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        packet.setSocketAddress(receiveraddress);
        socket.send(packet);
    }

    /**
     * Sends a tagged message to all hosts on network
     * Use MessageUtil.getMessage to get a tagged message
     * Generally used for discovery
     *
     * @param message tagged message (needs to contain the tag)
     */
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
        Log.printLog("Broadcasted " + message + " to All Hosts On Network");
    }

    /**
     * Sends a tagged message to all discovered hosts
     * Use MessageUtil.getMessage to get a tagged message
     * Generally used for normal messages and for disconnecting
     *
     * @param message tagged message (needs to contain the tag)
     */
    public void broadcastToDiscoveredHosts(String message){
        for (String host: discoveredHosts){
            try {
                sendMessage(host, message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.printLog("Broadcasted " + message + " to All Discovered Hosts");
    }

    /**
     * Adds a new host to the P2PConnection
     */
    public void addHost(String ipAddress){
        discoveredHosts.add(ipAddress);
    }

    /**
     * Remove a host from the P2PConnection
     * Used for disconnecting
     */
    public void removeHost(String ipAddress){
        discoveredHosts.remove(ipAddress);
    }

    /**
     * Clears all discovered hosts from the P2PConnection
     * Used for rediscovery
     */
    public void removeAllHost(){
        discoveredHosts.clear();
    }

    /**
     * Gets all discovered hosts
     *
     * @return ArrayList of discovered host
     */
    public ArrayList<String> getHosts(){
        return new ArrayList<String>(discoveredHosts);
    }

    /**
     * Stops the connection
     * Broadcast disconnect message to all hosts and clean up sockets and threads
     */
    public void stop(){
        broadcastToDiscoveredHosts(MessageUtil.getMessage(MessageUtil.MessageType.DISCONNECT, null));

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
    Thread thread;

    public ConnListen(DatagramSocket socket){
        this.socket = socket;
    }

    /**
     * Listens to the specified socket and receive the packets
     * Pass them to processMessage to handle the messages
     */
    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        running = true;
        while(running) {
            try {
                socket.receive(packet);
                String message = new String(buffer, 0, packet.getLength());
                MessageUtil.processMessage(packet, message);
            }
            catch (Exception e) {
            }
        }
    }

    /**
     * Starts the thread to listen to the socket
     */
    public void start() {
        thread = new Thread(this);
        thread.start();
    }

    /**
     * Stops the thread and close the socket
     */
    public void stop() {
        running = false;
        socket.close();
    }
}