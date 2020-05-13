package serviceRegistry;
import java.io.*;
import java.net.*;
import java.util.*;
import communication.SocketData;
import java.util.logging.Level;
import java.util.logging.Logger;
import offloadingserver.OffloadingServer;

public class ServiceRegistry {
    
    public Vector<DeviceData> deviceList;
    
    public ServiceRegistry()
    {
        deviceList = new Vector<>();
        deviceList.ensureCapacity(10);
    }
    
    public synchronized void addDevice(Socket socket,ObjectInputStream objectInputStream,ObjectOutputStream objectOutputStream,SocketData socketData)
    {
        DeviceData deviceData = new DeviceData(socket,objectOutputStream,objectInputStream,0);
        deviceList.add(deviceData);
        System.out.println("Successfully added device : "+deviceList.size());
        
        SocketData confData =  new SocketData(OffloadingServer.REGISTRATION_SUCCESS);
        try {
            objectOutputStream.writeObject(confData);
        } catch (IOException ex) {
            Logger.getLogger(ServiceRegistry.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error in sending confirmation message");
        }
        
        System.out.println("Successfully send confirmation");
        
        
        /*
        try{
            Thread.sleep(2000);
            objectOutputStream.writeObject(new SocketData(100));
        }catch(IOException ex){
            System.out.println("Error in sending data");
        } catch (InterruptedException ex) {
            Logger.getLogger(ServiceRegistry.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println("Successfully send data");
        
        SocketData recData=null;
        try {
            recData = (SocketData)objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(ServiceRegistry.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error in Receiving Data");
            return;
        }
        
        for(Integer i : recData.arr)
            System.out.print(i+" ");
        
        System.out.println();
                */
    }
}

