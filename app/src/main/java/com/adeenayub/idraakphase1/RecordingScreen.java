package com.adeenayub.idraakphase1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import kotlin.jvm.internal.markers.KMutableList;

public class RecordingScreen extends AppCompatActivity {
ImageView pause,record,stop;
//video stuff
private static int CAMERA_PERMISSION_CODE = 100;
private static int VIDEO_RECORD_CODE = 101;
private static int IMAGE_CAPTURE_CODE = 102;
private Uri videoPath, imagePath;
//popup
private AlertDialog.Builder dialogBuilder;
private AlertDialog dialog;
private TextView text_popupTitle, text_popupDescription,help_popupTitle,help_popupDescription;
private Button text_cancelButton,help_cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording_screen);

        pause = findViewById(R.id.pause_button);
        record = findViewById(R.id.record_button);
        stop = findViewById(R.id.stop_button);

        pause.setOnClickListener(v -> {
            Toast.makeText(this, "Pause clicked", Toast.LENGTH_SHORT).show();
        });

        stop.setOnClickListener(v -> {
            //using this for image capture for now
            Toast.makeText(this, "Stop clicked", Toast.LENGTH_SHORT).show();
            if (isCameraPresentInPhone()){
                Log.i("Image_capture_tag","Camera is detected");
                getCameraPermission();
                imageCapture();
            }
            else{
                Log.i("Image_capture_tag","Camera is not detected");
            }
        });

        record.setOnClickListener(v -> {
            Toast.makeText(this, "Record clicked", Toast.LENGTH_SHORT).show();
            if (isCameraPresentInPhone()){
                Log.i("Video_record_tag","Camera is detected");
                getCameraPermission();
                recordvideo();
            }
            else{
                Log.i("Video_record_tag","Camera is not detected");
            }
        });
    }
//menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.text_option:
                Toast.makeText(this, "Text option selected", Toast.LENGTH_SHORT).show();
                createTextPopup();
                return true;
            case R.id.audio_option:
                Toast.makeText(this, "Audio option selected", Toast.LENGTH_SHORT).show();
                Intent iaudio = new Intent(RecordingScreen.this, audio_player.class);
                startActivity(iaudio);
                return true;
            case R.id.dictionary_option:
                Toast.makeText(this, "Dictionary option selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.help_option:
                Toast.makeText(this, "Help option selected", Toast.LENGTH_SHORT).show();
                createHelpPopup();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void createTextPopup(){
        dialogBuilder = new AlertDialog.Builder(this);
        final View textPopupView = getLayoutInflater().inflate(R.layout.text_popup, null);
        text_popupTitle = (TextView) textPopupView.findViewById(R.id.text_popup_title);
        text_popupDescription = (TextView) textPopupView.findViewById(R.id.text_popup_description);
        text_cancelButton = (Button) textPopupView.findViewById(R.id.text_popup_button);

        dialogBuilder.setView(textPopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        text_cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    public void createHelpPopup(){
        dialogBuilder = new AlertDialog.Builder(this);
        final View helpPopupView = getLayoutInflater().inflate(R.layout.help_popup, null);
        help_popupTitle = (TextView) helpPopupView.findViewById(R.id.help_popup_title);
        help_popupDescription = (TextView) helpPopupView.findViewById(R.id.help_popup_description);
        help_cancelButton = (Button) helpPopupView.findViewById(R.id.help_popup_button);

        dialogBuilder.setView(helpPopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        help_cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


//video stuff
    private boolean isCameraPresentInPhone() {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            return true;
        } else {
            return false;
        }
    }

    private void getCameraPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    private void recordvideo(){
        Intent irecord=new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(irecord,VIDEO_RECORD_CODE);
    }
    private void imageCapture(){
        Intent iimgcap=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(iimgcap,IMAGE_CAPTURE_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VIDEO_RECORD_CODE) {
            if (resultCode == RESULT_OK) {
                videoPath = data.getData();
                Log.i("Video_record_tag","Video recorded at path " + videoPath);
                Toast.makeText(this, "Video recorded successfully", Toast.LENGTH_SHORT).show();
            }
            else if (resultCode == RESULT_CANCELED) {
                Log.i("Video_record_tag","Video recording cancelled");
                Toast.makeText(this, "Video recording cancelled", Toast.LENGTH_SHORT).show();
            }
            else {
                Log.i("Video_record_tag","Video recording failed");
                Toast.makeText(this, "Video recording failed", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == IMAGE_CAPTURE_CODE) {
            if (resultCode == RESULT_OK) {
                imagePath = data.getData();
                Log.i("Image_capture_tag","Image captured at path " + imagePath);
                Toast.makeText(this, "Image captured successfully", Toast.LENGTH_SHORT).show();
            }
            else if (resultCode == RESULT_CANCELED) {
                Log.i("Image_capture_tag","Image capture cancelled");
                Toast.makeText(this, "Image captured cancelled", Toast.LENGTH_SHORT).show();
            }
            else {
                Log.i("Image_capture_tag","Image captured failed");
                Toast.makeText(this, "Image captured failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}