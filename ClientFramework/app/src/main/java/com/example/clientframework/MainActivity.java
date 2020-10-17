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

    public static final int SERVICE_REGISTRY = 1;
    public static final int OCR_TASK_REGISTRY = 2;
    public static final int SORT_TASK_REGISTRY = 3;
    public static final int OCR_OFFLOAD_TASK = 4;
    public static final int SORT_OFFLOAD_TASK = 5;
    public static final int SUBMIT_RESULT = 6;
    public static final int REGISTRATION_SUCCESS = 7;
    public static final int EXIT_FAILURE = 8;
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
                Intent intent = new Intent(MainActivity.this, ConnectionSetup.class);
                startActivity(intent);
            }
        });

        sReceiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OffloadTask.class);
                startActivity(intent);
            }
        });
    }
}


