package com.example.vision;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class SMSLocation extends AppCompatActivity {

    private static String myLocation;
    private static ProgressDialog progressDialog;
    private static boolean isSmsSent = false;

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

        if(!isSmsSent)
            checkAppPermissions();
    }

    void checkAppPermissions(){

        ArrayList<String> permissionsList = new ArrayList<>();
        String[] permissionsArray;

        //Location permission
        if((ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                && (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)){
            permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION);
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

        //Requesting only those app permissions that are not yet granted
        if(permissionsArray.length > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissionsArray, 6000);
            }
            return;
        }

        sendLocation();
    }

    public void sendLocation(){

        //Checking location permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //Requesting Location permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    100);
            return;
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //if Location access is not enabled
        if(!(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))){

            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            return;
        }
        else{

            new TTS(this,"Please wait while we fetch your current Location. This may take a few seconds");
            progressDialog = ProgressDialog.show(this, "Fetching Location","Please wait...",true);

            Toast.makeText(getApplicationContext(), "GPS ENABLED", Toast.LENGTH_SHORT).show();

            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {

                    progressDialog.dismiss();
                    if (location != null) {

                        // Logic to handle location
                        myLocation = "http://maps.google.com/maps?q=loc:" + location.getLatitude() + "," + location.getLongitude();
                        sendSMS();
                    } else {
                        Toast.makeText(getApplicationContext(), "Unable to find location :(", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onProviderEnabled(@NonNull String provider) { }

                @Override
                public void onProviderDisabled(@NonNull String provider) { }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) { }

            }, Looper.myLooper());
        }
    }

    void sendSMS(){
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";
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

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);

        //---when the SMS has been sent--- is working alright
        registerReceiver(new BroadcastReceiver()
        {
            public void onReceive(Context arg0, Intent arg1)
            {
                progressDialog.dismiss();
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS sent",
                                Toast.LENGTH_SHORT).show();
                        isSmsSent = true;
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
                unregisterReceiver(this);
                startActivity(new Intent().setClass(getApplicationContext(), CallGuardian.class));
                //finish();

            }
        }, new IntentFilter(SENT));

        progressDialog = ProgressDialog.show(this, "Sending SMS. ","Please wait...",true);
        new TTS(this,"Please wait while we send an SMS of your current location to your emergency contact numbers");

        Toast.makeText(getApplicationContext(), "Sending SMS", Toast.LENGTH_SHORT).show();

        //Sending SMS
        SmsManager manager = SmsManager.getDefault();
        manager.sendTextMessage(num1, scAddress, "My phone's battery is low. This is my current location :\n" + myLocation, sentPI, null);
        manager.sendTextMessage(num2, scAddress, "My phone's battery is low. This is my current location :\n" + myLocation, null, null);
        manager.sendTextMessage(num3, scAddress, "My phone's battery is low. This is my current location :\n" + myLocation, null, null);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 6000){
            checkAppPermissions();
        }

        //Location
        if (requestCode == 100 & grantResults.length > 0 && (grantResults[0] + grantResults[1]) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Location Permission Granted", Toast.LENGTH_SHORT).show();
            checkAppPermissions();
        }

        //SMS
        if (requestCode == 101 && grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){
            Toast.makeText(getApplicationContext(), "SMS Permission Granted", Toast.LENGTH_SHORT).show();
            checkAppPermissions();
        }
    }


}