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

        new TTS().initializeTTS(getString(R.string.emergencyNumbersMessage),this);

        number1 = findViewById(R.id.number1);
        number2 = findViewById(R.id.number2);
        number3 = findViewById(R.id.number3);
    }

    public void saveNumber(View view) {

        String numberStr1 = "" + number1.getText().toString();
        String numberStr2 = "" + number2.getText().toString();
        String numberStr3 = "" + number3.getText().toString();

        //This condition checks whether at least one number is not equal to ""
        if(numberStr1.equals("") && numberStr2.equals("") && numberStr3.equals("")){
            return;
        }

        //Here on the three if statements check if the numbers are valid or not
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
        Emergency_no.this.finish();

    }

    void inValid(){
        new TTS().initializeTTS("Invalid number",this);
    }
}