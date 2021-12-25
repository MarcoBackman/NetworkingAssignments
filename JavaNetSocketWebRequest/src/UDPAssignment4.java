import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

public class UDPAssignment4 {
    int portnum = 31098;
    String ip = "35.231.27.109";
    DatagramSocket socket;
    InetAddress serverAddr;
    byte[] receivedBuffer = new byte[512];

    private void simulateUDPConnection() throws IOException  {
        //Socket using local host
        socket = new DatagramSocket(); 
        serverAddr = InetAddress.getByName(ip);

        try {
            String requestListString = "LIST";
            byte[] buffer = requestListString.getBytes();
            DatagramPacket requestPacket = 
               new DatagramPacket(buffer, buffer.length, serverAddr, portnum);
            socket.send(requestPacket);

            DatagramPacket response = new DatagramPacket(receivedBuffer, receivedBuffer.length, serverAddr, portnum);
            socket.receive(response);

            buffer = response.getData();

            //split received data by comma
            String tempString = new String(buffer, StandardCharsets.UTF_8);
            String[] arr = tempString.split(",");

            for (int i = 0; i < arr.length; i++) {
                tempString = "STATE " + arr[i];
                buffer = tempString.getBytes();
                DatagramPacket stateRequest = new DatagramPacket(buffer, buffer.length, serverAddr, portnum);
                
                //send request
                socket.send(stateRequest);
                
                //get response
                DatagramPacket responseFull = new DatagramPacket(receivedBuffer, receivedBuffer.length, serverAddr, portnum);
                socket.receive(responseFull);

                //form final result
                byte[] resultLine = responseFull.getData();
                tempString = new String(resultLine, StandardCharsets.UTF_8);
                System.out.println(tempString);
            }
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
        UDPAssignment4 inst = new UDPAssignment4();
        inst.simulateUDPConnection();
    }
}