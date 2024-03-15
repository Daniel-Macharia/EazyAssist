package com.example.eazy;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Properties;

public class ContactDevelopersActivity extends AppCompatActivity {

    private EditText emailSubject, emailBody;
    private Button sendEmail;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.contact_develpers );

        emailSubject = findViewById( R.id.email_subject );
        emailBody = findViewById( R.id.email_body );
        sendEmail = findViewById( R.id.send_email );

        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subject, body;
                subject = emailSubject.getText().toString();
                body = emailBody.getText().toString();

                sendEmailToDevs( subject, body);
            }
        });
    }

    private void sendEmailToDevs( String subject, String body)
    {
        try {
            if( !UtilityClass.networkIsAvailable( getApplicationContext() ) )
            {
                Toast.makeText(this, "Check network connection!", Toast.LENGTH_LONG).show();
            }

            Intent selectorIntent = new Intent( Intent.ACTION_SENDTO);
            selectorIntent.setData( Uri.parse("mailto:"));

            String mail = "ndungudanny444@gmail.com";

            final Intent emailIntent = new Intent( Intent.ACTION_SEND );
            emailIntent.putExtra( Intent.EXTRA_EMAIL, new String[]{mail});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            emailIntent.putExtra( Intent.EXTRA_TEXT, body);
            emailIntent.setSelector( selectorIntent );

            startActivity( Intent.createChooser( emailIntent, "Send Email") );
        }catch( Exception e )
        {
            Toast.makeText(getApplicationContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
        }
    }
}
