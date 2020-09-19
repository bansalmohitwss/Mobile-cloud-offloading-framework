package com.example.clientframework;

import androidx.appcompat.app.AppCompatActivity;
import communication.*;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.example.clientframework.ResourceHandler.*;

public class ConnectionSetup extends AppCompatActivity {

    static public Client client;
    private Button button;
    private Button homeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_setup);

        button = (Button)findViewById(R.id.button3);
        homeBtn = (Button)findViewById(R.id.home);
        client = new Client();

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConnectionSetup.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               initiateConnection(client);
               if (client.isConnected() == false){
                   Toast.makeText(ConnectionSetup.this,"Can't connect to Server",Toast.LENGTH_LONG).show();
               }
               else if(MainActivity.Id == MainActivity.SERVICE_REGISTRY)
               {
                   new ClientThread(client,new SocketData(MainActivity.SERVICE_REGISTRY),3).start();
                   ClientThread clientThread = new ClientThread(client,null,4);
                   clientThread.start();
                   SocketData socketData ;

                   synchronized (clientThread)
                   {
                       try {
                           clientThread.wait();
                       } catch (InterruptedException e) {
                           e.printStackTrace();
                       }
                       socketData = clientThread.getSocketData();
                   }

                   if(socketData != null && socketData.getType() == MainActivity.REGISTRATION_SUCCESS){
                       Toast.makeText(ConnectionSetup.this,"Successfully established connection with Server",Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(ConnectionSetup.this, ResourceAllocator.class);
                        startActivity(intent);
                        finish();
                   }
                   else{
                       Toast.makeText(ConnectionSetup.this,"Can't connect to Server",Toast.LENGTH_LONG).show();

                   }
               }
            }
        });

    }

    static public void initiateConnection(Client client)
    {
        Log.i("In funtion","Connecting to server, wait for couple of minutes");
        ClientThread clientThread = new ClientThread(client,null,1);
        clientThread.start();
        synchronized (clientThread) {
            try {
                clientThread.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}


class ClientThread extends Thread {

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
            synchronized (this){
                client.connect();
                this.notify();
            }
        }else if(type==2){
            client.disconnect();
        }else if(type==3){
            client.sendData(socketData);
        }else if(type==4){
            synchronized (this)
            {
                socketData = client.receiveData();
                this.notify();
            }
        }
    }
}
