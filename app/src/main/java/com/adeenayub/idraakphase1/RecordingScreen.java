package com.adeenayub.idraakphase1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RecordingScreen extends AppCompatActivity {
ImageView record,camera,uploadpicture;

//video stuff
private static int CAMERA_PERMISSION_CODE = 100;
private static int VIDEO_RECORD_CODE = 101;
private static int IMAGE_CAPTURE_CODE = 102;
private Uri videoPath, imagePath;
VideoView uploadvideo;
//popup
private AlertDialog.Builder dialogBuilder;
private AlertDialog dialog;
private TextView text_popupTitle, text_popupDescription,help_popupTitle,help_popupDescription;
private Button text_cancelButton,help_cancelButton;
String predictedclass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording_screen);

        record = findViewById(R.id.record_button);
        camera = findViewById(R.id.camera_button);
        uploadpicture = findViewById(R.id.taken_picture);
        uploadvideo = findViewById(R.id.taken_video);

        camera.setOnClickListener(v -> {
            //using this for image capture for now
            Toast.makeText(this, "Camera clicked", Toast.LENGTH_SHORT).show();
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
                Intent idic = new Intent(RecordingScreen.this, DictionaryContents.class);
                startActivity(idic);
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
        text_popupTitle = textPopupView.findViewById(R.id.text_popup_title);
        text_popupDescription = textPopupView.findViewById(R.id.text_popup_description);
        text_cancelButton = textPopupView.findViewById(R.id.text_popup_button);

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
        help_popupTitle = helpPopupView.findViewById(R.id.help_popup_title);
        help_popupDescription = helpPopupView.findViewById(R.id.help_popup_description);
        help_cancelButton = helpPopupView.findViewById(R.id.help_popup_button);

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

//image flask
    /*public String predictLetter(Uri imageFile) throws IOException, JSONException {
        URL url = new URL("http://localhost:5000/predict");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "image/jpeg");

        // Set the image file as the request body
        connection.setDoOutput(true);
        OutputStream outputStream = connection.getOutputStream();
        FileInputStream inputStream = new FileInputStream(imageFile);
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        // Read the response
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder responseBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            responseBuilder.append(line);
        }

        // Parse the response as JSON
        JSONObject jsonResponse = new JSONObject(responseBuilder.toString());
        String prediction = jsonResponse.getString("prediction");

        return prediction;
    }*/
public String predictLetter(Uri imageUri) throws IOException, JSONException {
    URL url = new URL("http://127.0.0.1:5000/predict");
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("POST");
    connection.setRequestProperty("Content-Type", "image/jpeg");

    // Set the image file as the request body
    connection.setDoOutput(true);
    OutputStream outputStream = connection.getOutputStream();
    InputStream inputStream = getContentResolver().openInputStream(imageUri);
    byte[] buffer = new byte[4096];
    int bytesRead;
    while ((bytesRead = inputStream.read(buffer)) != -1) {
        outputStream.write(buffer, 0, bytesRead);
    }

    // Read the response
    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    StringBuilder responseBuilder = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) {
        responseBuilder.append(line);
    }

    // Parse the response as JSON
    JSONObject jsonResponse = new JSONObject(responseBuilder.toString());
    String prediction = jsonResponse.getString("prediction");

    return prediction;
}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VIDEO_RECORD_CODE) {
            if (resultCode == RESULT_OK) {
                //display video
                uploadvideo.setVideoURI(data.getData());
                //uploadvideo.start();
                MediaController mediaController = new MediaController(this);
                uploadvideo.setMediaController(mediaController);
                mediaController.setAnchorView(uploadvideo);
                //storage
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
            if (resultCode == RESULT_OK && data != null) {
                //upload image
                Bundle bundle= data.getExtras();
                Bitmap finalphoto=(Bitmap) bundle.get("data");
                uploadpicture.setImageBitmap(finalphoto);
                //predict image
                imagePath = data.getData();
                Log.i("Image_capture_tag","Image captured at path " + imagePath);
                Toast.makeText(this, "Image captured successfully", Toast.LENGTH_SHORT).show();
                /*try {
                    predictedclass=predictLetter(imagePath);
                    // Display the predicted class in the TextView
                    TextView textView = findViewById(R.id.text_popup_description);
                    textView.setText(predictedclass);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }*/
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