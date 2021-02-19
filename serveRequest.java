import java.io.*;
import java.net.*;

public class serveRequest implements Runnable {
    Socket socket;

    public serveRequest(Socket socket) throws Exception {
        this.socket = socket;
    }

    public void run() {
        try {
            processRequest();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void processRequest() throws Exception {
        InputStream is = socket.getInputStream();
        DataOutputStream os = new
            DataOutputStream(socket.getOutputStream());
        
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        
        while(true) {
            String msg = br.readLine();
            System.out.println("Received from client: ");
            System.out.println(msg);
            
            String outputMsg = msg.toUpperCase();
            os.writeBytes(outputMsg);
            os.writeBytes("\r\n");
            System.out.println("Sent to client: ");
            System.out.println(outputMsg);
        }
    }
}