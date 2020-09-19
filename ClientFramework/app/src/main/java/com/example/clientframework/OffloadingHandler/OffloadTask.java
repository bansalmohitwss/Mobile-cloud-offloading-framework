package com.example.clientframework.OffloadingHandler;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.clientframework.ConnectionSetup;
import com.example.clientframework.MainActivity;
import com.example.clientframework.R;

import java.util.Collection;
import java.util.Collections;
import java.util.Vector;

import communication.Client;
import communication.SocketData;

public class OffloadTask extends AppCompatActivity {

    private Button offloadBtn;
    private Button localBtn;
    private Button close;
    private EditText arraySize;
    private boolean isRunning;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offload_task);

        offloadBtn = (Button)findViewById(R.id.button4);
        localBtn = (Button)findViewById(R.id.button5);
        close = (Button)findViewById(R.id.close2);
        arraySize = (EditText)findViewById(R.id.sizeText);
        isRunning = false;

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isRunning){
                    Toast.makeText(OffloadTask.this,"Can't exist. Task is running",Toast.LENGTH_LONG).show();
                }else{
                    Intent intent = new Intent(OffloadTask.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        offloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isRunning) {
                    Toast.makeText(OffloadTask.this, "Another task is in Progress.Wait till it finishes", Toast.LENGTH_LONG).show();
                    return;
                }
                Vector<Integer> vector = new Vector<Integer>();
                int size=0;
                if(arraySize.getText().toString().equals("") || (size = Integer.parseInt(arraySize.getText().toString()))==0){
                    Toast.makeText(OffloadTask.this,"Size of Array should be greater than 0",Toast.LENGTH_LONG).show();
                    return;
                }

                isRunning = true;

                for(int i=size;i>=1;i--)
                    vector.add(i);

                double startTime = System.nanoTime();
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
                    double duration = (System.nanoTime() - startTime)/1000000000;
                    Toast.makeText(OffloadTask.this,"Successfully Received the Result \n"+"Total Time : "+duration+"sec.",Toast.LENGTH_LONG).show();
                }

                isRunning = false;
            }
        });

        localBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isRunning){
                    Toast.makeText(OffloadTask.this,"Another task is in Progress.Wait till it finishes",Toast.LENGTH_LONG).show();
                    return;
                }

                Vector<Integer> vector = new Vector<Integer>();
                int size=0;
                if(arraySize.getText().toString().equals("") || (size = Integer.parseInt(arraySize.getText().toString()))==0){
                    Toast.makeText(OffloadTask.this,"Size of Array should be greater than 0",Toast.LENGTH_LONG).show();
                    return;
                }

                isRunning = true;

                for(int i=size;i>=1;i--)
                    vector.add(i);

                double startTime = System.nanoTime();

                for(int i=0;i<size-1;i++)
                    for(int j=0;j<size-i-1;j++){
                        if(vector.get(j) > vector.get(j+1)){
                            int temp = vector.get(j);
                            vector.set(j,vector.get(j+1));
                            vector.set(j+1,temp);
                        }
                    }

                double duration = (System.nanoTime() - startTime)/1000000000;
                Toast.makeText(OffloadTask.this,"Successfully Received the Result \n"+"Total Time : "+duration+"sec.",Toast.LENGTH_LONG).show();
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
