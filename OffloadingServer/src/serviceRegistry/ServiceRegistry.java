package serviceRegistry;
import java.io.*;
import java.net.*;
import java.util.*;
import communication.SocketData;
import java.util.logging.Level;
import java.util.logging.Logger;
import offloadingserver.OffloadingServer;
import communication.DeviceInfoData;

public class ServiceRegistry {
    
    public static Vector<DeviceData> deviceList;
    
    public ServiceRegistry()
    {
        deviceList = new Vector<>();
        deviceList.ensureCapacity(10);
    }
    
    public synchronized void addDevice(Socket socket,ObjectInputStream objectInputStream,ObjectOutputStream objectOutputStream,DeviceInfoData deviceInfoData)
    {
        DeviceData deviceData = new DeviceData(socket,objectOutputStream,objectInputStream,deviceInfoData, 0);
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
        
    }
}

