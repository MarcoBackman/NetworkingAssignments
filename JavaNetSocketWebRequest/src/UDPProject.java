import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

public class UDPProject {
    int portnum = 23185;
    String ip = "35.231.27.109";
    DatagramSocket socket;
    InetAddress serverAddr;
    byte[] buffer = new byte[512];
    
    private void simulateUDPConnection() throws IOException  {
        //Socket using local host
        socket = new DatagramSocket(); 
        serverAddr = InetAddress.getByName(ip);

        try {
            String trackAccount = "sbaek2015";
            byte[] buf = trackAccount.getBytes();
            DatagramPacket packet = 
               new DatagramPacket(buf, buf.length, serverAddr, portnum);
            socket.send(packet);
    
            DatagramPacket response = new DatagramPacket(buffer, buffer.length);
            socket.receive(response);
            
            byte[] buffer1 = response.getData();
            String str1 = new String(buffer1, StandardCharsets.UTF_8);
            System.out.println("Packet data contains: " + str1);
            System.out.println("Packet is using port: " + response.getPort());
            System.out.println("Length of the packet: " + response.getLength());

        } catch (SocketTimeoutException ex) {
            System.out.println("Timeout error: " + ex.getMessage());
            ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println("Client error: " + ex.getMessage());
            ex.printStackTrace();
        }
        socket.close();
    }

    public static void main(String[] args) throws IOException {
        UDPProject inst = new UDPProject();
        inst.simulateUDPConnection();
    }
}