package com.example.eazy;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import org.vosk.Model;
import org.vosk.Recognizer;
import org.vosk.android.RecognitionListener;
import org.vosk.android.SpeechService;
import org.vosk.android.SpeechStreamService;
import org.vosk.android.StorageService;

import java.io.IOException;
import java.io.InputStream;

public class RecognizeVoice extends Fragment {

    private View view;
    public static TextView resultView;

    private Context context;

    public RecognizeVoice(Context context){
        try {
            this.context = context;
            //vm = new VoskModel( getApplicationContext(), new Handler( Looper.getMainLooper()), resultView);
        }catch( Exception e )
        {
            Toast.makeText(context, "Error: " + e, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try
        {
            //vm = new VoskModel( getApplicationContext(), new Handler( Looper.getMainLooper()), resultView);
            MainActivity.vm.initModel();
        }catch( Exception e)
        {
            Toast.makeText(context, "Error: " + e, Toast.LENGTH_SHORT).show();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate( R.layout.recognize, container, false);

        resultView = view.findViewById( R.id.result);
        Button rec = view.findViewById( R.id.recognize_mic );

        //vm = new VoskModel( getApplicationContext(), new Handler( Looper.getMainLooper()), resultView);

        try {
            rec.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.vm.recognizeMicrophone();
                }
            });
        }catch( Exception e )
        {
            Toast.makeText(context, "Error: " + e, Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private Context getApplicationContext()
    {
        return this.context;
    }

}
