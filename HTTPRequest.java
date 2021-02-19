import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.StringTokenizer;


public final class HTTPRequest implements Runnable{

    private final static String CRLF = "\r\n";
    Socket socket;

    //Constuctor
    public HTTPRequest (Socket socket) throws Exception{
        this.socket = socket;
    }
    
    // Implement the run method of the Runnable interface 
    public void run() {
        try{
            processRequest();
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
    
    private void processRequest() throws Exception {
        InputStream is = socket.getInputStream();
        DataOutputStream os = new DataOutputStream(socket.getOutputStream());
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        
        String requestLine = br.readLine();
        
        // Extract the filename from the request line.
        StringTokenizer tokens = new StringTokenizer(requestLine);
        tokens.nextToken();
        String fileName = "." + tokens.nextToken();

        //Open the requested file.
        FileInputStream fis = null;
        boolean fileExists = true;
        try {
            fis = new FileInputStream(fileName);
        } catch (FileNotFoundException e){
            fileExists = false;
        }

        // Construct the response message.
        String statusLine = null;
        String contentTypeLine = null;
        String entityBody = null;
        if (fileExists) {
            statusLine = "HTTP/1.0 202 Accepted" + CRLF;
            contentTypeLine = "Content-type: " + contentType(fileName) + CRLF;
        } else {
            statusLine = "HTTP/1.0 404 Not Found" + CRLF;
            contentTypeLine = "text/html; charset=UTF-8" + CRLF;
            entityBody = "<HTML>" + 
                        "<HEAD><TITLE>Not Found</TITLE></HEAD>" + 
                        "<BODY>Not Found</BODY></HTML>";
        }
        
        // Send the status line.
        os.writeBytes(statusLine);
        // Send the content type line.
        os.writeBytes(contentTypeLine);
        // Send a blank line to indicate the end of the header lines.
        os.writeBytes(CRLF);

        // Send the entity body.
        if (fileExists) {
            sendBytes(fis, os);
            fis.close();
        } else {
            os.writeBytes(entityBody);
        }
        
        // Part A: Reading the request line and header lines.
        // System.out.println("\n" + requestLine);

        // String headerLine = null;
        // while((headerLine = br.readLine()).length() != 0) {
        //     System.out.println(headerLine);
        // }

        // Close streams and socket.
        os.close();
        br.close();
        socket.close();
    }

    private static void sendBytes(FileInputStream fis, DataOutputStream os) throws Exception {
        // Construct a 1K buffer to hold bytes on their way to the socket. 
        byte[] buffer = new byte[1024];
        int bytes = 0;

        // Copy requested file into the socket's output stream.
        while((bytes = fis.read(buffer)) != -1) {
            os.write(buffer, 0, bytes);
        }
    }

    private static String contentType(String fileName) {
        if (fileName.endsWith(".htm") || fileName.endsWith(".html")) {
            return "text/html";
        } 
        if (fileName.endsWith(".mp3")) {
            return "audio/mpeg";
        }
        if (fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        return "application/octet-stream";
    }
} 