import java.net.ServerSocket;
import java.net.Socket;

public final class WebServer {

    public static void main(String[] args) throws Exception {
        int port = 8000;

        ServerSocket socket = new ServerSocket(port);

        while (true) {
            Socket connection = socket.accept();
            HTTPRequest request = new HTTPRequest(connection);
            Thread thread = new Thread(request);
            thread.start();
        }
    }
}