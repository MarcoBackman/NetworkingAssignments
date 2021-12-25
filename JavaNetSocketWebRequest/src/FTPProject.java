import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class FTPProject {
    int httpPort = 80;
    int httpsPort = 443;
    String host = "www.fit.edu";
    byte[] buffer = new byte[512];

    String requestHeaders = "GET / HTTP/1.1\r\n"
    + "Host: www.fit.edu\r\n"
    + "Connection: Close\r\n\r\n";

    FileWriter fw;

    private void setUpFile() {
        File file = new File("./data.txt");
        try {
            fw = new FileWriter(file);    
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void SSLRequest() {
        try {
            //HTTPS - SSL socket
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket sslsocket = (SSLSocket) factory.createSocket(host, httpsPort);

            //Form SSL connetction with the server
            sslsocket.startHandshake();

            //Form byte data
            byte[] bytesOfRequest = requestHeaders.getBytes();

            //Request
            if (sslsocket.isConnected()) {
                OutputStream outputStream = sslsocket.getOutputStream();
                outputStream.write(bytesOfRequest);
                outputStream.flush();
            }

            //Response
            InputStream inputStream = sslsocket.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            //Read Response
            String outStr;
            while ((outStr = bufferedReader.readLine()) != null) {
                fw.write(outStr);
            }

            sslsocket.close();
        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }

    private void clientRequest() {
        try {
            //HTTP socket
            Socket socket = new Socket(host, httpPort);

            //Form byte data
            byte[] bytesOfRequest = requestHeaders.getBytes();

            //Request
            if (socket.isConnected()) {
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(bytesOfRequest);
                outputStream.flush();
            }

            //Response
            InputStream inputStream = socket.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            //Read Response
            String outStr;
            while ((outStr = bufferedReader.readLine()) != null) {
                System.out.println(outStr);
            }

            socket.close();
        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) throws IOException {
        FTPProject inst = new FTPProject();
        inst.clientRequest();
        inst.setUpFile();
        inst.SSLRequest();
    }
}
