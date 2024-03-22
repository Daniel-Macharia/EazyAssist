package com.example.eazy;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Camera;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import java.util.Arrays;

public class AppFunctionsFragment extends Fragment {

    private Context context;

    private SwitchFlashLight sf;
    private SaveContact sc;
    private CallPhone cp;
    private Handler handler;

    public AppFunctionsFragment(Context context){
        this.context = context;
    }

    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );

        handler = new Handler(Looper.getMainLooper());

        sf = new SwitchFlashLight( handler, getAppContext());

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

                    cp = new CallPhone( handler, getAppContext(), new String[]{""}, num);
                    cp.callPhone();
                }
            });

            v.findViewById( R.id.send ).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String num = number.getText().toString();
                    String mes = message.getText().toString();

                    SendSMSMessage sm = new SendSMSMessage( handler, getAppContext(), new String[]{""}, num, new String[]{mes});
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

                    sc = new SaveContact( handler, getContext(), contactName, contactNumber);
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
    private String[] name;
    private String number;
    private Context context;
    private Handler handler;
    public Contact(Handler handler, Context context, String[] name, String number)
    {
        this.handler = handler;
        this.context = context;
        this.name = name;
        this.number = number;
    }

    private Context getAppContext(){return this.context;}

    public void setName( String[] name )
    {
        this.name = name;
    }

    public void setNumber(String number )
    {
        this.number = number;
    }

    public String[] getName()
    {
        return this.name;
    }

    public String getNumber()
    {
        if( this.number == null || number.equals("") )//when number is null
        {
            return searchNumber( getName() );
            //return null;
        }

        try//when number is a string insted
        {

            int n = 0;
            n = Integer.parseInt( this.number );

        }catch( NumberFormatException e )
        {
            return searchNumber(new String[]{this.number} );
            //return null;
        }

        return this.number;
    }

    private void postToUI(String message)
    {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getAppContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public ArrayList<String[]> search( String whereClause, String[] selectionArgs)
    {
        try
        {
            String []projection = { ContactsContract.Contacts.DISPLAY_NAME_PRIMARY, ContactsContract.Contacts.Data._ID};

            ContentResolver cr = getAppContext().getContentResolver();

            Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, projection, whereClause, selectionArgs, null);

            //String data = "";
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
                        if( c.moveToNext() )
                        {
                            int index = c.getColumnIndex( ContactsContract.CommonDataKinds.Phone.NUMBER);
                            phone += c.getString( index );
                        }
                        c.close();

                        contacts.add( new String[]{ cursor.getString(nameIndex), phone});
                    }
                    while( cursor.moveToNext() );

                }

            }

            cursor.close();

            return contacts;

            //Toast.makeText(getAppContext(), "Found:\n" + data, Toast.LENGTH_LONG).show();
        }catch( Exception e )
        {
            postToUI("Error: " + e);
        }

        return new ArrayList<String[]>();
    }

    public String searchNumber( String[] name )
    {
        try {
            String whereClause;

            String []selectionArgs;
            ArrayList<String[]> contacts;
            String data = "";

            switch( name.length )
            {
                case 1:
                    whereClause = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " LIKE ? ";
                    selectionArgs = new String[]{ name[0] };
                    contacts = search( whereClause, selectionArgs );

                    if( contacts.size() == 0 )
                    {
                        selectionArgs = new String[]{"%" + name[0] + "%"};
                        contacts = search( whereClause, selectionArgs );
                    }
                    //process contacts
                    break;
                case 2:
                    whereClause = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " LIKE ? AND " + ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " LIKE ? ";
                    selectionArgs = new String[]{ "%" + name[0] + "%", "%" + name[1] + "%"};
                    contacts = search( whereClause, selectionArgs );
                    break;
                case 3:
                    whereClause = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " LIKE ? AND " + ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " LIKE ? AND " + ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " LIKE ? " ;
                    selectionArgs = new String[]{ "%" + name[0] + "%", "%" + name[1] + "%", "%" + name[2] + "%"};
                    contacts = search( whereClause, selectionArgs );
                    break;
                default:
                    whereClause = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " LIKE ? ";
                    String contactName = "";
                    //concat all names and search
                    for( String n : name)
                        contactName += n + " ";

                    selectionArgs = new String[]{ "%" + contactName + "%"};
                    contacts = search( whereClause, selectionArgs );
            }

            if( contacts.size() > 0 )
            {
                for( String[] contact : contacts )
                {
                    data += contact[0] + "  " + contact[1] + "\n\n";
                }
            }
            else
            {
                data += "no contacts match your search";
            }

            if( contacts.size() == 1 )
            {
                postToUI("One Contact found!" + contacts.get(0)[1]);
                return contacts.get( 0 )[1];
            }
            else
            {
                return "Found " + contacts.size() + " contacts: \n" + data;
            }

        }catch( ArrayIndexOutOfBoundsException iex)
        {
            postToUI("Index out of bounds exception when reading contact number!");
        }
        catch(Exception e )
        {
            //Toast.makeText(getAppContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
            postToUI("Error: " + e);
        }

        return null;
    }
}

class SaveContact// implements Functionality
{
    private Context context;

    private String name, number;

    private Handler handler;
    public SaveContact( Handler handler, Context context, String name, String number )
    {
        this.handler = handler;
        this.context = context;
        this.name = name;
        this.number = number;
    }

    private void postToUI(String message)
    {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getAppContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
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
            ArrayList<ContentProviderOperation> cp = new ArrayList<>();
            int rawContactInsertIndex = cp.size();

            cp.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                    .build());

            cp.add( ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                    .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, getName())
                    .build());

            cp.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID,rawContactInsertIndex)
                    .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, getNumber())
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .build());

            ContentProviderResult[]res = getAppContext().getContentResolver().applyBatch(ContactsContract.AUTHORITY, cp);

        }catch (Exception e )
        {
            //Toast.makeText(getAppContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
            postToUI("Error: " + e);
        }

        //Toast.makeText(getAppContext(), "Saving Contact " + getNumber() + " as " + getName(), Toast.LENGTH_SHORT).show();
        postToUI("Saving Contact " + getNumber() + " as " + getName());
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
    private Handler handler;
    public CallPhone(Handler handler, Context context, String[] name, String number)
    {
        this.handler = handler;
        contact = new Contact(handler, context, name, number);
        this.context = context;
    }

    private void postToUI(String message)
    {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getAppContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
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
            String number = contact.getNumber();
            if( number == null )
                return;
            else
            if( number.contains("Found"))
            {
                ResponseQueue.enqueue(getAppContext(), number);
                return;
            }

            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
            callIntent.setData(Uri.parse("tel:" + contact.getNumber()));

            if(ActivityCompat.checkSelfPermission( getAppContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED )
            {
                //Toast.makeText(getAppContext(), "Call Permission denied!", Toast.LENGTH_SHORT).show();
                postToUI("Call Permission denied!");
                return;
            }

            getAppContext().startActivity( callIntent );
        }catch(Exception e )
        {
            //Toast.makeText(getAppContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
            postToUI("Error: " + e);
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
    private String []statement;
    private Context context;
    private Handler handler;

    public SendSMSMessage(Handler handler, Context context, String []name, String number, String []statement)
    {
        this.handler = handler;
        this.context = context;
        contact = new Contact(handler, context, name, number);
        this.statement = statement;
    }

    private void postToUI(String message)
    {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getAppContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String getMessage()
    {
        String message = "";

        for( String s : statement )
            message += s + " ";

        return message;
    }

    private String getNumber( int names) {
        String number = new String(contact.getNumber());

        String internationalPhoneNumberRegex = "^\\+(?:[0-9]?){6,14}[0-9]$";
        String internationalPhoneNumberWithSpacesRegex = "^\\+([0-9]){1,3}\\s[0-9]{1,3}\\s[0-9]{4,8}$";
        String kenyanPhoneNumberRegex = "^0[17](?:[0-9]){8}&";
        String kenyanPhoneRegexWithSpace = "^0[17](?:[0-9]?){2}\\s[0-9]{6}$";

        if( number.matches(internationalPhoneNumberRegex) || number.matches( internationalPhoneNumberWithSpacesRegex )
                || number.matches(kenyanPhoneNumberRegex) || number.matches( kenyanPhoneRegexWithSpace ) )
        {
            postToUI("Number is valid!" + number);
            return number;
        }
        else
        {
            postToUI("Number does not match regex! " + number);
            if( names <= 3 && names <= statement.length)
            {
                try
                {
                    int num = Integer.parseInt( number );
                }catch( NumberFormatException ex )
                {
                    String []n = this.statement;
                    String []name = contact.getName();

                    String []newName = new String[name.length + 1];

                    for( int i = 0; i < name.length; i++ )
                        newName[i] = name[i];

                    newName[ name.length ] = n[0];

                    statement = Arrays.copyOfRange( statement, 1, statement.length);

                    contact.setName( newName );
                    postToUI("recurring to search further. Names: " + names);

                    return getNumber( names + 1 );
                }catch( Exception e )
                {
                    postToUI("Error: " + e);
                }
            }
        }

        return null;
    }

    public void sendSMSMessage() {
        try
        {
            String number = getNumber(1);
            if ( number == null)
                return;
            //ResponseQueue.enqueue(getAppContext(), number);

            if (ActivityCompat.checkSelfPermission(getAppContext(), android.Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                //ActivityCompat.requestPermissions( getAppContext(), new String[]{Manifest.permission.SEND_SMS}, 0);
                //Toast.makeText(getAppContext(), "Send SMS Permission Denied!", Toast.LENGTH_SHORT).show();
                postToUI("Send SMS Permission Denied!");
            }

            Intent sent = new Intent("SMS_SENT");
            sent.putExtra("recipient", number);
            Intent delivered = new Intent("SMS_DELIVERED");
            delivered.putExtra("recipient", number);
            PendingIntent sentIntent = PendingIntent.getBroadcast(getAppContext(), 0, sent, 0);
            PendingIntent deliveredIntent = PendingIntent.getBroadcast(getAppContext(), 0, delivered, 0);
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number, null, getMessage(), sentIntent, deliveredIntent);

        }catch(ArrayIndexOutOfBoundsException iex )
        {
            postToUI("index out of bounds exception while sending message!");
        }catch (Exception e)
        {
            postToUI("Error: " + e);
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
    private Handler handler;

    public SentReceivedBroadcastReceiver(Context context)
    {
        this.handler = new Handler( context.getMainLooper() );
        sent = "SMS_SENT";
        received = "SMS_RECEIVED";
        this.context = context;
    }

    private Context getAppContext(){return this.context;}

    private void postToUI(String message)
    {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getAppContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Context getContext(){return this.context;}
    @Override
    public void onReceive(Context context, Intent intent) {
        //display delivery and sent reports
        String action = intent.getAction();

        if( action.equals( sent ) )
        {
            //Toast.makeText(getContext(), "Message Sent!", Toast.LENGTH_SHORT).show();
            postToUI("Message Sent!");
        }
        else if( action.equals( received ) )
        {
            //Toast.makeText(getContext(), "Message Delivered!", Toast.LENGTH_SHORT).show();
            postToUI("Message Delivered!");
        }
    }

}
class SwitchFlashLight// implements Functionality
{
    private Context context;
    private static boolean status = false;

    private Handler handler;
    public SwitchFlashLight(Handler handler, Context context)
    {
        this.handler = handler;
        this.context = context;
    }

    private void postToUI(String message)
    {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getAppContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
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

                cm.registerTorchCallback(new CameraManager.TorchCallback() {
                    @Override
                    public void onTorchModeUnavailable(@NonNull String cameraId) {
                        super.onTorchModeUnavailable(cameraId);
                    }

                    @Override
                    public void onTorchModeChanged(@NonNull String cameraId, boolean enabled) {
                        super.onTorchModeChanged(cameraId, enabled);
                        if( enabled )
                            postToUI("Torch enabled");
                        else
                            postToUI("Torch disabled");
                    }
                }, handler);

                if( getState() )
                {
                    cm.setTorchMode( cameraId, false );
                    setState(false);
                }
                else
                {
                    cm.setTorchMode(cameraId, true);
                    setState(true);
                }

            }
            else
            {
                Camera c = Camera.open();

                if( c == null )
                {
                    postToUI("Camera.open() returns null!");
                }

                if( c.getParameters().getFlashMode().equals(Camera.Parameters.FLASH_MODE_ON) )
                {
                    postToUI("Flash is on");
                    setState(false);
                }
                else
                {
                    postToUI("Flash is off");
                    setState(true);
                }

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
            //Toast.makeText(getAppContext(), "Error: " + e, Toast.LENGTH_SHORT).show();
            postToUI("Error: " + e);
        }
    }

   // @Override
    public Context getAppContext(){return this.context;}

   // @Override
    public void giveResponse()
    {

    }
}

