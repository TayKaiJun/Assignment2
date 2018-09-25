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

class ConnListen implements Runnable{

    DatagramSocket mysocket;
    boolean running;

    public ConnListen(DatagramSocket mysocket){
        this.mysocket = mysocket;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        running = true;
        while(running)
        {
            try
            {
                mysocket.receive(packet);
                String message = new String(buffer, 0, packet.getLength());
                //String message = new String(packet.getData()).trim();
                System.out.println(packet.getAddress().getHostAddress() + " said: " + message);
            }
            catch (Exception e) {
            }

        }
    }

    public void start()
    {
        Thread thread = new Thread(this);
        thread.start();
    }

    public void stop()
    {
        running = false;
        mysocket.close();
    }
}