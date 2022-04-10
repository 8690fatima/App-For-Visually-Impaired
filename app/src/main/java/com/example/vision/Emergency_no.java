package com.example.vision;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Emergency_no extends AppCompatActivity {

    EditText number1,number2,number3;
    String emergencyNumbers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register_no);
        getSupportActionBar().setTitle(R.string.RegisterNumber);

        number1 = findViewById(R.id.number1);
        number2 = findViewById(R.id.number2);
        number3 = findViewById(R.id.number3);
    }

    @Override
    protected void onResume() {

        TTS.speakText(getString(R.string.emergencyNumbersMessage), getApplicationContext());

        super.onResume();
    }

    public void saveNumber(View view) {

        String numberStr1 = "" + number1.getText().toString();
        String numberStr2 = "" + number2.getText().toString();
        String numberStr3 = "" + number3.getText().toString();

        //This condition checks whether at least one number is not equal to ""
        if(numberStr1.equals("") && numberStr2.equals("") && numberStr3.equals("")){
            return;
        }

        //Below the three if statements check if the mobile numbers are valid or not
        if(!numberStr1.equals("") && !numberStr1.matches("^\\d{10}$")){
            inValid();
            return;
        }

        if(!numberStr2.equals("") && !numberStr2.matches("^\\d{10}$")){
            inValid();
            return;
        }

        if(!numberStr3.equals("") && !numberStr3.matches("^\\d{10}$")){
            inValid();
            return;
        }

        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();

        emergencyNumbers = numberStr1 + ";" + numberStr2 + ";" + numberStr3;

        myEdit.putString("emergencyNumbers", emergencyNumbers);
        myEdit.putBoolean("emergencyNumbersNotNull", true);

        myEdit.apply();
        TTS.stop();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        TTS.stop();
    }

    @Override
    public void onBackPressed() {
        TTS.stop();
        super.onBackPressed();
    }

    void inValid(){
        TTS.speakText(getString(R.string.invalid_number), Emergency_no.this);
    }
}