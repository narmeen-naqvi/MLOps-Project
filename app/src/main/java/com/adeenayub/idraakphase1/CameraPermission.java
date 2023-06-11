package com.adeenayub.idraakphase1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class CameraPermission extends AppCompatActivity {
TextView cdenytext,callowtext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_permission);
        getSupportActionBar().hide();
        cdenytext = findViewById(R.id.c_deny);
        callowtext = findViewById(R.id.c_allow);

        cdenytext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(CameraPermission.this, "Camera permission denied. Select allow to continue", Toast.LENGTH_SHORT).show();
            }
        });

        callowtext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent icallow = new Intent(CameraPermission.this,MicPermission.class);
                Toast.makeText(CameraPermission.this, "Camera permission allowed", Toast.LENGTH_SHORT).show();
                startActivity(icallow);
            }
        });
    }
}