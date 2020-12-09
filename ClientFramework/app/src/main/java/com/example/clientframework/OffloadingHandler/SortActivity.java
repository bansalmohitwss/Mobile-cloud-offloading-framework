package com.example.clientframework.OffloadingHandler;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.clientframework.MainActivity;
import com.example.clientframework.R;
import com.example.clientframework.Tasks.SortTask;

import java.sql.Time;
import java.util.Vector;

import communication.SocketData;
import communication.SortData;

public class SortActivity extends AppCompatActivity {

    private Button offloadBtn;
    private Button localBtn;
    private Button close;
    private EditText arraySize;
    private boolean isRunning;
    private int finalHour, finalMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort);

        offloadBtn = (Button)findViewById(R.id.button4);
        localBtn = (Button)findViewById(R.id.button5);
        close = (Button)findViewById(R.id.close2);
        arraySize = (EditText)findViewById(R.id.sizeText);
        isRunning = false;

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isRunning){
                    Toast.makeText(SortActivity.this,"Can't exist. Task is running",Toast.LENGTH_LONG).show();
                }else{
                    Intent intent = new Intent(SortActivity.this, OffloadTask.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        offloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isRunning) {
                    Toast.makeText(SortActivity.this, "Another task is in Progress.Wait till it finishes", Toast.LENGTH_LONG).show();
                    return;
                }
                Vector<Integer> vector = new Vector<Integer>();
                int size=0;
                if(arraySize.getText().toString().equals("") || (size = Integer.parseInt(arraySize.getText().toString()))==0){
                    Toast.makeText(SortActivity.this,"Size of Array should be greater than 0",Toast.LENGTH_LONG).show();
                    return;
                }

                isRunning = true;

                for(int i=size;i>=1;i--)
                    vector.add(i);

                TimePickerDialog timePickerDialog = new TimePickerDialog(SortActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        finalHour = i;
                        finalMinute = i1;
                    }
                }, 12, 0, true);
                timePickerDialog.updateTime(new Time(System.currentTimeMillis()).getHours(),
                        new Time(System.currentTimeMillis()).getMinutes());
                timePickerDialog.show();

                double startTime = System.nanoTime();
                SortData sortData = new SortData(MainActivity.SORT_TASK_REGISTRY,finalHour, finalMinute, vector);
                OffloadingThread offloadingThread = new OffloadingThread((Object)sortData);
                offloadingThread.start();

                synchronized (offloadingThread)
                {
                    try {
                        offloadingThread.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                sortData = (SortData)offloadingThread.getReceiveData();
                if(sortData==null || sortData.getType() != MainActivity.SUBMIT_RESULT){
                    Toast.makeText(SortActivity.this,"Some Error Occurred",Toast.LENGTH_LONG).show();
                }
                else{
                    double duration = (System.nanoTime() - startTime)/1000000000;
                    Toast.makeText(SortActivity.this,"Successfully Received the Result \n"+"Total Time : "+duration+"sec.",Toast.LENGTH_LONG).show();
                }

                isRunning = false;
            }
        });

        localBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isRunning){
                    Toast.makeText(SortActivity.this,"Another task is in Progress.Wait till it finishes",Toast.LENGTH_LONG).show();
                    return;
                }

                Vector<Integer> vector = new Vector<Integer>();
                int size=0;
                if(arraySize.getText().toString().equals("") || (size = Integer.parseInt(arraySize.getText().toString()))==0){
                    Toast.makeText(SortActivity.this,"Size of Array should be greater than 0",Toast.LENGTH_LONG).show();
                    return;
                }

                isRunning = true;

                for(int i=size;i>=1;i--)
                    vector.add(i);

                double startTime = System.nanoTime();
                vector = SortTask.performTask(vector, size);
                double duration = (System.nanoTime() - startTime)/1000000000;
                Toast.makeText(SortActivity.this,"Successfully Received the Result \n"+"Total Time : "+duration+"sec.",Toast.LENGTH_LONG).show();
                isRunning = false;
            }
        });
    }
}