package sample;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class MessageUtil {
    public enum MessageType {
        DISCOVER, DISCOVERRESPONSE, MESSAGE, DISCONNECT
    }

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

    public static void processMessage(DatagramPacket packet, String message){
        String[] messageSplit = message.split(":", 2);
        String tag = messageSplit[0];
        String body = messageSplit[1];
        String address = packet.getAddress().getHostAddress();
        P2PConnection connection = P2PConnection.getConnection();

        // DISCOVER:
        if (tag.equalsIgnoreCase("DISCOVER")){
            // TODO: Send back discoverresponse
            // TODO: Add to list of discovered hosts
            try {
                connection.sendMessage(address, getMessage(MessageType.DISCOVERRESPONSE, null));
            } catch (IOException e) {
                e.printStackTrace();
            }
            connection.addHost(address);
        }
        // DISCOVERRESPONSE:
        else if (tag.equalsIgnoreCase("DISCOVERRESPONSE")){
            // TODO: Add host to list of discovered hosts
            connection.addHost(address);
        }
        // MESSAGE: CONTENT
        else if (tag.equalsIgnoreCase("MESSAGE")){
            // TODO: Display content
            System.out.println(address + " said: " + body);
        }
        // DISCONNECT:
        else if (tag.equalsIgnoreCase("DISCONNECT")){
            // TODO: Remove host from discovered hosts list
            connection.removeHost(address);
        }
    }
}
