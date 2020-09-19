package com.example.clientframework.Accounts;

import androidx.appcompat.app.AppCompatActivity;

import com.example.clientframework.MainActivity;
import com.example.clientframework.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import communication.AccountData;

public class LoginActivity extends AppCompatActivity {

    private EditText email,password;
    private Button button,button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        button = (Button)findViewById(R.id.submit);
        button2 = (Button)findViewById(R.id.button8);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(email.getText().equals("") || password.getText().equals(""))
                    Toast.makeText(LoginActivity.this,"All fields Are Required",Toast.LENGTH_LONG).show();
                else{




                }
            }
        });

    }
}
