import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/*
    java.util.Date class only for milisecond precision.
    java.time.LocalDate for microsecond/nanosecond support, however low compatability with Date class
     which is also compatible with java.sql.Date and java.sql.Time
        Reference image: https://i.stack.imgur.com/4NSDx.png
 */

public class RTTAssignment5Micro {
    int portnum = 32123;
    String ip = "35.231.27.109";
    DatagramSocket socket;
    InetAddress serverAddr;
    byte[] receivedBuffer = new byte[512];

    private String changeFormat(LocalDateTime instant) {
        return instant.toString().replaceAll("[TZ]", " ");
    }

    private String getCurrentTimeStamp(int microsecond) {
        //Use bigdecimals
        int hours = microsecond / (60 * 60 * 1000 * 1000);
        int remainder = microsecond % (60 * 60 * 1000 * 1000);

        int minutes = remainder / (60 * 1000 * 1000);
        remainder = remainder % (60 * 1000 * 1000);

        int seconds = remainder / (1000 * 1000);
        remainder = remainder % (1000 * 1000);

        return String.format("%01d:%02d:%02d.%06d", hours, minutes, seconds, remainder);
    }

    //returns microseconds
    private int parseStringToInt(String format) {
        format = format.trim();
        format = format.replace(".", ":");
        String arr[] = format.split(" ");
        String time_format = arr[1];
        String times[] = time_format.split(":");

        int total = 0;
        int seconds = Integer.parseInt(times[2]);
        int microsecond = Integer.parseInt(times[3]);

        total += seconds * 1000000;
        total += microsecond;

        return total;
    }

    //all values are in seconds + microseconds
    private double getTimeDifference(int beforeTime, int rttTime, int serverTime) {
        double rrtTimeHalf = rttTime / 2;
        double resultInMicroSecs = serverTime - (beforeTime + rrtTimeHalf);
        //change microseconds to seconds
        double finalResult = resultInMicroSecs / 1000000;
        return finalResult;
    }

    //This only uses microsecond precision
    private void simulateUDPConnection() throws IOException  {
        //Socket using local host
        socket = new DatagramSocket();
        serverAddr = InetAddress.getByName(ip);
        LocalDateTime beforeDate = null;
        LocalDateTime afterDate = null;

        try {
            String requestListString = "TIME";
            byte[] buffer = requestListString.getBytes();
            DatagramPacket requestPacket =
               new DatagramPacket(buffer, buffer.length, serverAddr, portnum);
            DatagramPacket response
             = new DatagramPacket(receivedBuffer, receivedBuffer.length, serverAddr, portnum);

            //capture local machine time right before sending data
            beforeDate = LocalDateTime.now();

            //send the request to the server
            socket.send(requestPacket);

            socket.receive(response);

            //capture local machine time right after receiving data
            buffer = response.getData();
            afterDate = LocalDateTime.now();

            //decode received data buffer in UTF8 format
            String serverResponse = new String(buffer, StandardCharsets.UTF_8);
            //micro seconds
            int rrt = (afterDate.getNano() - beforeDate.getNano()) / 1000;

            int beforeSeconds = parseStringToInt(changeFormat(beforeDate));
            int serverSeconds = parseStringToInt(serverResponse);

            double result = getTimeDifference(beforeSeconds, rrt, serverSeconds);

            System.out.println("Time before:     " + changeFormat(beforeDate));
            System.out.println("Time Received:   " + serverResponse);
            System.out.println("Time after:      " + changeFormat(afterDate));
            System.out.println();
            System.out.println("RTT:             " + getCurrentTimeStamp(rrt)); // micro seconds
            System.out.println("Time Difference: " + result);

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
        RTTAssignment5Micro inst = new RTTAssignment5Micro();
        inst.simulateUDPConnection();
    }
}