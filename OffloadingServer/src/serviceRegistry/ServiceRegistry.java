package serviceRegistry;
import java.io.*;
import java.net.*;
import java.util.*;
import communication.SocketData;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        
        SocketData confData =  new SocketData();
        try {
            objectOutputStream.writeObject(confData);
        } catch (IOException ex) {
            Logger.getLogger(ServiceRegistry.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}

