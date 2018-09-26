package sample;

import java.io.IOException;
import java.net.DatagramPacket;

public class MessageUtil {
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

    public static void processMessage(DatagramPacket packet, String message){
        String[] messageSplit = message.split(":", 2);
        String tag = messageSplit[0];
        String body = messageSplit[1];
        String address = packet.getAddress().getHostAddress();
        P2PConnection connection = P2PConnection.getConnection();

        // DISCOVER: (username)
        if (tag.equalsIgnoreCase("DISCOVER")){
            try {
                // Respond to the discovery message
                connection.sendMessage(address, getMessage(MessageType.DISCOVERRESPONSE, null));
            } catch (IOException e) {
                e.printStackTrace();
            }
            connection.addHost(address);

            Log.printLog("Discovered host " + address + " with username " + body);

            // TODO: Add Discovered Procedure For FX (KJ)
            // E.g. Print on screen "xxx joined the network"
        }
        // DISCOVERRESPONSE:
        else if (tag.equalsIgnoreCase("DISCOVERRESPONSE")){
            connection.addHost(address);
            Log.printLog("Discovered response from host " + address );

            // TODO: Add Discovered Procedure For FX (KJ)
            // E.g. Print on screen "xxx joined the network"
        }
        // MESSAGE: CONTENT
        else if (tag.equalsIgnoreCase("MESSAGE")){
            Log.printLog(address + " said " + body);

            // TODO: Add Message Received For FX (KJ)
            // E.g. Print on screen "xxx: message"
        }
        // DISCONNECT:
        else if (tag.equalsIgnoreCase("DISCONNECT")){
            connection.removeHost(address);
            Log.printLog("Host " + address + " disconnected from network");

            // TODO: Add Disconnect Procedure For FX (KJ)
            // E.g. Print on screen "xxx disconnected from network"
        }
    }
}
