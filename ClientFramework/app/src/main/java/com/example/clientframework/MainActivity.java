package com.example.clientframework;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.clientframework.ResourceSharing.*;
import communication.*;


public class MainActivity extends AppCompatActivity {

    private Button sProvider;
    private Button sReceiver;
    public static Client client;

    public static final int SERVICE_REGISTRY = 1;
    public static final int TASK_REGISTRY = 2;
    public static final int OFFLOAD_TASK = 3;
    public static final int SUBMIT_RESULT = 4;
    public static final int REGISTRATION_SUCCESS = 5;
    public static final int EXIT_FAILURE = 6;
    public static final String SERVER_IP = "192.168.43.142";
    public static final int PORT_NO = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sProvider = (Button)findViewById(R.id.button);
        sReceiver = (Button)findViewById(R.id.button2);

        client = new Client();
        ClientThread clientThread = new ClientThread(client,null,1);
        clientThread.start();

        sProvider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i("MainActivity.java ","You just Pressed Provider button");
                if(client.isConnected() == false){
                    Toast.makeText(MainActivity.this,"Can't connect to the Server",Toast.LENGTH_LONG).show();
                }
                else
                {
                    Intent intent = new Intent(MainActivity.this,ResourceHandler.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        sReceiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(client.isConnected() == false){
                    Toast.makeText(MainActivity.this,"Can't connect to the Server",Toast.LENGTH_LONG).show();
                }
                else
                {

                }
            }
        });
    }
}
