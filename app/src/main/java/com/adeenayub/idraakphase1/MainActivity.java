package com.adeenayub.idraakphase1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
TextView loginText,signupText;
EditText username,password;
CheckBox showPassword;
DBHelper DB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        loginText = findViewById(R.id.login);
        signupText = findViewById(R.id.signup);
        username = findViewById(R.id.main_username);
        password = findViewById(R.id.main_password);
        showPassword = findViewById(R.id.checkbox);
        DB = new DBHelper(this);

        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(username.getText().toString().equals("") || password.getText().toString().equals("")){
                    Toast.makeText(MainActivity.this, "Username and password are required", Toast.LENGTH_SHORT).show();
                }
                else{
                Intent ilogin = new Intent(MainActivity.this,Login.class);
                Toast.makeText(MainActivity.this, "Login completed", Toast.LENGTH_SHORT).show();
                startActivity(ilogin);}
            }
        });

        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = username.getText().toString();
                String pass = password.getText().toString();

                if(username.getText().toString().equals("") || password.getText().toString().equals("")){
                    Toast.makeText(MainActivity.this, "Username and password are required", Toast.LENGTH_SHORT).show();
                }
                else{
                    boolean checkuser = DB.checkusername(user);
                    if(checkuser == false){
                        Boolean insert = DB.insertData(user,pass);
                        if(insert == true){
                            Toast.makeText(MainActivity.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                            Intent isignup = new Intent(MainActivity.this,RecordingScreen.class);
                            startActivity(isignup);
                        }
                        else{ Toast.makeText(MainActivity.this, "Registration failed", Toast.LENGTH_SHORT).show(); }
                    }
                    else {Toast.makeText(MainActivity.this, "User already exists! Please sign in", Toast.LENGTH_SHORT).show();}
                    Toast.makeText(MainActivity.this, "Signup completed", Toast.LENGTH_SHORT).show();
               }}
        });

        showPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else{
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

    }
}