package com.example.vision;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import java.util.Locale;

public class TTS {

    static Context context;
    private TextToSpeech textToSpeech;
    private String message;

    TTS(Context context, String message) {

        TTS.context = context;
        this.message = message;

        textToSpeech = new TextToSpeech(
                context,
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int i) {

                        if (i == TextToSpeech.SUCCESS) {

                            textToSpeech.setLanguage(Locale.ENGLISH);

                            textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null);
                        }

                    }
                });
    }

}
