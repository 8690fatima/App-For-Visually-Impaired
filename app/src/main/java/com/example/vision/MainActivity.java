package com.example.vision;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        TTS.speakText(getString(R.string.welcomeMessage), getApplicationContext());
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Keep asking the user to set emergency numbers if not yet set; this step cannot be skipped
        setEmergencyNumbers();

    }

    void setEmergencyNumbers() {

        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);

        boolean emergencyNumbersIsSet = sharedPreferences.getBoolean("emergencyNumbersNotNull", false);

        //Checking if emergency numbers were set before
        if(!emergencyNumbersIsSet){

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            //Redirecting to Emergency_No Activity
            Intent emergencyNoIntent = new Intent();
            emergencyNoIntent.setClass(this, Emergency_no.class);
            startActivity(emergencyNoIntent);
        }
        else{
            //Redirecting to App Permissions Activity after emergency numbers
            Intent appPermissionsIntent = new Intent();
            appPermissionsIntent.setClass(this, AppPermissions.class);
            startActivity(appPermissionsIntent);
            finish();
        }
    }
}


