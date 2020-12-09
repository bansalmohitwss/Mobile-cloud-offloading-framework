package serviceRegistry;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import communication.DeviceInfoData;

public class DeviceData
{
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private DeviceInfoData deviceInfoData;
    private int isbusy;
    
    public DeviceData(){
    }
    
    public DeviceData(Socket socket, ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream, DeviceInfoData deviceInfoData, int isbusy) {
        this.socket = socket;
        this.objectOutputStream = objectOutputStream;
        this.objectInputStream = objectInputStream;
        this.deviceInfoData = deviceInfoData;
        this.isbusy = isbusy;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public ObjectOutputStream getObjectOutputStream() {
        return objectOutputStream;
    }

    public void setObjectOutputStream(ObjectOutputStream objectOutputStream) {
        this.objectOutputStream = objectOutputStream;
    }

    public ObjectInputStream getObjectInputStream() {
        return objectInputStream;
    }

    public void setObjectInputStream(ObjectInputStream objectInputStream) {
        this.objectInputStream = objectInputStream;
    }

    public int getIsbusy() {
        return isbusy;
    }

    public void setIsbusy(int isbusy) {
        this.isbusy = isbusy;
    }

    public DeviceInfoData getDeviceInfoData() {
        return deviceInfoData;
    }
    
    
}
