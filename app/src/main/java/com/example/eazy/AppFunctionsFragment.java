package com.example.eazy;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Camera;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;

import java.util.ArrayList;

public class AppFunctionsFragment extends Fragment {

    private Context context;

    private SwitchFlashLight sf;
    private SaveContact sc;
    private CallPhone cp;

    public AppFunctionsFragment(Context context){
        this.context = context;
    }

    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        sf = new SwitchFlashLight(getAppContext());

        BroadcastReceiver smsReceiver = new SentReceivedBroadcastReceiver( getAppContext() );

        getAppContext().registerReceiver(smsReceiver, new IntentFilter( "SMS_SENT") );
        getAppContext().registerReceiver(smsReceiver, new IntentFilter( "SMS_RECIEVED") );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate( R.layout.app_functions, container, false);

        try {

            EditText num = v.findViewById( R.id.number );
            EditText name = v.findViewById( R.id.name );


            EditText number = v.findViewById( R.id.smsNumber );
            EditText message = v.findViewById( R.id.smsMessage );

            EditText callNumber = v.findViewById( R.id.callNumber );

            v.findViewById( R.id.call ).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String num = callNumber.getText().toString();

                    cp = new CallPhone( getAppContext(), "", num);
                    cp.callPhone();
                }
            });

            v.findViewById( R.id.send ).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String num = number.getText().toString();
                    String mes = message.getText().toString();

                    SendSMSMessage sm = new SendSMSMessage( getAppContext(), "", num, mes);
                    sm.sendSMSMessage();
                }
            });

            v.findViewById( R.id.switchTorch ).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sf.switchFlashlight();
                }
            });

            v.findViewById( R.id.saveContact ).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String contactNumber = num.getText().toString();
                    String contactName = name.getText().toString();

                    sc = new SaveContact( getContext(), contactName, contactNumber);
                    sc.saveContact();
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

}
/*
interface Functionality
{

    Context getAppContext();
    void giveResponse();
} */

class Contact
{
    private String name;
    private String number;
    private Context context;
    public Contact(Context context, String name, String number)
    {
        this.context = context;
        this.name = name;
        this.number = number;
    }

    private Context getAppContext(){return this.context;}

    public void setName( String name )
    {
        this.name = name;
    }

    public void setNumber(String number )
    {
        this.number = number;
    }

    public String getName()
    {
        return this.name;
    }

    public String getNumber()
    {
        if( this.number == null )//when number is null
        {
            searchNumber( getName() );
            return null;
        }

        try//when number is a string insted
        {

            int n = 0;
            n = Integer.parseInt( this.number );

        }catch( NumberFormatException e )
        {
            searchNumber( this.number );
            return null;
        }

        return this.number;
    }

    public String searchNumber( String name )
    {
        try {

            String []projection = { ContactsContract.Contacts.DISPLAY_NAME_PRIMARY, ContactsContract.Contacts.Data._ID};
            String whereClause = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " LIKE ? ";

            String []selectionArgs = { "%" + name + "%"};

            ContentResolver cr = getAppContext().getContentResolver();

            Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, projection, whereClause, selectionArgs, null);

            String data = "";
            ArrayList<String[]> contacts = new ArrayList<>();

            int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY);
            int contactId = cursor.getColumnIndex(ContactsContract.Contacts.Data._ID);

            //when cursor returns a valid value
            if( cursor != null )
            {
                //if cursor has data or rows
                if( cursor.moveToFirst() )
                {
                    do
                    {
                        String id = cursor.getString(contactId);

                        Cursor c = cr.query( ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id},
                                null);

                        String phone = "";
                        while( c.moveToNext() )
                        {
                            int index = c.getColumnIndex( ContactsContract.CommonDataKinds.Phone.NUMBER);
                            phone += c.getString( index );
                        }
                        c.close();

                        contacts.add( new String[]{ cursor.getString(nameIndex), phone});
                    }
                    while( cursor.moveToNext() );

                    for( String[] contact : contacts )
                    {
                        data += contact[0] + "  " + contact[1] + "\n\n";
                    }
                }
                else
                {
                    data += "no contacts match your search";
                }
            }

            cursor.close();

            Toast.makeText(getAppContext(), "Found:\n" + data, Toast.LENGTH_LONG).show();

        }catch(Exception e )
        {
            Toast.makeText(getAppContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
        }

        return name;
    }
}

class SaveContact// implements Functionality
{
    private Context context;

    private String name, number;
    public SaveContact( Context context, String name, String number )
    {
        this.context = context;
        this.name = name;
        this.number = number;
    }

    private void setName( String name )
    {
        this.name = name;
    }

    private void setNumber( String number )
    {
        this.number = number;
    }

    private String getName(){return this.name;}
    private String getNumber(){return this.number;}

    public void saveContact()
    {
        try
        {
            ContentResolver cr = getAppContext().getContentResolver();

            ContentValues cv = new ContentValues();
            Uri url = ContactsContract.Contacts.CONTENT_URI;

            cv.put(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY, getName() );
            //cv.put( ContactsContract.CommonDataKinds.Phone.NUMBER, getNumber() );

            cr.insert( url, cv );

        }catch (Exception e )
        {
            Toast.makeText(getAppContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
        }

        Toast.makeText(getAppContext(), "Saving Contact " + getNumber() + " as " + getName(), Toast.LENGTH_SHORT).show();
    }

   // @Override
    public Context getAppContext(){return this.context;}

   // @Override
    public void giveResponse()
    {

    }
}

class CallPhone //implements Functionality
{
    private Contact contact;
    private Context context;
    public CallPhone(Context context, String name, String number)
    {
        contact = new Contact(context, name, number);
        this.context = context;
    }

   // @Override
    public Context getAppContext()
    {
        return this.context;
    }

    public void callPhone()
    {
        try
        {
            if( contact.getNumber() == null )
                return;

            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
            callIntent.setData(Uri.parse("tel:" + contact.getNumber()));

            if(ActivityCompat.checkSelfPermission( getAppContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED )
            {
                Toast.makeText(getAppContext(), "Call Permission denied!", Toast.LENGTH_SHORT).show();
                return;
            }

            getAppContext().startActivity( callIntent );
        }catch(Exception e )
        {
            Toast.makeText(getAppContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
        }
    }

   // @Override
    public void giveResponse()
    {

    }
}

class SendSMSMessage //extends BroadcastReceiver //implements Functionality
{
    private Contact contact;
    private String message;
    private Context context;

    public SendSMSMessage(Context context, String name, String number, String message) {
        try
        {
            this.context = context;
            this.contact = new Contact(context, name, number);
            this.message = message;
        }catch( Exception e )
        {
            Toast.makeText(getAppContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
        }
    }

    public String getMessage() {
        return this.message;
    }

    public void sendSMSMessage() {
        if (contact.getNumber() == null)
            return;

        try {
            if (ActivityCompat.checkSelfPermission(getAppContext(), android.Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                //ActivityCompat.requestPermissions( getAppContext(), new String[]{Manifest.permission.SEND_SMS}, 0);
                Toast.makeText(getAppContext(), "Send SMS Permission Denied!", Toast.LENGTH_SHORT).show();
            }

            Intent sent = new Intent("SMS_SENT");
            sent.putExtra("recipient", contact.getNumber());
            Intent delivered = new Intent("SMS_DELIVERED");
            delivered.putExtra("recipient", contact.getNumber());
            PendingIntent sentIntent = PendingIntent.getBroadcast(getAppContext(), 0, sent, 0);
            PendingIntent deliveredIntent = PendingIntent.getBroadcast(getAppContext(), 0, delivered, 0);
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(contact.getNumber(), null, getMessage(), sentIntent, deliveredIntent);

        } catch (Exception e) {
            Toast.makeText(getAppContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    // @Override
    public Context getAppContext() {
        return this.context;
    }

    //@Override
    public void giveResponse() {

    }

}

class SentReceivedBroadcastReceiver extends BroadcastReceiver
{
    private String sent;
    private String received;
    private Context context;
    public SentReceivedBroadcastReceiver(Context context)
    {
        sent = "SMS_SENT";
        received = "SMS_RECEIVED";
        this.context = context;
    }

    private Context getContext(){return this.context;}
    @Override
    public void onReceive(Context context, Intent intent) {
        //display delivery and sent reports
        String action = intent.getAction();

        if( action.equals( sent ) )
        {
            Toast.makeText(getContext(), "Message Sent!", Toast.LENGTH_SHORT).show();
        }
        else if( action.equals( received ) )
        {
            Toast.makeText(getContext(), "Message Delivered!", Toast.LENGTH_SHORT).show();
        }
    }

}
class SwitchFlashLight// implements Functionality
{
    private Context context;
    private static boolean status;
    public SwitchFlashLight(Context context)
    {
        this.context = context;
        status = false;
    }

    private void setState( boolean state )
    {
        status = state;
    }

    private boolean getState(){return status;}

    public void switchFlashlight()
    {
        try
        {
            if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.M )
            {
                CameraManager cm = (CameraManager) getAppContext().getSystemService( Context.CAMERA_SERVICE );
                String cameraId = cm.getCameraIdList()[0];

                cm.setTorchMode( cameraId, getState() );

                if( getState() )
                    setState( false );
                else
                    setState( true );
            }
            else
            {
                Camera c = Camera.open();

                if( getState() )
                {
                    Camera.Parameters p = c.getParameters();
                    p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    c.setParameters( p );
                    c.startPreview();

                    setState( true );
                }
                else
                {
                    c.stopPreview();
                    c.release();

                    setState( false );
                }

            }
        }catch( Exception e )
        {
            Toast.makeText(getAppContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
        }
    }

   // @Override
    public Context getAppContext(){return this.context;}

   // @Override
    public void giveResponse()
    {

    }
}

