package resourceAllocation;

import communication.DeviceInfoData;
import serviceRegistry.*;
import java.io.*;
import java.net.*;
import communication.SocketData;
import communication.SortData;
import communication.OcrData;
import communication.Server;
import communication.TaskData;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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
        Server.serverUi.setLogs("Inside addTask Method");
        SocketData socketData = (SocketData)data;
        TaskInfoData taskInfoData = new TaskInfoData(socket,objectInputStream,objectOutputStream);
        if(socketData.getType() == OffloadingServer.SORT_TASK_REGISTRY){
            taskInfoData.setType("Sorting Task");
        }else if(socketData.getType() == OffloadingServer.OCR_TASK_REGISTRY){
            taskInfoData.setType("OCR Task");
        }
        taskInfoData.setTaskData(data);
        taskInfoData.setStatus("Pending");
        taskList.add(taskInfoData);
    }
    
    public void resAlloc(Vector<Vector<Integer> > x, Vector<Integer> y){
        
        synchronized(ServiceRegistry.deviceList){
            synchronized(taskList){
                Collections.sort(ServiceRegistry.deviceList, new SortDevices());
                if(taskList.size()>0 &&  !taskList.get(0).getStatus().equals("Completed"))
                this.allocateResource(ServiceRegistry.deviceList.get(0), taskList.get(0));
            }
        }
    }
    
    public void allocateResource(DeviceData deviceData, TaskInfoData taskInfoData)
    {
        deviceData.setIsbusy(1);
        taskInfoData.setStatus("Executing");
        Object data = taskInfoData.getTaskData();
        SocketData socketData = (SocketData)data;
        Server.serverUi.setLogs(""+socketData.getType());
        
        if(socketData.getType() == OffloadingServer.SORT_TASK_REGISTRY){
            SortData sortData = (SortData)data;
            sortData.setType(OffloadingServer.SORT_OFFLOAD_TASK);
            data = (Object)sortData;
        }else if(socketData.getType() == OffloadingServer.OCR_TASK_REGISTRY){
            OcrData ocrData = (OcrData)data;
            ocrData.setType(OffloadingServer.OCR_OFFLOAD_TASK);
            data = (Object)ocrData;
        }
        
        SocketData sData = new SocketData(OffloadingServer.EXIT_FAILURE, -1);
        
        try{
            deviceData.getObjectOutputStream().writeObject(data);
            data = (Object)deviceData.getObjectInputStream().readObject();
            Server.serverUi.setLogs("Read from provider");
            taskInfoData.getObjectOutputStream().writeObject(data);
        }catch(IOException ex){
            Logger.getLogger(ResourceAllocator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ResourceAllocator.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Server.serverUi.setLogs("Exit allocate Resource");
        deviceData.setIsbusy(0);
        taskInfoData.setStatus("Completed");
    }
    
    
    public synchronized DeviceData findDevice(Object offloadData)
    {
        DeviceData data = null;
        Server.serverUi.setLogs("In findDevice Method");
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
        Server.serverUi.setLogs("Exiting FindDevice Method: "+data);
        return data;
    }
}

class SortTasks implements Comparator<TaskInfoData> 
{
    @Override
    public int compare(TaskInfoData o1, TaskInfoData o2) {
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);
        
        TaskData taskData1 = (TaskData)o1.getTaskData();
        TaskData taskData2 = (TaskData)o2.getTaskData();
        
        int totalTime1 = (taskData1.getFinalHour() - currentHour)*60 + (taskData1.getFinalMinute() - currentMinute);
        int totalTime2 = (taskData2.getFinalHour() - currentHour)*60 + (taskData2.getFinalMinute() - currentMinute);
        
        return 1;
    }
    
}

class SortDevices implements Comparator<DeviceData>{

    @Override
    public int compare(DeviceData o1, DeviceData o2) {
        return (int)(o1.getDeviceInfoData().getBidPrice()*1e6/o1.getDeviceInfoData().getCpuFreq()) - 
                (int)(o2.getDeviceInfoData().getBidPrice()*1e6/o2.getDeviceInfoData().getCpuFreq());
    }
    
}

