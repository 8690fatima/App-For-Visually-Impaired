package com.example.vision;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;



public class Emergency_no extends AppCompatActivity {

    EditText number1,number2,number3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_no);
        getSupportActionBar().setTitle("Register Number");

        number1 = findViewById(R.id.number1);
        number2 = findViewById(R.id.number2);
        number3 = findViewById(R.id.number3);
    }

    public void saveNumber(View view) {
        String numberStr1 = number1.getText().toString();
        String numberStr2 = number2.getText().toString();
        String numberStr3 = number3.getText().toString();

        if(numberStr1.length()==10 && numberStr2.length()==10 && numberStr3.length()==10){
            SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putString("num1", numberStr1);
            myEdit.putString("num2", numberStr2);
            myEdit.putString("num3", numberStr3);
            myEdit.apply();
            Emergency_no.this.finish();
        }else {
            Toast.makeText(this, "Enter Valid Number!", Toast.LENGTH_SHORT).show();
        }

    }
}