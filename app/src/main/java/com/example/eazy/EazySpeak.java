package com.example.eazy;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.widget.Toast;

import java.util.ArrayDeque;
import java.util.Queue;

public class EazySpeak implements Runnable, TextToSpeech.OnInitListener {

    private TextToSpeech tts;
    private Context context;
    private Thread thread;

    public EazySpeak(Context context)
    {
        this.context = context;
        tts = new TextToSpeech( getAppContext(), this);
        thread = null;
    }

    public void startExecutingCommands()
    {
        try
        {
            if( thread == null )
            {
                thread = new Thread( this );
                thread.start();
            }
            else if( !thread.isAlive() )
            {
                thread = null;
                thread = new Thread( this );
                thread.start();
            }
        }catch( Exception e )
        {
            Toast.makeText( getAppContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void run()
    {
        try
        {
            while( !ResponseQueue.isEmpty() )
            {
                speak( ResponseQueue.dequeue() );
            }
        }catch ( Exception e )
        {
            Toast.makeText(getAppContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onInit(int status) {
        switch( status )
        {
            case TextToSpeech.SUCCESS:
                Toast.makeText(getAppContext(), "Successfully initialized the TTS engine!", Toast.LENGTH_SHORT).show();
               // speak("Hello World!");
               // speak("Hello Java!");
                //speak("#Eazy Response");
                //speak("#Android Development");
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
