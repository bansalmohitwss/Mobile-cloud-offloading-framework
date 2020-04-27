package communication;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import offloadingserver.OffloadingServer;
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
    
    public Server()
    {
        serviceRegistry = new ServiceRegistry();
        resourceAllocator = new ResourceAllocator(serviceRegistry);
    }
    
    public void serverStart()
    {
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
            
            Thread thread = new ServerThread(this,socket);
            thread.start();
        }
    }
    
    public synchronized void deviceRegistry(Socket socket)
    {
        try {
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            socketData = (SocketData)objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(socketData.getType() == OffloadingServer.SERVICE_REGISTRY){
            serviceRegistry.addDevice(socket, objectInputStream, objectOutputStream, socketData);
        }else if(socketData.getType() == OffloadingServer.TASK_REGISTRY){
            resourceAllocator.allocateResource(socket, objectInputStream, objectOutputStream, socketData);
        }
            
    }
    
}


