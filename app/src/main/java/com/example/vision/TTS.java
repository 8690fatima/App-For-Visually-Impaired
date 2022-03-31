package com.example.vision;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class TTS{

    static String systemLang = Locale.getDefault().toString();
    static final ArrayList<String> supportedLanguages = new ArrayList<>(
            Arrays.asList("mr_IN","hi_IN","en_IN","en_US","en_gb")
    );

    private static TextToSpeech textToSpeech;
    private static boolean isTTSInitialized = false;

    public static void speakText(String text, Context context) {

        if(!isTTSInitialized){
            textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int i) {
                    if (i == TextToSpeech.SUCCESS) {

                        //We set it to true so that initialization is skipped the next time and directly we do speak
                        isTTSInitialized = true;
                        Log.i("TTS","INITIALIZED");

                        //if the system language is supported
                        if(supportedLanguages.contains(systemLang)){

                            //Setting tts language as the system language
                            textToSpeech.setLanguage(new Locale(systemLang.split("_")[0],systemLang.split("_")[1]));
                        }
                        else
                        {
                            //If it's any other language other than the supported languages then we set the default as english US
                            textToSpeech.setLanguage(new Locale("en","US"));
                        }

                        textToSpeech.speak(text, TextToSpeech.QUEUE_ADD,null);
                        Log.i("TTS","SPEAKING...");
                    }
                }
            });
        }else{
            textToSpeech.speak(text, TextToSpeech.QUEUE_ADD,null);
            Log.i("TTS","SPEAKING...");
        }
    }

    static void stop(){
        if(textToSpeech.isSpeaking()) {
            textToSpeech.stop();
            Log.i("TTS", "STOPPED SPEAKING");
        }
    }

    static void shutdown(){
        if(textToSpeech != null) {
            textToSpeech.shutdown();
            textToSpeech = null;
            isTTSInitialized = false;
            Log.i("TTS", "SHUTDOWN");
        }
    }
}