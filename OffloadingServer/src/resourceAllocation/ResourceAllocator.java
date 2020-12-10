package resourceAllocation;

import serviceRegistry.*;
import java.io.*;
import java.net.*;
import communication.SocketData;
import communication.SortData;
import communication.OcrData;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import offloadingserver.OffloadingServer;


public class ResourceAllocator {

    public static Vector<TaskInfoData> taskList;
    
    public ResourceAllocator() {
        taskList = new Vector<>();
        taskList.ensureCapacity(10);
    }
    
    
    public void addTask(Socket socket,ObjectInputStream objectInputStream,ObjectOutputStream objectOutputStream, Object data){
        System.out.println("Inside addTask Method");
        SocketData socketData = (SocketData)data;
        TaskInfoData taskInfoData = new TaskInfoData(socket,objectInputStream,objectOutputStream);
        if(socketData.getType() == OffloadingServer.SORT_TASK_REGISTRY){
            taskInfoData.setType("Array Sorting Task");
        }else if(socketData.getType() == OffloadingServer.OCR_TASK_REGISTRY){
            taskInfoData.setType("OCR Task");
        }
        taskInfoData.setTaskData(data);
        taskInfoData.setStatus("Pending");
        taskList.add(taskInfoData);
    }
    
    public void allocateResource(Socket socket,ObjectInputStream objectInputStream,ObjectOutputStream objectOutputStream,Object data)
    {
        SocketData socketData = (SocketData)data;
        
        if(socketData.getType() == OffloadingServer.SORT_TASK_REGISTRY){
            SortData sortData = (SortData)data;
            sortData.setType(OffloadingServer.SORT_OFFLOAD_TASK);
            data = (Object)sortData;
        }else if(socketData.getType() == OffloadingServer.OCR_TASK_REGISTRY){
            OcrData ocrData = (OcrData)data;
            ocrData.setType(OffloadingServer.OCR_OFFLOAD_TASK);
            data = (Object)ocrData;
        }
        
        DeviceData deviceData;
        deviceData = findDevice(data);
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
        
        try {
            data = (Object)deviceData.getObjectInputStream().readObject();
            System.out.println("Read from provider");
            objectOutputStream.writeObject(data);
        } catch (IOException ex) {
            Logger.getLogger(ResourceAllocator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ResourceAllocator.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println("Exit allocate Resource");
        deviceData.setIsbusy(0);
    }
    
    public synchronized DeviceData findDevice(Object offloadData)
    {
        DeviceData data = null;
        System.out.println("In findDevice Method");
        for(DeviceData deviceData : ServiceRegistry.deviceList)
        {
            if(deviceData.getIsbusy() == 1)
                continue;
            boolean flag=false;
            
            try {
                deviceData.getObjectOutputStream().writeObject(offloadData);
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
        System.out.println("Exiting FindDevice Method: "+data);
        return data;
    }
}
