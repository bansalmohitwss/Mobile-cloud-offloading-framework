package com.example.clientframework.ResourceHandler;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.clientframework.ConnectionSetup;
import com.example.clientframework.MainActivity;
import com.example.clientframework.R;

import java.util.Collections;

import communication.Client;
import communication.SocketData;

public class ResourceAllocator extends AppCompatActivity {

    private Client client;
    private PerfromTask perfromTask;
    private Button closeButton;
    private TextView textView;
    private Handler handler;
    private Bundle bundle;
    public static boolean isRunning;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource_handler);

        client = ConnectionSetup.client;
        closeButton = (Button)findViewById(R.id.close);
        textView = (TextView)findViewById(R.id.statusView);
        bundle = new Bundle();
        isRunning = false;

        handler = new Handler(Looper.getMainLooper()){

            @Override
            public void handleMessage(@NonNull Message msg) {
                bundle = msg.getData();
                String str = bundle.getString("status");
                textView.setText(str);
            }
        };

        perfromTask = new PerfromTask(client,handler);
        perfromTask.start();

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isRunning == true){
                    Toast.makeText(ResourceAllocator.this,"A Task is running, can't exit now",Toast.LENGTH_LONG).show();
                }else{
                    client.setConnected(false);
                    client.disconnect();
                    Intent intent = new Intent(ResourceAllocator.this,ConnectionSetup.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}


class PerfromTask extends Thread
{
    Client client;
    Handler handler;

    public PerfromTask(Client client,Handler handler){
        this.client = client;
        this.handler = handler;
    }

    public void run()
    {
        while(client.isConnected())
        {
            String status = "Waiting for Tasks from Server..."; showStatus(status);
            SocketData socketData = client.receiveData();
            if(socketData == null || socketData.getType() != MainActivity.OFFLOAD_TASK)
                break;

            ResourceAllocator.isRunning=true;
            status = "Successfully Received Task, Executing Task..."; showStatus(status);
            Collections.sort(socketData.vector);

            status = "Task Execution Successful, Ready to send Result..." ; showStatus(status);
            socketData.setType(MainActivity.SUBMIT_RESULT);
            client.sendData(socketData);
            status = "Successfully Send Result to Server..."; showStatus(status);

            ResourceAllocator.isRunning = false;
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void showStatus(String status)
    {
        Message message = Message.obtain();
        Bundle bundle = new Bundle();
        bundle.putString("status",status);
        message.setData(bundle);
        handler.sendMessage(message);
    }
}

