package com.example.vision;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Main Activity");
    }

    void setEmergencyNumbers() {

        //Checking if emergency numbers are set before
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);

        boolean emergencyNumbersIsSet = sharedPreferences.getBoolean("emergencyNumbersNotNull", false);

        if(!emergencyNumbersIsSet){

            //Redirecting to Emergency No Activity
            Intent emergencyNoIntent = new Intent();
            emergencyNoIntent.setClass(this, Emergency_no.class);
            startActivity(emergencyNoIntent);
            return;
        }
        else{

            //Redirecting to App Permissions Activity
            Intent appPermissionsIntent = new Intent();
            appPermissionsIntent.setClass(this, AppPermissions.class);
            startActivity(appPermissionsIntent);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        setEmergencyNumbers();

    }
}


