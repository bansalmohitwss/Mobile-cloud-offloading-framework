package com.example.clientframework.OffloadingHandler;

import com.example.clientframework.ConnectionSetup;

import communication.Client;
import communication.SocketData;

public class OffloadingThread extends Thread
{
    private SocketData sendData;
    private SocketData receiveData;
    private Client client;

    public OffloadingThread(SocketData sendData){
        this.client = new Client();
        this.sendData = sendData;
        this.receiveData = null;
    }

    public SocketData getReceiveData() {
        return receiveData;
    }

    public void setReceiveData(SocketData receiveData) {
        this.receiveData = receiveData;
    }

    public void run()
    {
        synchronized (this)
        {
            ConnectionSetup.initiateConnection(client);
            if(client.isConnected() == false)
                return;
            client.sendData(sendData);
            receiveData = client.receiveData();
            this.notify();
        }
    }
}
