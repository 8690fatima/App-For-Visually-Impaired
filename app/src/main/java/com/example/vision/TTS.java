package com.example.vision;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class TTS{

    static String systemLang = Locale.getDefault().toString();
    static final ArrayList<String> supportedLanguages = new ArrayList<>(
            Arrays.asList("mr_IN","hi_IN","en_IN","en_US","en_gb")
    );

    private TextToSpeech textToSpeech;

    public void initializeTTS(String text, Context context) {
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {

                    //if the system language is supported, adding it to your tts object
                    if(supportedLanguages.contains(systemLang)){
                        textToSpeech.setLanguage(new Locale(systemLang.split("_")[0],systemLang.split("_")[1]));
                    }else{
                        //If it's any other language other than (english, hindi and marathi)(india) then we set the default as english india
                        textToSpeech.setLanguage(new Locale("en","IN"));
                    }
                    textToSpeech.setPitch(1.0f);
                    textToSpeech.setSpeechRate(1.0f);
                    textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH,null);
                }else{
                    Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
