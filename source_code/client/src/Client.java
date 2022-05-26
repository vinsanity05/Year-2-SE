import java.io.*;
import java.net.Socket;

public class Client extends Thread {

    private boolean connected = false;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private int port = 1000;
    private String fromUser = null;
    private String hostName = "localhost";


    @Override
    public void run() {
        try {
            initSocket();
            out.println("connected");
            System.out.println(in.readLine());
            socket.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public String requestFromServer(String message) {
        try {
            initSocket();
            out.println(message);
            String response = in.readLine();
            socket.close();
            return response;
        } catch (IOException e) {
            System.out.println(e);
            return null;
        }
    }

    private void initSocket() throws IOException {
        socket = new Socket(hostName, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        connected = true;
    }
}

