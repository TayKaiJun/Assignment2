package sample;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Test {
    public static void main(String[] args){
        P2PConnection connection = P2PConnection.getConnection();

        // Discovery
        connection.broadcastToAllHostsOnNetwork(MessageUtil.getMessage(MessageUtil.MessageType.DISCOVER, null));

        boolean run = true;
        Scanner scanner = new Scanner(System.in);

        while(run) {
            switch (scanner.next()) {
                case "SEND":
                    // Sends out new message to discovered hosts
                    System.out.print("Please enter the message to be sent: ");
                    String message = scanner.next();
                    connection.broadcastToDiscoveredHosts(MessageUtil.getMessage(MessageUtil.MessageType.MESSAGE, message));
                    break;
                case "STOP":
                    // Stops listening to the socket and inform all discovered hosts about disconnection
                    connection.stop();
                    run = false;
                    break;
                case "GETHOSTS":
                    // Prints out list of discovered hosts
                    ArrayList<String> hosts = connection.getHosts();
                    System.out.println("Discovered hosts: " + hosts.toString());
                    break;
                case "REDISCOVER":
                    // Removes all discovered hosts and send out discovery again to all on network
                    connection.removeAllHost();
                    connection.broadcastToAllHostsOnNetwork(MessageUtil.getMessage(MessageUtil.MessageType.DISCOVER, null));
                    break;
            }
        }
    }
}
