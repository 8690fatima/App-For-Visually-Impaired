package com.example.vision;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

public class CallGuardian extends AppCompatActivity {

    private final int REQUEST_CALL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_guardian);
        getSupportActionBar().setTitle(R.string.Call);

        phoneCall();
    }

    void phoneCall(){

        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String num1 = (sharedPreferences.getString("emergencyNumbers","NONE").split(";"))[0];

        //if call permission is not granted
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            //Requesting call permission
            ActivityCompat.requestPermissions(CallGuardian.this,
                    new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);

            return;
        }

        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + num1));
        startActivity(callIntent);
        TTS.shutdown();
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CALL) {
            //if call permission is already granted at the first place
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Call Permission Granted", Toast.LENGTH_SHORT).show();
                phoneCall();
            }
        }
    }
}