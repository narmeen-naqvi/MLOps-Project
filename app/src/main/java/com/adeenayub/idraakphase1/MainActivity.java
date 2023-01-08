package com.adeenayub.idraakphase1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
TextView loginText,signupText;
EditText username,password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        loginText = findViewById(R.id.login);
        signupText = findViewById(R.id.signup);
        username = findViewById(R.id.main_username);
        password = findViewById(R.id.main_password);

        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(username.getText().toString().equals("") || password.getText().toString().equals("")){
                    Toast.makeText(MainActivity.this, "Username and password are required", Toast.LENGTH_SHORT).show();
                }
                else{
                Intent ilogin = new Intent(MainActivity.this,CameraPermission.class);
                Toast.makeText(MainActivity.this, "Login completed", Toast.LENGTH_SHORT).show();
                startActivity(ilogin);}
            }
        });

        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(username.getText().toString().equals("") || password.getText().toString().equals("")){
                    Toast.makeText(MainActivity.this, "Username and password are required", Toast.LENGTH_SHORT).show();
                }
                else{
                Intent isignup = new Intent(MainActivity.this,CameraPermission.class);
                Toast.makeText(MainActivity.this, "Signup completed", Toast.LENGTH_SHORT).show();
                startActivity(isignup);}
            }
        });
    }
}