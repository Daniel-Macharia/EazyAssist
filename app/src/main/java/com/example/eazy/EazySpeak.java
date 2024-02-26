package com.example.eazy;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.widget.Toast;

public class EazySpeak implements TextToSpeech.OnInitListener {

    private TextToSpeech tts;
    private Context context;

    public EazySpeak(Context context)
    {
        this.context = context;
        tts = new TextToSpeech( getAppContext(), this);
    }

    @Override
    public void onInit(int status) {
        switch( status )
        {
            case TextToSpeech.SUCCESS:
                Toast.makeText(getAppContext(), "Successfully initialized the TTS engine!", Toast.LENGTH_SHORT).show();
                speak("Hello World!");
                speak("Hello Java!");
                speak("#Eazy Response");
                speak("#Android Development");
                break;
            case TextToSpeech.ERROR:
                Toast.makeText(getAppContext(), "Failed to initialize the TTS!", Toast.LENGTH_SHORT).show();
                break;

        }
    }

    private Context getAppContext(){return this.context;}

    public void speak(String message)
    {
        try
        {
            String id = "444";
            tts.speak(message, TextToSpeech.QUEUE_ADD, null, id);

            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {
                    Toast.makeText(getAppContext(), "Started Talking.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onDone(String utteranceId) {
                    Toast.makeText(getAppContext(), "Finished Talking", Toast.LENGTH_SHORT).show();
                    quitSpeaking();
                }

                @Override
                public void onError(String utteranceId) {
                    Toast.makeText(getAppContext(), "Error while talking!", Toast.LENGTH_SHORT).show();
                    quitSpeaking();
                }
            });

        }catch( Exception e )
        {
            Toast.makeText(getAppContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
        }
    }

    public void quitSpeaking()
    {
        try
        {
            tts.shutdown();
        }catch( Exception e )
        {
            Toast.makeText(getAppContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
        }
    }
}
