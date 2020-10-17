package com.example.clientframework.ResourceHandler;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.example.clientframework.Tasks.OcrTask;
import com.example.clientframework.Tasks.SortTask;

import java.util.Vector;

import communication.Client;
import communication.OcrData;
import communication.SocketData;
import communication.SortData;

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
        closeButton = (Button)findViewById(R.id.localBtn);
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

        perfromTask = new PerfromTask(client,handler, getApplicationContext());
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
    Context context;

    public PerfromTask(Client client,Handler handler, Context context){
        this.client = client;
        this.handler = handler;
        this.context = context;
    }

    public void run()
    {
        while(client.isConnected())
        {
            String status = "Waiting for Tasks from Server..."; showStatus(status);
            Object data = client.receiveData();
            SocketData socketData = (SocketData)data;
            if(socketData == null)
                break;

            ResourceAllocator.isRunning=true;
            status = "Successfully Received Task, Executing Task..."; showStatus(status);

            Log.i("Inside run Method", "Starting executing tasks.");
            if(socketData.getType() == MainActivity.SORT_OFFLOAD_TASK){
                SortData sortData = (SortData)data;
                Vector<Integer> vector = sortData.vector;
                int size = vector.size();
                SortTask.performTask(vector, size);
                status = "Task Execution Successful, Ready to send Result..." ; showStatus(status);
                sortData.setType(MainActivity.SUBMIT_RESULT);
                data = (Object)sortData;
            }
            else if(socketData.getType() == MainActivity.OCR_OFFLOAD_TASK){
                OcrData ocrData = (OcrData)data;
                Bitmap bitmap =  BitmapFactory.decodeByteArray(ocrData.getImage(), 0, ocrData.getImage().length);
                String string  = OcrTask.performTask(bitmap, context);
                ocrData.setResultText(string);
                status = "Task Execution Successful, Ready to send Result..." ; showStatus(status);
                ocrData.setType(MainActivity.SUBMIT_RESULT);
                data = (Object)ocrData;
            }

            client.sendData(data);
            status = "Successfully Send Result to Server...";
            Log.i("Inside run sort", status);
            showStatus(status);

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

