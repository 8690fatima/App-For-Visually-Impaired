package com.example.vision;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;

import java.util.ArrayList;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    static {
        if (OpenCVLoader.initDebug()) {
            Log.d("MainActivity: ", "Opencv is loaded");
        } else {
            Log.d("MainActivity: ", "Opencv failed to load");
        }
    }

    //Checking for low battery
    private final BroadcastReceiver Batterynot = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            //get battery level
            int level= intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);

            //check if the battery is low
            if (level <= 90){
                Toast.makeText(HomeActivity.this, "Battery low detected", Toast.LENGTH_SHORT).show();
                startActivity(new Intent().setClass(getApplicationContext(), SMSLocation.class));
                unregisterReceiver(Batterynot);
                finish();
            }
        }
    };

    private Button camera_button;
    private Button ocr_button;
    private ImageView mic_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        new TTS(this, "It's a pleasure to meet you. " +
                "Please press mic button and say search for knowing what's around you. " +
                "Or say read to let me help you know the content directed by the camera");

        camera_button=findViewById(R.id.camera_button);
        camera_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,
                        CameraActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        ocr_button=findViewById(R.id.ocr_button);
        ocr_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,OcrActivity.class));
            }
        });

        mic_button=findViewById(R.id.mic_button);

        this.registerReceiver(this.Batterynot, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    public void getspeechinput(View view){

        if(view.equals(mic_button)){

        }

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

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    call(result.get(0));
                }
                break;
            case 500:
                if(resultCode == RESULT_OK){
                    Toast.makeText(getApplicationContext(), "Result OK, did you enable GPS?", Toast.LENGTH_SHORT).show();
                }
        }
    }

    public void call(String result){
        if(result.equals("search") == true){
            startActivity(new Intent(HomeActivity.this,CameraActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
        else if (result.equals("read") == true){
            startActivity(new Intent(HomeActivity.this,OcrActivity.class));
        }
        else{
            new TTS(HomeActivity.this, "Sorry I didn't understand. Please press mic button and say search for knowing what's around you. Or say read to let me help you know the content directed by the camera.");
        }
    }

    public void PopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(HomeActivity.this, view);
        popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.changeNum) {
                    startActivity(new Intent(HomeActivity.this, Emergency_no.class));
                }
                return true;
            }
        });
        popupMenu.show();
    }

}