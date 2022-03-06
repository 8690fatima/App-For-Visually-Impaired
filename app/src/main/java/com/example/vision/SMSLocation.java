package com.example.vision;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.telephony.SmsManager;
import android.widget.Toast;

import java.util.ArrayList;

public class SMSLocation extends AppCompatActivity {

    private static String myLocation;
    private static ProgressDialog progressDialog;
    private static boolean isSmsSent = false;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_guardian);

        getSupportActionBar().setTitle("SMS Current Location");

        isSmsSent = false;
        myLocation = "";
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!isSmsSent) {
            checkAppPermissions();
        }
    }

    void checkAppPermissions() {

        ArrayList<String> permissionsList = new ArrayList<>();
        String[] permissionsArray;

        //Location permission
        if ((ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                && (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        //SMS permission
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.SEND_SMS);
        }

        //Converting the permissions list to array
        permissionsArray = new String[permissionsList.size()];
        for (int i = 0; i < permissionsList.size(); i++) {
            permissionsArray[i] = permissionsList.get(i);
        }

        //Requesting only those app permissions that are not yet granted
        if (permissionsArray.length > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissionsArray, 6000);
            }
            return;
        }

        checkLocationAccess();
    }

    public void checkLocationAccess(){

        //Checking if GPS option is enabled on the mobile

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //if Location access is not enabled
        if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) &&
                !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

            return;
        }

        boolean isNetworkProvider, isGPSProvider;

        isNetworkProvider = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        isGPSProvider = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(isNetworkProvider) {
            //getting location using Network is faster compared to GPS however the result may not be that accurate
            getLocation("network");
        }
        else if(isGPSProvider) {
            //getting location using GPS is quite time consuming even though it gives quite accurate results
            getLocation("gps");
        }

    }

    @SuppressLint("MissingPermission")
    void getLocation(String Provider){

        new TTS(this, "Please wait while we fetch and send your current location to your emergency contact numbers. This may take a few seconds");
        progressDialog = ProgressDialog.show(this, "Fetching location and sending SMS", "Please wait...", true);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //here provider can be Network or GPS based on whichever one is available
        locationManager.requestSingleUpdate(Provider, new LocationListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onLocationChanged(@NonNull Location location) {

                if (location != null) {

                    // Logic to handle location
                    if(location != null) {
                        myLocation = "http://maps.google.com/maps?q=loc:" + location.getLatitude() + "," + location.getLongitude();
                        sendSMS();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "LOCATION: NULL", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

        }, Looper.myLooper());
    }

    void sendSMS(){
        String scAddress = null;
        String num1, num2, num3;

        //Checking if SMS permission is not granted
        if ((ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)){

            //Requesting SMS permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    101);
            return;
        }

        //Checking if emergency numbers are set
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);

        boolean emergencyNumbersSet = sharedPreferences.getBoolean("emergencyNumbersNotNull", false);
        if(!emergencyNumbersSet){
            startActivity(new Intent().setClass(SMSLocation.this, Emergency_no.class));
            return;
        }else{
            num1 = sharedPreferences.getString("num1", "NONE");
            num2 = sharedPreferences.getString("num2", "NONE");
            num3 = sharedPreferences.getString("num3", "NONE");
        }

        Toast.makeText(getApplicationContext(), "Sending SMS", Toast.LENGTH_SHORT).show();

        //Sending SMS
        SmsManager manager = SmsManager.getDefault();
        manager.sendTextMessage(num1, scAddress, "My phone's battery is low. This is my current location :\n" + myLocation, null, null);
        manager.sendTextMessage(num2, scAddress, "My phone's battery is low. This is my current location :\n" + myLocation, null, null);
        manager.sendTextMessage(num3, scAddress, "My phone's battery is low. This is my current location :\n" + myLocation, null, null);

        progressDialog.dismiss();
        isSmsSent = true;
        startActivity(new Intent().setClass(this, CallGuardian.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 6000){
            checkAppPermissions();
        }
    }


}