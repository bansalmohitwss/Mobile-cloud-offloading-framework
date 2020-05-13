package com.example.clientframework.OffloadingHandler;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.clientframework.ConnectionSetup;
import com.example.clientframework.MainActivity;
import com.example.clientframework.R;

import java.util.Vector;

import communication.Client;
import communication.SocketData;

public class OffloadTask extends AppCompatActivity {

    private Button button1;
    private Button close;
    private boolean isRunning;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offload_task);

        button1 = (Button)findViewById(R.id.button4);
        close = (Button)findViewById(R.id.close2);
        isRunning = false;

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isRunning == true){
                    Toast.makeText(OffloadTask.this,"Can't exist. Task is running",Toast.LENGTH_LONG).show();
                }else{
                    Intent intent = new Intent(OffloadTask.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isRunning = true;
                Vector<Integer> vector = new Vector<Integer>();
                for(int i=1000000;i>=0;i--)
                    vector.add(i);

                long startTime = System.nanoTime();
                SocketData socketData = new SocketData(MainActivity.TASK_REGISTRY,vector);
                OffloadingThread offloadingThread = new OffloadingThread(socketData);
                offloadingThread.start();

                synchronized (offloadingThread)
                {
                    try {
                        offloadingThread.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                socketData = offloadingThread.getReceiveData();
                if(socketData==null || socketData.getType() != MainActivity.SUBMIT_RESULT){
                    Toast.makeText(OffloadTask.this,"Some Error Occurred",Toast.LENGTH_LONG).show();
                }
                else{
                    long duration = (System.nanoTime() - startTime)/1000000000;
                    Toast.makeText(OffloadTask.this,"Successfully Received the Result \n"+"Total Time : "+duration+"sec.",Toast.LENGTH_LONG).show();
                }

                isRunning = false;
            }
        });
    }
}


class OffloadingThread extends Thread
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
