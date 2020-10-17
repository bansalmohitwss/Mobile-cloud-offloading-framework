package com.example.clientframework;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.clientframework.Accounts.LoginActivity;
import com.example.clientframework.OffloadingHandler.OffloadTask;


public class MainActivity extends AppCompatActivity {

    private Button sProvider;
    private Button sReceiver;
    static public int Id;

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

        sProvider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Id = SERVICE_REGISTRY;
                Intent intent = new Intent(MainActivity.this, ConnectionSetup.class);
                startActivity(intent);
                finish();
            }
        });

        sReceiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Id = TASK_REGISTRY;
                Intent intent = new Intent(MainActivity.this, OffloadTask.class);
                startActivity(intent);
            }
        });
    }
}


