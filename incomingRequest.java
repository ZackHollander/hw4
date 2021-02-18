import java.io.*;
import java.net.*;
import java.util.*;

public class incomingRequest {
    public static void main(String argv[]) throws Exception {
        int port = Integer.parseInt(argv[0]);

        ServerSocket socket = new ServerSocket(port);

        while(true) {
            Socket connection = socket.accept();
            serveRequest request = new serveRequest(connection);
            Thread thread = new Thread(request);
            thread.start();
        }
    }
}