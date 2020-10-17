package com.example.clientframework.OffloadingHandler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

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


public class OffloadTask extends AppCompatActivity {

    private Button button;
    private CardView sortView, ocrView;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offload_task);

        button = (Button)findViewById(R.id.backBtn);
        sortView = (CardView)findViewById(R.id.sorting);
        ocrView = (CardView)findViewById(R.id.ocr);

        sortView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OffloadTask.this, SortActivity.class);
                startActivity(intent);
            }
        });

        ocrView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OffloadTask.this, OcrActivity.class);
                startActivity(intent);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OffloadTask.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}



