package com.example.eazy;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

public class AppFunctionsFragment extends Fragment {

    private Context context;
    public AppFunctionsFragment(Context context){
        this.context = context;
    }

    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate( R.layout.app_functions, container, false);

        try {

            EditText number, message;
            number = v.findViewById( R.id.contact );
            message = v.findViewById( R.id.message );
            v.findViewById( R.id.call ).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String num = number.getText().toString();

                    //make phone call
                    callPhone( getNumber(num) );

                    number.setText("");
                }
            });

            v.findViewById( R.id.send ).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String msg = message.getText().toString();
                    String num = number.getText().toString();

                    sendTextMessage( getNumber(num), msg );

                    message.setText("");
                    number.setText("");
                }
            });
        }catch( Exception e )
        {
            Toast.makeText(getAppContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
        }

        return v;
    }

    private Context getAppContext()
    {
        return this.context;
    }

    private void callPhone( String number )
    {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + number));

        if(ActivityCompat.checkSelfPermission( getAppContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED )
        {
            Toast.makeText(context, "Permission denied!", Toast.LENGTH_SHORT).show();
            return;
        }

        startActivity( callIntent );
    }

    private void sendTextMessage(String number, String message )
    {
        try{
            if( ActivityCompat.checkSelfPermission( getAppContext(), android.Manifest.permission.SEND_SMS )
                    != PackageManager.PERMISSION_GRANTED )
            {
                //ActivityCompat.requestPermissions( getAppContext(), new String[]{Manifest.permission.SEND_SMS}, 0);
                Toast.makeText(context, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }

            Intent s = new Intent("SMS_SENT");
            s.putExtra("recipient", number);
            Intent d = new Intent("SMS_DELIVERED");
            d.putExtra("recipient", number);
            PendingIntent sentIntent = PendingIntent.getBroadcast(getAppContext(), 0, s, 0);
            PendingIntent deliveredIntent = PendingIntent.getBroadcast(getAppContext(), 0, d, 0);
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number, null, message, sentIntent, deliveredIntent);

        }catch( NumberFormatException n )
        {
            //contact does not contai integer value
            //try searching user's contact list


        }
        catch( Exception e )
        {
            Toast.makeText( getAppContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private String getNumber( String contact )
    {
        try {
            int n = 0;
            n = Integer.parseInt( contact );
        }
        catch( NumberFormatException n)
        {
            //if cannot be parsed to an int then it contains characters
            //search user's contact list for a match
            

        }
        catch( Exception e )
        {
            Toast.makeText(context, "Error: " + e, Toast.LENGTH_SHORT).show();
        }

        return contact;
    }
}
