package com.example.clientframework.ResourceSharing;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.clientframework.MainActivity;
import com.example.clientframework.R;

import communication.Client;
import communication.ClientThread;
import communication.SocketData;

public class ResourceHandler extends AppCompatActivity {

    private Client client;
    private Button startButton;
    private Button closeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource_handler);

        startButton = (Button)findViewById(R.id.start);
        closeButton = (Button)findViewById(R.id.close);
        client = MainActivity.client;

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new ClientThread(client,null,2).start();
                Intent intent = new Intent(ResourceHandler.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startButton.setEnabled(false);
                new ClientThread(client,new SocketData(MainActivity.SERVICE_REGISTRY),3).start();

                ClientThread clientThread = new ClientThread(client,null,4);
                clientThread.start();
                SocketData socketData = getReceiveData(clientThread);
                Log.i("socketData123 ",""+socketData);

                if(socketData !=null && socketData.getType() == MainActivity.REGISTRATION_SUCCESS){
                    Log.i("In Last block","going to show toast");
                    Toast.makeText(ResourceHandler.this,"Successfully registered to the Server",Toast.LENGTH_LONG).show();
                    waitForData();
                }
                else{
                    Toast.makeText(ResourceHandler.this,"Registration at Server Failed",Toast.LENGTH_LONG).show();
                    startButton.setEnabled(true);
                }
            }
        });

    }

    private void waitForData()
    {
        SocketData socketData;
        while(true)
        {
            ClientThread clientThread = new ClientThread(client,null,4);
            clientThread.start();
            socketData = getReceiveData(clientThread);
            Toast.makeText(ResourceHandler.this,"Successfully Received data from Server",Toast.LENGTH_LONG);
        }
    }

    private SocketData getReceiveData(ClientThread clientThread)
    {
        SocketData socketData;
        synchronized (clientThread)
        {
            try {
                clientThread.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            socketData = clientThread.getSocketData();
        }
        return socketData;
    }

}


class ReceiverThread extends Thread
{

}
