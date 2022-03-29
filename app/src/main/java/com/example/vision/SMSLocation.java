package com.example.vision;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Toast;

import java.util.ArrayList;

//TODO: Try converting this activity to a background Service so that we can fetch the location
//      and send SMS even when the app is not running on the screen.

public class SMSLocation extends AppCompatActivity {

    private static boolean fetchedLocation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_guardian);

        //Setting the page title
        getSupportActionBar().setTitle(R.string.SMSLocation);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //If location is not yet fetched
        if(!fetchedLocation) {
            checkAppPermissions();
        }
    }

    //Checks the app permissions
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
            requestPermissions(permissionsArray, 6000);
            return;
        }

        //Only if all the permissions are granted, then we check if the GPS is enabled
        checkLocationAccess();
    }

    //Checking if GPS option is enabled on the mobile
    public void checkLocationAccess(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //if Location access is not enabled then we redirect user to the location settings page
        if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) &&
                !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            new TTS().initializeTTS(getString(R.string.GPSMessage),getApplicationContext());

            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

            return;
        }

        boolean isNetworkProvider, isGPSProvider;

        isNetworkProvider = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        isGPSProvider = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(isNetworkProvider) {
            //getting location using Network is faster compared to GPS however the result may not be that accurate
            getLocationUsingProvider("network");
        }
        else if(isGPSProvider) {
            //getting location using GPS is quite time consuming even though it gives quite accurate results
            getLocationUsingProvider("gps");
        }
    }

    void getLocationUsingFusedLocationClientProvider(){



    }

    @SuppressLint("MissingPermission")
    void getLocationUsingProvider(String Provider){

        new TTS().initializeTTS(getString(R.string.locationMessage),getApplicationContext());
        ProgressDialog.show(this,"Fetching Location","Please wait...",true);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //here provider can be Network or GPS based on whichever one is available
        locationManager.requestSingleUpdate(Provider, new LocationListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onLocationChanged(@NonNull Location location) {

                // Logic to handle location
                if(location != null) {
                    fetchedLocation = true;
                    String myLocation = getString(R.string.location) + location.getLatitude() + "," + location.getLongitude();
                    Intent intentSendSMS = new Intent();
                    intentSendSMS.putExtra("locationMessage",myLocation);
                    intentSendSMS.setClass(getApplicationContext(),SendSMS.class);
                    startActivity(intentSendSMS);
                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(), "LOCATION: NULL", Toast.LENGTH_SHORT).show();
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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 6000){
            checkAppPermissions();
        }
    }
}