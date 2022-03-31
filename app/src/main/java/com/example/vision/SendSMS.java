package com.example.vision;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

//TODO: Try sending all three SMS using a Service

public class SendSMS extends AppCompatActivity {

    PendingIntent sentPI;
    TextView smsLog; //Displays the execution details while sending SMS
    private final List<String> numberList = new ArrayList<>(); //list of emergency contact numbers
    private static String message; // User's current location

    //This broadcast receiver listens to the SMS being sent and is executed when the
    BroadcastReceiver SMSSentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if(intent.getAction().equals("SMS SENT")) {

                if (getResultCode() == Activity.RESULT_OK) { //SMS was sent successfully
                    smsLog.append("\nSMS SENT\n");
                    Toast.makeText(getBaseContext(),
                            "SMS sent successfully", Toast.LENGTH_SHORT).show();
                }
                else { //all other codes are error
                    smsLog.append("\nERROR\n");
                    Toast.makeText(getBaseContext(),
                            "Error: SMS was not sent", Toast.LENGTH_SHORT).show();
                }

                //This statement is very important. We need to unregister each receiver after sending
                //the SMS because it can act leaky and the user may keep sending SMS to the
                //emergency numbers non-stop!!!.
                unregisterReceiver(SMSSentReceiver);

                sendSMS();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sms);

        smsLog = findViewById(R.id.log);

        message = getIntent().getStringExtra("locationMessage");

        TTS.speakText(getString(R.string.SMSMessage),getApplicationContext());

        getEmergencyNumbers();
    }

    //Fetching the emergency numbers
    void getEmergencyNumbers(){
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);

        String[] numbers = sharedPreferences.getString("emergencyNumbers","NONE").split(";");

        for(String number: numbers){ // num1;; OR ;num2; OR ;;num3

            if(!number.equals("")){
                smsLog.append("\nSending SMS to "+number+"...");
                numberList.add(number);
            }
        }

        //Sending SMS
        sendSMS();
    }

    void sendSMS(){

        //Checking if any more emergency contact numbers are left to send an SMS
        if (numberList.size() == 0) {

            //starting the call Activity
            startActivity(new Intent(getApplicationContext(),CallGuardian.class));

            //Closing SMS Activity
            finish();
            return;
        }

        //Registering receiver to check if the SMS is actually sent or not
        registerReceiver(SMSSentReceiver,new IntentFilter("SMS SENT"));

        sentPI = PendingIntent.getBroadcast(getApplicationContext(), numberList.size(), new Intent("SMS SENT"), 0);

        //Sending an SMS
        SmsManager.getDefault().sendTextMessage(numberList.get(0), null, message, sentPI, null);

        //Removing the number from the number list after sending SMS
        numberList.remove(0);
    }

}