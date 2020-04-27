package communication;

import java.net.Socket;

public class ServerThread extends Thread {

    Server server;
    Socket socket;
    
    public ServerThread(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }
    
    public void run()
    {
        server.deviceRegistry(socket);
    }
    
}
