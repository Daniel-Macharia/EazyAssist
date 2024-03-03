package com.example.eazy;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.vosk.Model;
import org.vosk.Recognizer;
import org.vosk.android.RecognitionListener;
import org.vosk.android.SpeechService;
import org.vosk.android.SpeechStreamService;
import org.vosk.android.StorageService;

import java.io.IOException;
import java.util.Scanner;

public class VoskModel implements RecognitionListener/*, Runnable */{

    static private final int STATE_START = 0;
    static private final int STATE_READY = 1;
    static private final int STATE_DONE = 2;
    static private final int STATE_FILE = 3;
    static private final int STATE_MIC = 4;

    private Model model;
    private SpeechService speechService;
    private SpeechStreamService speechStreamService;

    private boolean isReady, isListening;

    private Context context;
    //private TextView resultView;
    private Handler handler;

   // private Thread thread;

    public VoskModel( Context context, Handler handler/*, TextView resultView*/)
    {
        this.context = context;
        //this.resultView = resultView;
        //resultView = new TextView(context);
        this.handler = handler;
        isReady = false;
        isListening = false;
    }

    public void startListening()
    {
        try
        {
            if( isReady )//if model is ready
            {
                if( ! isListening )
                {
                    isListening = true;
                    recognizeMicrophone();
                }
            }
            /*if( thread != null )
            {
                thread = null;
            }

            thread = new Thread( this );
            thread.start(); */
        }catch( Exception e )
        {
            Toast.makeText(getApplicationContext(), "Error: " + e , Toast.LENGTH_SHORT).show();
        }
    }
/*
    @Override
    public void run()
    {
        try
        {
            if( !isReady )
            {
                initModel();
            }
            else //if model is ready
            {
                recognizeMicrophone();
            }

            Toast.makeText(getApplicationContext(), "Exiting run method in vosk model", Toast.LENGTH_SHORT).show();

        }catch( Exception e )
        {
            Toast.makeText(getApplicationContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
        }
    } */

    private Context getApplicationContext()
    {
        return this.context;
    }

    public void initModel() {
        StorageService.unpack(getApplicationContext(), "model-en-us", "model",
                (model) -> {
                    this.model = model;
                    /*try {
                        this.model = new Model("/assets/model_unpacked");
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
                    }*/
                    setUiState(STATE_READY);

                    isReady = true;

                    Toast.makeText(getApplicationContext(), "Unpacked Model Successfully", Toast.LENGTH_SHORT).show();
                },
                (exception) -> setErrorState("Failed to unpack the model: " + exception.getMessage()));
    }

    private Command parseToCommand( String result )
    {
        try
        {
            String text = null;

            Scanner s = new Scanner( result );
            for( int i = 0; i < 3; i++)
                s.next();

            text = s.nextLine();

            s = new Scanner(text);

            text = text.replace( text.charAt( text.length() - 1), ' ');
            text = text.replace( text.charAt( 0 ), ' ');
            text = text.replace( text.charAt( 1 ), ' ');

            String []parts = text.split(" ");

            String arg = "";

            int i = 0;
            while( i < parts.length)
            {
                arg += new String(parts[i]);
                i++;
            }


            //Toast.makeText(getApplicationContext(), "Parts[0] = " + parts[0], Toast.LENGTH_SHORT).show();

            return new Command( "", arg );
        }catch( Exception e )
        {
            Toast.makeText(getApplicationContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
        }

        return new Command( "", "");
    }

    @Override
    protected void finalize() {
        if (speechService != null) {
            speechService.stop();
            speechService.shutdown();
        }

        if (speechStreamService != null) {
            speechStreamService.stop();
        }
    }

    @Override
    public void onResult(String hypothesis) {
        //resultView.append(hypothesis + "\n");
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Enqueueing command", Toast.LENGTH_SHORT).show();
                CommandQueue.enqueue( getApplicationContext(), parseToCommand(hypothesis));
                RecognizeVoice.resultView.append(hypothesis + "\n");
            }
        });
    }

    @Override
    public void onFinalResult(String hypothesis) {
        //resultView.append(hypothesis + "\n");

        handler.post(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(getApplicationContext(), "Enqueueing command", Toast.LENGTH_SHORT).show();
                //CommandQueue.enqueue( new Command(hypothesis, "no arg :)"));
                RecognizeVoice.resultView.append(hypothesis + "\n");
            }
        });
        setUiState(STATE_DONE);
        if (speechStreamService != null) {
            speechStreamService = null;
        }
    }

    @Override
    public void onPartialResult(String hypothesis) {
        //resultView.append(hypothesis + "\n");
        /*handler.post(new Runnable() {
            @Override
            public void run() {
                RecognizeVoice.resultView.append(hypothesis + "\n");
            }
        }); */
    }

    @Override
    public void onError(Exception e) {
        setErrorState(e.getMessage());
    }

    @Override
    public void onTimeout() {
        setUiState(STATE_DONE);
    }

    private void setUiState(int state) {
        /* switch (state) {
            case STATE_START:
                resultView.setText(R.string.preparing);
                resultView.setMovementMethod(new ScrollingMovementMethod());
                //findViewById(R.id.recognize_file).setEnabled(false);
                view.findViewById(R.id.recognize_mic).setEnabled(false);
                //findViewById(R.id.pause).setEnabled((false));
                break;
            case STATE_READY:
                resultView.setText(R.string.ready);
                ((Button) view.findViewById(R.id.recognize_mic)).setText(R.string.recognize_microphone);
                //findViewById(R.id.recognize_file).setEnabled(true);
                view.findViewById(R.id.recognize_mic).setEnabled(true);
                //findViewById(R.id.pause).setEnabled((false));
                break;
            case STATE_DONE:
                //((Button) findViewById(R.id.recognize_file)).setText(R.string.recognize_file);
                ((Button) view.findViewById(R.id.recognize_mic)).setText(R.string.recognize_microphone);
                //findViewById(R.id.recognize_file).setEnabled(true);
                view.findViewById(R.id.recognize_mic).setEnabled(true);
                //findViewById(R.id.pause).setEnabled((false));
                //((ToggleButton) findViewById(R.id.pause)).setChecked(false);
                break;
            case STATE_FILE:
                //((Button) findViewById(R.id.recognize_file)).setText(R.string.stop_file);
                resultView.setText(getString(R.string.starting));
                view.findViewById(R.id.recognize_mic).setEnabled(false);
                //findViewById(R.id.recognize_file).setEnabled(true);
                //findViewById(R.id.pause).setEnabled((false));
                break;
            case STATE_MIC:
                ((Button) view.findViewById(R.id.recognize_mic)).setText(R.string.stop_microphone);
                resultView.setText(getString(R.string.say_something));
                //findViewById(R.id.recognize_file).setEnabled(false);
                view.findViewById(R.id.recognize_mic).setEnabled(true);
                //findViewById(R.id.pause).setEnabled((true));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + state);
        } */
    }

    private void setErrorState(String message) {
        //resultView.setText(message);
        handler.post(new Runnable() {
            @Override
            public void run() {
                RecognizeVoice.resultView.append(message + "\n");
            }
        });
       // ((Button) view.findViewById(R.id.recognize_mic)).setText(R.string.recognize_microphone);
        //findViewById(R.id.recognize_file).setEnabled(false);
        //view.findViewById(R.id.recognize_mic).setEnabled(false);
    }

    private void recognizeFile() {
        /*if (speechStreamService != null) {
            setUiState(STATE_DONE);
            speechStreamService.stop();
            speechStreamService = null;
        } else {
            setUiState(STATE_FILE);
            try {
                Recognizer rec = new Recognizer(model, 16000.f, "[\"one zero zero zero one\", " +
                        "\"oh zero one two three four five six seven eight nine\", \"[unk]\"]");

                InputStream ais = getAssets().open(
                        "10001-90210-01803.wav");
                if (ais.skip(44) != 44) throw new IOException("File too short");

                speechStreamService = new SpeechStreamService(rec, ais, 16000);
                speechStreamService.start(this);
            } catch (IOException e) {
                setErrorState(e.getMessage());
            }
        } */
    }

    private void recognizeMicrophone() {
        if (speechService != null) {
            setUiState(STATE_DONE);
            speechService.stop();
            speechService = null;
            isListening = false;
            Toast.makeText(getApplicationContext(), "Stopped Listening", Toast.LENGTH_SHORT).show();
        } else {
            setUiState(STATE_MIC);
            try {
                Recognizer rec = new Recognizer(model, 16000.0f);
                speechService = new SpeechService(rec, 16000.0f);
                speechService.startListening(this);
                Toast.makeText(getApplicationContext(), "Started listening", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                setErrorState(e.getMessage());
            }
        }
    }


    private void pause(boolean checked) {
        if (speechService != null) {
            speechService.setPause(checked);
        }
    }

}
