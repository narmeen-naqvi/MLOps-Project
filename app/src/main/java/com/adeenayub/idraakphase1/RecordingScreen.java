package com.adeenayub.idraakphase1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
private Button text_cancelButton,help_cancelButton,connect;
String selectedImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording_screen);

        record = findViewById(R.id.record_button);
        camera = findViewById(R.id.camera_button);
        uploadpicture = findViewById(R.id.taken_picture);
        uploadvideo = findViewById(R.id.taken_video);
        connect = findViewById(R.id.connectServer);

        camera.setOnClickListener(v -> {
            //using this for image capture for now
            Toast.makeText(this, "Camera clicked", Toast.LENGTH_SHORT).show();
            if (isCameraPresentInPhone()) {
                Log.i("Image_capture_tag", "Camera is detected");
                getCameraPermission();
                imageCapture();
            } else {
                Log.i("Image_capture_tag", "Camera is not detected");
            }
        });

        record.setOnClickListener(v -> {
            Toast.makeText(this, "Record clicked", Toast.LENGTH_SHORT).show();
            if (isCameraPresentInPhone()) {
                Log.i("Video_record_tag", "Camera is detected");
                getCameraPermission();
                recordvideo();
            } else {
                Log.i("Video_record_tag", "Camera is not detected");
            }
        });

        connect.setOnClickListener(v -> {
            String postUrl = "http://192.168.1.6:5000/";
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            // Read BitMap by file path
            Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath, options);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            RequestBody postBodyImage = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("image", "androidFlask.jpg", RequestBody.create(MediaType.parse("image/*jpg"), byteArray))
                    .build();

            TextView responseText = findViewById(R.id.responseText);
            responseText.setText("Please wait ...");

            postRequest(postUrl, postBodyImage);

            //String postBodyText = "Hello";
            //MediaType mediaType = MediaType.parse("text/plain; charset=utf-8");
            //RequestBody postBody = RequestBody.create(mediaType, postBodyText);
            //postRequest(postUrl, postBody);
        });
    }
    private void postRequest(String postUrl, RequestBody postBody) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // Cancel the post on failure.
                call.cancel();
                Toast.makeText(RecordingScreen.this, "Failed to connect to server", Toast.LENGTH_SHORT).show();
                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView responseText = findViewById(R.id.responseText);
                        responseText.setText("Failed to Connect to Server");
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull final Response response) throws IOException {
                // In order to access the TextView inside the UI thread, the code is executed inside runOnUiThread()
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView responseText = findViewById(R.id.responseText);
                        try {
                            responseText.setText(response.body().string());
                            Toast.makeText(RecordingScreen.this, "Connected to server", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
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
/*public String predictLetter(Uri imageUri) throws IOException, JSONException {
    URL url = new URL("http://127.0.0.1:5000/");
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
}*/

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
                selectedImagePath = getPath(getApplicationContext(), imagePath);
                Toast.makeText(getApplicationContext(), selectedImagePath, Toast.LENGTH_LONG).show();

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
    // Implementation of the getPath() method and all its requirements is taken from the StackOverflow Paul Burke's answer: https://stackoverflow.com/a/20559175/5426539
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
