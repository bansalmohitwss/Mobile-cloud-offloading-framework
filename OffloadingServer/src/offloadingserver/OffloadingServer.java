package offloadingserver;
import communication.Server;

public class OffloadingServer {

    public static final int SERVICE_REGISTRY = 1;
    public static final int OCR_TASK_REGISTRY = 2;
    public static final int SORT_TASK_REGISTRY = 3;
    public static final int OCR_OFFLOAD_TASK = 4;
    public static final int SORT_OFFLOAD_TASK = 5;
    public static final int SUBMIT_RESULT = 6;
    public static final int REGISTRATION_SUCCESS = 7;
    public static final int EXIT_FAILURE = 8;
    public static final int ACTIVE_CHECK = 9;
    public static final int PORT_NO = 5000;

    
    public static void main(String[] args) {
        // TODO code application logic here
        
        Server server = new Server();
        Server.serverUi.setLogs("Starting Offloading Server");
        server.serverStart();
        
    }
    
}
