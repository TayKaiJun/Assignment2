package sample;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Scanner;

/**
 * Created by Kai Jun on 25/9/2018.
 */
public class tester {


    public static void main(String[] args) throws Exception {
        DatagramSocket mysocket;
        String ipadd = "";
        int port = 1024;

        Scanner scanner = new Scanner(System.in);
        System.out.print("IP Address: ");
        if(scanner.hasNext()){
            ipadd = scanner.next();
        }
        System.out.print("Port:");
        if(scanner.hasNextInt()){
            port = scanner.nextInt();
        }
        System.out.println("Source" + ipadd + "," + port );
        InetSocketAddress address = new InetSocketAddress(ipadd, port);
        mysocket = new DatagramSocket(address);

        String message = "";

        ConnListen connListen = new ConnListen(mysocket);
        connListen.start();

        String targetipadd = "";
        int targetport = 1024;

        System.out.print("Receiver IP Address: ");
        if(scanner.hasNext()){
            targetipadd = scanner.next();
        }
        System.out.print("Receiver Port:");
        if(scanner.hasNextInt()){
            targetport = scanner.nextInt();
        }
        System.out.println("Receiver: " + targetipadd + "," + targetport);
        InetSocketAddress receiveraddress = new InetSocketAddress(targetipadd, targetport);
        System.out.println("Enter \"SEND\" to type a message to send to peers or \"STOP\" to end the program");


        boolean run = true;
        while(run) {
            switch (scanner.next()) {
                case "SEND":
                    System.out.print("Please enter the message to be sent: ");
                    message = scanner.next();
                    byte[] buffer = message.getBytes();
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    packet.setSocketAddress(receiveraddress);
                    mysocket.send(packet);
                    System.out.println("Done");
                    break;
                case "STOP":
                    connListen.stop();
                    run = false;
                    break;
            }
        }
    }


}