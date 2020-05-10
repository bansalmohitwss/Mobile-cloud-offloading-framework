package communication;

import android.util.Log;

public class ClientThread extends Thread {

    Client client;
    SocketData socketData;
    int type;

    public ClientThread(Client client, SocketData socketData, int type)
    {
        this.client = client;
        this.socketData = socketData;
        this.type = type;
    }

    public SocketData getSocketData() {
        return socketData;
    }

    public void setSocketData(SocketData socketData) {
        this.socketData = socketData;
    }

    @Override
    public void run()
    {
        if(type==1){
            client.connect();
        }else if(type==2){
            client.disconnect();
        }else if(type==3){
            client.sendData(socketData);
        }else if(type==4){
            Log.i("ClientThread.java","Going to receive socket data from server");
            synchronized (this)
            {
                socketData = client.receiveData();
                this.notify();
            }
            Log.i("ResourceHandler.java ","Confirmation Received");
        }
    }
}
