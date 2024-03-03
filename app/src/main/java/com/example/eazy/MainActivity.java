package com.example.eazy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.SmsManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {


    private TabLayout tabs;
    private ViewPager pager;

    /* Used to handle permission request */
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;

    public static VoskModel vm;
    public static ExecuteCommand ec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabs = findViewById( R.id.tabs );
        pager = findViewById( R.id.pager );

        try {
            vm = new VoskModel( getApplicationContext(), new Handler( Looper.getMainLooper()));
            ec = new ExecuteCommand( getApplicationContext() );
        }catch( Exception e )
        {
            Toast.makeText(this, "Error: " + e, Toast.LENGTH_SHORT).show();
        }

        requestPermissions();

        EazySpeak es = new EazySpeak(getApplicationContext());
        //es.speak("Hello World!");


        TabLayout.Tab tab1 = tabs.newTab();
        TabLayout.Tab tab2 = tabs.newTab();

        tab1.setText("Speak");
        tab2.setText("Functions");

        tabs.addTab(tab1);
        tabs.addTab(tab2);

        PagerAdapter pagerAdapter = new PagerAdapter( getSupportFragmentManager(), tabs.getTabCount(), getApplicationContext());
        pager.setAdapter( pagerAdapter );

        pager.addOnPageChangeListener( new TabLayout.TabLayoutOnPageChangeListener(tabs) );
        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch( tab.getPosition() )
                {
                    case 0:
                        pager.setCurrentItem(0);
                        break;
                    case 1:
                        pager.setCurrentItem(1);
                        break;

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



    }

    private void requestPermissions()
    {
        /*requestSendSMSPermission();
        requestCallPermission();
        requestReadContactPermission();
        requestRecordAudioPermission();
        requestWriteContactsPermission();*/

        //if( ActivityCompat.checkSelfPermission( this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.WRITE_CONTACTS,
                            Manifest.permission.CALL_PHONE,
                            Manifest.permission.SEND_SMS,
                            Manifest.permission.RECORD_AUDIO
                    },
                    1);
        }

    }

    private void requestRecordAudioPermission()
    {
        if(ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.RECORD_AUDIO ) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions( this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        }
        else
        {
            Toast.makeText( this, "Record Audio Permission Denied!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Recognizer initialization is a time-consuming and it involves IO,
                // so we execute it in async task
                try {
                    vm.initModel();
                }catch ( Exception e )
                {
                    Toast.makeText(getApplicationContext(), "Error Starting Listening thread: " + e, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                //finish();
            }
        }
    }

    private void requestSendSMSPermission()
    {
        if( ActivityCompat.checkSelfPermission( MainActivity.this, android.Manifest.permission.SEND_SMS )
                != PackageManager.PERMISSION_GRANTED )
        {
            ActivityCompat.requestPermissions( MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, 0);
        }
    }

    private void requestCallPermission()
    {
        if( ActivityCompat.checkSelfPermission( MainActivity.this, Manifest.permission.CALL_PHONE )
                != PackageManager.PERMISSION_GRANTED )
        {
            ActivityCompat.requestPermissions( MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 0);
        }
    }

    private void requestReadContactPermission()
    {
        if( ActivityCompat.checkSelfPermission( this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED )
        {
            ActivityCompat.requestPermissions( this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
        }
    }

    private void requestWriteContactsPermission()
    {
        if( ActivityCompat.checkSelfPermission( this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions( this, new String[]{Manifest.permission.WRITE_CONTACTS}, 1);
        }
    }
}