package com.example.clientframework;

import androidx.appcompat.app.AppCompatActivity;
import communication.*;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.example.clientframework.ResourceHandler.*;

import java.sql.Time;
import java.util.Calendar;

public class ConnectionSetup extends AppCompatActivity {

    static public Client client;
    private Button button;
    private Button homeBtn;
    private EditText bidPrice;
    private TextView avaliableTime;
    private int avalHour=-1, avalMinute=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_setup);

        button = (Button)findViewById(R.id.button3);
        homeBtn = (Button)findViewById(R.id.home);
        bidPrice = (EditText)findViewById(R.id.bid);
        avaliableTime = (TextView)findViewById(R.id.avalTime);

        client = new Client();

        avaliableTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(ConnectionSetup.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                avalHour = i;
                                avalMinute = i1;
                            }
                        }, 12, 0, true);
                timePickerDialog.updateTime(new Time(System.currentTimeMillis()).getHours(),
                        new Time(System.currentTimeMillis()).getMinutes());
                timePickerDialog.show();
            }
        });

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

                if(avalHour == -1 || avalMinute == -1){
                    Toast.makeText(ConnectionSetup.this, "Select available time to continue", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(bidPrice.getText().toString().equals("")){
                    Toast.makeText(ConnectionSetup.this, "Enter bid price to continue", Toast.LENGTH_SHORT).show();
                    return;
                }

               initiateConnection(client);
               if (client.isConnected() == false){
                   Toast.makeText(ConnectionSetup.this,"Can't connect to Server",Toast.LENGTH_LONG).show();
               }
               else
               {
                   new ClientThread(client,new DeviceInfoData(MainActivity.SERVICE_REGISTRY,MainActivity.cpuFreq,
                           Build.BRAND+" "+Build.MODEL, avalHour, avalMinute, Double.parseDouble(bidPrice.getText().toString())),3).start();
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
                       socketData = (SocketData) clientThread.getData();
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
    Object data;
    int type;

    public ClientThread(Client client, Object data, int type)
    {
        this.client = client;
        this.data = data;
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setSocketData(Object data) {
        this.data = data;
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
            client.sendData(data);
        }else if(type==4){
            synchronized (this)
            {
                data = client.receiveData();
                this.notify();
            }
        }
    }
}
