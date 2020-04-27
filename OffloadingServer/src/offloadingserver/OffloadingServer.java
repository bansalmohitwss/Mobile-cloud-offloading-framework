package offloadingserver;
import communication.Server;

public class OffloadingServer {

    public static final int SERVICE_REGISTRY = 1;
    public static final int TASK_REGISTRY = 2;
    public static final int OFFLOAD_TASK = 3;
    public static final int SUBMIT_RESULT = 4;
    public static final int REGISTRATION_SUCCESS = 5;
    public static final int EXIT_FAILURE = 6;
    
    public static final int PORT_NO = 5000;
    
    public static void main(String[] args) {
        // TODO code application logic here
        
        Server server = new Server();
        System.out.println("Starting Offloading Server");
        server.serverStart();
        
    }
    
}
