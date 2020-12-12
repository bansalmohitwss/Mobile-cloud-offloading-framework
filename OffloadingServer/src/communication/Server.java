package communication;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import offloadingserver.OffloadingServer;
import offloadingserver.ServerUi;
import serviceRegistry.ServiceRegistry;
import resourceAllocation.ResourceAllocator;

public class Server extends OffloadingServer{
    
    ServerSocket serverSocket;
    Socket socket;
    ObjectInputStream objectInputStream;
    ObjectOutputStream objectOutputStream;
    SocketData socketData;
    ServiceRegistry serviceRegistry;
    ResourceAllocator resourceAllocator;
    ServerUiHandler serverUiHandler;
    AllocatorThread allocatorThread;
    public static ServerUi serverUi;
    
    public Server()
    {
        serviceRegistry = new ServiceRegistry();
        resourceAllocator = new ResourceAllocator();
        serverUi = new ServerUi();
        serverUiHandler = new ServerUiHandler(serverUi);
        allocatorThread = new AllocatorThread(resourceAllocator);
    }
    
    public void serverStart()
    {
        serverUiHandler.start();
        allocatorThread.start();
        try{
            serverSocket = new ServerSocket(OffloadingServer.PORT_NO);
            Server.serverUi.setLogs("Successfully Server Started");
        }catch(IOException e){
            Server.serverUi.setLogs("Error in Starting Server " + e);
        }
            
        while(true)
        {
            try {
                Server.serverUi.setLogs("Waiting for client to connect");
                socket = serverSocket.accept();
            } catch (IOException ex) {
                Server.serverUi.setLogs("Socket Error "+ex);
            }
            
            Server.serverUi.setLogs("One Request for connection");
            Thread thread = new ServerThread(this,socket);
            thread.start();
        }
    }
    
    public synchronized void deviceRegistry(Socket socket)
    {
        Object data;
        try {
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            data = (Object)objectInputStream.readObject();
            Server.serverUi.setLogs("SocketData is received");
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        
        socketData = (SocketData)data;
        
        Server.serverUi.setLogs("Successfully created streams");
        if(socketData.getType() == OffloadingServer.SERVICE_REGISTRY){
            Server.serverUi.setLogs("In Service Provider Section");
            serviceRegistry.addDevice(socket, objectInputStream, objectOutputStream, (DeviceInfoData)data);
        }else if(socketData.getType() == OffloadingServer.OCR_TASK_REGISTRY || socketData.getType() == OffloadingServer.SORT_TASK_REGISTRY){
            Server.serverUi.setLogs("Inside Service Receiver Section");
            resourceAllocator.addTask(socket, objectInputStream, objectOutputStream, data);
        }
            
    }
    
}

class ServerUiHandler extends Thread {
    
    ServerUi serverUi;

    public ServerUiHandler(ServerUi serverUi) {
        this.serverUi = serverUi;
    }
    
    public void run(){
        serverUi.init();
        serverUi.setVisible(true);
        
        while(true){
            serverUi.setProviders();
            serverUi.setReceivers();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                Logger.getLogger(ServerUiHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

class AllocatorThread extends Thread{
    ResourceAllocator resourceAllocator;

    public AllocatorThread(ResourceAllocator resourceAllocator) {
        this.resourceAllocator = resourceAllocator;
    }
    
    public void run(){
        try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                
            }
        while(true){
            resourceAllocator.resAlloc(null, null);
            Server.serverUi.setLogs("Inside AllocatorThread");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                
            }
        }
    }
}