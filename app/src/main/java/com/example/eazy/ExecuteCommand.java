package com.example.eazy;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.util.Arrays;

public class ExecuteCommand implements Runnable {

    private Thread thread;

    private Context context;
    private Handler handler;

    public ExecuteCommand( Context context )
    {
        this.context = context;
        handler = new Handler( Looper.getMainLooper());
    }

    private void postToUI( String message)
    {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getAppContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
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
            else
            {
                if( !thread.isAlive() )
                {
                    thread = null;

                    thread = new Thread( this );
                    thread.start();
                }
            }

        }catch( Exception e )
        {
            postToUI("Error: " + e);
        }
    }

    private Context getAppContext(){return this.context;}

    @Override
    public void run()
    {
        try
        {
            Command command;
            String []statement;

            while( !CommandQueue.isEmpty() )
            {
                command = CommandQueue.dequeue();
                //postToUI("Command = " + command.getCommand() + "\nArg = " + command.getArg());

                if( command.getCommand().equals("") )//if empty command, no need of processing the statement
                    continue;

                statement = command.getStatement();
                String cmd = new String( statement[0] );

                switch( cmd )
                {
                    case "call" :
                        makePhoneCall(statement);
                        break;
                    case "switch", "turn":
                        switch( statement[2] )
                        {
                            case "flashlight", "torch":
                            switchFlashLight(statement);
                            default:
                                wrongCommand(statement);
                        }
                        break;
                    case "create", "save":
                        createContact(statement);
                        break;
                    case "text", "sms", "message":
                        sendSMS(statement);
                        break;
                    default:
                        wrongCommand(statement);
                }

            }

        }catch( Exception e )
        {
            postToUI("Error: " + e);
        }
    }

    private void makePhoneCall(String []statement)
    {
        try
        {
            postToUI("Making phone call");
            if( statement.length > 1 )
            {
                String []contactName = new String[statement.length - 1];

                for( int i = 0; i < statement.length; i++)
                {
                    if( (i + 1) < statement.length ) {
                        contactName[i] = new String( statement[i + 1] );
                        postToUI("" + contactName[i]);
                    }
                }
                CallPhone cp = new CallPhone( handler, getAppContext(), contactName, "");
                cp.callPhone();
            }
            else
            {
                postToUI("Please specify a contact name or number to call!");
            }
        }catch( Exception e )
        {
            postToUI("Error: " + e );
        }
    }
    private void switchFlashLight(String []statement)
    {
        try
        {
            postToUI("Switching flashlight");
            if( statement.length > 1 )
            {
                SwitchFlashLight sf = new SwitchFlashLight(handler, getAppContext());
                sf.switchFlashlight();
            }
            else
            {
                postToUI("Wrong arguements to switch or turn command!");
            }
        }catch( Exception e )
        {
            postToUI("Error: " + e );
        }
    }

    private void sendSMS(String []statement)
    {
        postToUI("sending sms");
        try
        {
            if( statement.length > 2 )
            {
                String []name = new String[]{ new String(statement[1])};

                String []state = Arrays.copyOfRange( statement, 1, statement.length );

                SendSMSMessage send = new SendSMSMessage(handler, getAppContext(), name, "", state);
                send.sendSMSMessage();
            }
            else if( statement.length == 2 )
            {
                postToUI("Please specify a message to send!");
            }
            else
            {
                postToUI("Please specify the contact to message and the message to send!");
            }

        }catch( Exception e )
        {
            postToUI("Error: " + e);
        }
    }
    private void createContact(String []statement)
    {
        postToUI("Creating contact");
    }

    private void wrongCommand( String []statement)
    {
        postToUI("Wrong command!");
    }
}
