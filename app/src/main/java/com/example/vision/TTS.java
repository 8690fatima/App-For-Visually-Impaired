package com.example.vision;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import java.util.Locale;

public class TTS {

    static Context context;
    private TextToSpeech textToSpeech;
    private String text;

    TTS(Context context, String text) {

        this.context = context;
        this.text = text;

        textToSpeech = new TextToSpeech(
                this.context,
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int i) {

                        if (i == TextToSpeech.SUCCESS) {

                            textToSpeech.setLanguage(Locale.ENGLISH);

                            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                        }

                    }
                });
    }

}
