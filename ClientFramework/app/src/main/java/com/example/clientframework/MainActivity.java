package com.example.clientframework;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.clientframework.Accounts.LoginActivity;
import com.example.clientframework.OffloadingHandler.OffloadTask;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

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
    public static final int ACTIVE_CHECK = 9;
    public static final String SERVER_IP = "192.168.43.142";
    public static final int PORT_NO = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sProvider = (Button)findViewById(R.id.button);
        sReceiver = (Button)findViewById(R.id.button2);

        try{
            ProcessBuilder processBuilder;
            String Holder = "";
            String[] DATA = {"/system/bin/cat", "/proc/cpuinfo"};
            InputStream inputStream;
            Process process ;
            byte[] byteArry ;
            byteArry = new byte[1024];

            processBuilder = new ProcessBuilder(DATA);

            process = processBuilder.start();

            inputStream = process.getInputStream();

            while(inputStream.read(byteArry) != -1){

                Holder = Holder + new String(byteArry);
            }

            inputStream.close();

            Log.i("Value of Result", Holder);
        }catch(IOException e){
            e.printStackTrace();
        }

        ActivityManager actManager = (ActivityManager) this.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        actManager.getMemoryInfo(memInfo);
        long totalMemory = memInfo.totalMem/(1024*1024);
        Log.i("Total Memory", ""+totalMemory);

        sProvider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String  details =  "VERSION.RELEASE : "+Build.VERSION.RELEASE
                        +"\nVERSION.INCREMENTAL : "+Build.VERSION.INCREMENTAL
                        +"\nVERSION.SDK.NUMBER : "+Build.VERSION.SDK_INT
                        +"\nBOARD : "+Build.BOARD
                        +"\nBOOTLOADER : "+Build.BOOTLOADER
                        +"\nBRAND : "+Build.BRAND
                        +"\nCPU_ABI : "+Build.CPU_ABI
                        +"\nCPU_ABI2 : "+Build.CPU_ABI2
                        +"\nDISPLAY : "+Build.DISPLAY
                        +"\nFINGERPRINT : "+Build.FINGERPRINT
                        +"\nHARDWARE : "+Build.HARDWARE
                        +"\nHOST : "+Build.HOST
                        +"\nID : "+Build.ID
                        +"\nMANUFACTURER : "+Build.MANUFACTURER
                        +"\nMODEL : "+Build.MODEL
                        +"\nPRODUCT : "+Build.PRODUCT
                        +"\nSERIAL : "+Build.SERIAL
                        +"\nTAGS : "+Build.TAGS
                        +"\nTIME : "+Build.TIME
                        +"\nTYPE : "+Build.TYPE
                        +"\nUNKNOWN : "+Build.UNKNOWN
                        +"\nUSER : "+Build.USER;

                Log.d("Device Details",details);



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


