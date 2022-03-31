package com.example.vision;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;

import java.util.ArrayList;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private ImageView mic_button;

    public static int speechInputCode = 10;
    //Code 10(default): For user to say search or read
    //Code 20: For user to say Yes or No for functioning of battery low feature

    public static int speechOutputCode = R.string.intro;
    //Speech output code is the string ID from string.xml and by default is introduction

    private static final int batteryLowLimit = 20;
    //The battery limit for implementing battery low feature

    private static boolean batteryLowNotificationDismissed = false;
    //Set to true after user says yes or no for implementing battery low feature

    static {
        if (OpenCVLoader.initDebug()) {
            Log.d("MainActivity: ", "Opencv is loaded");
        } else {
            Log.d("MainActivity: ", "Opencv failed to load");
        }
    }

    //Battery level broadcast. Checking for low battery
    private static BroadcastReceiver Batterynot = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            //get battery level
            int level= intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);

            //check if the battery is low
            if (level <= batteryLowLimit && !batteryLowNotificationDismissed){
                speechInputCode = 20;
                speechOutputCode = R.string.batteryLowMessage;
            }
        }
    };

    //Check if battery is low everytime user restarts the app
    public int batteryLevel(Context context)
    {
        Intent intent  = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int    level   = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        int    scale   = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
        return (level * 100) / scale;
    }

    //Function to implement battery low feature
    public void batteryLow()
    {
        batteryLowNotificationDismissed = true;
        //Go to SMSLocation Activity for fetching users current location
        startActivity(new Intent().setClass(getApplicationContext(), SMSLocation.class));

        TTS.stop();
        //End the HomeActivity
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Set the page title
        getSupportActionBar().setTitle(getString(R.string.home));

        mic_button=findViewById(R.id.mic_button);

        speechInputCode = 10;
        speechOutputCode = R.string.intro;
        batteryLowNotificationDismissed = false;

        TTS.speakText(getString(R.string.welcomeMessage),getApplicationContext());

        if(!batteryLowNotificationDismissed && speechInputCode!=20){

            //Checking if battery level is low when the user starts the application
            if(batteryLevel(getApplicationContext()) <= batteryLowLimit)
            {
                Toast.makeText(HomeActivity.this, "Battery low detected", Toast.LENGTH_SHORT).show();
                speechInputCode = 20;
                speechOutputCode = R.string.batteryLowMessage;
            }
            else
            {
                //We create a battery level broadcast receiver only if
                //the battery was not low at the start of application
                closeBatteryLevelReceiver(); //Close the previous registered receiver
                registerReceiver(Batterynot, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onResume() {
        super.onResume();

        //Every time the activity resumes speech output is given
        TTS.speakText(getString(speechOutputCode), getApplicationContext());

        //If battery is low and not yet dismissed then dont go further from here and return
        if(!batteryLowNotificationDismissed && speechInputCode==20){
            return;
        }

        //Set the input and output speech code to default
        speechInputCode = 10;
        speechOutputCode = R.string.intro;
    }

    @Override
    protected void onPause() {
        //We stop TTS everytime the activity is paused
        //i.e. when the Speech recognition engine is running
        //i.e when google assistant is running
        //etc...
        TTS.stop();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //Unregistering the battery receiver
        closeBatteryLevelReceiver();
    }

    @SuppressWarnings("deprecation")
    public void getspeechinput(View view){

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if(intent.resolveActivity(getPackageManager())!= null){
            startActivityForResult(intent,speechInputCode);
        }
        else{
            Toast.makeText(this,"Your device doesn't support",Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){

        ArrayList<String> result;

        if (resultCode == RESULT_OK && data != null)
        {
            result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
        }else
        {
            //Removed the tts("sorry, invalid, try again") because it was getting too complicated
            return;
        }

        switch (requestCode) {
            case 10:
                call(result.get(0));
                break;
            case 20:
                call(getString(R.string.battery)+" "+result.get(0));
        }
    }

    public void call(String result){
        if(result.equalsIgnoreCase(getString(R.string.inputCommandSearch))){
//            speechOutputCode = R.string.cameraSearch;
            startActivity(new Intent(HomeActivity.this,CameraActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
        else if (result.equalsIgnoreCase(getString(R.string.inputCommandRead))){
//            speechOutputCode = R.string.cameraRead;
            startActivity(new Intent(HomeActivity.this,OcrActivity.class));
        }
        else if(result.equalsIgnoreCase(getString(R.string.inputCommandHelp))){
            TTS.speakText(getString(R.string.help),getApplicationContext());
        }
        else if(result.equalsIgnoreCase(getString(R.string.inputCommandAssistant))){
            speechOutputCode = R.string.empty;
            startActivity(new Intent(Intent.ACTION_VOICE_COMMAND).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
        else if(result.equals(getString(R.string.inputCommandBatteryYes))){
            closeBatteryLevelReceiver();
            batteryLow();
        }else if(result.equals(getString(R.string.inputCommandBatteryNo))){
            batteryLowNotificationDismissed = true;
            TTS.speakText(getString(R.string.batteryDismiss),getApplicationContext());
            speechInputCode = 10;
            speechOutputCode = R.string.intro;
            closeBatteryLevelReceiver();
        }
    }

    //It is the three dot view on the home page for changing emergency numbers
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

    //Unregistering the battery broadcast receiver is very important
    //Because if we keep registering then multiple registers will work together in parallel
    void closeBatteryLevelReceiver(){

        //try-catch is used for unregistering just to make sure we are not getting an exception for
        //unregistering a null receiver.
        try{
            unregisterReceiver(Batterynot);
        }catch(Exception e){
            //Do nothing
        }
    }

}