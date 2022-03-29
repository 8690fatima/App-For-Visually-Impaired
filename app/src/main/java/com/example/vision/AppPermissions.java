package com.example.vision;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;

public class AppPermissions extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_permissions);
        getSupportActionBar().setTitle(R.string.AppPermissions);

        checkAppPermissions();
    }

    void checkAppPermissions(){

        ArrayList<String> permissionsList = new ArrayList<>();
        String[] permissionsArray;

        //Camera permission
        if(ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            permissionsList.add(Manifest.permission.CAMERA);
        }

        //Call permission
        if(ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            permissionsList.add(Manifest.permission.CALL_PHONE);
        }

        //Location permission
        if((ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                && (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)){

            permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            permissionsList.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        }

        //SMS permission
        if(ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            permissionsList.add(Manifest.permission.SEND_SMS);
        }

        //Converting the permissions list to array
        permissionsArray = new String[permissionsList.size()];
        for(int i=0; i<permissionsList.size(); i++){
            permissionsArray[i] = permissionsList.get(i);
        }

        //Requesting app permissions that are not yet granted
        if(permissionsArray.length > 0) {

            requestPermissions(permissionsArray, 1000);
            new TTS().initializeTTS(getString(R.string.appPermissionMessage),this);

        }else{
            goToHomeActivity();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1000){
            checkAppPermissions();
        }
    }

    void goToHomeActivity(){
        Intent homeActivityIntent = new Intent();
        homeActivityIntent.setClass(this, HomeActivity.class);
        startActivity(homeActivityIntent);
        finish();
    }

}