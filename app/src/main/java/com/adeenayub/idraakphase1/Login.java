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

public class Login extends AppCompatActivity {
    TextView loginbutton;
    EditText lusername,lpassword;
    CheckBox lshowPassword;
    DBHelper DB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginbutton = findViewById(R.id.login_loginButton);
        lusername = findViewById(R.id.login_username);
        lpassword = findViewById(R.id.login_password);
        lshowPassword = findViewById(R.id.login_checkbox);
        DB = new DBHelper(this);

        lshowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    lpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else{
                    lpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = lusername.getText().toString();
                String pass = lpassword.getText().toString();

                if(user.equals("") || pass.equals("")){
                    Toast.makeText(Login.this, "Username and password are required", Toast.LENGTH_SHORT).show();
                }
                else{
                    Boolean checkuserpass = DB.checkusernamepassword(user,pass);
                    if(checkuserpass == true){
                        Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();
                        Intent logintent = new Intent(getApplicationContext(),RecordingScreen.class);
                        startActivity(logintent);
                    }
                    else{
                        Toast.makeText(Login.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}