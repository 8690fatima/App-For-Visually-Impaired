package com.example.vision;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.opencv.android.OpenCVLoader;
import android.widget.ImageView;
import android.speech.RecognizerIntent;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    static {
        if(OpenCVLoader.initDebug()){
            Log.d("MainActivity: ","Opencv is loaded");
        }
        else {
            Log.d("MainActivity: ","Opencv failed to load");
        }
    }

    private Button camera_button;
    private Button ocr_button;
    private ImageView mic_button;
    private boolean showBatteryLowNotification = true;

    //private Ringtone ringtone;
    private static final int REQUEST_CALL=1;


    private final BroadcastReceiver Batterynot=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //get battery level
            int level= intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);
            //check if the battery is low
            if (level <=20 && showBatteryLowNotification){
                Toast.makeText(MainActivity.this, "Battery low detected", Toast.LENGTH_SHORT).show();
                makeCall();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        new TTS().initializeTTS("It's a pleasure to meet you. Please press mic button and say search for knowing what's around you. Or say read to let me help you know the content directed by the camera", MainActivity.this);

        camera_button=findViewById(R.id.camera_button);
        camera_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,CameraActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
        ocr_button=findViewById(R.id.ocr_button);
        ocr_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,OcrActivity.class));
            }
        });
        mic_button=findViewById(R.id.mic_button);
        this.registerReceiver(this.Batterynot,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    public void getspeechinput(View view){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        if(intent.resolveActivity(getPackageManager())!= null){
            startActivityForResult(intent,10);
        }
        else{
            Toast.makeText(this,"Your device doesn't support",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case 10:
                if(resultCode == RESULT_OK && data != null){
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    call(result.get(0));
                }
                break;
        }
    }

    public void call(String result){
        if(result.equals("search") == true){
            startActivity(new Intent(MainActivity.this,CameraActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
        else if (result.equals("read") == true){
            startActivity(new Intent(MainActivity.this,OcrActivity.class));
        }
        else{
            new TTS().initializeTTS("Sorry I didn't understand. Please press mic button and say search for knowing what's around you. Or say read to let me help you know the content directed by the camera.", MainActivity.this);
        }
    }

    private void makeCall(){

        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String ENUM = sharedPreferences.getString("ENUM", "NONE");
        //if call permission is not granted;hence we give permission
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        }
        else{
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + ENUM));
            startActivity(callIntent);

            showBatteryLowNotification = false;
        }
    }

    //if call permission is already granted at the first place
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makeCall();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void PopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
        popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.changeNum) {
                    startActivity(new Intent(MainActivity.this, Emergency_no.class));
                }
                return true;
            }
        });
        popupMenu.show();
    }

}


