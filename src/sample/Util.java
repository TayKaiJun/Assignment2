package sample;

import java.math.BigInteger;
import java.net.*;

public class Util {
    public static String getLocalIpAddressWithSubnet(){
        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            String ip = socket.getLocalAddress().getHostAddress();

            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(socket.getLocalAddress());

            for (InterfaceAddress address : networkInterface.getInterfaceAddresses()) {
                if (address.getAddress() instanceof Inet6Address) continue;
                return address.getAddress().getHostAddress() + "/" + Short.toString(address.getNetworkPrefixLength());
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int convertIpAddressToInt(String ipAddress){
        String[] parts = ipAddress.split("\\.");

        int ipNumbers = 0;
        for (int i = 0; i < 4; i++) {
            ipNumbers += Integer.parseInt(parts[i]) << (24 - (8 * i));
        }

        return ipNumbers;
    }

    public static String convertIntToIpAddress(int ipAddress){
        String ipAddressString = "";
        for (int i = 0; i < 4; i++) {
            if (i != 0) ipAddressString += ".";
            int ipIntNumber = (int)((ipAddress >> 8 * (3 - i)) & 255);
            ipAddressString += Integer.toString(ipIntNumber);
        }

        return ipAddressString;
    }

    public static String getFirstAddress(String address, int subnetMaskValue){
        int intAddress = convertIpAddressToInt(address);
        int subnetMask = getSubnetMask(subnetMaskValue);

        return convertIntToIpAddress(intAddress & subnetMask);
    }

    public static String getLastAddress(String address, int subnetMaskValue){
        int intAddress = convertIpAddressToInt(getFirstAddress(address, subnetMaskValue));
        int subnetMask = ~getSubnetMask(subnetMaskValue);

        return convertIntToIpAddress(intAddress | subnetMask);
    }

    public static int getSubnetMask(int subnetMaskValue){
        return ~((1 << (32 - subnetMaskValue)) - 1);
    }

    public static void main(String[] args){
        System.out.println(Util.convertIpAddressToInt("192.168.0.1"));
        System.out.println(Util.convertIntToIpAddress(Util.convertIpAddressToInt("192.168.0.1")+255));
        System.out.println(Util.getFirstAddress("192.168.0.1", 22));
        System.out.println(Util.getLastAddress("192.168.0.1", 22));
    }
}
