package resourceAllocation;

import serviceRegistry.*;
import java.io.*;
import java.net.*;
import communication.SocketData;
import java.util.logging.Level;
import java.util.logging.Logger;
import offloadingserver.OffloadingServer;


public class ResourceAllocator {

    ServiceRegistry serviceRegistry;

    public ResourceAllocator() {
    }
    
    public ResourceAllocator(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }
    
    public void allocateResource(Socket socket,ObjectInputStream objectInputStream,ObjectOutputStream objectOutputStream,SocketData socketData)
    {
        socketData.setType(OffloadingServer.OFFLOAD_TASK);
        DeviceData deviceData;
        deviceData = findDevice(socketData);
        SocketData sData = new SocketData(OffloadingServer.EXIT_FAILURE);
        
        if(deviceData == null)
        {
            try {
                
                objectOutputStream.writeObject(sData);
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(ResourceAllocator.class.getName()).log(Level.SEVERE, null, ex);
            }
            return;
        }
        
        SocketData data;
        try {
            data = (SocketData)deviceData.getObjectInputStream().readObject();
            objectOutputStream.writeObject(data);
        } catch (IOException ex) {
            Logger.getLogger(ResourceAllocator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ResourceAllocator.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        deviceData.setIsbusy(0);
    }
    
    public synchronized DeviceData findDevice(SocketData socketData)
    {
        DeviceData data = null;
        System.out.println("In findDevice Method");
        for(DeviceData deviceData : serviceRegistry.deviceList)
        {
            if(deviceData.getIsbusy() == 1)
                continue;
            boolean flag=false;
            
            try {
                deviceData.getObjectOutputStream().writeObject(socketData);
            } catch (IOException ex) {
                flag=true;
            }
            
            if(!flag)
            {
                deviceData.setIsbusy(1);
                data=deviceData;
                break;
            }
        }
        System.out.println("Exiting FindDevice Method"+data);
        return data;
    }
}
