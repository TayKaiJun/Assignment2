package sample;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.HashMap;
import java.util.Map;

public class MessageUtil {
    private static final Map<String, String> mapIpToUsername = new HashMap<String, String>();

    public enum MessageType {
        DISCOVER, DISCOVERRESPONSE, MESSAGE, DISCONNECT
    }

    /**
     * Gets tagged messages to be sent out
     * Note: Most types would not require a content (e.g. DISCOVER can have null content because address)
     *
     * @param type type of message
     * @param content content of message
     * @return
     */
    public static String getMessage(MessageType type, String content){
        switch(type){
            case DISCOVER:
                return "DISCOVER:" + content;
            case DISCOVERRESPONSE:
                return "DISCOVERRESPONSE:" + content;
            case MESSAGE:
                return "MESSAGE:" + content;
            case DISCONNECT:
                return "DISCONNECT:" + content;
        }
        return null;
    }

    public static void processMessage(DatagramPacket packet, String message, String hostName){
        String[] messageSplit = message.split(":", 2);
        String tag = messageSplit[0];
        String body = messageSplit[1];
        String address = packet.getAddress().getHostAddress();
        P2PConnection connection = P2PConnection.getConnection();

        // DISCOVER: (username)
        if (tag.equalsIgnoreCase("DISCOVER")){
            try {
                // Respond to the discovery message
                connection.sendMessage(address, getMessage(MessageType.DISCOVERRESPONSE, hostName));
            } catch (IOException e) {
                e.printStackTrace();
            }
            connection.addHost(address);
            // Puts IP and address map
            mapIpToUsername.put(address, body);

            Log.printLog("Discovered host " + mapIpToUsername.get(address) + "(" + address + ")");

            Controller.writeMessage(mapIpToUsername.get(address) + " has joined the network. Say hi!\n");
        }
        // DISCOVERRESPONSE:
        else if (tag.equalsIgnoreCase("DISCOVERRESPONSE")){
            connection.addHost(address);
            // Puts IP and address map
            mapIpToUsername.put(address, body);
            Log.printLog("Discovered response from " + mapIpToUsername.get(address) + "(" + address + ")");

            Controller.writeMessage(mapIpToUsername.get(address) + " has joined the network. Say hi!\n");
        }
        // MESSAGE: CONTENT
        else if (tag.equalsIgnoreCase("MESSAGE")){
            Log.printLog(mapIpToUsername.get(address) + "(" + address + ")" + " said " + body);

            Controller.writeMessage(mapIpToUsername.get(address) + ": " + body + "\n");
        }
        // DISCONNECT:
        else if (tag.equalsIgnoreCase("DISCONNECT")){
            connection.removeHost(address);
            Log.printLog("Host " + mapIpToUsername.get(address) + "(" + address + ")" + " disconnected from network ");

            // TODO: Add Disconnect Procedure For FX (KJ)
            Controller.writeMessage(mapIpToUsername.get(address) + " left.\n");
        }
    }

    public static String getHostName(String ipaddress){
        return mapIpToUsername.get(ipaddress);
    }
}
