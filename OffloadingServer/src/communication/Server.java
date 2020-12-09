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
    
    public Server()
    {
        serviceRegistry = new ServiceRegistry();
        resourceAllocator = new ResourceAllocator();
        serverUiHandler = new ServerUiHandler();
    }
    
    public void serverStart()
    {
        serverUiHandler.start();
        try{
            serverSocket = new ServerSocket(OffloadingServer.PORT_NO);
            System.out.println("Successfully Server Started");
        }catch(IOException e){
            System.out.println("Error in Starting Server " + e);
        }
            
        while(true)
        {
            try {
                System.out.println("Waiting for client to connect");
                socket = serverSocket.accept();
            } catch (IOException ex) {
                System.out.println("Socket Error "+ex);
            }
            
            System.out.println("One Request for connection");
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
            System.out.println("SocketData is received");
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        
        socketData = (SocketData)data;
        
        System.out.println("Successfully created streams");
        if(socketData.getType() == OffloadingServer.SERVICE_REGISTRY){
            System.out.println("In Service Provider Section");
            serviceRegistry.addDevice(socket, objectInputStream, objectOutputStream, (DeviceInfoData)data);
        }else if(socketData.getType() == OffloadingServer.OCR_TASK_REGISTRY || socketData.getType() == OffloadingServer.SORT_TASK_REGISTRY){
            System.out.println("Inside Service Receiver Section");
            resourceAllocator.addTask(socket, objectInputStream, objectOutputStream, data);
        }
            
    }
    
}

class ServerUiHandler extends Thread {
    
    ServerUi serverUi;

    public ServerUiHandler() {
        serverUi = new ServerUi();
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

