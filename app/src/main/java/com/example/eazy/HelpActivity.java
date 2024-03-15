package com.example.eazy;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;

public class HelpActivity extends AppCompatActivity {

    private TextView helpMessage;
    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.help );

        helpMessage = findViewById( R.id.help_message );

        loadHelpMessage();
    }

    private void loadHelpMessage()
    {
        try {
            String helpText = "";
            InputStream readHelp = getAssets().open("app_assets/help.txt");
            int size = readHelp.available();

            byte[] buffer = new byte[size];

            readHelp.read(buffer);

            helpText = new String(buffer);

            helpMessage.setText(helpText);

        }catch( Exception e )
        {
            Toast.makeText(getApplicationContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
        }
    }
}
