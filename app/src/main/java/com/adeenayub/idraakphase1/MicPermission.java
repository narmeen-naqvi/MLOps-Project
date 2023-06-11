package com.adeenayub.idraakphase1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MicPermission extends AppCompatActivity {
TextView mdenytext,mallowtext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mic_permission);
        getSupportActionBar().hide();
        mdenytext = findViewById(R.id.m_deny);
        mallowtext = findViewById(R.id.m_allow);

        mdenytext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MicPermission.this, "Mic permission denied. Select allow to continue", Toast.LENGTH_SHORT).show();
            }
        });

        mallowtext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent imallow = new Intent(MicPermission.this,RecordingScreen.class);
                Toast.makeText(MicPermission.this, "Mic permission allowed", Toast.LENGTH_SHORT).show();
                startActivity(imallow);
            }
        });
    }
}