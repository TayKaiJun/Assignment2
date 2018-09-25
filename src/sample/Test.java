package sample;


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
                    System.out.print("Please enter the message to be sent: ");
                    String message = scanner.next();
                    connection.broadcastToDiscoveredHosts(MessageUtil.getMessage(MessageUtil.MessageType.MESSAGE, message));
                    break;
                case "STOP":
                    connection.stop();
                    break;
            }
        }
    }
}
